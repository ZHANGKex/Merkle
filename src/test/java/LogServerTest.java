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
    private MerkleTree merkleTree;

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
        // 设置测试参数
        int oldSize = 2;
        int newSize = 3;

        // 调用待测试的方法
        List<byte[]> proof = logServer.genProof(oldSize, newSize);

        // 验证结果不应为空
        assertNotNull(proof, "The proof should not be null");

        // 验证证明的长度是否符合预期
        int expectedSize = calculateExpectedProofSize(oldSize, newSize);
        assertEquals(expectedSize, proof.size(), "The proof size does not match the expected value");

        // 验证证明的内容正确性（可根据具体实现添加）
        // 例如，可以添加对证明中哈希值正确性的校验

        // 测试特殊情况，例如oldSize >= newSize
        proof = logServer.genProof(newSize, oldSize);
        assertTrue(proof.isEmpty(), "The proof should be empty for invalid tree size inputs");
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
