import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ArrayList;

public class MerkleTree implements IMerkleTree {
    private Node root;
    private List<String> eventList;

    public MerkleTree(List<String> events) {
        eventList = new ArrayList<>(events);
        this.root = buildTree(events);
    }

    public void appendEvent(String event) {
        eventList.add(event); // 将新事件添加到事件列表
        Node newLeaf = new Node(event);

        if (root == null) {
            root = newLeaf;
        } else {
            // 添加新的叶子节点，并且逐步向上更新节点哈希值
            root = insertAndRecalculate(root, newLeaf);
        }
    }
    //这将需要 O(log n) 的时间，其中 n 是Merkle树中的事件数量。
    //空间复杂度：由于仅存储必要的节点，空间复杂度为 O(n)，每个事件对应一个节点。

    private Node insertAndRecalculate(Node current, Node newLeaf) {
        if (current == null) {
            return newLeaf;
        }

        // 如果当前节点是叶子节点，创建一个新的内部节点
        if (current.isLeaf()) {
            Node parent = new Node(current, newLeaf);
            return parent;
        }

        // 决定是向左还是向右分支更新树
        if (shouldGoLeft(current)) {
            current.left = insertAndRecalculate(current.left, newLeaf);
        } else {
            current.right = insertAndRecalculate(current.right, newLeaf);
        }

        // 更新当前节点的哈希值
        current.updateHash();
        return current;
    }

    private boolean shouldGoLeft(Node node) {
        // 根据树的结构或者其他标准来决定新的叶子节点是应该添加到左边还是右边
        // 例如，可以根据节点的数量或者深度来决定
        // 这里需要具体的实现逻辑
        return true; // 临时返回值，需要替换为真正的逻辑
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

        // 检查节点列表是否为空，如果是，返回 null
        return nodes.isEmpty() ? null : nodes.get(0); // 安全地返回根节点或null
    }


    private static byte[] calculateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not find hashing algorithm", e);
        }
    }

    @Override
    public byte[] getRootHash() {
        if (root != null) {
            return root.hash;
        } else {
            return null; // 或者考虑返回一个特定的值，比如空字节数组
        }
    }



    @Override
    public List<byte[]> genPath(String event) {
        List<byte[]> path = new ArrayList<>();
        Node current = findNode(root, event); // 你需要实现这个方法来找到包含特定事件数据的节点
        if (current == null) {
            return path; // 如果事件不在树中，返回空路径
        }
        while (current != null) {
            Node sibling = getSibling(current); // 你需要实现这个方法来找到当前节点的兄弟节点
            if (sibling != null) {
                path.add(sibling.hash);
            }
            current = current.parent; // 假设你有指向父节点的引用
        }
        return path;
    }
    //时间复杂度：要找到特定事件的审计路径，我们需要从该事件的叶节点向上遍历到根节点，这个操作的时间复杂度为 O(log n)。
    //空间复杂度：存储审计路径所需的空间为 O(log n)，因为路径长度与树的高度成正比。

    private Node getSibling(Node node) {
        if (node == null || node.parent == null) {
            return null;
        }
        if (node.parent.left == node && node.parent.right != null) {
            return node.parent.right;
        } else if (node.parent.right == node && node.parent.left != null) {
            return node.parent.left;
        }
        return null;
    }

    private Node findNode(Node node, String event) {
        if (node == null) {
            return null;
        }
        if (event.equals(node.data)) {
            return node;
        }
        Node foundNode = findNode(node.left, event);
        if (foundNode == null) {
            foundNode = findNode(node.right, event);
        }
        return foundNode;
    }


    @Override
    public List<byte[]> genProof(int oldTreeSize, int newTreeSize) {
        List<byte[]> proof = new ArrayList<>();
        Node oldRoot = getRoot(oldTreeSize); // 获取包含 oldTreeSize 事件的子树的根
        Node newRoot = getRoot(newTreeSize); // 获取包含 newTreeSize 事件的子树的根
        if (oldRoot == null || newRoot == null) {
            return proof; // 如果无法获取旧根或新根，返回空证明
        }
        // 收集路径上的哈希值
        Node current = oldRoot;
        while (current != null && current != newRoot) {
            if (isLeftChild(current)) {
                // 如果当前节点是左子节点，那么需要它右侧兄弟的哈希
                Node sibling = getSibling(current);
                if (sibling != null) {
                    proof.add(sibling.hash);
                }
            }
            // 向上移动到父节点
            current = current.parent;
        }
        // 需要对新树根部进行同样的操作，可能需要更多逻辑来处理两树的不同部分
        // 这里的代码需要根据您实际存储和访问历史状态的方式来完成
        return proof;
    }
    //时间复杂度：生成一致性证明的时间复杂度也是 O(log n)，类似于 genPath 方法。
    //空间复杂度：一致性证明所需的空间也是 O(log n)，取决于树的高度。

    private boolean isLeftChild(Node node) {
        return node.parent != null && node.parent.left == node;
    }


    private Node getRoot(int treeSize) {
        // 确定完整树的深度
        int fullDepth = getTreeDepth(root); // 需要实现这个方法
        // 计算期望大小的树的深度
        int targetDepth = getTreeDepthForSize(treeSize);
        // 找到对应深度的子树的根
        return findRootAtDepth(root, targetDepth, fullDepth);
    }

    private int getTreeDepth(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(getTreeDepth(node.left), getTreeDepth(node.right));
    }

    private int getTreeDepthForSize(int size) {
        // 计算树的深度，假设为完全二叉树
        int depth = 0;
        while (size > 0) {
            size = size / 2;
            depth++;
        }
        return depth;
    }

    private Node findRootAtDepth(Node node, int targetDepth, int currentDepth) {
        if (node == null || currentDepth == targetDepth) return node;
        // 递归地在左子树中查找，因为完全二叉树首先填充左侧
        return findRootAtDepth(node.left, targetDepth, currentDepth - 1);
    }

}
