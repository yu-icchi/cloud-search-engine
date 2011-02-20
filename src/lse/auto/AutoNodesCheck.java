//---------------------------------------------------------
//AutoNodesCheckクラス
//
//LSEDaemonクラスから使用される。
//一定間隔で、Apache Solrが可動しているかをSolrJのSolrPingを使って調べる
//調べるSolrサーバは次のノードとそのまた次のノードである
//---------------------------------------------------------
package lse.auto;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.SolrPingResponse;

import location.Location;
import lse.logic.ConsistentHashing;

public class AutoNodesCheck {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	//このクラスを終了するためのフラグ
	private boolean fStop;

	//チェック間隔
	private long time;

	//Consistent Hashingクラスをシングルトンパターンで読み込む
	private ConsistentHashing hash = ConsistentHashing.getInstance();

	//Location Server
	private Map<String, String> cassandra;

	//Solrのアドレス
	private Map<String, String> solr;

	//監視用ノード
	private Map<String, String> nextNode = new HashMap<String, String>();
	private Map<String, String> next2Node = new HashMap<String, String>();
	//監視ノードリスト
	private List<Map<String, String>> nextNodeList = new ArrayList<Map<String, String>>();

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------
	/**
	 * コンストラクタ（引数にチェック間隔の時間を指定する）
	 * @param time
	 */
	public AutoNodesCheck(long time) {
		this.time = time;
	}

	//-----------------------------------------------------
	//制御用メソッド
	//-----------------------------------------------------
	/**
	 * スタートメソッド
	 */
	public void start() {
		fStop = false;
		//Locationクラスの定義
		Location location = new Location(this.cassandra.get("host"), Integer.valueOf(this.cassandra.get("port")));
		//次のノードを登録
		this.nextNode.put("host", hash.nextNode(this.solr.get("host")));
		this.nextNode.put("port", this.solr.get("port"));
		location.setNodes(this.nextNode.get("host"), "active");
		this.nextNodeList.add(this.nextNode);
		//次の次のノードを登録
		this.next2Node.put("host", hash.nextNode(this.nextNode.get("host")));
		this.next2Node.put("port", this.solr.get("port"));
		location.setNodes(this.next2Node.get("host"), "active");
		this.nextNodeList.add(this.next2Node);
		//標準出力
		System.out.println("AutoNodesCheck : start [" + this.solr.get("host") + "]... [ok]");
		//デーモンスレッド化
		Thread thread = new Thread(new AutoChecker());
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * ストップメソッド
	 */
	public void stop() {
		fStop = true;
	}

	/**
	 * SolrPing確認メソッド
	 */
	public void check() {
		//Solrサーバにアクセスする
		for (Map<String, String> next : this.nextNodeList) {
			Thread nextSolrServer = new Thread(new SolrPing(next, this.cassandra));
			nextSolrServer.start();
		}
	}

	/**
	 * resetメソッド
	 * @param node
	 */
	public void resetNodeList(Map<String, String> node) {
		this.nextNodeList.remove(node);
	}

	//-----------------------------------------------------
	//ゲッター・セッター
	//-----------------------------------------------------
	/**
	 * Cassandraのアドレス指定
	 * @param cassandra
	 */
	public void setCassandra(Map<String, String> cassandra) {
		this.cassandra = cassandra;
	}

	/**
	 * Cassandraの呼び出し
	 * @return
	 */
	public Map<String, String> getCassandra() {
		return cassandra;
	}

	/**
	 * Solrのアドレス指定
	 */
	public void setSolr(Map<String, String> solr) {
		this.solr = solr;
	}

	/**
	 * Solrのアドレス呼び出し
	 * @return
	 */
	public Map<String, String> getSolr() {
		return solr;
	}

	//-----------------------------------------------------
	//内部クラス
	//-----------------------------------------------------
	/**
	 * チェック間隔を指定するクラス
	 */
	private class AutoChecker implements Runnable {
		public void run() {
			while (!fStop) {
				try {
					Thread.sleep(1000 * time); // チェック間隔
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				check();
			}
		}
	}

	/**
	 * SolrPingをスレッドにして調べる
	 */
	private class SolrPing implements Runnable {

		//Solr Server
		private Map<String, String> solr = null;

		//Location Server
		private Location location = null;

		public SolrPing(Map<String, String> solr, Map<String, String> cassandra) {
			this.solr = solr;
			this.location = new Location(cassandra.get("host"), Integer.valueOf(cassandra.get("port")));
		}

		/**
		 * @Override
		 * SolrJを使ってping()をする
		 */
		public void run() {
			SolrPingResponse res = null;
			try {
				SolrServer server = new CommonsHttpSolrServer(setSolrAddress());
				res = server.ping();
			} catch (MalformedURLException e) {
				this.location.setNodes(this.solr.get("host"), "fault");
				resetNodeList(this.solr);
				System.out.println("Solr Ping " + res + " : " + this.solr.get("host") + " [URL is Error]");
			} catch (SolrServerException e) {
				this.location.setNodes(this.solr.get("host"), "fault");
				resetNodeList(this.solr);
				System.out.println("Solr Ping " + res + " : " + this.solr.get("host") + " [Server is Error]");
			} catch (IOException e) {
				this.location.setNodes(this.solr.get("host"), "fault");
				resetNodeList(this.solr);
				System.out.println("Solr Ping " + res + " : " + this.solr.get("host") + " [IO is Error]");
			}
		}

		/**
		 * Apache Solrのアドレスを作成メソッド
		 * @param host
		 * @return
		 */
		private String setSolrAddress() {
			return "http://" + this.solr.get("host") + ":" + this.solr.get("port") + "/solr/core0/";
		}
	}
}
