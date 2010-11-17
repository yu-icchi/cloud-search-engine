package solr;

import java.util.Iterator;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import upload.consistency.ConsistentHashing;

public class SolrJIndexReader {

	public static void main(String[] args) throws Exception {

		ConsistentHashing hash = new ConsistentHashing();

		hash.addNode("localhost:8081", "localhost:8082", "localhost:8083", "localhost:8084", "localhost:8085");
		hash.addNode("localhost:8086", "localhost:8087", "localhost:8088", "localhost:8089", "localhost:8090");

		SolrServer server = new CommonsHttpSolrServer("http://localhost:8983/solr/");
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		System.out.println(query);
		QueryResponse response = server.query(query, SolrRequest.METHOD.POST);
		SolrDocumentList list = response.getResults();
		Iterator<SolrDocument> dociterator = list.iterator();
		while (dociterator.hasNext()) {
			 SolrDocument doc = dociterator.next();
			 System.out.println(doc.getFieldNames());
			 String node = hash.searchNode((String) doc.get("id"));
			 System.out.println(node);
		}
	}
}
