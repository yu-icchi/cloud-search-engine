package location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import upload.consistency.ConsistentHashing;

public class LocationTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		map.put("192.168.220.131:6365", "active");
		map.put("192.168.220.132:6365", "active");
		map.put("192.168.220.133:6365", "active");
		map.put("192.168.220.134:6365", "active");

		Location location = new Location();
		//location.setNodes("nodelist", map);

		//location.setNodes("nodelist", "192.168.220.132:6365", "fault");

		//location.deleteNodes("nodelist", "http://192.168.220.131:6365/solr/core0/");

		//location.setNodes("nodelist", "192.168.220.132:6365", "fault");

		Map<String, String> data = location.getNodes("nodelist");

		Iterator<String> it = data.keySet().iterator();

		ConsistentHashing hash = new ConsistentHashing();

		List<String> fault = new ArrayList<String>();

		while (it.hasNext()) {
			String id = it.next();
			System.out.println(id + " (" + data.get(id) + ")");

			hash.addNode(id);

			if (data.get(id).equals("fault")) {
				fault.add(id);
			}

		}


		hash.nodeList();
		System.out.println(fault);

		String node = "192.168.220.132:6365";
		String url = "http://" + node + "/solr/core0/";

		if (fault.contains(node)) {
			node = hash.nextNode(node);
			url = "http://" + node + "/solr/core1/";
			if (fault.contains(node)) {
				node = hash.nextNode(node);
				url = "http://" + node + "/solr/core2/";
			}
		}

		System.out.println(url);


		//System.out.println(data.get("node1"));
		//System.out.println(data.get("node2"));

		//location.setNodes("http://192.168.220.132:6365/solr/core0/", "node1", "http://192.168.220.132:6365/solr/core0/");

		//System.out.println(location.getNodes("http://192.168.220.131:6365/solr/core0/"));

		//location.deleteNodes("nodelist", "192.168.220.134:6365");

		//data = location.getNodes("http://192.168.220.131:6365/solr/core0/");

		//System.out.println(data.get("node1"));
		//System.out.println(data.get("node2"));

		//location.setNodes("http://192.168.220.131:6365/solr/core0/", "node1", "192.168.168.105");

		//System.out.println(location.getNodes("http://192.168.220.131:6365/solr/core0/"));

	}

}
