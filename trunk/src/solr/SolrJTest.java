package solr;


import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrJTest {

	public static void main(String[] args) throws Exception {

		SolrServer server = new CommonsHttpSolrServer("http://localhost:8983/solr/");
		SolrQuery query = new SolrQuery("electron");
		QueryResponse response = server.query(query);
		SolrDocumentList list = response.getResults();
		for(SolrDocument doc: list){
		  System.out.println(doc.getFieldValue("path"));
		}

	}
}
