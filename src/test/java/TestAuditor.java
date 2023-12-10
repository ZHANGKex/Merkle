import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.LinkedList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestAuditor {

    private Auditor auditor;
    private LogServer mockServer;
    private MerkleTree mockTree;
    private byte[] rootHash;

    @Before
    public void setUp() {
        mockServer = Mockito.mock(LogServer.class);
        mockTree = Mockito.mock(MerkleTree.class);
        when(mockServer.getMerkleTree()).thenReturn(mockTree);

        rootHash = new byte[]{ 0x01, 0x02, 0x03, 0x04 }; // 假设的 rootHash
        when(mockServer.currentRootHash()).thenReturn(rootHash);

        auditor = new Auditor(mockServer);
    }

    @Test
    public void testIsMemberWithValidEvent() {
        String event = "testEvent";
        int index = 0;

        LinkedList<byte[]> mockPath = new LinkedList<>();
        mockPath.add(new byte[]{ 0x05, 0x06, 0x07, 0x08 }); // 假设的路径哈希值
        when(mockServer.genPath(index)).thenReturn(mockPath);

        byte[] eventHash = new byte[]{ 0x09, 0x0A, 0x0B, 0x0C }; // 假设的事件哈希值
        when(mockTree.computeHash(event.getBytes())).thenReturn(eventHash);

        when(mockTree.computeInternalNodeHash(any(byte[].class), any(byte[].class))).thenReturn(rootHash);

        boolean isMember = auditor.isMember(event, index);
        assertTrue("The event should be a member with the given audit path hash", isMember);
    }
    @Test
    public void testIsMemberWithInvalidEvent() {
        String event = "invalidEvent";
        int index = 0;

        LinkedList<byte[]> mockPath = new LinkedList<>();
        mockPath.add(new byte[]{ 0x05, 0x06, 0x07, 0x08 });
        when(mockServer.genPath(index)).thenReturn(mockPath);

        when(mockTree.computeHash(event.getBytes())).thenReturn(new byte[]{ 0x09, 0x0A, 0x0B, 0x0C });
        when(mockTree.computeInternalNodeHash(any(), any())).thenReturn(new byte[]{ 0x0D, 0x0E, 0x0F, 0x10 });

        boolean result = auditor.isMember(event, index);

        assertFalse("Event should not be a member", result);
    }

    @Test
    public void testBuildAuditPathHashThroughIsMember() {
        String event = "someEvent";
        int index = 0;

        LinkedList<byte[]> mockPath = new LinkedList<>();
        byte[] mockPathHash = new byte[]{ 0x05, 0x06, 0x07, 0x08 };
        mockPath.add(mockPathHash);
        when(mockServer.genPath(index)).thenReturn(mockPath);

        byte[] eventHash = new byte[]{ 0x09, 0x0A, 0x0B, 0x0C };
        when(mockTree.computeHash(event.getBytes())).thenReturn(eventHash);

        byte[] combinedHash = new byte[]{ 0x0D, 0x0E, 0x0F, 0x10 }; // 组合后的哈希值
        when(mockTree.computeInternalNodeHash(any(byte[].class), any(byte[].class))).thenReturn(combinedHash);

        rootHash = combinedHash;
        when(mockServer.currentRootHash()).thenReturn(rootHash);

        boolean isMember = auditor.isMember(event, index);
        assertTrue("The event should be a member with the given audit path hash", isMember);
    }


}
