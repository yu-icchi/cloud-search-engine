//---------------------------------------------------------
//LSEDaemonクラス
//
//実際の実行ではjarファイルにして、LSEサーバに導入して使用する
//---------------------------------------------------------
package lse;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lse.auto.AutoDirCheck;
import lse.auto.AutoLocationCheck;
import lse.auto.AutoNodesCheck;
import lse.logic.ConsistentHashing;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.msgpack.rpc.Server;
import org.msgpack.rpc.loop.EventLoop;

public class LSEDaemon implements LSE {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------
	//コンシテントハッシングのクラスをシングルトンパターンで読み込む
	private static ConsistentHashing hash = ConsistentHashing.getInstance();
	//Location Serverへの通知をするクラス
	private static AutoLocationCheck lsCheck = null;
	//ディレクトリのチェックをするクラス
	private static AutoDirCheck dirCheck = null;
	//他のノードを監視しするクラス
	private static AutoNodesCheck nodesCheck = null;

	private static Map<String, String> solr = null;

	//-----------------------------------------------------
	//LSEインターフェースのオーバーライドの定義
	//-----------------------------------------------------

	/**
	 * @Override
	 */
	public String hello(String msg) {
		System.out.println(msg);
		return msg;
	}

	/**
	 * @Override
	 * デーモンスレッドを全て起動させる
	 */
	public void startAll() {
		try {
			//Apache SolrのmulticotreをRELOADさせる
			SolrServer solrServer = new CommonsHttpSolrServer("http://" + solr.get("host") + ":" + solr.get("port") + "/solr/");
			CoreAdminRequest.reloadCore("core0", solrServer);
			CoreAdminRequest.reloadCore("core1", solrServer);
		} catch (Exception e) {
			System.out.println("LSEDaemon Solr Multicore RELOAD is Error");
		}
		lsCheck.start();
		dirCheck.start();
		nodesCheck.start();
	}

	/**
	 * @Override
	 * デーモンスレッドを全て終了させる
	 */
	public void stopAll() {
		lsCheck.stop();
		dirCheck.stop();
		nodesCheck.stop();
	}

	/**
	 * @Override
	 * Location Serverへの更新要求フラグ
	 */
	public void lsCheckFlag(boolean flag) {
		lsCheck.check(flag);
	}

	/**
	 * @Override
	 * 新規ノードを追加する
	 */
	public void addNode(String node) {
		hash.addNode(node);
	}

	/**
	 * @Override
	 * 削除ノードを指定する
	 */
	public void delNode(String node) {
		hash.delNode(node);
	}

	/**
	 * @Override
	 * インデックスの最適化
	 */
	public void optimize(String host, String port) {
		//最適化するSolrサーバを決める
		String address = setSolrAddress(host, port);
		try {
			SolrServer solr = new CommonsHttpSolrServer(address);
			solr.optimize();
			System.out.println("LSEDaemon " + address + " Optimize... [ok]");
		} catch (Exception e) {
			System.out.println("LSEDaemon " + address + " Optimize... [Error]");
		}
	}

	//-----------------------------------------------------
	//staticメソッド
	//-----------------------------------------------------
	/**
	 * configDataメソッド
	 *
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private static Map<String, Object> configData(String path) throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();

		//LSEクラスタを制御するための設定ファイルを読み込む
		XMLConfig config = null;

		config = new XMLConfig(path);
		//デーモンのポート番号
		result.put("port", config.getElement("port"));
		//ディレクトリ
		result.put("dir", config.getElement("dir"));
		//ディレクトリのチェック間隔時間
		result.put("crawlerTimeSec", config.getElement("crawlerTimeSec"));
		//location　Server
		result.put("location", config.getHost2Port("location"));
		//location Server Time
		result.put("locationTimeSec", config.getElement("locationTimeSec"));
		//nodes
		result.put("nodes", config.getNodes("node"));
		//solr
		result.put("solr", config.getHost2Port("solr"));
		//pingをする間隔時間
		result.put("pingTime", config.getElement("solrPingTimeSec"));
		//Multicoreの保存ディレクトリ
		result.put("core0", config.getElement("core0"));
		result.put("core1", config.getElement("core1"));

		return result;
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

	/**
	 * Master側
	 * solrconfig.xmlを作成するメソッド
	 * @param dir
	 */
	private static void solrConfigMaster(String dir) {
		SolrConfig conf = new SolrConfig();
		conf.abortOnConfigurationError();
		conf.lib();
		conf.indexDefaults();
		conf.mainIndex();
		conf.jmx();
		conf.updateHandler();
		conf.query();
		conf.requestDispatcher();
		conf.standardHandler();
		conf.xmlHandler();
		conf.javabinHandler();
		conf.documentHandler();
		conf.fieldHandler();
		conf.debugHandler();
		conf.csvHandler();
		conf.replicationHandlerMaster("optimize", "startup", "commit");
		conf.termsHandler();
		conf.adminHandler();
		conf.pingHandler();
		conf.highlighting();
		conf.queryResponseWriter();
		conf.fileWrite(dir);
	}

