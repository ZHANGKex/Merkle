import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Node {
    String data;
    byte[] hash;
    Node left;
    Node right;
    public Node parent;

    Node(String data) {
        this.data = data;
        this.hash = calculateHash(data);
        this.left = null;
        this.right = null;
        this.parent = null;
    }

    Node(Node left, Node right) {
        this.left = left;
        this.right = right;
        // 如果左右子节点不为空，将它们的数据串联后计算哈希值
        this.data = (left != null ? left.data : "") + (right != null ? right.data : "");
        this.hash = calculateHash(this.data);
        // 为左右子节点设置父节点为当前节点
        if (left != null) left.parent = this;
        if (right != null) right.parent = this;
    }

    private static byte[] calculateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // 使用UTF-8编码将字符串转换为字节
            byte[] inputBytes = data.getBytes("UTF-8");
            return digest.digest(inputBytes);
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Could not create hash", e);
        }
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    // 更新当前节点的哈希值
    public void updateHash() {
        if (isLeaf()) {
            // 如果是叶子节点，直接使用数据重新计算哈希
            this.hash = calculateHash(this.data);
        } else {
            // 否则，使用左右子节点的哈希值计算当前节点的哈希
            String combinedHash = "";
            if (left != null) {
                combinedHash += bytesToHex(left.hash);
            }
            if (right != null) {
                combinedHash += bytesToHex(right.hash);
            }
            this.hash = calculateHash(combinedHash);
        }
    }

    // 将字节数组转换为十六进制字符串
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

