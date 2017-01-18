import java.util.HashSet;
import java.util.Set;

/**
 * Created by bingrui on 1/17/17.
 */
public class Request implements Comparable<Request>{
    int senderId;
    private int clock;
    boolean finish;
    public String message;
    Set<Integer> finishSet;
    public Request(int senderId, int clock, String message) {
        this.senderId = senderId;
        this.clock = clock;
        this.message = message;
        this.finishSet = new HashSet<>();
        this.finish = false;
    }

    @Override
    public int compareTo(Request o) {
        if (this.clock == o.clock)
            return this.senderId - o.senderId;
        else
            return this.clock - o.clock;
    }

    @Override
    public boolean equals(Object obj) {
        Request o = (Request) obj;
        return this.senderId == o.senderId;
    }

    public String toMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(senderId).append(",").append(clock).append(",").append(message);
        return sb.toString();
    }
}