	/**
	 * Slave側
	 * @param dir
	 * @param host
	 * @param port
	 */
	private static void solrConfigSlave(String dir, String host, String port) {
		SolrConfig conf = new SolrConfig();
		conf.abortOnConfigurationError();
		conf.lib();
		conf.indexDefaults();
		conf.mainIndex();
		conf.jmx();
		conf.updateHandler();
		conf.query();
		conf.requestDispatcher();
		conf.standardHandler();
		conf.xmlHandler();
		conf.javabinHandler();
		conf.documentHandler();
		conf.fieldHandler();
		conf.debugHandler();
		conf.csvHandler();
		conf.replicationHandlerSlave(setSolrAddress(host, port) + "replication", "00:05:00");
		conf.termsHandler();
		conf.adminHandler();
		conf.pingHandler();
		conf.highlighting();
		conf.queryResponseWriter();
		conf.fileWrite(dir);
	}

	//-----------------------------------------------------
	//メイン処理
	//-----------------------------------------------------
	/**
	 * main
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		try {
			//起動時にconfig.xmlファイルを指定する
			String path = System.getProperty("config");
			if (path == null) {
				System.out.println("Not Find... [config.xml]");
			}
			//config.xmlの読み込み
			Map<String, Object> xml = configData(path);
			int port = Integer.valueOf(xml.get("port").toString()).intValue();
			String dir = xml.get("dir").toString();
			Map<String, String> location = (Map<String, String>) xml.get("location");
			List<String> nodes = (List<String>) xml.get("nodes");
			solr = (Map<String, String>) xml.get("solr");
			long lsTime = Integer.valueOf(xml.get("locationTimeSec").toString()).longValue();
			long crawlerTime = Integer.valueOf(xml.get("crawlerTimeSec").toString()).longValue();
			long pingTime = Integer.valueOf(xml.get("pingTime").toString()).longValue();
			String core0Dir = xml.get("core0").toString();
			String core1Dir = xml.get("core1").toString();

			//MessagePack-RPCでServer作成
			EventLoop loop = EventLoop.defaultEventLoop();
			//サーバの設定
			Server svr = new Server(loop);
			svr.serve(new LSEDaemon());
			svr.listen(port);

			//Consistent Hashing作成
			hash.addNode(nodes);

			//SolrConfig.xmlの作成（multicore数が２つの場合である）
			//Master側でのsolrconfig.xmlの作成
			solrConfigMaster(core0Dir);
			//Slave側でのsolrconfig.xmlの作成
			solrConfigSlave(core1Dir, hash.prevNode(solr.get("host")), solr.get("port"));

			//ディレクトリチェックデーモンの起動
			dirCheck = new AutoDirCheck(dir, crawlerTime);
			dirCheck.setSolrPort(solr.get("port"));

			//Location Serverへの通知デーモンの起動
			lsCheck = new AutoLocationCheck(lsTime);
			lsCheck.setCassandra(location);
			lsCheck.setSolr(solr);

			//Nodeチェックデーモンの起動
			nodesCheck = new AutoNodesCheck(pingTime);
			nodesCheck.setCassandra(location);
			nodesCheck.setSolr(solr);

			//起動確認用の表示
			System.out.println("LSEDaemon server address : [" + InetAddress.getLocalHost().toString() +"]");
			System.out.println("LSEDaemon port number : [" + port +"]");
			System.out.println("LSEDaemon start...  [ok]");

			loop.join();
		} catch (Exception e) {
			System.out.println("LSEDaemon... [Error]");
		}
	}

}
