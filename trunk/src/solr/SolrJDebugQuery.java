package solr;


import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public class SolrJDebugQuery {

	public static void main(String[] args) throws Exception {
		SolrServer server = new CommonsHttpSolrServer("http://localhost:8983/solr");
	    SolrQuery solrquery = new SolrQuery();
	    solrquery.set("shards", "localhost:8983/solr", "localhost:7574/solr");
	    solrquery.set("debugQuery", "on");
	    solrquery.setQuery("*:*");

	    QueryResponse response = server.query(solrquery);
	    SolrDocumentList docs = response.getResults();
	    System.out.println(docs);
	}
}
