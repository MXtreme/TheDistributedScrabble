import java.rmi.Remote;
import java.rmi.RemoteException;

public interface INetworkManager extends Remote {
	void manageMsg(Message m) throws RemoteException;
	boolean isAlive()throws RemoteException;
}

