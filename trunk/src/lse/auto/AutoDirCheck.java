//---------------------------------------------------------
//AutoDirCheckクラス
//
//LSEDaemonクラスから使用される
//---------------------------------------------------------
package lse.auto;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.msgpack.rpc.Client;
import org.msgpack.rpc.loop.EventLoop;

import lse.LSE;
import lse.crawler.FileCrawler;
import lse.logic.ConsistentHashing;


public class AutoDirCheck {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	//読み込むディレクトリの指定
	private File TARGET_DIR = null;

	//このクラスを終了させるフラグ
	private boolean fStop = false;

	//登録しているファイルのリスト
	private List<String> fRegistereds = null;

	//登録されているファイルの最終更新時間
	private Map<String, Long> fLastModifieds = null;

	//チェック間隔の時間
	private long time;

	//Consistent Hashingクラスをシングルトンパターンで読み込む
	private ConsistentHashing hash = ConsistentHashing.getInstance();

	//SolrPort番号
	private String solrPort;

	//更新したアドレスの保持
	private Set<String> setSolrAddress = new HashSet<String>();

	//MessagePack-RPC Client
	private EventLoop loop = null;
	private Client client = null;

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------
	/**
	 * @param dir
	 * @param time
	 */
	public AutoDirCheck(String dir, long time) {
		TARGET_DIR = new File(dir);
		this.time = time;
	}

	/**
	 * Solrのポート番号を指定する
	 * @param port
	 */
	public void setSolrPort(String port) {
		this.solrPort = port;
	}

	//-----------------------------------------------------
	//制御用のメソッド
	//-----------------------------------------------------
	/**
	 * スタートメソッド
	 */
	public void start() {
		fStop = false;
		fRegistereds = new ArrayList<String>();
		fLastModifieds = new HashMap<String, Long>();
		loop = EventLoop.defaultEventLoop();
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
	 * 更新処理メソッド
	 */
	public void check() {
		checkRemovedFile();
		checkNewFile(TARGET_DIR);
		checkModifiedFile();
		if (!this.setSolrAddress.isEmpty()) {
			locationUpdateFlag();
		}
	}

	/**
	 * Location Serverへの更新要求を更新したLSEDaemonに通知する
	 */
	public void locationUpdateFlag() {
		for (String address : this.setSolrAddress) {
			System.out.println(address);
			try {
				client = new Client(address, 1988, loop);
				LSE server = client.proxy(LSE.class);
				server.lsCheckFlag(true);
			} catch (Exception e) {
				System.out.println("RPC Error : [" + address + "]");
			}
		}
		this.setSolrAddress.clear();
	}

	//-----------------------------------------------------
	//ファイルを調べるメソッド
	//-----------------------------------------------------

	/**
	 * 更新メソッド
	 */
	private void checkModifiedFile() {
		Iterator<String> it = fRegistereds.iterator();
	    while (it.hasNext()) {
	    	String filename = it.next();
	    	File file = new File(TARGET_DIR, filename);
	    	Long lastModified = fLastModifieds.get(filename);
	    	long newLastModified = file.lastModified();
	    	if (lastModified == null) {
	    		fLastModifieds.put(filename, new Long(newLastModified));
	    	} else {
	    		// 更新処理
	    		if (lastModified.longValue() < newLastModified) {
	    			fLastModifieds.put(filename, new Long(newLastModified));
	    			//文書の格納先を調べる
	    			String node = hash.searchNode(file.getPath());
	    			String address = setSolrAddress(node);
	    			//Apache Solrにインデックスの更新
	    			FileCrawler fc = new FileCrawler(TARGET_DIR.getName(), file, address);
	    			boolean flag = fc.setIndex();
	    			if (flag == false) {
	    				System.out.println("AutoDirCheck : checkModifiedFile [" + address + " : " + filename + "]... [Error]");
	    			} else {
	    				System.out.println("AutoDirCheck : checkModifiedFile [" + address + " : " + filename + "]... [ok]");
	    				//更新したノードを格納
	    				this.setSolrAddress.add(node);
	    			}
	    		}
	    	}
	    }
	}

	/**
	 * 新規メソッド
	 * （隠しファイルは読み込まない）
	 */
	private void checkNewFile(File dir) {
		File[] fileList = dir.listFiles();
		for (File file : fileList) {
			//ファイルのみ
			if (file.isFile() == true && file.isHidden() == false) {
				//拡張子判定
				String name = getSuffix(file.getName());
				if (name.equals("txt") || name.equals("html") || name.equals("xml") || name.equals("ppt") || name.equals("doc") || name.equals("pdf")) {
					//リストと照合する
					if (!fRegistereds.contains(file.getPath())) {
						//追加処理
						fRegistereds.add(file.getPath());
						//文書の格納先を調べる
						String node = hash.searchNode(file.getPath());
						String address = setSolrAddress(node);
						//Apache Solrにインデックスの更新
						FileCrawler fc = new FileCrawler(TARGET_DIR.getName(), file, address);
						boolean flag = fc.setIndex();
						if (flag == false) {
							System.out.println("AutoDirCheck : checkNewFile [" + address + " : " + file.getPath() + "]... [Error]");
						} else {
							System.out.println("AutoDirCheck : checkNewFile [" + address + " : " + file.getPath() + "]... [ok]");
		    				//更新したノードを格納
		    				this.setSolrAddress.add(node);
		    			}
					}
				}
			} else if (file.isDirectory() == true && file.isHidden() == false) {
				//ディレクトリの場合では再帰処理する
				checkNewFile(file);
			}
		}
	}

	/**
	 * 削除メソッド
	 */
	private void checkRemovedFile() {
		Iterator<String> it = fRegistereds.iterator();
		while (it.hasNext()) {
			String filename = it.next();
			File file = new File(filename);
			if (!file.exists()) {
				//削除処理
				it.remove();
				//文書の格納先を調べる
				String node = hash.searchNode(file.getPath());
				String address = setSolrAddress(node);
				//solrから削除する
				try {
					//solrサーバに接続する
					SolrServer solr = new CommonsHttpSolrServer(address);
					solr.deleteById(filename);
					//solr.commit();
					//solr.optimize();
					System.out.println("AutoDirCheck : checkRemovedFile ["+ address + " : " + filename + "]... [ok]");
					//更新したノードを格納
					this.setSolrAddress.add(node);
				} catch (Exception e) {
					System.out.println("AutoDirCheck : checkRemovedFile [" + address + " : " + filename + "]... [Error]");
				}
			}
		}
	}

	/**
	 * 拡張子抽出メソッド
	 * @param fileName
	 * @return
	 */
	private static String getSuffix(String fileName) {
		if (fileName == null) {
			return null;
		}

		int point = fileName.lastIndexOf(".");
		if (point != -1) {
			return fileName.substring(point + 1);
		}

		return fileName;
	}

	/**
	 * Apache Solrのアドレスを作成メソッド
	 * @param host
	 * @return
	 */
	private String setSolrAddress(String host) {
		return "http://" + host + ":" + this.solrPort + "/solr/";
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

}