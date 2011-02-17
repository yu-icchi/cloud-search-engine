//---------------------------------------------------------
//TextFileReaderクラス(未完成)
//---------------------------------------------------------
package lse.crawler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.solr.common.SolrInputDocument;

public class TextFileReader {

	public TextFileReader() {

	}

	/**
	 * extractTextメソッド
	 *
	 * @param dir
	 * @return
	 */
	public String extractText(String dir) {
		try {
			//文字コード判定
			CharCodeDet charDet = new CharCodeDet(dir);
			String enc = charDet.encodeType();
			//テキストファイルを読み込む
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dir), enc));
			String tmp;
			String str = "";
			//Stringクラスにどんどん追加していく形になっている、この方法はあまり好ましくない
			while ((tmp = br.readLine()) != null) {
				str += tmp;
				if (!tmp.isEmpty()) {
					System.out.println(tmp);
				}
			}
			br.close();
			return str;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 *
	 * @param dir
	 * @param document
	 * @return
	 */
	public SolrInputDocument extractText(String dir, SolrInputDocument document) {
		try {
			//文字コード判定
			CharCodeDet charDet = new CharCodeDet(dir);
			String enc = charDet.encodeType();
			//テキストファイルを読み込む
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dir), enc));
			String tmp;
			//addFieldで一行毎に追加する
			while ((tmp = br.readLine()) != null) {
				if (!tmp.isEmpty()) {
					document.addField("text", tmp);
				}
			}
			br.close();
			return document;
		} catch (Exception e) {
			return document;
		}
	}

}
