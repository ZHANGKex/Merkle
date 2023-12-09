import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Node {
    String data;
    byte[] hash;
    Node left;
    Node right;
    public Node parent;
    int startIndex; // Node's start index
    int endIndex; // Node's end index

    // Constructor for leaf nodes
    Node(String data, int index) {
        this.data = data;
        this.hash = calculateHash(data.getBytes());
        this.left = null;
        this.right = null;
        this.parent = null;
        this.startIndex = index;
        this.endIndex = index; // Leaf node covers itself
    }

    // Constructor for internal nodes
    Node(Node left, Node right, int start, int end) {
        this.left = left;
        this.right = right;
        this.data = ""; // Not used for internal nodes
        this.startIndex = start;
        this.endIndex = end;
        // Update parent references
        if (left != null) left.parent = this;
        if (right != null) right.parent = this;
        // Calculate the hash for the internal node
        this.hash = calculateInternalNodeHash(left.hash, right.hash);
    }

    // Hash calculation for internal nodes
    private byte[] calculateInternalNodeHash(byte[] leftHash, byte[] rightHash) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update((byte)0x01); // Prepend the internal node marker
            digest.update(leftHash);
            if (rightHash != null) {
                digest.update(rightHash);
            }
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }

    // Hash calculation for data
    private static byte[] calculateHash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update((byte)0x00); // Prepend the leaf node marker
            return digest.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }

    // Convert a byte array to a hex string
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Check if the node is a leaf
    public boolean isLeaf() {
        return left == null && right == null;
    }

    // Update the hash value of the current node
    public void updateHash() {
        if (!isLeaf()) {
            this.hash = calculateInternalNodeHash(left.hash, right.hash);
        }
    }
}
