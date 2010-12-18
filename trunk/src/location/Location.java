//---------------------------------------------------------
//GlobalIDFクラス(URLを指定し、データベースからGlobal-IDFの値を登録・取得する)
//
//直接Cassandraにアクセスし、データベースの管理をしる
//---------------------------------------------------------
package location;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import location.qbss.QbSS;
import location.query.LocationQueryConverter;

import net.arnx.jsonic.JSON;

public class Location {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	//Solrサーバのタームフィールドの指定
	static String _termField = "text";
	//ホスト名
	static String _host = "localhost";
	//ポート番号
	static int _port = 9160;
	//クエリ
	static String _query;

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ(デフォルト)
	 */
	public Location() {

	}

	/**
	 * コンストラクタ (ホスト・ポートの指定)
	 *
	 * @param host (String) ホストを指定する
	 * @param port (int) ポート番号を指定する
	 */
	public Location(String host, int port) {
		_host = host;
		_port = port;
	}

	/**
	 * コンストラクタ (ホスト指定)
	 *
	 * @param host
	 */
	public Location(String host) {
		_host = host;
	}

	//-----------------------------------------------------
	//get・setメソッド
	//-----------------------------------------------------

	/**
	 * termFieldメソッド(検索サーバのタームフィールドを指定する)
	 *
	 *  @param field (String) fieldを指定する
	 */
	public static void termField(String field) {
		_termField = field;
	}

	/**
	 * queryメソッド
	 *
	 * @param query
	 * @param type
	 * @throws Exception
	 */
	public void query(String query, String type) throws Exception {
		LocationQueryConverter convert = new LocationQueryConverter(type);
		convert.parser(query);
		_query = convert.getQuery();
	}

	/**
	 * queryメソッド
	 *
	 * @param query
	 * @throws Exception
	 */
	public void query(String query) throws Exception {
		_query = query;
	}

	//-----------------------------------------------------
	//Cassandraにデータを格納する
	//-----------------------------------------------------

	/**
	 * setメソッド(格納)
	 *
	 *  @param url (String) URLを指定する
	 *  @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void set(String url) throws Exception {
		//URLのチェック
		if (urlCheck(url)) {
			//MaxDocsデータを格納する
			setMaxDocs(url);
			//idf値を取得する
			List<Object> list = docFreq(url + "terms");
			//List → List<Map<String, String>>
			List<Map<String, String>> data = new ArrayList<Map<String, String>>();
			Map<String, String> map;
			for (int i = 0; i < list.size(); i+=2) {
				map = new HashMap<String, String>();
				map.put("term", list.get(i).toString());
				map.put("docFreq", list.get(i + 1).toString());
				map.put("url", url);
				data.add(map);
			}
			//Cassandraに接続する
			CassandraClient cc = new CassandraClient(_host, _port);
			cc.insertDocFreq(data);
			cc.closeConnection();
		}
	}

	/**
	 * setMaxDocsメソッド
	 *
	 *  @param  url (String) URLを指定する
	 *  @throws Exception
	 */
	public static void setMaxDocs(String url) throws Exception {
		CassandraClient cc = new CassandraClient(_host, _port);
		cc.insertMaxDoc(url, maxDoc(url + "admin/luke"));
		cc.closeConnection();
	}

	//-----------------------------------------------------
	//Cassandraからデータを取得する
	//-----------------------------------------------------

	/**
	 * getメソッド(取得)
	 *
	 *  @param term (String) Termを指定する
	 *   @return (Map) IDFの値とURLをListでまとめたMapオブジェクトを返す
	 */
	public Map<String, Object> get(String term) {
		Map<String, Object> data = new HashMap<String, Object>();
		List<String> urlList = new ArrayList<String>();
		//MaxDocsの値を取得する
		int maxDocs_number = getMaxDocs();
		int docFreq_number = 0;
		CassandraClient cc = new CassandraClient(_host, _port);
		List<Map<String, String>> list = cc.get(term);
		for (int i = 0; i < list.size(); i++) {
			for (Map.Entry<String, String> e : list.get(i).entrySet()) {
				if (e.getKey().equals("docFreq")) {
					int n = Integer.valueOf(e.getValue()).intValue();
					docFreq_number += n;
				}
				if (e.getKey().equals("url")) {
					urlList.add(e.getValue().toString());
				}
			}
		}
		data.put("maxDocs", maxDocs_number);
		data.put("docFreq", docFreq_number);
		data.put("url", urlList);
		cc.closeConnection();
		return data;
	}

