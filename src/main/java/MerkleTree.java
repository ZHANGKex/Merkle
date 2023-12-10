import java.security.MessageDigest;
import java.util.Arrays;

public class MerkleTree {
    private byte[] hash;
    private MerkleTree left, right;
    private int start, end, size, nextPower;

    public MerkleTree getLeft() {
        return left;
    }
    public MerkleTree getRight() {
        return right;
    }
    public int getStart() {
        return start;
    }
    public int getEnd() {
        return end;
    }
    public int getSize() {
        return size;
    }
    public int getNextPower() {
        return nextPower;
    }

    // Constructor for existing tree - used for deep copying
    public MerkleTree(MerkleTree old) {
        this.hash = Arrays.copyOf(old.hash, old.hash.length);
        this.left = (old.left != null) ? new MerkleTree(old.left) : null;
        this.right = (old.right != null) ? new MerkleTree(old.right) : null;
        this.start = old.start;
        this.end = old.end;
        this.size = old.size;
        this.nextPower = old.nextPower;
    }

    // Constructor for leaf node - integrates Hash(String s) functionality
    public MerkleTree(String s, int index) {
        this.start = index;
        this.end = index;
        this.size = 1;
        this.nextPower = 1;
        this.hash = computeHash(s);
    }



    // Constructor for internal node - integrates Hash(Hash h1, Hash h2) functionality
    public MerkleTree(MerkleTree l, MerkleTree r) {
        if (l.end != r.start - 1) {
            System.out.println("Trees not contiguous, left end at " + l.end + "; right starts at " + r.start);
            System.exit(1);
        }
        this.left = l;
        this.right = r;
        this.start = l.start;
        this.end = r.end;
        this.size = l.size + r.size;
        this.nextPower = (size < Math.max(l.nextPower, r.nextPower)) ? Math.max(l.nextPower, r.nextPower) : 2 * Math.max(l.nextPower, r.nextPower);
        this.hash = computeHash(concatenateHashes(l.hash, r.hash));
    }

    public byte[] getHash() {
        return this.hash;
    }

    // Replaces Hash() default constructor and computeHash(byte[] leaf) method
    public byte[] computeHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update((byte) 0x00);
            digest.update(data.getBytes("UTF-8"));
            return digest.digest();
        } catch (Exception e) {
            throw new RuntimeException("Unable to compute hash", e);
        }
    }

    // Used by the internal node constructor to concatenate and hash two hashes
    public byte[] computeHash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update((byte) 0x01);
            digest.update(data);
            return digest.digest();
        } catch (Exception e) {
            throw new RuntimeException("Unable to compute hash", e);
        }
    }


    public byte[] computeInternalNodeHash(byte[] leftHash, byte[] rightHash) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update((byte) 0x01);
            digest.update(leftHash);
            digest.update(rightHash);
            return digest.digest();
        } catch (Exception e) {
            throw new RuntimeException("Unable to compute hash", e);
        }
    }

    // Helper method to concatenate two byte arrays (hashes)
    private byte[] concatenateHashes(byte[] leftHash, byte[] rightHash) {
        byte[] concatenated = new byte[leftHash.length + rightHash.length];
        System.arraycopy(leftHash, 0, concatenated, 0, leftHash.length);
        System.arraycopy(rightHash, 0, concatenated, leftHash.length, rightHash.length);
        return concatenated;
    }

    // Integrates Hash.toString() functionality
    @Override
    public String toString() {
        return "MerkleTree{" +
                "hash=" + Arrays.toString(hash) +
                ", start=" + start +
                ", end=" + end +
                ", size=" + size +
                '}';
    }

    // Integrates Hash.equals(Object obj) functionality
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MerkleTree that = (MerkleTree) obj;
        return start == that.start &&
                end == that.end &&
                size == that.size &&
                Arrays.equals(hash, that.hash) &&
                (left == that.left || (left != null && left.equals(that.left))) &&
                (right == that.right || (right != null && right.equals(that.right)));
    }

    // Display method for the tree (for debugging and visualization)
    public void show() {
        printTree(this, 0);
    }

    private void printTree(MerkleTree node, int space) {
        if (node == null)
            return;

        space += 10;
        printTree(node.right, space);
        System.out.println();

        for (int i = 10; i < space; i++)
            System.out.print(" ");
        System.out.println(Arrays.toString(node.hash) + " {" + node.start + ", " + node.end + "}");

        printTree(node.left, space);
    }

}
