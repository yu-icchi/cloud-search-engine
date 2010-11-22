//---------------------------------------------------------
//CJKAnalyzerExtractクラス
//
//ユーザのクエリーテキストをCJKAnalyzerで分解する (2-gram)
//---------------------------------------------------------
package analysis;

import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;

public class CJKAnalyzerExtract {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	@SuppressWarnings("deprecation")
	private static Analyzer analyzer = new CJKAnalyzer();

	private String text;

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ
	 */
	public CJKAnalyzerExtract() {

	}

	public CJKAnalyzerExtract(String text) {
		this.text = text;
	}

	//-----------------------------------------------------
	//ゲッター・セッター
	//-----------------------------------------------------

	public void setText(String text) {
		this.text = text;
	}


	public String getText() {
		return text;
	}

	//-----------------------------------------------------
	//publicメソッド
	//-----------------------------------------------------

	@SuppressWarnings("deprecation")
	public ArrayList<String> extract() throws Exception {

		TokenStream stream = analyzer.tokenStream( "F", new StringReader( this.text ) );

		ArrayList<String> array = new ArrayList<String>();

		for( Token token = stream.next(); token != null; token = stream.next() ){

			array.add(token.termText());

		}

		stream.close();

		return array;
	}

}
