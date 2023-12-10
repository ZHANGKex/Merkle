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
        // 创建 LogServer 对象，提供输入文件路径
        LogServer logServer = new LogServer("src/test/java/logFile/input.txt");

        // 向 Merkle 树中添加一些条目
        logServer.append("Log 1");
        logServer.append("Log 2");
        logServer.append("Log 3");

        // 选择要测试的日志索引
        int logIndex = 1; // 请提供一个日志索引以测试

        // 生成审核路径
        LinkedList<byte[]> auditPath = logServer.genPath(logIndex);

        System.out.println(auditPath);
        int treeDepth = calculateTreeDepth(logServer.getMerkleTree());
        System.out.println("Tree Depth: " + treeDepth);
        System.out.println(auditPath);

        int expectedPathLength = treeDepth - 1;
        assertEquals(expectedPathLength, auditPath.size());
    }

    private int calculateTreeDepth(MerkleTree tree) {
        if (tree == null) {
            return 0;
        } else if (tree.getLeft() == null && tree.getRight() == null) {
            return 1;
        } else {
            int leftDepth = calculateTreeDepth(tree.getLeft());
            int rightDepth = calculateTreeDepth(tree.getRight());
            return Math.max(leftDepth, rightDepth) + 1;
        }
    }

    @Test
    public void testGenProof() {
        LogServer logServer = new LogServer("src/test/java/logFile/input.txt");
        logServer.append("Log 1");
        logServer.append("Log 2");
        logServer.append("Log 3");

        int logIndex = 1; // 请提供一个日志索引以测试

        LinkedList<byte[]> proof = logServer.genProof(logIndex);

        System.out.println(proof);
        int treeDepth = calculateTreeDepth(logServer.getMerkleTree());
        assertEquals(treeDepth, proof.size());

    }


    @Test
    public void testEquals() {
        LogServer logServer1 = new LogServer("src/test/java/logFile/input.txt");
        LogServer logServer2 = new LogServer("src/test/java/logFile/input.txt");

        boolean areEqual = logServer1.equals(logServer2);

        assertTrue(areEqual);
    }


    public static void main(String[] args) {
        String inputFile = "src/test/java/logFile/input.txt"; // 请替换为你的输入文件路径
        LogServer logServer = new LogServer(inputFile);

        logServer.append("1");
        logServer.append("1");

        byte[] expectedRootHash = logServer.currentRootHash();

        System.out.println("Expected Root Hash: " + Arrays.toString(expectedRootHash));

    }
}
