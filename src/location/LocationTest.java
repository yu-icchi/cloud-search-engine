package location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocationTest {

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		Location location = new Location();

		location.query("solr AND 前田 OR ipod");

		ArrayList<String> input = new ArrayList<String>();

		input.add("ipod");
		input.add("solr");
		input.add("前田");

		Map<String, Object> map = location.get(input);
		System.out.println("LocationTest: " + map);
		List<String> list = (List<String>) map.get("url");
		System.out.println("maxDocs: " + map.get("maxDocs"));
		System.out.println("url: " + list);
		System.out.println("maxDocs: " + map.get("docFreq"));
	}

}
