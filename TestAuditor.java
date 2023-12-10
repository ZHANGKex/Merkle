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
    private byte[] rootHash; // 类级别的 rootHash 字段

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

        // 确保 computeInternalNodeHash 方法返回一个能使 isMember 返回 true 的哈希值
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

        // 模拟路径哈希值
        LinkedList<byte[]> mockPath = new LinkedList<>();
        byte[] mockPathHash = new byte[]{ 0x05, 0x06, 0x07, 0x08 };
        mockPath.add(mockPathHash);
        when(mockServer.genPath(index)).thenReturn(mockPath);

        // 模拟事件哈希值
        byte[] eventHash = new byte[]{ 0x09, 0x0A, 0x0B, 0x0C };
        when(mockTree.computeHash(event.getBytes())).thenReturn(eventHash);

        // 模拟内部节点哈希计算的简化逻辑
        byte[] combinedHash = new byte[]{ 0x0D, 0x0E, 0x0F, 0x10 }; // 组合后的哈希值
        when(mockTree.computeInternalNodeHash(any(byte[].class), any(byte[].class))).thenReturn(combinedHash);

        // 设置 rootHash 以使事件成为成员
        rootHash = combinedHash; // 使 rootHash 与模拟的组合哈希匹配
        when(mockServer.currentRootHash()).thenReturn(rootHash);

        // 测试 isMember 方法
        boolean isMember = auditor.isMember(event, index);

        // 验证结果
        assertTrue("The event should be a member with the given audit path hash", isMember);
    }


}
