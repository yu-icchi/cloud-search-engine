//---------------------------------------------------------
//DirCheckDaemonクラス
//
//特定のディレクトリのファイルが更新されているかをチェックするクラス
//---------------------------------------------------------
package upload.dir;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DirCheckDaemon {

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

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ
	 *
	 * @param dir
	 */
	public DirCheckDaemon(String dir) {
		TARGET_DIR = new File(dir);
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
	public void check() {
		this.removeFile();
		this.newFile();
		this.modifierFile();
		System.out.println(this.lastmodifieds);
		System.out.println(this.registereds);
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
			String filename = it.next();
			File file = new File(TARGET_DIR, filename);
			if (!file.exists()) {
				//削除処理
				it.remove();
				System.out.println(filename + " が削除されました");
			}
		}
	}

	/**
	 * newFileメソッド
	 * 新たに追加されたファイルを見つけ、リストを更新する
	 */
	private void newFile() {
		String[] files = TARGET_DIR.list();
		for (int i = 0; i < files.length; i++) {
			//リストと照合する
			if (!registereds.contains(files[i])) {
				//追加処理
				registereds.add(files[i]);
				System.out.println(files[i] + " が追加されました");
			}
		}
	}

	/**
	 * modifierFileメソッド
	 * 更新されたファイルを見つけ、リストを更新する
	 */
	private void modifierFile() {
		Iterator<String> it = registereds.iterator();
		while (it.hasNext()) {
			String filename = it.next();
			File file = new File(TARGET_DIR, filename);
			Long lastModified = lastmodifieds.get(filename);
			long newLastModified = file.lastModified();
			if (lastModified == null) {
				//新規
				lastmodifieds.put(filename, new Long(newLastModified));
			} else {
				//更新処理
				if (lastModified.longValue() < newLastModified) {
					lastmodifieds.put(filename, new Long(newLastModified));
					System.out.println(filename + " が更新されました");
				}
			}
		}
	}

	//-----------------------------------------------------
	//バックグラウンド・クラス定義
	//-----------------------------------------------------

	/**
	 * AutoCheckFileクラス
	 *
	 * バックグラウンドで更新チェックをするスレッド
	 *
	 * @author yuta
	 *
	 */
	private class AutoCheckFile implements Runnable {

		public void run() {
			while (!stopFlag) {
				try {
					//チェック間隔
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					check();
				}
			}
		}

	}
}
