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
        List<String> events = Arrays.asList("Event1", "Event2", "Event3");
        MerkleTree tree = new MerkleTree(events);
        assertNotNull(tree.getRootHash(), "The root hash should not be null for non-empty tree");
        // 更多的断言可以加入来检查树的结构
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
        List<String> events = Arrays.asList("Event1", "Event2", "Event3");
        MerkleTree tree = new MerkleTree(events);
        List<byte[]> path = tree.genPath("Event2");
        assertNotNull(path, "Path should not be null");
        assertFalse(path.isEmpty(), "Path should not be empty for existing event");
        // 进一步检查路径的正确性，例如长度和具体的哈希值
    }

    @Test
    void testGenProof() {
        List<String> events = Arrays.asList("Event1", "Event2", "Event3");
        MerkleTree tree = new MerkleTree(events);
        tree.appendEvent("Event4");
        List<byte[]> proof = tree.genProof(3, 4);
        assertNotNull(proof, "Proof should not be null");
        // 断言证明的正确性，例如长度和哈希值
    }

    @Test
    void testGenPathForNonExistingEvent() {
        MerkleTree tree = new MerkleTree(Arrays.asList("Event1", "Event2", "Event3"));
        List<byte[]> path = tree.genPath("EventX");
        assertTrue(path.isEmpty(), "Path should be empty for non-existing event");
    }






}
