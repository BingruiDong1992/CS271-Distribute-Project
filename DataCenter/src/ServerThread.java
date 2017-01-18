import java.io.*;
import java.net.*;

/**
 * Created by bingrui on 1/17/17.
 */
public class ServerThread extends Thread{
    private static final int TIMEOUT = 5000;  //设置接收数据的超时时间
    private static final int MAXNUM = 5;      //设置重发数据的最多次数
    private DatagramSocket ds;
    private DatagramPacket dp_send;
    public ServerThread(DatagramSocket ds, String message, InetAddress ip, int port) {
        dp_send= new DatagramPacket(message.getBytes(),message.length(), ip, port);
        this.ds = ds;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(3000);
            ds.send(dp_send);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
