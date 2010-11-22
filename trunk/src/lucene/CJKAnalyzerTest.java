package lucene;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;

public class CJKAnalyzerTest {

	@SuppressWarnings("deprecation")
	private static Analyzer analyzer = new CJKAnalyzer();

	private static final String TEXT = "メガネは顔の一部です。";



	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {

		TokenStream stream = analyzer.tokenStream( "F", new StringReader( TEXT ) );

		ArrayList<String> array = new ArrayList<String>();

		StringBuffer sb = new StringBuffer();

		for( Token token = stream.next(); token != null; token = stream.next() ){

			System.out.println( token.termText() );

			array.add(token.termText());

			sb.append( '[' ).append( token.termText() ).append( "] " );

		}

		stream.close();

		System.out.println( TEXT + " => " + sb.toString() );

		System.out.println(array);

	}
}
