package manager;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RMIServer implements RMIInterface {

	private List<String> nodeList = new ArrayList<String>();

	@Override
	public List<String> get() throws RemoteException {
		return nodeList;
	}

	@Override
	public void set(String node) throws RemoteException {
		nodeList.add(node);
	}

	public static void main(String[] args) {
		try {
			RMIServer server = new RMIServer();
			RMIInterface stub = (RMIInterface) UnicastRemoteObject.exportObject(server, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.bind("nodelist", stub);

			System.out.println("Server ready");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
