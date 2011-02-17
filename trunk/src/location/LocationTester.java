package location;

import java.util.ArrayList;
import java.util.Map;


public class LocationTester {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Location location = new Location("192.168.220.141", 9160);

		//-------------------------------------------------
		//setメソッド
		//-------------------------------------------------
		//location.set("http://192.168.220.140:8983/solr/");

		//Location.setMaxDocs("http://localhost:8983/solr/");


		//-------------------------------------------------
		//getメソッド
		//-------------------------------------------------

		System.out.println(location.get("佐々木"));

		//System.out.println(Location.getMaxDocs());

		//location.getSuperColumn("solr");

		//Map<String, Object> data = location.getSuperColumn(list);
		//System.out.println(data);

		//location.getSuperColumnAND(list);


		//-------------------------------------------------
		//deleteメソッド
		//-------------------------------------------------
		//location.delete("http://localhost:6365/solr/");
		//location.delete("http://localhost:7574/solr/");

		//location.deleteURL("http://localhost:6365/solr/");

		//location.deleteTerm("solr");

		//location.deleteMaxDocs("http://localhost:8983/solr/");

		//location.deleteSuperColumnAll();
		//location.deleteSuperColumn("http://localhost:8983/solr/");

		//-------------------------------------------------
		//otherメソッド
		//-------------------------------------------------
		//System.out.println(location.terms());

		//System.out.println(location.termsLength());

		//g_idf.search("", "");

	}

}
