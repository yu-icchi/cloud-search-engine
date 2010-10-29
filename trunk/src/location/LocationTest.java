package location;

import java.util.ArrayList;

public class LocationTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Location location = new Location();

		ArrayList<String> input = new ArrayList<String>();

		input.add("solr");

		location.getAND(input);
	}

}
