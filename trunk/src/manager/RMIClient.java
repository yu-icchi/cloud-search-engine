package manager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import upload.consistency.ConsistentHashing2;

public class RMIClient {

	public static void main(String[] args) {
		String host = "192.168.1.3";
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			RMIInterface stub = (RMIInterface) registry.lookup("nodelist");
			stub.set("192.168.1.5");
			System.out.println(stub.get());
			ConsistentHashing2 hash2 = new ConsistentHashing2();
			hash2.addNode(stub.get());
			hash2.nodeList();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
