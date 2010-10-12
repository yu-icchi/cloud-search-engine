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
		//クエリーの設定
		String query = "ipod solr electron";
		//GlobalIDFクラスに接続し、TermからURLとIDFを取得する
		ArrayList<String> list = new ArrayList<String>();
		list.add("ipod");
		list.add("solr");
		list.add("electron");
		GlobalIDF g_idf = new GlobalIDF();
		Map<String, Object> gidf = g_idf.get(list);
		List urlList = (List) gidf.get("url");
		//分散検索先の設定
		String shards = "";
		for (int i = 0; i < urlList.size(); i++) {
			//URLを取り出す
			String url = urlList.get(i).toString();
			//「http://」を切り取る
			shards += url.substring(7, url.length()) + ",";
		}
		System.out.println(shards);
		System.out.println(gidf.get("maxDocs"));
		System.out.println(gidf.get("docFreq"));
		//検索式
		out.print("shards=" + shards + "&q=" + query +"&debugQuery=on&wt=json");
		out.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line = in.readLine();
		//System.out.println(line);
		Map map = (Map) JSON.decode(line);
		//System.out.println(map.get("debug"));
		Map map2 = (Map) map.get("debug");
		System.out.println(map2.get("explain"));

		//正しいIDF
		Ranking ranking = new Ranking();
		System.out.println(ranking.idf(11, 3));
		int max = Integer.valueOf(gidf.get("maxDocs").toString()).intValue();
		System.out.println(max);
		System.out.println((float)(Math.log(max/(double)(3+1))+1));
		//TF
		System.out.println((float)Math.sqrt(3.0));
	}
}