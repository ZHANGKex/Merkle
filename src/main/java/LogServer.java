import java.util.Queue;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.FileReader;
import java.io.FileNotFoundException;

public class LogServer {
    private MerkleTree tree;

    public LogServer(MerkleTree t) {
        tree = t;
    }

    public MerkleTree getMerkleTree() {
        return this.tree;
    }

    public byte[] computeHash(byte[] data) {
        return tree.computeHash(data);
    }

    public LogServer(String inputFile) {
        Queue<MerkleTree> merkleQueue = new LinkedList<>();
        try {
            Scanner input = new Scanner(new FileReader(inputFile));
            int i = 0;
            while (input.hasNextLine()) {
                String line = input.nextLine();
                MerkleTree merkle = new MerkleTree(line, i);
                merkleQueue.add(merkle);
                i++;
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + inputFile);
            System.exit(1);
        }
        buildTree(merkleQueue);
    }

    private void buildTree(Queue<MerkleTree> merkleQueue) {
        while (merkleQueue.size() > 1) {
            MerkleTree left = merkleQueue.poll();
            MerkleTree right = merkleQueue.poll();
            if (right == null) {
                merkleQueue.add(left);
            } else {
                merkleQueue.add(new MerkleTree(left, right));
            }
        }
        tree = merkleQueue.poll();
    }

    public byte[] currentRootHash() {
        return tree.getHash();
    }

    public void append(String log) {
        MerkleTree newLeaf = new MerkleTree(log, tree.getSize());
        appendLeaf(newLeaf);
    }

    public void append(LinkedList<String> list) {
        for (String log : list) {
            append(log);
        }
    }

    private void appendLeaf(MerkleTree newLeaf) {
        tree = new MerkleTree(tree, newLeaf);
    }

    public LinkedList<byte[]> genPath(int index) {
        LinkedList<byte[]> path = new LinkedList<>();
        MerkleTree current = tree;
        while (current != null && current.getStart() != current.getEnd()) {
            MerkleTree left = current.getLeft();
            MerkleTree right = current.getRight();

            if (index <= left.getEnd()) {
                path.add(right.getHash());
                current = left;
            } else {
                path.add(left.getHash());
                current = right;
            }
        }
        return path;
    }

    public LinkedList<byte[]> genProof(int index) {
        LinkedList<byte[]> proof = new LinkedList<>();
        MerkleTree current = tree;
        while (current != null) {
            proof.add(current.getHash());
            if (current.getStart() == current.getEnd()) {
                break;
            } else {
                MerkleTree left = current.getLeft();
                MerkleTree right = current.getRight();

                if (index <= left.getEnd()) {
                    current = left;
                } else {
                    current = right;
                }
            }
        }
        return proof;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LogServer other = (LogServer) obj;
        return tree.equals(other.tree);
    }
}
