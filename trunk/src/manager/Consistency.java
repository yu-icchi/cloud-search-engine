package manager;

import java.math.BigInteger;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.NavigableMap;

public interface Consistency extends Remote {

	void get() throws RemoteException;

	NavigableMap<BigInteger, String> set(String node) throws RemoteException;

}
