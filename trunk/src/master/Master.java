//---------------------------------------------------------
//Managerクラス
//
//LSEノードを管理するクラス（最終的には、ServletでREST形式で使えるようにする）
//---------------------------------------------------------
package master;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

public class Master {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	//接続するホスト名の変数
	private String host;
	//接続するホストのポート番号の変数
	private String port;


	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ
	 */
	public Master() {

	}

	/**
	 * コンストラクタ (hostとport指定)
	 *
	 * @param host
	 * @param port
	 */
	public Master(String host, int port) {
		this.host = host;
		this.port = Integer.toString(port);
	}

	//-----------------------------------------------------
	//ゲッター・セッター
	//-----------------------------------------------------

	/**
	 * setHostメソッド (ホストを指定する)
	 *
	 * @param host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * getHostメソッド (指定したホストを出力)
	 *
	 * @return
	 */
	public String getHost() {
		return host;
	}

	/**
	 * setPortメソッド
	 *
	 * @param port (String)
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * setPortメソッド
	 *
	 * @param port (int)
	 */
	public void setPort(int port) {
		this.port = Integer.toString(port);
	}

	/**
	 * getPort
	 *
	 * @return
	 */
	public String getPort() {
		return port;
	}

	//-----------------------------------------------------
	//制御用メソッド
	//-----------------------------------------------------

	/**
	 * attachメソッド (新規ノード追加した時の処理)
	 *
	 * @return
	 */
	public String attach() {
		//コア全体のsolrconfig.xmlファイルを作成する
		//SolrConfig xml = new SolrConfig();
		//コア全体のRELOADをする
		//http://host:port/solr/admin/cores?action=RELOAD&core=core0&file=http://host:port/config/core0/solrconfig.xml
		//http://host:port/solr/admin/cores?action=RELOAD&core=core1&file=http://host:port/config/core1/solrconfig.xml
		//http://host:port/solr/admin/cores?action=RELOAD&core=core2&file=http://host:port/config/core2/solrconfig.xml
		//core0を強制レプリケーションさせる
		//http://host:port/solr/core0/replication?command=fetchindex
		//全文検索
		//http://host:port/solr/select?q=*:*
		//ConsistentHashingにより要らないモノを削除
		//<delete><id>◯</id><id>◯</id><id>◯</id>
		//コア0のみのsolrconfig.xmlファイルの作成する
		//SolrConfig core0 = new SolrConfig();
		//コア0のみをRELOADする
		//http://host:port/solr/admin/cores?action=RELOAD&core=core0&file=http://host:port/config/core0/solrconfig.xml
		//次のノードのレプリケーション先を修正
		//SolrConfig xml = new SolrConfig();
		//RELOAD
		//http://nextnode:port/solr/admin/cores?action=RELOAD&core=core0&file=http://host:port/config/core0/solrconfig.xml
		//http://nextnode:port/solr/admin/cores?action=RELOAD&core=core1&file=http://host:port/config/core1/solrconfig.xml
		//http://nextnode:port/solr/admin/cores?action=RELOAD&core=core2&file=http://host:port/config/core2/solrconfig.xml
		return null;
	}

	/**
	 * dettachメソッド (１つのノードがダウンした時の処理)
	 *
	 * @return
	 */
	public String dettach() {
		//ダウンした次のノードにあるcore0とcore1をマージさせる
		//http://nextnode:port/solr/admin/cores?action=MERGEINDEXES&core=core0&indexDir=core1/data/index
		//ダウンした次のノードのレプリケーション先を修正する
		//SolrConfig xml = new SolrConfig();
		//全コアをRELOADさせる
		//http://nextnode:port/solr/admin/cores?action=RELOAD&core=core0&file=http://host:port/config/core0/solrconfig.xml
		//http://nextnode:port/solr/admin/cores?action=RELOAD&core=core1&file=http://host:port/config/core1/solrconfig.xml
		//http://nextnode:port/solr/admin/cores?action=RELOAD&core=core2&file=http://host:port/config/core2/solrconfig.xml
		return null;
	}

	/**
	 * restoreメソッド (ダウンしたノードを再起動させた時の処理)
	 *
	 * @return
	 */
	public String restore() {
		//nextnodeからcore1とcore2を持ってくる
		//SolrConfig xml = new SolrConfig();
		//http://host:port/solr/admin/cores?action=RELOAD&core=core0&file=http://host:port/config/core0/solrconfig.xml
		//http://host:port/solr/admin/cores?action=RELOAD&core=core1&file=http://host:port/config/core1/solrconfig.xml
		//prevnodeからcore1を持ってくる
		//http://host:port/solr/admin/cores?action=RELOAD&core=core2&file=http://host:port/config/core2/solrconfig.xml
		//コピーが完了したら元のレプリケーションに戻す
		//SolrConfig xml = new SolrConfig();
		//http://host:port/solr/admin/cores?action=RELOAD&core=core0&file=http://host:port/config/core0/solrconfig.xml
		//http://host:port/solr/admin/cores?action=RELOAD&core=core1&file=http://host:port/config/core1/solrconfig.xml
		//http://host:port/solr/admin/cores?action=RELOAD&core=core2&file=http://host:port/config/core2/solrconfig.xml
		return null;
	}

	public static void httpConnect(String url) {
		HttpClient httpClient = new HttpClient();
		PostMethod method = new PostMethod(url);
		try {
			int status = httpClient.executeMethod(method);
			if (status != HttpStatus.SC_OK) {
				System.out.println("Method failed: " + method.getStatusText());
			}
			byte[] responseBody = method.getResponseBody();
			System.out.println(new String(responseBody));
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}

	}


	public static void main(String[] args) {
		httpConnect("http://www.google.com");
	}

}