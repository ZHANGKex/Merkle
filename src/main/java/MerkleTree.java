import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ArrayList;

public class MerkleTree implements IMerkleTree {
    private Node root;

    private static class Node {
        String data;
        Node left;
        Node right;
        byte[] hash;

        Node(String data) {
            this.data = data;
            this.hash = calculateHash(data);
        }
    }

    public MerkleTree(List<String> events) {
        this.root = buildTree(events);
    }

    private Node buildTree(List<String> events) {
        List<Node> nodes = new ArrayList<>();
        for (String event : events) {
            nodes.add(new Node(event));
        }

        // 当节点数量大于1时，继续组合它们
        while (nodes.size() > 1) {
            List<Node> updatedNodes = new ArrayList<>();
            for (int i = 0; i < nodes.size(); i += 2) {
                Node left = nodes.get(i);
                Node right = (i + 1 < nodes.size()) ? nodes.get(i + 1) : null;

                // 创建父节点的数据字符串
                String parentData = left.hash.toString(); // 将字节转换为适当的字符串表示
                if (right != null) {
                    parentData += right.hash.toString(); // 同上
                }

                // 创建父节点
                Node parent = new Node(parentData);
                parent.left = left;
                parent.right = right;
                updatedNodes.add(parent);
            }
            nodes = updatedNodes; // 准备下一轮合并
        }

        return nodes.get(0); // 返回根节点
    }


    private static byte[] calculateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not find hash algorithm", e);
        }
    }

    @Override
    public byte[] getRootHash() {
        // getRootHash 方法实现保持不变
        return root.hash;
    }

    @Override
    public List<byte[]> genPath(String event) {
        // TODO: 实现根据事件生成审计路径的方法
        return null;
    }

    @Override
    public List<byte[]> genProof(int oldTreeSize, int newTreeSize) {
        // TODO: 实现生成一致性证明的方法
        return null;
    }



    // Additional methods like genPath, genProof, etc. to be implemented
}
