package upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class FileCrawler {

	/**
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		fileIndex("tmp", "http://localhost:8983/solr");
		System.out.println("インデックス登録完了");
	}

	/**----------------------------------------------------
	 * インデックス作成・更新
	 * ----------------------------------------------------
	 * @param file_dir
	 * @param url
	 * @throws Exception
	 * --------------------------------------------------*/
	public static void fileIndex(String file_dir, String url) throws Exception {
		//探索対象のrootを決める
		File dir = new File(file_dir);
		//文書ファイルがある絶対パスを保持する
		ArrayList<String> arr = new ArrayList<String>();
		listPath(dir, arr);
		//solrサーバにアクセスする
		SolrServer server = new CommonsHttpSolrServer(url);
		for (int i = 0; i < arr.size(); i ++) {
			String str = readTextFile(arr.get(i));
			System.out.println(arr.get(i));
			System.out.println(str);
			//Solrサーバにインデックスを登録
			SolrInputDocument document = new SolrInputDocument();
			document.addField("id", arr.get(i));
			document.addField("text", str);
			server.add(document);
		}
		//コミット
		server.commit();
	}

	/**----------------------------------------------------
	 * ローカルのデータを再帰的に探索する
	 * ----------------------------------------------------
	 * @param file
	 * @param array
	 * --------------------------------------------------*/
	private static void listPath(File file, ArrayList<String> array) {
		File[] infiles = file.listFiles();
		for (File inf : infiles) {
			if (inf.isDirectory()) {
				listPath(inf, array);
			} else {
				String name = inf.getAbsolutePath();
				//txtファイルデータのみを見つける
				if (name.contains("txt") || name.contains("html")) {
					//System.out.println(name);
					array.add(name);
				}
			}
		}
	}

	/**----------------------------------------------------
	 * ファイルデータを読み込み、内容を一列のStringにして返す
	 * ----------------------------------------------------
	 * @param dir
	 * @return
	 * @throws Exception
	 * --------------------------------------------------*/
	private static String readTextFile(String dir) throws Exception {
		CharCodeDet charDet = new CharCodeDet(dir);
		String enc = charDet.encodeType();
		//System.out.println(enc);
		//FileReader fr = new FileReader(dir);
		//BufferedReader br = new BufferedReader(fr);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dir), enc));
		String tmp;
		String str = "";
		while ((tmp = br.readLine()) != null) {
			str += tmp;
		}
		//System.out.println(str);
		br.close();
		//fr.close();
		return str;
	}

	/**----------------------------------------------------
	 * getSuffixメソッド (ファイル拡張子を返す)
	 * ----------------------------------------------------
	 * @param fileName
	 * @return
	 * --------------------------------------------------*/
	public static String getSuffix(String fileName) {
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
