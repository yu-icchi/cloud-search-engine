package location;

import java.util.ArrayList;

public class LocationTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Location location = new Location();

		location.query("solr AND (前田 OR ipod)");

		ArrayList<String> input = new ArrayList<String>();

		input.add("ipod");
		input.add("solr");
		input.add("前田");

		System.out.println("LocationTest: " + location.get(input));
	}

}
