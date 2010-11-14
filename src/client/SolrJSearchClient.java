//---------------------------------------------------------
//SolrJSearchClient クラス
//
//SolrJを利用して検索をする(最終的にはServletにする)
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

public class SolrJSearchClient {

	/**
	 * Cloud-Search-Engineの検索部分
	 * 		1.ユーザーがGoogle形式のクエリーを入力
	 * 		2.LocationにアクセスしQbSSにより最適なアクセス先を探す
	 * 		3.分散検索をするためのクエリーを設定する
	 * 		3.トップレベルSolrのアドレスを指定し、分散検索をする
	 * 		4.ランキングを修正する
	 * 		5.修正したランキング順に結果を表示する
	 *
	 *
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {

		//ユーザーからのクエリー
		String queryString = "solr | ipod";
		//ユーザーのアカウント情報
		String account = "user1";
		//クエリーの解析
		QueryConverter queryConverter = new QueryConverter();
		queryConverter.parser(queryString);
		//データベース(Locationサーバ)にアクセス
		Location location = new Location();
		//正規化したクエリーを与える
		location.query(queryConverter.getQuery());
		//クエリーのタームを与える
		Map<String, Object> map = location.get(queryConverter.getTermList());
		//URL
		List<String> urlList = (List<String>) map.get("url");
		//maxDocs
		int maxDocs = (Integer) map.get("maxDocs");
		//docFreq
		Map<String, Integer> docFreq = (Map<String, Integer>) map.get("docFreq");
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
		query.setQuery("(" + queryConverter.getQuery() + ") AND account:" + account);
		//GSEサーバのSolrの指定
		SolrServer server = new CommonsHttpSolrServer("http://localhost:8983/solr/");
		//POST通信で検索をする
		QueryResponse response = server.query(query, SolrRequest.METHOD.POST);
		//Solrの結果を格納
		Map<String, SolrDocument> solrResultMap = new HashMap<String, SolrDocument>();
		SolrDocumentList list = response.getResults();
		Iterator<SolrDocument> dociterator = list.iterator();
		while (dociterator.hasNext()) {
			SolrDocument doc = dociterator.next();
			//Mapに格納
			solrResultMap.put(doc.get("id").toString(), doc);
		}
		//debugQueryの結果を持ってくる
		Map<String, String> debug = response.getExplainMap();
		//ランキング修正をする
		DistributedSimilarity ranking = new DistributedSimilarity(docFreq, maxDocs);
		//Solrのスコアデータを格納する
		ranking.solrScoreImport(debug);
		//ランキング修正結果を返す
		List<Map<String, Object>> documentResult = ranking.ranking();
		//Documentをランキング順に表示する
		for (int i = 0; i < documentResult.size(); i++) {
			//ランキング結果を順番に表示
			System.out.println(solrResultMap.get(documentResult.get(i).get("id")).getFieldValueMap());
		}
	}
}
