package location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import upload.consistency.ConsistentHashing;

public class LocationNodeTester {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		map.put("192.168.220.137", "active");
		map.put("192.168.220.139", "active");
		map.put("192.168.220.140", "active");

		Location location = new Location("192.168.1.2", 9160);
		//location.setNodes(map);

		location.setNodes("192.168.220.137", "fault");

		//location.deleteNodes("192.168.220.139");

		Map<String, String> data = location.getNodes();

		ConsistentHashing hash = new ConsistentHashing();

		List<String> fault = new ArrayList<String>();

		for (Iterator<String> it = data.keySet().iterator(); it.hasNext();) {
			String id = it.next();
			System.out.println(id + " (" + data.get(id) + ")");

			hash.addNode(id);

			if (data.get(id).equals("fault")) {
				fault.add(id);
			}
		}

		hash.nodeList();
		System.out.println(fault);

		String node = "192.168.220.137";
		String url = "http://" + node + ":8983/solr/core0/";

		if (fault.contains(node)) {
			node = hash.nextNode(node);
			url = "http://" + node + ":8983/solr/core1/";
			if (fault.contains(node)) {
				node = hash.nextNode(node);
				url = "http://" + node + ":8983/solr/core2/";
			}
		}

		System.out.println(url);

	}

}
