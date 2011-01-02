package manager;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIInterface extends Remote {

	List<String> get() throws RemoteException;

	void set(String node) throws RemoteException;

}
