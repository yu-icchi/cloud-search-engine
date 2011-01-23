package location;

import java.util.HashMap;
import java.util.Map;

public class LocationTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		map.put("node1", "http://192.168.220.133:6365/solr/core0/");
		map.put("node2", "http://192.168.220.132:6365/solr/core1/");
		map.put("node3", "");

		Location location = new Location();
		//location.setNodes("http://192.168.220.133:6365/solr/core0/", map);

		//Map<String, String> data = location.getNodes("http://192.168.220.133:6365/solr/core0/");

		//System.out.println(data.get("node1"));
		//System.out.println(data.get("node2"));

		location.setNodes("http://192.168.220.132:6365/solr/core0/", "node1", "http://192.168.220.132:6365/solr/core0/");

		//System.out.println(location.getNodes("http://192.168.220.131:6365/solr/core0/"));

		//location.deleteNodes("http://192.168.220.132:6365/solr/core0/", "node1");

		//data = location.getNodes("http://192.168.220.131:6365/solr/core0/");

		//System.out.println(data.get("node1"));
		//System.out.println(data.get("node2"));

		//location.setNodes("http://192.168.220.131:6365/solr/core0/", "node1", "192.168.168.105");

		//System.out.println(location.getNodes("http://192.168.220.131:6365/solr/core0/"));

	}

}
