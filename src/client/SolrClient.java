//---------------------------------------------------------
//SolrClientクラス
//
//SolrJを利用してクライアント操作をする(最終的にはServletにする)
//---------------------------------------------------------
package client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import location.Location;
import location.query.QueryConverter;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import solr.ranking.DistributedSimilarity;

public class SolrClient {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {

		//GSEサーバのSolrの指定
		SolrServer server = new CommonsHttpSolrServer("http://localhost:8983/solr/");
		//ユーザーからのクエリー
		String queryString = "solr | ipod";
		//クエリーの解析
		QueryConverter queryConverter = new QueryConverter();
		queryConverter.parser(queryString);
		//データベース(Locationサーバ)にアクセス
		Location location = new Location();
		//正規化したクエリーを与える
		location.query(queryConverter.getQuery());
		//クエリーのタームを与える
		Map<String, Object> map = location.get(queryConverter.getTermList());
		List<String> urlList = (List<String>) map.get("url");
		int maxDocs = (Integer) map.get("maxDocs");
		Map<String, Integer> docFreq = (Map<String, Integer>) map.get("docFreq");
		System.out.println(maxDocs + ":" + docFreq);
		//分散検索先の設定
		String shards = "";
		for (int i = 0; i < urlList.size(); i++) {
			//URLを取り出す
			String url = urlList.get(i).toString();
			//「http://」を切り取る
			shards += url.substring(7, url.length()) + ",";
		}
		//GSEのSolrサーバのクエリー設定
		SolrQuery query = new SolrQuery();
		//スコア情報指定
		query.set("debugQuery", "on");
		//分散検索のアクセス先
		query.set("shards", shards);
		//正規化したクエリーを指定
		query.setQuery(queryConverter.getQuery());
		System.out.println(query);
		//POST通信で検索をする
		QueryResponse response = server.query(query, SolrRequest.METHOD.POST);
		Object debug = (Object) response.getDebugMap().get("explain");
		System.out.println(debug);
		//ランキング修正をする
		DistributedSimilarity ranking = new DistributedSimilarity(docFreq, maxDocs);
		//Solrのスコアデータを格納する
		ranking.solrScoreImport( (HashMap) debug);		// <==ここが原因だ！！
		//ランキング修正結果を返す
		System.out.println(ranking.ranking());
		SolrDocumentList list = response.getResults();
		Iterator<SolrDocument> dociterator = list.iterator();
		while (dociterator.hasNext()) {
			 SolrDocument doc = dociterator.next();
			 System.out.println(doc.getFieldValuesMap());
		}
	}
}
