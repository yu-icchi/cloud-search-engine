package solr.fork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrDocumentList;

import solr.ranking.DistributedSimilarity;

public class SolrClient {

	private SolrServer server;

	private int row = 10;

	private int start = 0;

	public SolrClient(String adress) {
		try {
			server = new CommonsHttpSolrServer(adress);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SolrDocumentList getResponse(String query) {
		try {
			SolrQuery params = new SolrQuery();
			params.setQuery(query);
			params.setStart(start);
			params.setRows(row);
			QueryResponse response = server.query(params, SolrRequest.METHOD.POST);
			SolrDocumentList list = response.getResults();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean ping() {
		try {
			SolrPingResponse res = server.ping();
			System.out.println(res);
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public Map<String, String> getExplain(String query) {
		try {
			SolrQuery params = new SolrQuery();
			params.setQuery(query);
			params.setStart(start);
			params.setRows(row);
			params.set("debugQuery", "on");
			params.setFields("id");
			QueryResponse response = server.query(params);
			return response.getExplainMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		SolrClient client1 = new SolrClient("http://192.168.220.131:6365/solr/core0/");
		SolrClient client2 = new SolrClient("http://192.168.220.132:6365/solr/core0/");
		SolrClient client3 = new SolrClient("http://192.168.220.133:6365/solr/core0/");

		//List<Map<String, String>> debugList = new ArrayList<Map<String, String>>();
		//if (client1.ping()) {
		//	debugList.add(client1.getExplain("芥川"));
		//}
		//if (client2.ping()) {
		//	debugList.add(client2.getExplain("芥川"));
		//}
		//if (client3.ping()) {
		//	debugList.add(client3.getExplain("芥川"));
		//}

		Map<String, Integer> docFreq2 = new HashMap<String, Integer>();
		docFreq2.put("芥川", 208);
		docFreq2.put("夏目", 82);
		docFreq2.put("菊池", 56);
		DistributedSimilarity ranking = new DistributedSimilarity(docFreq2, 1211);

		List<Map<String, Object>> document = new ArrayList<Map<String, Object>>();


		ranking.solrScoreImport(client1.getExplain("夏目"));
		List<Map<String, Object>> documentResult = ranking.ranking();
		for (int i = 0; i < documentResult.size(); i++) {
			document.add(documentResult.get(i));
		}

		ranking.solrScoreImport(client2.getExplain("夏目"));
		documentResult = ranking.ranking();
		for (int i = 0; i < documentResult.size(); i++) {
			document.add(documentResult.get(i));
		}

		ranking.solrScoreImport(client3.getExplain("夏目"));
		documentResult = ranking.ranking();
		for (int i = 0; i < documentResult.size(); i++) {
			document.add(documentResult.get(i));
		}

		//ソート
		Collections.sort(document, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> map1, Map<String, Object> map2) {
				String p1 = map1.get("score").toString();
				String p2 = map2.get("score").toString();
				return p2.compareTo(p1);
			}
		});

		System.out.println(document);

		String ids = "";
		int rows = 10;

		for (int i = 0; i < rows -1 ; i++) {
			String id = document.get(i).get("id").toString();
			ids +=  "id:" + id + " ";
		}

		ids += "id:" + document.get(rows - 1).get("id").toString();

		System.out.println(ids);

		System.out.println(client1.getResponse(ids));
		System.out.println(client2.getResponse(ids));
		System.out.println(client3.getResponse(ids));

	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getRow() {
		return row;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getStart() {
		return start;
	}
}
