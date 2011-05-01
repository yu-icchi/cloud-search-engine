package test;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class Test3Index {

	public static void main(String[] args)throws Exception{

		SolrServer server =
			new CommonsHttpSolrServer("http://192.168.244.133:8983/solr");
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id","http://gihyo.jp/ThisIsSample1");
		document.addField("name","田中秀直");
		server.add(document);
		server.commit();

		System.out.println("OK");
	}

}
