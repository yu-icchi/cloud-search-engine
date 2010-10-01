package master;

import java.util.ArrayList;
import java.util.Map;

public class GlobalIDFTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		GlobalIDF g_idf = new GlobalIDF();
		
		//-------------------------------------------------
		//setメソッド
		//-------------------------------------------------
		//g_idf.set("http://localhost:8983/solr/");
		//g_idf.set("http://localhost:7574/solr/");
		
		//g_idf.setSuperColumn("http://localhost:8983/solr/");
		//g_idf.setSuperColumn("http://localhost:7574/solr/");
		
		//String[] arr = {"ipod", "solr"};
		//Map<String, Object> map = g_idf.get(arr);
		//System.out.println(map.get("maxDocs"));
		//System.out.println(map.get("docFreq"));
		//System.out.println(map.get("url"));

		ArrayList<String> list = new ArrayList<String>();
		list.add("ipod");
		list.add("solr");
		list.add("electron");
		list.add("1");
		Map<String, Object> obj = g_idf.get(list);
		System.out.println(obj);
		
		//g_idf.get("solr");
		//g_idf.delete("http://localhost:8983/solr/");
		//g_idf.delete("http://localhost:7574/solr/");
		//g_idf.deleteTerm("MaxDocs");
		//System.out.println(GlobalIDF.getMaxDocs());
		//g_idf.describe();
		//g_idf.deleteSuperColumn();
		//System.out.println(g_idf.terms());
		//g_idf.search("apach", "base");
		//g_idf.deleteMaxDocs("http://localhost:8983/solr/");
		//GlobalIDF.setMaxDocs("http://localhost:8983/solr/");
	}

}
