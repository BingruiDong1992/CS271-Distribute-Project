import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class DataCenter implements Runnable{
    private int dataCenterId;
    private int port;
    private int totalTickets = 100;
    private int clock = 0;

    private DatagramSocket ds;

    final private Map<Integer, Integer> PortsOfOtherDataCenter;
    final private PriorityQueue<Request> priorityQueue;

    public DataCenter(int id, int port, Map<Integer, Integer> allDataCenter) throws SocketException {
        dataCenterId = id;
        this.port = port;
        ds = new DatagramSocket(port);
        priorityQueue = new PriorityQueue<>();

        PortsOfOtherDataCenter = new HashMap<>();
        for (int key : allDataCenter.keySet()) {
            if (key != dataCenterId)
                PortsOfOtherDataCenter.put(key, allDataCenter.get(key));
        }
    }

    @Override
    public void run(){
        byte[] buf = new byte[1024];
        DatagramPacket dp_receive = new DatagramPacket(buf, 1024);
        System.out.println("DataCenter " + dataCenterId + " is on，waiting for client to send data......");

        boolean f = true;
        while(f){
            //Receive the Data from Client and Other DataCenter
            try {
                ds.receive(dp_receive);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("DataCenter " + dataCenterId + " received data");

//            int MessagePort = (dp_receive.getPort());
//            String MessageIP = dp_receive.getAddress().getHostAddress();

            // Processing the Data
            String str_receive = new String(dp_receive.getData(),0,dp_receive.getLength());
            String[] strs_receive = str_receive.split(",");
            System.out.println("DataCenter " + dataCenterId + " Receive the data " + str_receive);

            // Send data out to Client or Other DataCenter
            switch (strs_receive[0]) {
                case "Client":
                    clock++;
                    int numOfTickert = Integer.parseInt(strs_receive[1]);
                    Request re = new Request(dataCenterId, clock, "" + numOfTickert);
                    priorityQueue.offer(re);
                    broadCastRequest(re);
                    break;

                case "DataCenter":
                    System.out.println(str_receive);
                    int sourceId = Integer.parseInt(strs_receive[2]);
                    int clockM = Integer.parseInt(strs_receive[3]);
                    clock = Math.max(clockM, clock) + 1;
                    switch (strs_receive[1]) {
                        case "Release":
                            for (Request r : priorityQueue)
                                if (r.senderId == sourceId) {
                                    r.finish = true;
                                }

                            if (priorityQueue.peek().finish)
                                handleRequest();
                            break;

                        case "Reply":
                            for (Request r : priorityQueue) {
                                if (r.senderId == dataCenterId) {
                                    r.finishSet.add(sourceId);
                                    if (r.finishSet.size() == PortsOfOtherDataCenter.size()) {
                                        r.finish = true;
                                    }
                                    break;
                                }
                            }
                            if (priorityQueue.peek().finish)
                                handleRequest();
                            break;

                        case "Request":
                            priorityQueue.offer(new Request(sourceId, clockM, strs_receive[4]));
                            reply(PortsOfOtherDataCenter.get(sourceId));
                            break;
                    }

                default:

                    break;
            }
//            new ServerThread(ds, str_send.toString(), dp_receive.getAddress(), dp_receive.getPort()).start();

            //由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
            //所以这里要将dp_receive的内部消息长度重新置为1024
            dp_receive.setLength(1024);
        }
        ds.close();
    }

    private void handleRequest() {
        Request request = priorityQueue.poll();
        if (request.senderId == dataCenterId) {
            broadCastRelease(request);
        }

        totalTickets -= Integer.parseInt(request.message);
        System.out.println("DataCenter " + dataCenterId + " Now have Tickets " + totalTickets);

        if (!priorityQueue.isEmpty() && priorityQueue.peek().finish) handleRequest();
    }

    private void broadCastRequest(Request request) {
        for (int port : PortsOfOtherDataCenter.values()) {
            try {
                new ServerThread(ds, "DataCenter,Request," + request.toMessage(), InetAddress.getLocalHost(), port).start();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadCastRelease(Request request) {
        for (int port : PortsOfOtherDataCenter.values()) {
            try {
                new ServerThread(ds, "DataCenter,Release," + dataCenterId +"," + clock + "," + request.message, InetAddress.getLocalHost(), port).start();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    private void reply(int port) {
        try {
            new ServerThread(ds, "DataCenter,Reply," + dataCenterId + "," + clock, InetAddress.getLocalHost(), port).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
