package test;


import java.util.ArrayList;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class Test1Index {
	public static void main(String[] args) throws Exception {
		SolrServer server = new CommonsHttpSolrServer("http://192.168.168.252:8983/solr");
		Collection <SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		SolrInputDocument document = new SolrInputDocument();
		document.addField("url","http://gihyo.jp/ThisIsSample1");
		document.addField("title", "はじめてのSolr");
		String[] authors = {"山田太郎","鈴木一郎"};
		document.addField("author",authors);
		document.addField("summary", "この書籍はフィクションです。");
		docs.add(document);
		document=new SolrInputDocument();
		document.addField("url","http://gihyo.jp/ThisIsSample2");
		document.addField("title", "続・はじめてのSolr");
		document.addField("author","autors");
		document.addField("summary", "この書籍もフィクションです。");
		docs.add(document);
		server.add(docs);
		server.add(document);
		server.commit();
		System.out.println("OK");


	}

}