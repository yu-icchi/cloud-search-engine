package master;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class GlobalIDFTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		GlobalIDF g_idf = new GlobalIDF();
		//g_idf.setSuperColumn("http://localhost:8983/solr/");
		//g_idf.set("http://localhost:7574/solr/");
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
		list.add("é");
		Map<String, Map<String, Object>> map = g_idf.multiGet(list);
		System.out.println(map);
		/*
		for (Entry<String, Map<String, Object>> e : map.entrySet()) {
			System.out.println(e.getKey() + " : " + e.getValue());
		}
		*/
		Map<String, Object> obj = g_idf.get(list);
		System.out.println(obj);
		g_idf.getSuperColumn("1");
		//g_idf.delete("http://localhost:8983/solr/");
		//g_idf.delete("http://localhost:7574/solr/");
		//g_idf.deleteTerm("MaxDocs");
		//System.out.println(GlobalIDF.getMaxDocs());
		//g_idf.describe();
		//g_idf.deleteSuperColumn();
		System.out.println(g_idf.terms());
		//g_idf.search("apach", "base");
	}

}
