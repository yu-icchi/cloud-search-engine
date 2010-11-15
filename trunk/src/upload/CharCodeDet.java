package upload;

import java.io.FileInputStream;
import java.io.IOException;

import org.mozilla.universalchardet.UniversalDetector;

public class CharCodeDet {

	private static String _path;

	/**
	 *
	 * @param path
	 */
	public CharCodeDet(String path) {
		_path = path;
	}

	/**
	 *
	 * @return
	 * @throws IOException
	 */
	public String encodeType() throws IOException {
		//サンプルとしてデータを読み込む
		byte[] buf = new byte[4096];
		FileInputStream fis = new FileInputStream(_path);
		//判定
		UniversalDetector detector = new UniversalDetector(null);
		int nread;
		while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}
		detector.dataEnd();
		//結果を返す、判断出来ない場合はnullになる。
		return detector.getDetectedCharset();
	}
}
