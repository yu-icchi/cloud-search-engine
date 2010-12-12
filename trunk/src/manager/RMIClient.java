package manager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {

	public static void main(String[] args) {
		String host = "192.168.1.3";
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			RMISum stub = (RMISum) registry.lookup("sum");
			int sum = stub.calcSum(1, 10);
			System.out.println("sum=" + sum);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
