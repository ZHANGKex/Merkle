import static org.junit.Assert.*;
import org.junit.Test;
import java.util.LinkedList;
import java.util.Arrays;

public class TestLogServer {
    @Test
    public void testRootHashCalculation() {
        LogServer logServer = new LogServer("src/test/java/logFile/input.txt");
        logServer.append("1");
        logServer.append("1");

        byte[] expectedRootHash = new byte[]{122, 97, 17, 62, 84, -96, 39, -80, 7, -70, 96, -71, -74, -6, -71, -38, 65, 61, -82, 81, -108, -15, -34, -111, -38, -81, 52, -59, -28, 31, 55, 37};

        byte[] actualRootHash = logServer.currentRootHash();
        assertArrayEquals(expectedRootHash, actualRootHash);
    }



    @Test
    public void testGenerateAuditPath() {
        // 创建 LogServer 对象
        LogServer logServer = new LogServer("src/test/java/logFile/input.txt");

        // 向日志中添加一些条目
        logServer.append("Log 1");
        logServer.append("Log 2");
        logServer.append("Log 3");

        // 选择要测试的日志索引
        int logIndex = 1; // 请提供一个日志索引以测试

        // 生成审核路径
        LinkedList<byte[]> auditPath = logServer.genPath(logIndex);

        // 打印审核路径（用于调试）
        System.out.println(auditPath);

        // 验证路径中的哈希值是否正确
        // 这里你需要编写代码来验证生成的审核路径是否有效和正确
        // 例如，你可以验证路径的长度是否足够，以及哈希值是否与预期的一致
        // 可以使用 assert 语句进行验证
    }

//
//    @Test
//    public void testGenerateProof() {
//        LogServer logServer = new LogServer("data/input.txt"); // 请替换为你的输入文件路径
//        logServer.append("Log 1");
//        logServer.append("Log 2");
//        logServer.append("Log 3");
//        int logIndex = /* 日志的索引 */; // 请提供一个日志索引以测试
//        LinkedList<byte[]> proof = logServer.genProof(logIndex);
//        // 验证证明中的哈希值是否正确
//        // 检查证明是否足够长，并验证哈希值
//    }
//
//    @Test
//    public void testLogServerEquality() {
//        LogServer logServer1 = new LogServer("data/input1.txt"); // 请替换为不同的输入文件路径
//        LogServer logServer2 = new LogServer("data/input2.txt"); // 请替换为与logServer1不同的输入文件路径
//        // 添加相同的日志到两个日志服务器
//        assertEquals(logServer1, logServer2);
//    }

    public static void main(String[] args) {
        String inputFile = "src/test/java/logFile/input.txt"; // 请替换为你的输入文件路径
        LogServer logServer = new LogServer(inputFile);

        logServer.append("1");
        logServer.append("1");

        // 计算根哈希值
        byte[] expectedRootHash = logServer.currentRootHash();

        // 打印根哈希值
        System.out.println("Expected Root Hash: " + Arrays.toString(expectedRootHash));

    }
}
