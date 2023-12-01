import java.util.List;

public interface IMerkleTree {
    byte[] getRootHash();
    List<byte[]> genPath(String event);
    List<byte[]> genProof(int oldTreeSize, int newTreeSize);
    // 可以根据需要添加其他方法
}
