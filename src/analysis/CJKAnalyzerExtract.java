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

	private ArrayList<String> stringArray = new ArrayList<String>();

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ
	 */
	public CJKAnalyzerExtract() {

	}

	public CJKAnalyzerExtract(ArrayList<String> array) {
		this.stringArray = array;
	}

	//-----------------------------------------------------
	//ゲッター・セッター
	//-----------------------------------------------------

	public void setStringArray(ArrayList<String> stringArray) {
		this.stringArray = stringArray;
	}

	public ArrayList<String> getStringArray() {
		return stringArray;
	}

	//-----------------------------------------------------
	//publicメソッド
	//-----------------------------------------------------

	@SuppressWarnings("deprecation")
	public ArrayList<String> extract() throws Exception {

		ArrayList<String> array = new ArrayList<String>();

		for (String str : this.stringArray) {
			TokenStream stream = analyzer.tokenStream( "F", new StringReader( str ) );
			for( Token token = stream.next(); token != null; token = stream.next() ){
				array.add(token.termText());
			}
			stream.close();
		}

		return array;
	}

}
