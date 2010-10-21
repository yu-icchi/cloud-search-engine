package solr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import net.arnx.jsonic.JSON;

import solr.ranking.DistributedSimilarity;
//import solr.ranking.Ranking;

//-----------------------------------------------
//分散検索するために、トップレベルサーバに問い合わせをするプログラム
//-----------------------------------------------
public class CopyOfShardsSolrApp {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		//POST送信でトップサーバにアクセス
		URL solrURL = new URL("http://localhost:6365/solr/select");
		URLConnection con = solrURL.openConnection();
		con.setDoOutput(true);
		PrintWriter out = new PrintWriter(con.getOutputStream());
		//パラメータ設定
		//クエリーの設定
		String query = "solr^3.0 ipod";

		//検索式
		out.print("shards=" + "localhost:8081/solr,localhost:8082/solr" + "&q=" + query +"&debugQuery=on&wt=json");
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
		System.out.println(map3);

		//グローバルIDFに必要なdocFreqの値を取り出す
		Map<String, Integer> docFreq = new HashMap<String, Integer>();
		docFreq.put("solr", 1);
		docFreq.put("ipod", 3);
		//グローバルIDFに必要なmaxDocsの値を取り出す
		int maxDocs = 19;
		//ランキング修正をする
		DistributedSimilarity ranking = new DistributedSimilarity(docFreq, maxDocs);
		//Solrのスコアデータを格納する
		ranking.solrScoreImport(map3);
		//ランキング修正結果を返す
		ranking.ranking();
	}
}
