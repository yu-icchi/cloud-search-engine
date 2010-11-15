//---------------------------------------------------------
//Crawlerクラス
//
//ファサードパターンで、ファイルの拡張子から最適な方法で文書を抽出し、Solrに格納
//---------------------------------------------------------
package upload;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class Crawler {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	//ファイルパス
	private static String filePath;
	//インデックスを格納するサーバ
	private static String server;
	//アカウント情報格納
	private static String account;

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ
	 *
	 * @param account
	 * @param path
	 * @param server
	 */
	public Crawler(String account, String path, String server) {
		Crawler.setAccount(account);
		Crawler.setFilePath(path);
		Crawler.setServer(server);
	}

	//-----------------------------------------------------
	//get・setメソッド
	//-----------------------------------------------------

	/**
	 * setAccountメソッド
	 *
	 * @param account
	 */
	public static void setAccount(String account) {
		Crawler.account = account;
	}

	/**
	 * getAccountメソッド
	 *
	 * @return
	 */
	public static String getAccount() {
		return account;
	}

	/**
	 * setFilePathメソッド
	 *
	 * @param filePath
	 */
	public static void setFilePath(String filePath) {
		Crawler.filePath = filePath;
	}

	/**
	 * getFilePathメソッド
	 *
	 * @return
	 */
	public static String getFilePath() {
		return filePath;
	}

	/**
	 * setServerメソッド
	 *
	 * @param server
	 */
	public static void setServer(String server) {
		Crawler.server = server;
	}

	/**
	 * getServerメソッド
	 *
	 * @return
	 */
	public static String getServer() {
		return server;
	}

	//-----------------------------------------------------
	//publicメソッド
	//-----------------------------------------------------

	/**
	 * setIndexメソッド
	 *
	 * @param data
	 * @return
	 */
	public boolean setIndex() {
		//インデックスに格納するテキスト
		String text = "";
		//拡張子
		String suffix = getSuffix(filePath);
		//格納するインデックスの情報
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", filePath);
		map.put("account", Crawler.account);
		//拡張子により読み込み処理を変える
		if (suffix.equals("txt")) {
			TextFileReader reader = new TextFileReader();
			text = reader.extractText(filePath);
		} else if (suffix.equals("doc")) {
			WordReader reader = new WordReader();
			text = reader.extractDoc(filePath);
		} else if (suffix.equals("pdf")) {
			PDFReader reader = new PDFReader();
			text = reader.extractPDF(filePath);
		} else if (suffix.equals("ppt")) {
			PowerPointReader reader = new PowerPointReader();
			text = reader.extractPPT(filePath);
		}
		System.out.println(text);
		if (text == null) {
			return false;
		}
		map.put("text", text);
		//インデックス格納
		return indexWriterSolr(Crawler.server, map);
	}

	//-----------------------------------------------------
	//privateメソッド
	//-----------------------------------------------------

	/**
	 * indexWriterSolrメソッド (指定したSolrサーバにインデックスを登録する)
	 *
	 * @param host
	 * @param data
	 * @return
	 */
	private static boolean indexWriterSolr(String host, Map<String, String> data) {
		try {
			System.out.println(data);
			//インデックスを格納するサーバを決める
			SolrServer server = new CommonsHttpSolrServer(host);
			//ドキュメントを作成
			SolrInputDocument document = new SolrInputDocument();
			//フィールドの指定
			document.addField("id", data.get("id"));
			document.addField("account", data.get("account"));
			document.addField("text", data.get("text"));
			//サーバに追加
			server.add(document);
			//コミット
			server.commit();
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * getSuffixメソッド (拡張子を調べる)
	 *
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

}
