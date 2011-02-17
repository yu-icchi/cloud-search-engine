//---------------------------------------------------------
//AutoLocationCheckクラス
//
//LSEDaemonクラスから使用される。
//一定間隔で、Apache Solrのインデックスの情報を調べて、Apache Cassandraへ送る
//---------------------------------------------------------
package lse.auto;

import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;

import location.Location;

public class AutoLocationCheck {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	//このクラスを終了するためのフラグ
	private boolean fStop;

	//Solrが更新されたかのフラグ
	private boolean flag = false;

	//チェック間隔
	private long time;

	//Cassandraのアドレス
	private Map<String, String> cassandra;

	//Solrのアドレス
	private Map<String, String> solr;

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------
	/**
	 * コンストラクタ（引数にチェック間隔の時間を指定する）
	 * @param time
	 */
	public AutoLocationCheck(long time) {
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
	 * checkメソッド
	 * Solrのインデックスが更新された事を知らせる
	 * @param flag
	 */
	public void check(boolean flag) {
		this.flag = flag;
	}

	/**
	 * updateメソッド
	 * Location Serverへの更新要求をおくる
	 */
	public void update() {
		try {
			if (this.flag == true) {
				//Apache Solrサーバのインデックスを最適化する
				SolrServer solr = new CommonsHttpSolrServer(setSolrAddress());
				solr.optimize();
				//最適化終了後にLocation Serverに書き込み
				Location location = new Location(this.cassandra.get("host"), Integer.valueOf(this.cassandra.get("port")));
				location.set(this.solr.get("host"), this.solr.get("port"));
				//完了表示
				System.out.println("AutoLocationCheck : Update [" + this.cassandra.get("host") + " : " + this.cassandra.get("port") + "]... [ok]");
			}
		} catch (Exception e) {
			System.out.println("AutoLocationCheck : Update [" + this.cassandra.get("host") + " : " + this.cassandra.get("port") + "]... [Error]");
		} finally {
			this.flag = false;
		}
	}

	/**
	 * Apache Solrのアドレスを作成メソッド
	 * @param host
	 * @return
	 */
	private String setSolrAddress() {
		return "http://" + this.solr.get("host") + ":" + this.solr.get("port") + "/solr/";
	}

	//-----------------------------------------------------
	//ゲッター・セッター
	//-----------------------------------------------------
	/**
	 * Cassandraのアドレスの指定
	 * @param host
	 */
	public void setCassandra(Map<String, String> address) {
		this.cassandra = address;
	}

	/**
	 * Cassandraのアドレスの呼び出し
	 * @return
	 */
	public Map<String, String> getCassandra() {
		return this.cassandra;
	}

	/**
	 * Solrのアドレスの指定
	 * @param solr
	 */
	public void setSolr(Map<String, String> solr) {
		this.solr = solr;
	}

	/**
	 * Solrのアドレスの呼び出し
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
				update();
			}
		}
	}
}
