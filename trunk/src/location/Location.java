//---------------------------------------------------------
//Location Databaseアクセスするクラス(URLを指定し、データベースからdocFreq・maxDocの値を登録・取得する)
//
//直接Cassandraにアクセスし、データベースの管理をする
//---------------------------------------------------------
package location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.response.LukeResponse;

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
	public void query(String query, String type) {
		try {
			LocationQueryConverter convert = new LocationQueryConverter(type);
			convert.parser(query);
			_query = convert.getQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * queryメソッド
	 *
	 * @param query
	 * @throws Exception
	 */
	public void query(String query) {
		_query = query;
	}

	//-----------------------------------------------------
	//Cassandraにデータを格納する
	//-----------------------------------------------------

	/**
	 * setメソッド(格納)
	 *
	 *  @param host (String) HOST名を指定する
	 */
	@SuppressWarnings("unchecked")
	public void set(String host, String port) throws Exception {
		//MaxDocsデータを格納する
		setMaxDocs(host, port);
		//docFreq値を取得する
		List<Object> list = docFreq(host, port);
		//List → List<Map<String, String>>
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		Map<String, String> map;
		for (int i = 0; i < list.size(); i+=2) {
			map = new HashMap<String, String>();
			map.put("term", list.get(i).toString());
			map.put("docFreq", list.get(i + 1).toString());
			map.put("url", host);
			data.add(map);
		}
		//Cassandraに接続する
		CassandraClient cc = new CassandraClient(_host, _port);
		cc.insertDocFreq(data);
		cc.closeConnection();
	}

	/**
	 * setNodesメソッド（まとめて保存）
	 *
	 * @param nodes
	 */
	public void setNodes(Map<String, String> nodes) {
		try {
			//Cassandraに接続する
			CassandraClient cc = new CassandraClient(_host, _port);
			cc.insertNodes(nodes);
			cc.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * setNodesメソッド（1つ保存）
	 *
	 * @param host
	 * @param type
	 */
	public void setNodes(String host, String type) {
		try {
			//Cassandraに接続する
			CassandraClient cc = new CassandraClient(_host, _port);
			cc.insertNodes(host, type);
			cc.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * setMaxDocsメソッド
	 *
	 *  @param  url (String) URLを指定する
	 *  @throws Exception
	 */
	public static void setMaxDocs(String host, String port) {
		try {
			CassandraClient cc = new CassandraClient(_host, _port);
			cc.insertMaxDoc(host, maxDoc(host, port));
			cc.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	 * getNodesメソッド
	 * @param host
	 * @return
	 */
	public Map<String, String> getNodes() {
		CassandraClient cc = new CassandraClient(_host, _port);
		Map<String, String> map = cc.getNodes();
		cc.closeConnection();
		return map;
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
		//MaxDocsの値を取得する
		int maxDocs_number = getMaxDocs();
		result.put("maxDocs", maxDocs_number);
		Map<String, Integer> term = new HashMap<String, Integer>();
		Map<String, List<String>> urls = new HashMap<String, List<String>>();
		Object data = null;
		List<String> urlList;
		//Cassandraに接続する
		CassandraClient cc = new CassandraClient(_host, _port);
		//複数クエリの結果を取得する
		List<Map<String, String>> list = cc.get(input);
		//System.out.println(list);
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
		//System.out.println(urls);
		//query式が与えられているか
		//if (!_query.isEmpty()) {
			//QbSS
			QbSS qbss = new QbSS(_query, urls);
			try {
				data = qbss.parser();
			} catch(Exception e) {
				System.out.println("アクセス先がありません");
				return null;
			}
		//}
		result.put("docFreq", term);
		result.put("url", data);

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
	public void delete(String host, String port) {
		try {
			//docFreq値を取得する
			List list = docFreq(host, port);
			for (int i = 0; i < list.size(); i+=2) {
				//Cassandraのデータベースを削除する
				CassandraClient cc = new CassandraClient(_host, _port);
				//一致するタームフィールドを削除する
				cc.delete(list.get(i).toString(), host);
				cc.closeConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * deleteURLメソッド
	 *
	 * @param host
	 * @throws Exception
	 */
	public void deleteURL(String host) throws Exception {
		//Cassandraにアクセスする
		CassandraClient cc = new CassandraClient(_host, _port);
		cc.deleteURL(host);
		cc.closeConnection();
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
	 * @param host (String)MaxDocsの中から指定したURLを削除する
	 */
	public void deleteMaxDocs(String host) {
		CassandraClient cc = new CassandraClient(_host, _port);
		//MD5("MaxDocs")=>9b8fc883a0157d95b549084d9958a2dd
		cc.delete("9b8fc883a0157d95b549084d9958a2dd", host);
		cc.closeConnection();
	}

	/**
	 * deleteNodesメソッド
	 * @param host
	 */
	public void deleteNodes(String host) {
		CassandraClient cc = new CassandraClient(_host, _port);
		cc.deleteNodes(host);
		cc.closeConnection();
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
		List<Map<String, String>> list = cc.getMaxDocs();
		for (int i = 0; i < list.size(); i++) {
			for (Map.Entry<String, String> e : list.get(i).entrySet()) {
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
	 * docFreqメソッド(SolrサーバのdocFreqの値を取得する)
	 *
	 *  @param host (String) HOST名を指定する
	 *  @return (List) List< Map<String, String, String> >
	 *  @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static List docFreq(String host, String port) throws Exception {
		String url = setSolrAddress(host, port);
		url += "terms?terms.fl=" + _termField + "&terms.limit=-1&terms.raw=true&wt=json";
		HttpClient httpClient = new HttpClient();
		PostMethod method = new PostMethod(url);
		try {
			int status = httpClient.executeMethod(method);
			if (status != HttpStatus.SC_OK) {
				//System.out.println("Method failed: " + method.getStatusText());
			}
			byte[] responseBody = method.getResponseBody();
			//System.out.println(new String(responseBody));
			Map map = (Map) JSON.decode(new String(responseBody, "UTF-8"));
			List list = (List) map.get("terms");
			//Map time = (Map) map.get("responseHeader");
			//System.out.println("QTime : " + time.get("QTime"));
			return (List) list.get(1);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		return null;
	}

	/**
	 * maxDocメソッド
	 *
	 *  @param host (String) HOSTを指定する
	 *  @return (String) maxDoc値を返す
	 *  @throws Exception
	 */
	private static String maxDoc(String host, String port) throws Exception {
		String url = setSolrAddress(host, port);
		//SolrJ使用
		SolrServer solr = new CommonsHttpSolrServer(url);
		LukeRequest luke = new LukeRequest();
		luke.setShowSchema(false);
		LukeResponse rsp = luke.process(solr);
		//Integer max = rsp.getMaxDoc();
		//System.out.println("maxDoc : " + max);
		Integer max = rsp.getNumDocs();
		return max.toString();
	}

	/**
	 * Apache Solrのアドレスを作成メソッド
	 * @param host
	 * @param port
	 * @return
	 */
	private static String setSolrAddress(String host, String port) {
		return "http://" + host + ":" + port + "/solr/core0/";
	}
}