	/**
	 * getメソッド(ArrayList版)
	 *
	 * @param input
	 * @return
	 */
	public Map<String, Object> get(ArrayList<String> input) {
		//結果を返すデータ構造
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Integer> term = new HashMap<String, Integer>();
		Map<String, List<String>> urls = new HashMap<String, List<String>>();
		Object data;
		List<String> urlList;
		//Cassandraに接続する
		CassandraClient cc = new CassandraClient(_host, _port);
		//複数クエリの結果を取得する
		List<Map<String, String>> list = cc.get(input);
		//接続を切断する
		cc.closeConnection();
		//結果をまとめる
		for (Map<String, String> map : list) {
			//Term
			if (term.get(map.get("key")) == null) {
				//docFreq新規作成
				term.put(map.get("key"), Integer.valueOf(map.get("docFreq").toString()).intValue());
				//url新規作成
				urlList = new ArrayList<String>();
				urlList.add(map.get("url"));
				urls.put(map.get("key"), urlList);
			} else {
				//docFreq追加作成
				int a = term.get(map.get("key").toString());
				int b = Integer.valueOf(map.get("docFreq").toString()).intValue();
				term.put(map.get("key"), a + b);
				//url追加作成
				List<String> leftList = urls.get(map.get("key"));
				List<String> rightList = new ArrayList<String>();
				rightList.add(map.get("url"));
				for (int i = 0; i < leftList.size(); i++) {
					if (!rightList.contains(leftList.get(i))) {
						rightList.add(leftList.get(i));
					}
				}
				urls.put(map.get("key"), rightList);
			}
		}
		System.out.println(urls);
		//QbSS
		QbSS qbss = new QbSS(_query, urls);
		try {
			data = qbss.parser();
		} catch(Exception e) {
			System.out.println("アクセス先がありません");
			return null;
		}
		result.put("docFreq", term);
		result.put("url", data);
		//MaxDocsの値を取得する
		int maxDocs_number = getMaxDocs();
		result.put("maxDocs", maxDocs_number);

		return result;
	}

	/**
	 * getIDFメソッド(IDF値を取得する)
	 *
	 *  @param term (String) Termを指定する
	 *   @return (int) IDF値を返す
	 */
	public int getDocFreq(String term) {
		int docFreq_number = 0;
		//Cassandra接続する
		CassandraClient cc = new CassandraClient(_host, _port);
		List<Map<String, String>> list = cc.get(term);
		for (int i = 0; i < list.size(); i++) {
			for (Map.Entry<String, String> e : list.get(i).entrySet()) {
				if (e.getKey().equals("docFreq")) {
					//System.out.println(e.getKey() + " : " + e.getValue());
					int n = Integer.valueOf(e.getValue()).intValue();
					docFreq_number += n;
				}
			}
		}
		cc.closeConnection();
		return docFreq_number;
	}

	/**
	 * getURLメソッド(URLを取得する)
	 *
	 *  @param term (String) Termを指定する
	 *   @return (List) URLをまとめたリストを返す
	 */
	public List<String> getURL(String term) {
		List<String> urlList = new ArrayList<String>();
		CassandraClient cc = new CassandraClient(_host, _port);
		List<Map<String, String>> list = cc.get(term);
		for (int i = 0; i < list.size(); i++) {
			for (Map.Entry<String, String> e : list.get(i).entrySet()) {
				if (e.getKey().equals("url")) {
					//System.out.println(e.getKey() + " : " + e.getValue());
					urlList.add(e.getValue().toString());
				}
			}
		}
		cc.closeConnection();
		return urlList;
	}

	/**
	 * termsメソッド(データベースに登録しているターム一覧を取得する)
	 *
	 *  @return
	 */
	public ArrayList<String> terms() {
		CassandraClient cc = new CassandraClient(_host, _port);
		ArrayList<String> list = cc.terms();
		cc.closeConnection();
		return list;
	}

	/**
	 * termsLengthメソッド(データベースに登録しているターム数を取得する)
	 *
	 * @return
	 */
	public int termsLength() {
		CassandraClient cc = new CassandraClient(_host, _port);
		int length = cc.termsLength();
		cc.closeConnection();
		return length;
	}

	//-----------------------------------------------------
	//Cassandraのデータを削除する
	//-----------------------------------------------------

	/**
	 * deleteメソッド(削除)
	 *
	 *　Solrサーバにアクセスし、内容を消す⇒改善が必要
	 *  @param url (String) URLを指定する
	 */
	@SuppressWarnings("unchecked")
	public void delete(String url) throws Exception {
		//URLのチェック
		if (urlCheck(url)) {
			//idf値を取得する
			List list = docFreq(url + "terms");
			for (int i = 0; i < list.size(); i+=2) {
				//Cassandraのデータベースを削除する
				CassandraClient cc = new CassandraClient(_host, _port);
				//一致するタームフィールドを削除する
				cc.delete(list.get(i).toString(), url);
				cc.closeConnection();
			}
		}
	}

