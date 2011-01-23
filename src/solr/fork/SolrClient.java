package solr.fork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
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
			//params.set("fl", "id");
			//params.addField("id");
			//params.addFacetQuery("id");
			QueryResponse response = server.query(params, SolrRequest.METHOD.POST);
			SolrDocumentList list = response.getResults();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, String> getExplain(String query) {
		try {
			SolrQuery params = new SolrQuery();
			params.setQuery(query);
			params.setStart(start);
			params.setRows(row);
			params.set("debugQuery", "on");
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
		List<Map<String, String>> debugList = new ArrayList<Map<String, String>>();
		debugList.add(client1.getExplain("芥川"));
		debugList.add(client2.getExplain("芥川"));
		debugList.add(client3.getExplain("芥川"));

		Map<String, Integer> docFreq2 = new HashMap<String, Integer>();
		docFreq2.put("芥川", 208);
		docFreq2.put("夏目", 82);
		docFreq2.put("菊池", 56);
		DistributedSimilarity ranking = new DistributedSimilarity(docFreq2, 1211);
		ranking.solrScoreImport(debugList);
		List<Map<String, Object>> documentResult = ranking.ranking();
		System.out.println(documentResult);

		String ids = "";
		int rows = 10;

		for (int i = 0; i < rows -1 ; i++) {
			String id = documentResult.get(i).get("id").toString();
			ids +=  "id:" + id + " ";
		}

		ids += "id:" + documentResult.get(rows - 1).get("id").toString();

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
