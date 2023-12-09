import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MerkleTreeTest {

    public static String bytesToHex(byte[] bytes) {
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


    @Test
    void testAppendEventAndRootHash() {
        MerkleTree tree = new MerkleTree(new ArrayList<>());
        tree.appendEvent("Event1");
        byte[] rootHash1 = tree.getRootHash();

        tree.appendEvent("Event2");
        byte[] rootHash2 = tree.getRootHash();

        assertNotEquals(bytesToHex(rootHash1), bytesToHex(rootHash2));
    }

    @Test
    void testMerkleTreeConstruction() {
        // Setup
        List<String> events = Arrays.asList("Event1", "Event2", "Event3");
        MerkleTree tree = new MerkleTree(events);

        // Assertions
        assertNotNull(tree.getRootHash(), "The root hash should not be null for a non-empty tree");
        // Additional assertions to check the structure of the tree.
        // This can include checking the indices and hashes of the nodes.
    }



    @Test
    void testEmptyTree() {
        MerkleTree tree = new MerkleTree(new ArrayList<>());
        assertNull(tree.getRootHash(), "The root hash should be null for empty tree");
    }

    @Test
    void testSingleEventTree() {
        MerkleTree tree = new MerkleTree(Collections.singletonList("Event1"));
        assertNotNull(tree.getRootHash(), "The root hash should not be null for single event tree");
        // 断言这个根哈希实际上与单个事件的哈希相同
    }

    @Test
    void testGenPath() {
        // 创建一个包含三个事件的默克尔树
        MerkleTree tree = new MerkleTree(Arrays.asList("Event1", "Event2", "Event3"));
        // 为存在的事件生成路径
        List<byte[]> path = tree.genPath("Event2");
        // 验证路径不应为空
        assertNotNull(path, "Path should not be null");
        // 路径不应为空
        assertFalse(path.isEmpty(), "Path should not be empty for existing event");
        // 这里可以添加更多的断言来检查路径的具体内容
    }


    @Test
    void testGenProof() {
        // 创建一个包含三个事件的默克尔树
        MerkleTree tree = new MerkleTree(Arrays.asList("Event1", "Event2", "Event3"));
        // 向树中添加一个新事件
        tree.appendEvent("Event4");
        // 生成前三个事件和四个事件状态之间的一致性证明
        List<byte[]> proof = tree.genProof(3, 4);
        // 验证证明不应为空
        assertNotNull(proof, "Proof should not be null");
        // 这里可以添加更多的断言来检查证明的具体内容
    }


    @Test
    void testGenPathForNonExistingEvent() {
        // 创建一个包含三个事件的默克尔树
        MerkleTree tree = new MerkleTree(Arrays.asList("Event1", "Event2", "Event3"));
        // 尝试为不存在的事件生成路径
        List<byte[]> path = tree.genPath("EventX");
        // 路径应该为空
        assertTrue(path.isEmpty(), "Path should be empty for non-existing event");
    }


    @Test
    void testNodeRangeIndex() {
        Node leaf1 = new Node("Event1", 0);
        Node leaf2 = new Node("Event2", 1);
        Node internalNode = new Node(leaf1, leaf2, leaf1.startIndex, leaf2.endIndex);

        assertEquals(0, leaf1.startIndex);
        assertEquals(0, leaf1.endIndex);
        assertEquals(1, leaf2.startIndex);
        assertEquals(1, leaf2.endIndex);
        assertEquals(0, internalNode.startIndex);
        assertEquals(1, internalNode.endIndex);
    }


// 测试其他功能和边界情况...







}
