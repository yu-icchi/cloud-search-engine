//---------------------------------------------------------
//Daemonクラス
//
//LSEの処理をするメインのクラスである。
//LSEでローカルファイルシステムにアクセスして、更新などの処理に対してSolrにインデックス作成をさせる。
//---------------------------------------------------------
package localsearchengine.demon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;

import upload.consistency.ConsistentHashing;

import localsearchengine.config.SolrConfig;
import localsearchengine.crawler.FileCrawler;
import location.Location;

public class Daemon {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	//ディレクトリを指定する
	private File TARGET_DIR;
	//スレッド停止用のフラグ
	private boolean stopFlag;
	//登録されているファイルリスト
	private List<String> registereds;
	//登録されているファイルの最終更新時刻
	private Map<String, Long> lastmodifieds;

	private boolean LSFLAG = false;

	List<File> list = new ArrayList<File>();

	//Consistent Hashingのデータ
	private ConsistentHashing hash = new ConsistentHashing();
	//
	private String locationServerHost;
	private int locationServerPort;
	//
	private String lseSolrServer;
	//solr port
	private String solrPort = "8983";

	private String lseNode;

	private List<String> locationServerList = new ArrayList<String>();

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ
	 */
	public Daemon() {

	}

	//-----------------------------------------------------
	//初期設定
	//-----------------------------------------------------

	/**
	 * dirメソッド
	 *
	 * @param path
	 */
	public void dir(String path) {
		TARGET_DIR = new File(path);
	}

	/**
	 * locationメソッド
	 *
	 * @param server
	 */
	public void location(Map<String, String> server) {
		locationServerHost = server.get("host");
		locationServerPort = Integer.valueOf(server.get("port"));
	}

	public String getLocation() {
		return locationServerHost + ":" + locationServerPort;
	}

	/**
	 * addNodeメソッド
	 * nodeのアドレスを追加すると、ハッシュに追加する
	 *
	 * @param nodes
	 */
	public void addNode(List<String> nodes) {
		hash.addNode(nodes);
		for (String node : nodes) {
			locationServerList.add(node);
		}
	}

	/**
	 * delNodeメソッド
	 *
	 * @param node
	 */
	public void delNode(String node) {
		hash.delNode(node);
	}

	/**
	 * lseSolrメソッド
	 * Solrサーバのアドレス作成
	 *
	 * @param server
	 */
	public void lseSolr(Map<String, String> server) {
		lseNode = server.get("host");
		lseSolrServer = "http://" + server.get("host") + ":" + server.get("port") + "/solr/";
		setSolrPort(Integer.valueOf(server.get("port")).intValue());
	}

	public String getLSESolr() {
		return lseSolrServer;
	}

	public void setSolrPort(int port) {
		solrPort = Integer.valueOf(port).toString();
	}

	public String getSolrPort() {
		return solrPort;
	}

	//-----------------------------------------------------
	//SolrConfig作成
	//-----------------------------------------------------

	public void solrConfigWriter(String core) {
		System.out.println("core: " + core);
		//solrconfig.xmlを作成する
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
		conf.replicationHandler(lseSolrServer + "replication", "00:00:30", "optimize", "startup");
		conf.termsHandler();
		conf.adminHandler();
		conf.pingHandler();
		conf.highlighting();
		conf.queryResponseWriter();
		//Consistent Hashing
		System.out.println(hash.nextNode(lseNode));
		conf.fileWrite("src/localsearchengine/config/" + core + "SolrConfig.xml");
		//マルチコアで指定したコアをRELOADする
		boolean flag = solrServerCoreReload(lseSolrServer +"admin/core?action=RELOAD&core=" + core);
		System.out.println(flag);
	}

