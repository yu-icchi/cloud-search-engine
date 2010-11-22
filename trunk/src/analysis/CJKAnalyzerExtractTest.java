package analysis;

public class CJKAnalyzerExtractTest {

	public static void main(String[] args) throws Exception {
		CJKAnalyzerExtract analyzerText = new CJKAnalyzerExtract("Hello分散システム");

		System.out.println(analyzerText.extract());

	}
}
