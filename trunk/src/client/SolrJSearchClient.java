//---------------------------------------------------------
//SolrJSearchClient クラス
//
//SolrJを利用して検索をする(最終的にはServletにする)
//---------------------------------------------------------
package client;

//import java.net.InetAddress;
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

import client.config.XMLConfig;

//import analysis.CJKAnalyzerExtract;
import analysis.SenAnalyzerExtract;

import solr.ranking.DistributedSimilarity;
import upload.consistency.ConsistentHashing;

public class SolrJSearchClient {

	/**
	 * Cloud-Search-Engineの検索部分
	 * 		1.ユーザーがGoogle形式のクエリーを入力
	 * 		2.クエリー内容をインデックスで作成されたタームの形にする
	 * 		3.LocationにアクセスしQbSSにより最適なアクセス先を探す
	 * 		4.分散検索をするためのクエリーを設定する
	 * 		5.トップレベルSolrのアドレスを指定し、分散検索をする
	 * 		6.ランキングを修正する
	 * 		7.修正したランキング順に結果を表示する
	 *
	 *
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {

		//GSEのConfig.xmlデータを取得する
		XMLConfig config = new XMLConfig("demo/gse-config.xml");
		//Location Serverのアドレスとポートを取得する
		Map<String, String> locationConfig = config.getHost2Port("location");
		String locationHost = locationConfig.get("host");
		int locationPort = Integer.valueOf(locationConfig.get("port"));
		//ユーザーからのクエリー
		String queryString = "芥川";
		//ユーザーのアカウント情報
		String account = "aozora";
		//クエリーの解析
		QueryConverter queryConverter = new QueryConverter();
		queryConverter.parser(queryString);
		//データベース(Locationサーバ)にアクセス
		Location location = new Location(locationHost, locationPort);
		//正規化したクエリーを与える
		location.query(queryConverter.getQuery(), "sen");
		//CJKAnalyzerでタームを分割してLocationに与えるデータを作る
		SenAnalyzerExtract analyzerExtract = new SenAnalyzerExtract(queryConverter.getTermList());
		//クエリーのタームを与える
		Map<String, Object> map = location.get(analyzerExtract.extract());
		System.out.println(map);
		//URL
		List<String> urlList = (List<String>) map.get("url");
		//maxDocs
		int maxDocs = (Integer) map.get("maxDocs");
		//docFreq
		Map<String, Integer> docFreq = (Map<String, Integer>) map.get("docFreq");
		//分散検索先の設定
		String shards = "";
		//エラー処理
		if (urlList == null) {
			return;
		}
		//Nodes List

		Map<String, String> data = location.getNodes();
		Iterator<String> it = data.keySet().iterator();
		ConsistentHashing hash = new ConsistentHashing();
		while (it.hasNext()) {
			String id = it.next();
			if (data.get(id).equals("active")) {
				hash.addNode(id);
			}
		}
		hash.nodeList();

		for (int i = 0; i < urlList.size(); i++) {
			//URLを取り出す
			String url = urlList.get(i).toString();
			/*
			if (hash.isNode(url)) {
				System.out.println(hash.nextNode(url));
			}
			*/
			//レプリケーションとの切り替え
			Map<String, String> replica = location.getNodes();
			if (replica.get("node1") != null) {
				url = replica.get("node1");
				//System.out.println(node1);
			} else if (replica.get("node2") != null) {
				url = replica.get("node2");
				//System.out.println(node2);
			} else if (replica.get("node3") != null) {
				url = replica.get("node3");
				//System.out.println(node3);
			} else {
				return;
			}
			//shardsパラメータ作成
			shards += url.substring(7, url.length()) + ",";
		}
		//GSEのSolrサーバのクエリー設定
		SolrQuery query = new SolrQuery();
		//
		query.setRows(20);
		//
		query.setStart(0);
		//スコア情報指定
		query.set("debugQuery", "on");
		//分散検索のアクセス先
		System.out.println(shards);
		query.set("shards", shards /*"192.168.220.131:6365/solr/core0/,192.168.220.132:6365/solr/core0/,192.168.220.133:6365/solr/core0/"*/);
		//正規化したクエリーを指定
		System.out.println("Query : " + queryConverter.getQuery());
		System.out.println();
		query.setQuery("(" + queryConverter.getQuery() + ") AND account:" + account/*queryConverter.getQuery()*/);
		//GSEサーバのSolrのアドレスを読み出す
		//Solrサーバを指定する
		//InetAddress address = InetAddress.getLocalHost();
		SolrServer server = new CommonsHttpSolrServer("http://" + "192.168.220.131" + ":6365/solr/core0/");
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
		//Map<String, Integer> docFreq2 = new HashMap<String, Integer>();
		//docFreq2.put("芥川", 208);
		//docFreq2.put("夏目", 82);
		//docFreq2.put("菊池", 56);
		DistributedSimilarity ranking = new DistributedSimilarity(docFreq/*docFreq2*/, maxDocs/*1211*/);
		//Solrのスコアデータを格納する
		ranking.solrScoreImport(debug);
		//ランキング修正結果を返す
		List<Map<String, Object>> documentResult = ranking.ranking();
		//Documentをランキング順に表示する
		for (int i = 0; i < /*documentResult.size()*/10; i++) {
			//ランキング結果を順番に表示
			System.out.println("---------- " + (i+1) + " ----------");
			System.out.println("score : " + documentResult.get(i).get("score"));
			System.out.println("id : " + documentResult.get(i).get("id"));
			List<String> msg = (List<String>) solrResultMap.get(documentResult.get(i).get("id")).get("text");
			System.out.println(msg.get(0) + " " + msg.get(1));
			System.out.println();
		}
	}
}
