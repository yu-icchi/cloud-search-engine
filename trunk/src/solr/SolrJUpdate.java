package solr;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class SolrJUpdate {

	public static void main(String[] args) throws Exception {
		SolrServer server = new CommonsHttpSolrServer("http://localhost:8081/solr");
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", "demo/sample.txt");
		document.addField("account", "user1");
		document.addField("text", "Hello World");
		server.add(document);
		server.commit();
	}
}
