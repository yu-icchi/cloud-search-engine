package location;

import java.util.ArrayList;
import java.util.Map;

public class GlobalIDFTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		GlobalIDF g_idf = new GlobalIDF("192.168.168.143", 9160);

		//-------------------------------------------------
		//setメソッド
		//-------------------------------------------------
		//g_idf.set("http://192.168.168.147:8983/solr/");
		g_idf.set("http://localhost:8085/solr/");

		//g_idf.setSuperColumn("http://localhost:8983/solr/");
		//g_idf.setSuperColumn("http://localhost:7574/solr/");

		//GlobalIDF.setMaxDocs("http://localhost:8983/solr/");


		//-------------------------------------------------
		//getメソッド
		//-------------------------------------------------

		//g_idf.queryParser("(solr AND ipod)");

		//ArrayList<String> list = new ArrayList<String>();
		//list.add("ipod");
		//list.add("前田");
		//list.add("1");

		//Map<String, Object> obj = g_idf.get(list);
		//System.out.println(obj);

		//System.out.println(g_idf.get("solr"));

		//System.out.println(GlobalIDF.getMaxDocs());

		//g_idf.getSuperColumn("solr");
		/*
		Map<String, Object> data = g_idf.getSuperColumn(list);
		System.out.println(data);
		*/
		//g_idf.getSuperColumnAND(list);


		//-------------------------------------------------
		//deleteメソッド
		//-------------------------------------------------
		//g_idf.delete("http://localhost:6365/solr/");
		//g_idf.delete("http://localhost:7574/solr/");

		//g_idf.deleteURL("http://localhost:8085/solr/");

		//g_idf.deleteTerm("MaxDocs");

		//g_idf.deleteMaxDocs("http://localhost:8983/solr/");

		//g_idf.deleteSuperColumnAll();
		//g_idf.deleteSuperColumn("http://localhost:8983/solr/");

		//-------------------------------------------------
		//otherメソッド
		//-------------------------------------------------
		//System.out.println(g_idf.terms());

		//System.out.println(g_idf.termsLength());

		g_idf.search("", "");


	}

}
