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
/**
 * @test
 */
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
		String query = "(前田敦子^2.0 OR 大島優子)";

		//検索式
		out.print(/*"shards=" + "localhost:6365/solr,localhost:6366/solr" + */"&q=" + query +"&debugQuery=on&wt=json");
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
		docFreq.put("前田", 1);
		docFreq.put("敦子", 1);
		docFreq.put("大島", 1);
		docFreq.put("優子", 1);
		//グローバルIDFに必要なmaxDocsの値を取り出す
		int maxDocs = 3;
		//ランキング修正をする
		DistributedSimilarity ranking = new DistributedSimilarity(docFreq, maxDocs);
		//Solrのスコアデータを格納する
		ranking.solrScoreImport(map3);
		//ランキング修正結果を返す
		System.out.println(ranking.ranking());
	}
}
