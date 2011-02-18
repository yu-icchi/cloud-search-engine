package lse.crawler;

import java.io.File;
import java.util.Date;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;


public class FileCrawler {
	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	//ファイルパス
	private static File file;
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
	public FileCrawler(String account, File file, String server) {
		FileCrawler.setAccount(account);
		FileCrawler.setFile(file);
		FileCrawler.setServer(server);
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
		FileCrawler.account = account;
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
	public static void setFile(File file) {
		FileCrawler.file = file;
	}

	/**
	 * getFilePathメソッド
	 *
	 * @return
	 */
	public static File getFile() {
		return file;
	}

	/**
	 * setServerメソッド
	 *
	 * @param server
	 */
	public static void setServer(String server) {
		FileCrawler.server = server;
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
	 * @return
	 */
	public boolean setIndex() {
		try {

			//拡張子
			String suffix = getSuffix(file.getName());

			//インデックスを格納するサーバを決める
			SolrServer solr = new CommonsHttpSolrServer(server);

			//ドキュメントを作成
			SolrInputDocument document = new SolrInputDocument();
			//フィールドの指定
			document.addField("id", FileCrawler.file.getPath());
			document.addField("account", FileCrawler.account);
			document.addField("type", suffix);
			//System.out.println(new Date(file.lastModified()));
			document.addField("time", new Date(file.lastModified()).toString());
			//System.out.println(file.getPath());
			document.addField("path", file.getPath());
			//System.out.println(file.length());
			document.addField("size", file.length());
			//System.out.println(file.getName());
			document.addField("title", file.getName());

			//拡張子により読み込み処理を変える
			if (suffix.equals("txt") || suffix.equals("html") || suffix.equals("xml")) {
				TextFileReader reader = new TextFileReader();
				document = reader.extractText(file.getPath(), document);
			} else if (suffix.equals("doc")) {
				WordReader reader = new WordReader();
				document = reader.extractDoc(file.getPath(), document);
			} else if (suffix.equals("pdf")) {
				PDFReader reader = new PDFReader();
				document = reader.extractPDF(file.getPath(), document);
			} else if (suffix.equals("ppt")) {
				PowerPointReader reader = new PowerPointReader();
				document = reader.extractPPT(file.getPath(), document);
			}

			//サーバに追加
			solr.add(document);
			//コミット
			//solr.commit();
			//最適化
			//solr.optimize();

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	//-----------------------------------------------------
	//内部メソッド
	//-----------------------------------------------------

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
