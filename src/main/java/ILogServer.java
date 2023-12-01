import java.util.List;

public interface ILogServer {
    byte[] getCurrentRootHash();
    void appendEvent(String event);
    List<byte[]> genPath(String event);
    List<byte[]> genProof(int oldTreeSize, int newTreeSize);
}
