import java.util.List;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ILogServer extends Remote {
    byte[] getCurrentRootHash() throws RemoteException;
    void appendEvent(String event) throws RemoteException;
    List<byte[]> genPath(String event) throws RemoteException;
    List<byte[]> genProof(int oldTreeSize, int newTreeSize) throws RemoteException;
}

