import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LogClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ILogServer stub = (ILogServer) registry.lookup("LogServer");

            byte[] rootHash = stub.getCurrentRootHash();
            // 使用rootHash...
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