	/**
	 * deleteURLメソッド
	 *
	 * @param url
	 * @throws Exception
	 */
	public void deleteURL(String url) throws Exception {
		//URLのチェック
		if (urlCheck(url)) {

		}
	}

	/**
	 * deleteTermメソッド(termを指定しそのデータベースを削除する)
	 *
	 *  @param term (String) Termを指定する
	 */
	public void deleteTerm(String term) {
		CassandraClient cc = new CassandraClient(_host, _port);
		cc.delete(term);
		cc.closeConnection();
	}

	/**
	 * deleteMaxDocsメソッド
	 *
	 * @param url (String)MaxDocsの中から指定したURLを削除する
	 */
	public void deleteMaxDocs(String url) {
		//URLのチェック
		if (urlCheck(url)) {
			CassandraClient cc = new CassandraClient(_host, _port);
			cc.delete("MaxDocs", url);
			cc.closeConnection();
		}
	}

	/**
	 * searchメソッド(未完成)
	 *
	 * @param start
	 * @param end
	 */
	public void search(String start, String end) {
		CassandraClient cc = new CassandraClient(_host, _port);
		cc.search(start, end);
		cc.closeConnection();
	}

	//-----------------------------------------------------
	//staticメソッド
	//-----------------------------------------------------

	/**
	 * maxDocsメソッド
	 */
	public static int getMaxDocs() {
		int maxDocs_number = 0;
		CassandraClient cc = new CassandraClient(_host, _port);
		//cc.insertMaxDoc(url, maxDoc(url + "admin/luke"));
		List<Map<String, String>> list = cc.getMaxDocs();
		for (int i = 0; i < list.size(); i++) {
			for (Map.Entry<String, String> e : list.get(i).entrySet()) {
				//System.out.println(e.getKey() + " : " + e.getValue());
				if (e.getKey().equals("maxDocs")) {
					int n = Integer.valueOf(e.getValue()).intValue();
					maxDocs_number += n;
				}
			}
		}
		cc.closeConnection();
		return maxDocs_number;
	}

	/**
	 * urlCheckメソッド
	 *
	 *  @param url (String) URLを指定する
	 *   @return (boolean) True or False
	 */
	static boolean urlCheck(String url) {
		//「http://localhost:8983/solr/」or「http://localhost:8983/core0/solr/」のような形にマッチするようになっている
		final String MATCH_URL = "^https?:\\/\\/[-_.a-zA-Z0-9]+(:[0-9]+)*(\\/[-_a-zA-Z0-9]+)*(\\/solr\\/)$";
		Pattern pattern = Pattern.compile(MATCH_URL);
		Matcher match = pattern.matcher(url);
		if (match.find()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * docFreqメソッド(SolrサーバのdocFreqの値を取得する)
	 *
	 *  @param url (String) URLを指定する
	 *  @return (List) List< Map<String, String, String> >
	 *  @throws Exception
	 */
	@SuppressWarnings("unchecked")
	static List docFreq(String url) throws Exception {
		//POST送信でトップサーバにアクセス
		URL solrURL = new URL(url);
		URLConnection con = solrURL.openConnection();
		con.setDoOutput(true);
		PrintWriter out = new PrintWriter(con.getOutputStream());
		//パラメータ設定
		out.print("terms.fl=" + _termField + "&terms.limit=-1&terms.raw=true&wt=json");
		out.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		//JSONをListで取り出す
		String line = in.readLine();
		Map map = (Map) JSON.decode(line);
		//System.out.println(map);
		List list = (List) map.get("terms");
		//返す
		return (List) list.get(1);
	}

	/**
	 * maxDocメソッド
	 *
	 *  @param url (String) URLを指定する
	 *  @return (String) maxDoc値を返す
	 *  @throws Exception
	 */
	@SuppressWarnings("unchecked")
	static String maxDoc(String url) throws Exception {
		//POST送信でトップサーバにアクセス
		URL solrURL = new URL(url);
		URLConnection con = solrURL.openConnection();
		con.setDoOutput(true);
		PrintWriter out = new PrintWriter(con.getOutputStream());
		//パラメータ設定
		out.print("wt=json");
		out.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		//JSONをListで取り出す
		String line = in.readLine();
		Map map = (Map) JSON.decode(line);
		map = (Map) map.get("index");
		return map.get("maxDoc").toString();
	}
}
