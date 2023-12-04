import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LogServerMain {
    public static void main(String[] args) {
        try {
            LogServer server = new LogServer("path/to/logfile.txt");
            Registry registry = LocateRegistry.createRegistry(1099); // 1099是RMI默认端口
            registry.bind("LogServer", server);
            System.out.println("LogServer is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
