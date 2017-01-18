import java.io.IOException;

/**
 * Created by bingrui on 1/17/17.
 */
public class ClientDemo {
    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.buyTicket(4, 3000);

        client.buyTicket(1, 3001);
    }

}
