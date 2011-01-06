package location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LocationTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		map.put("node1", "192.168.168.101");
		map.put("node2", "192.168.168.102");
		map.put("node3", "192.168.168.103");

		Location location = new Location();
		location.setNodes("192.168.168.101", map);

		System.out.println(location.getNodes("192.168.168.101"));

		location.setNodes("192.168.168.101", "node2", "192.168.168.105");

		System.out.println(location.getNodes("192.168.168.101"));

		location.deleteNodes("192.168.168.101", "node1");

		System.out.println(location.getNodes("192.168.168.101"));

		ArrayList<String> list = new ArrayList<String>();
		list.add("solr");
		System.out.println(location.get(list));

	}

}
