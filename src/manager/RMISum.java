package manager;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMISum extends Remote {

	int calcSum(int min, int max) throws RemoteException;
}
