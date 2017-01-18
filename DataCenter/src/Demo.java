import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bingrui on 1/17/17.
 */
public class Demo {
    public static void main(String[] args) throws InterruptedException, IOException {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1, 3000);
        map.put(2, 3001);
        DataCenter dc1 = new DataCenter(1, 3000, map);
        DataCenter dc2 = new DataCenter(2, 3001, map);
        new Thread(dc1).start();
        new Thread(dc2).start();
    }
}
