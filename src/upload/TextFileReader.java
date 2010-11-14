//---------------------------------------------------------
//TextFileReaderクラス(未完成)
//---------------------------------------------------------
package upload;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

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
			}
			br.close();
			return str;
		} catch (Exception e) {
			return null;
		}
	}

}
