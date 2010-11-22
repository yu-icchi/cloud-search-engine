package analysis;

import java.util.ArrayList;

public class CJKAnalyzerExtractTest {

	public static void main(String[] args) throws Exception {

		ArrayList<String> array = new ArrayList<String>();
		array.add("Hello");
		array.add("分散システム");
		array.add("アパッチ");

		CJKAnalyzerExtract analyzerText = new CJKAnalyzerExtract(array);

		System.out.println(analyzerText.extract());

	}
}
