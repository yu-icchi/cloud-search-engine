package solr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import location.GlobalIDF;
import location.Location;
import location.query.QueryConverter;

import solr.ranking.DistributedSimilarity;
//import solr.ranking.Ranking;

//-----------------------------------------------
//分散検索するために、トップレベルサーバに問い合わせをするプログラム
//-----------------------------------------------
public class ShardsSolrApp {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		//POST送信でトップサーバにアクセス
		URL solrURL = new URL("http://localhost:8983/solr/select");
		URLConnection con = solrURL.openConnection();
		con.setDoOutput(true);
		PrintWriter out = new PrintWriter(con.getOutputStream());
		//パラメータ設定
		//ユーザークエリーの指定
		String query = "solr | ipod";
		//クエリーの解析
		QueryConverter queryConverter = new QueryConverter();
		queryConverter.parser(query);
		//データベース(Locationサーバ)にアクセス
		Location location = new Location();
		//正規化したクエリーを与える
		location.query(queryConverter.getQuery());
		//クエリーのタームを与える
		Map<String, Object> locationMap = location.get(queryConverter.getTermList());
		//URL
		List<String> urlList = (List<String>) locationMap.get("url");
		//maxDocs
		int maxDocs = (Integer) locationMap.get("maxDocs");
		//docFreq
		Map<String, Integer> docFreq = (Map<String, Integer>) locationMap.get("docFreq");
		System.out.println(urlList + ":" + maxDocs + ":" + docFreq);
		//分散検索先の設定
		String shards = "";
		for (int i = 0; i < urlList.size(); i++) {
			//URLを取り出す
			String url = urlList.get(i).toString();
			//「http://」を切り取る
			shards += url.substring(7, url.length()) + ",";
		}
		System.out.println(shards);
		//検索式
		out.print("shards=" + shards + "&q=" + queryConverter.getQuery() +"&debugQuery=on&wt=json");
		out.close();
		//検索
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line = in.readLine();
		//System.out.println(line);
		Map map = (Map) JSON.decode(line);
		//System.out.println(map.get("debug"));
		Map map2 = (Map) map.get("debug");
		//System.out.println(map2.get("explain"));
		Map map3 = (Map) map2.get("explain");
		//ランキング修正をする
		DistributedSimilarity ranking = new DistributedSimilarity(docFreq, maxDocs);
		//Solrのスコアデータを格納する
		ranking.solrScoreImport(map3);
		//ランキング修正結果を返す
		System.out.println(ranking.ranking());
	}
}
