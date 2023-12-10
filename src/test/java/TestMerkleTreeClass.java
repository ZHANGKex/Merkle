import org.junit.Test;

import java.security.MessageDigest;

import static org.junit.Assert.*;

public class TestMerkleTreeClass {

    @Test
    public void testComputeHashWithString() {
        String s1 = "1";
        MerkleTree t1 = new MerkleTree(s1, 1);
        byte[] expectedHash = computeExpectedHash(s1);

        byte[] actualHash = t1.getHash();
        assertArrayEquals(expectedHash, actualHash);
    }

    private byte[] computeExpectedHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update((byte) 0x00);
            digest.update(data.getBytes("UTF-8"));
            return digest.digest();
        } catch (Exception e) {
            throw new RuntimeException("Unable to compute hash", e);
        }
    }

    @Test
    public void testMerkleTreeEquality() {
        MerkleTree t1 = new MerkleTree("1", 1);
        MerkleTree t2 = new MerkleTree("1", 1);
        assertEquals(t1, t2);

        MerkleTree t3 = new MerkleTree("1", 1);
        MerkleTree t4 = new MerkleTree("2", 1);
        assertNotEquals(t3, t4);

        MerkleTree leaf1 = new MerkleTree("1", 1);
        MerkleTree leaf2 = new MerkleTree("2", 2);
        MerkleTree internal1 = new MerkleTree(leaf1, leaf2);
        MerkleTree internal2 = new MerkleTree(leaf1, leaf2);
        assertEquals(internal1, internal2);
    }

    @Test
    public void testMerkleTreeInequality() {
        MerkleTree t1 = new MerkleTree("1", 1);
        MerkleTree t2 = new MerkleTree("2", 2);
        assertNotEquals(t1, t2);
    }

    public static void main(String [] args)
    {
        //Testing adding a String
        String s1 = new String("1");
        String s2 = new String("1");

        MerkleTree t1 = new MerkleTree(s1,1);
        MerkleTree t2 = new MerkleTree(s2,2);

        t1.show();
        t2.show();


    }
}


