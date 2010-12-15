package analysis;

import java.util.ArrayList;

public class SenAnalyzerExtractTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ArrayList<String> array = new ArrayList<String>();
		array.add("HELLO");
		array.add("分散システム");
		array.add("アパッチ");
		array.add("AKB48");
		array.add("プログラム");

		SenAnalyzerExtract analyzerText = new SenAnalyzerExtract(array);

		System.out.println(analyzerText.extract());

	}

}
