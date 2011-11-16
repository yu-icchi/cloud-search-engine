package test;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;



public class Test2Index {

	public static void main(String[] args) throws Exception{

		SolrServer server =
			new CommonsHttpSolrServer("http://192.168.168.243:8983/solr");
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", "http://gihyo.jp/ThisIsSample1");
		document.addField("name", "小澤龍之介");
		server.add(document);
		server.commit();

		System.out.println("OK");
	}

}
