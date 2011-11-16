package test;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public class Test2Search {
	public static void main(String[] args) throws Exception{
		
		SolrQuery query = new SolrQuery();
		query.setQuery("小澤龍之介");
		SolrServer server = new CommonsHttpSolrServer("http://192.168.168.243:8983/solr/");
		QueryResponse response = server.query(query, SolrRequest.METHOD.POST);
		SolrDocumentList list = response.getResults();
		
		System.out.println(list);
	}

}
