package analysis;

import java.util.ArrayList;

public class CJKAnalyzerExtractTest {

	public static void main(String[] args) throws Exception {

		ArrayList<String> array = new ArrayList<String>();
		array.add("HELLO");
		array.add("分散システム");
		array.add("アパッチ");
		array.add("ＡＫＢ４８");
		array.add("プログラム");

		CJKAnalyzerExtract analyzerText = new CJKAnalyzerExtract(array);

		System.out.println(analyzerText.extract());

		System.out.println(analyzerText.qbssExtract("大島"));

	}
}
