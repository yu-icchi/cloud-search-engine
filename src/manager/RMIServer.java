package manager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer implements RMISum {

	public int calcSum(int min, int max) {
		int retval = 0;
		for (int i = min; i <= max; i++) {
			retval += i;
		}
		return retval;
	}

	public static void main(String[] args) {
		try {
			RMIServer server = new RMIServer();
			RMISum stub = (RMISum) UnicastRemoteObject.exportObject(server, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.bind("sum", stub);

			System.out.println("Server ready");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
