package test;


import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public class Test3Search {

	public static void main(String[] args) throws Exception{

		SolrQuery query = new SolrQuery();
		query.setQuery("田中秀直");
		SolrServer server = new CommonsHttpSolrServer("http://192.168.244.133:8983/solr/");
		QueryResponse response = server.query(query, SolrRequest.METHOD.POST);
		SolrDocumentList list = response.getResults();

		System.out.println(list);

	}

}
