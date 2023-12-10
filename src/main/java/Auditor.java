import java.security.MessageDigest;
import java.util.Arrays;
import java.util.LinkedList;

public class Auditor {
    public byte[] rootHash;
    public LogServer server;

    public Auditor(LogServer server) {
        this.server = server;
        this.rootHash = server.currentRootHash();
    }

    public boolean isMember(String event, int index) {
        LinkedList<byte[]> auditPath = server.genPath(index);
        byte[] pathHash = buildAuditPathHash(event, index, auditPath);
        return Arrays.equals(pathHash, rootHash);
    }

    private byte[] buildAuditPathHash(String event, int index, LinkedList<byte[]> auditPath) {
        byte[] currentHash = server.getMerkleTree().computeHash(event.getBytes());
        int pathIndex = index;

        for (byte[] siblingHash : auditPath) {
            if (pathIndex % 2 == 0) {
                currentHash = server.getMerkleTree().computeInternalNodeHash(currentHash, siblingHash);
            } else {
                currentHash = server.getMerkleTree().computeInternalNodeHash(siblingHash, currentHash);
            }
            pathIndex >>>= 1;
        }

        return currentHash;
    }


}