	/**
	 * solrServerCoreReloadメソッド
	 *
	 * @param url
	 * @return
	 */
	private static boolean solrServerCoreReload(String url) {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
		try {
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				System.out.println("Method failed: " + method.getStatusText());
				//404 Not Found Error
				if (method.getStatusText().indexOf("Not Found") != -1) {
					return false;
				}
			}
			byte[] responseBody = method.getResponseBody();
			System.out.println(new String(responseBody));
			return true;
		} catch(Exception e) {
			return false;
		} finally {
			method.releaseConnection();
		}
	}

	//-----------------------------------------------------
	//制御用メソッド
	//-----------------------------------------------------

	/**
	 * startメソッド
	 */
	public void start() {
		stopFlag = false;
		registereds = new ArrayList<String>();
		lastmodifieds = new HashMap<String, Long>();
		Thread thread = new Thread(new AutoCheckFile());
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * stopメソッド
	 */
	public void stop() {
		stopFlag = true;
	}

	/**
	 * checkメソッド
	 * ファイルのチェックをする
	 */
	private void check() {
		this.removeFile();
		this.newFile();
		this.modifierFile();
		this.locationUpdate();
	}


	private void locationUpdate() {
		if (LSFLAG == true) {
			//locationへ通知する
			Location ls = new Location(locationServerHost, locationServerPort);
			for (String node : locationServerList) {
				node = "http://" + node + ":" + solrPort + "/solr/";
				ls.set(node);
			}
			LSFLAG = false;
			System.out.println("end locationUpdate");
		}
	}


	private void threadLocationUpdate(String node) {
		if (LSFLAG) {
			ThreadLocation th = new ThreadLocation(node);
			th.start();
		}
	}

	/**
	 * putメソッド
	 * @test
	 * @param msg
	 */
	public void put(String msg) {
		System.out.println(msg);
	}

	//-----------------------------------------------------
	//ディレクトリのチェックメソッド
	//-----------------------------------------------------

	/**
	 * removeFileメソッド
	 * 削除されたファイルを見つけて、リストを更新する
	 */
	private void removeFile() {
		Iterator<String> it = registereds.iterator();
		while (it.hasNext()) {
			String filepath = it.next();
			File file = new File(filepath);
			if (!file.exists()) {
				//削除処理
				it.remove();
				System.out.println(filepath + " が削除されました");
				//solrから削除する
				try {
					//consistent hashing search node
					String node = hash.searchNode(filepath);
					node = "http://" + node + ":" + solrPort + "/solr/";
					//solrサーバに接続する
					SolrServer solr = new CommonsHttpSolrServer(node);
					solr.deleteById(filepath);
					//solr.commit();
					solr.optimize();
					//locationへ通知する
					//this.locationUpdate();
					//this.threadLocationUpdate(node);
					LSFLAG = true;
					System.out.println("success");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		//this.threadLocationUpdate(lseSolrServer);
	}

	/**
	 * newFileメソッド
	 * 新たに追加されたファイルを見つけ、リストを更新する
	 */
	private void newFile() {
		boolean flag = false;
		File[] fileList = TARGET_DIR.listFiles();
		//listPath(TARGET_DIR, list);
		for (File file : fileList) {
			//リストと照合する
			if (!registereds.contains(file.getPath())) {
				//追加処理
				registereds.add(file.getPath());
				System.out.println(file.getPath() + " が追加されました");
				//consistent hashing search node
				String node = hash.searchNode(file.getPath());
				node = "http://" + node + ":" + solrPort + "/solr/";
				//solrに格納する
				FileCrawler crawler = new FileCrawler(TARGET_DIR.getName(), file, node);
				//Solrを更新
				/*
				try {
					SolrServer solr = new CommonsHttpSolrServer(node);
					//solr.commit();
					solr.optimize();
				} catch (Exception e) {
					e.printStackTrace();
				}
				*/
				flag = crawler.setIndex();
				LSFLAG = flag;
				//this.threadLocationUpdate(node);
			}
			if (flag) {
				//locationへ通知する
				//this.locationUpdate();
				System.out.println("success");
			}
		}
		//this.threadLocationUpdate(locationServerHost);
	}

	/**
	 * modifierFileメソッド
	 * 更新されたファイルを見つけ、リストを更新する
	 */
	private void modifierFile() {
		boolean flag = false;
		Iterator<String> it = registereds.iterator();
		while (it.hasNext()) {
			String filepath = it.next();
			File file = new File(filepath);
			Long lastModified = lastmodifieds.get(filepath);
			long newLastModified = file.lastModified();
			if (lastModified == null) {
				//新規
				lastmodifieds.put(filepath, new Long(newLastModified));
			} else {
				//更新処理
				if (lastModified.longValue() < newLastModified) {
					lastmodifieds.put(filepath, new Long(newLastModified));
					System.out.println(filepath + " が更新されました");
					//consistent hashing search node
					String node = hash.searchNode(filepath);
					node = "http://" + node + ":" + solrPort + "/solr/";
					//solrに格納する
					FileCrawler crawler = new FileCrawler(TARGET_DIR.getName(), file, node);
					//Solrを更新
					/*
					try {
						SolrServer solr = new CommonsHttpSolrServer(node);
						//solr.commit();
						solr.optimize();
					} catch (Exception e) {
						e.printStackTrace();
					}
					*/
					flag = crawler.setIndex();
					LSFLAG = flag;
					//this.threadLocationUpdate(node);
				}
			}
		}
		if (flag) {
			//locationへ通知する
			//this.locationUpdate();
			System.out.println("success");
		}
		//this.threadLocationUpdate(locationServerHost);
	}

	/**
	 * listPathメソッド
	 * ディレクトリの階層構造を調べて、ファイルを見つける
	 *
	 * @param file
	 * @param array
	 */
	/*
	private static void listPath(File file, List<File> array) {
		File[] infiles = file.listFiles();
		for (File inf : infiles) {
			if (inf.isDirectory()) {
				listPath(inf, array);
			} else {
				if (!array.contains(inf)) {
					array.add(inf);
				}
			}
		}
	}
	*/

	//-----------------------------------------------------
	//内部クラス
	//-----------------------------------------------------

	/**
	 * AutoCheckFileクラス
	 *
	 * バックグラウンドで更新チェックをするスレッド
	 */
	private class AutoCheckFile implements Runnable {

		public void run() {
			while (!stopFlag) {
				try {
					//チェック間隔
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//更新を確認する
				check();
			}
		}

	}

	private class ThreadLocation extends Thread {

		private String node;

		public ThreadLocation(String node) {
			this.node = node;
		}
		public void run() {
			Location ls = new Location(locationServerHost, locationServerPort);
			ls.set("http://" + this.node + ":" + solrPort + "/solr/");
		}
	}
}
