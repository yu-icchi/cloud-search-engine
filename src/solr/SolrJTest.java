package solr;


import java.util.Iterator;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrJTest {

	public static void main(String[] args) throws Exception {

		SolrServer server = new CommonsHttpSolrServer("http://localhost:8081/solr/");
		SolrQuery query = new SolrQuery();
		query.set("debugQuery", "on");
		query.set("shards", "localhost:8081/solr", "localhost:8082/solr");
		query.setQuery("solr ipod");
		System.out.println(query);
		QueryResponse response = server.query(query, SolrRequest.METHOD.POST);
		System.out.println(response.getDebugMap().get("explain"));
		SolrDocumentList list = response.getResults();
		Iterator<SolrDocument> dociterator = list.iterator();
		while (dociterator.hasNext()) {
			 SolrDocument doc = dociterator.next();
			 System.out.println(doc.getFieldValuesMap());
		}
	}
}
