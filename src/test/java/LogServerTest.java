import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogServerTest {
    private LogServer logServer;

    @TempDir
    Path tempDir; // JUnit 5 将在这里注入一个临时目录的路径

    @BeforeEach
    void setUp() throws IOException {
        // 使用临时目录中的文件作为日志文件
        Path tempFile = tempDir.resolve("test-log.txt");
        Files.write(tempFile, Arrays.asList("Event1", "Event2", "Event3")); // 用于 Java 8 的写法
        logServer = new LogServer(tempFile.toString());
    }

    @Test
    void testAppendEvent() {
        // 测试appendEvent是否正确添加了事件
        byte[] initialRootHash = logServer.getCurrentRootHash();
        logServer.appendEvent("NewEvent");
        byte[] updatedRootHash = logServer.getCurrentRootHash();

        // 根哈希值应该在添加事件后改变
        assertNotEquals(bytesToHex(initialRootHash), bytesToHex(updatedRootHash));
    }

    @Test
    void testGetCurrentRootHash() {
        // 测试getCurrentRootHash是否返回了正确的哈希值
        assertNotNull(logServer.getCurrentRootHash());
    }

    @Test
    void testGenPath() {
        // 测试genPath是否为现有事件返回了正确的审计路径
        String existingEvent = "ExistingEvent"; // 确保这个事件在日志文件中
        List<byte[]> path = logServer.genPath(existingEvent);
        assertNotNull(path);
        assertFalse(path.isEmpty());

        // 进一步的测试可以包括路径的长度和具体的哈希值
    }

    @Test
    void testGenProof() {
        // 测试genProof是否为给定的树大小返回了正确的一致性证明
        int oldSize = 2; // 用旧的树大小
        int newSize = 3; // 新树添加了一个事件后的大小
        List<byte[]> proof = logServer.genProof(oldSize, newSize);

        assertNotNull(proof);
        // 证明的长度应该是逻辑上的长度，可以根据 Merkle 树的具体实现来断言
        // 这里的长度只是一个示例
        assertEquals(proof.size(), calculateExpectedProofSize(oldSize, newSize));
    }

    // 辅助方法将字节数组转换为十六进制字符串
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // 辅助方法用于计算一致性证明的预期大小
    private int calculateExpectedProofSize(int oldSize, int newSize) {
        // 实现根据Merkle树的性质来计算一致性证明的预期大小
        // 这里需要具体的实现逻辑
        return (int) (Math.log(newSize) / Math.log(2));
    }
}
