import java.nio.charset.StandardCharsets;
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
        int newIndex = eventList.size(); // Assuming the index is 0-based and contiguous
        eventList.add(event);
        Node newLeaf = new Node(event, newIndex); // Pass the new index here

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
        // Base case: if current is null, just return the new leaf
        if (current == null) {
            return newLeaf;
        }

        // If the current node is a leaf, then we need to create a new parent node
        if (current.isLeaf()) {
            // New internal node will have the current leaf as the left child, newLeaf as the right child
            // The start index will be that of the current leaf, and the end index will be that of newLeaf
            Node parent = new Node(current, newLeaf, current.startIndex, newLeaf.endIndex);
            // Now, newLeaf and current both have the same parent
            newLeaf.parent = parent;
            current.parent = parent;
            return parent;
        }

        // If the current node is not a leaf, decide where to insert the new leaf
        // This logic will depend on your tree structure and how you want to balance it
        // For simplicity, let's say we always add to the right
        current.right = insertAndRecalculate(current.right, newLeaf);

        // After insertion, recalculate the hash for the current node
        current.updateHash();
        return current;
    }



    private boolean shouldGoLeft(Node node) {
        // 检查左子树是否比右子树矮或者右子树已满
        if (node.left == null || (node.right != null && getTreeDepth(node.left) <= getTreeDepth(node.right))) {
            return true;
        } else {
            return false;
        }
    }


    private Node buildTree(List<String> events) {
        List<Node> nodes = new ArrayList<>();

        // Create leaf nodes for each event with correct indices
        for (int i = 0; i < events.size(); i++) {
            nodes.add(new Node(events.get(i), i));
        }

        // Combine nodes until only the root node is left
        while (nodes.size() > 1) {
            List<Node> updatedNodes = new ArrayList<>();
            for (int i = 0; i < nodes.size(); i += 2) {
                Node left = nodes.get(i);
                Node right = (i + 1 < nodes.size()) ? nodes.get(i + 1) : null;

                // Calculate the range covered by the new internal node
                int start = left.startIndex;
                int end = (right != null) ? right.endIndex : left.endIndex;

                // Create the parent node
                Node parent = new Node(left, right, start, end);
                updatedNodes.add(parent);
            }
            nodes = updatedNodes; // Update the list for the next iteration
        }

        // Return the root of the tree or null if the list of events was empty
        return nodes.isEmpty() ? null : nodes.get(0);
    }


    private static byte[] calculateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update((byte)0x00); // Leaf node marker
            digest.update(data.getBytes(StandardCharsets.UTF_8)); // Your data
            return digest.digest();
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
