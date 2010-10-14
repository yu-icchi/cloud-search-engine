//---------------------------------------------------------
//Rankingクラス (未完成)
//
//Solrの検索時にdebugQuery=onにし、スコア計算の情報を取得する
//その内容からランキングを修正する
//---------------------------------------------------------
package solr.ranking;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ranking {

	/**
	 * プロパティ
	 */
	//検索指定フィール
	private static String field = "text";

	//IDFの値を計算するためのデータ
	private static int maxDocs;
	private static Map<String, Integer> docFreq;

	/**----------------------------------------------------
	 * コンストラクタ
	 * --------------------------------------------------*/
	public Ranking() {

	}

	/**----------------------------------------------------
	 * コンストラクタ (引数あり)
	 * ----------------------------------------------------
	 * @param docFreq
	 * @param maxDocs
	 * --------------------------------------------------*/
	public Ranking(Map<String, Integer> docFreq, int maxDocs) {
		Ranking.docFreq = docFreq;
		Ranking.maxDocs = maxDocs;
	}

	/**----------------------------------------------------
	 * set_maxDocsメソッド
	 * ----------------------------------------------------
	 * @param maxDocs
	 * --------------------------------------------------*/
	public static void set_maxDocs(int maxDocs) {
		Ranking.maxDocs = maxDocs;
	}

	/**----------------------------------------------------
	 * getmaxDocsメソッド
	 * ----------------------------------------------------
	 * @return
	 * --------------------------------------------------*/
	public static int getmaxDocs() {
		return maxDocs;
	}

	/**----------------------------------------------------
	 * setdocFreqメソッド
	 * ----------------------------------------------------
	 * @param docFreq
	 * --------------------------------------------------*/
	public static void set_docFreq(Map<String, Integer> docFreq) {
		Ranking.docFreq = docFreq;
	}

	/**----------------------------------------------------
	 * getdocFreqメソッド
	 * ----------------------------------------------------
	 * @return
	 * --------------------------------------------------*/
	public static Map<String, Integer> getdocFreq() {
		return docFreq;
	}

	/**----------------------------------------------------
	 * setfieldメソッド
	 * ----------------------------------------------------
	 * @param field
	 * --------------------------------------------------*/
	public static void setfield(String field) {
		Ranking.field = field;
	}

	/**----------------------------------------------------
	 * getfieldメソッド
	 * ----------------------------------------------------
	 * @return
	 * --------------------------------------------------*/
	public static String getfield() {
		return field;
	}

	/**----------------------------------------------------
	 * solrScoreメソッド
	 * --------------------------------------------------*/
	public void solrScore() {

	}

	/**----------------------------------------------------
	 * scoreメソッド
	 * --------------------------------------------------*/
	public void score() {

	}

	/**----------------------------------------------------
	 * rankingメソッド
	 * --------------------------------------------------*/
	public void ranking() {

	}

	/**----------------------------------------------------
	 * idfメソッド (グローバルIDFを付けるための計算式)
	 * ----------------------------------------------------
	 * @param maxDocs
	 * @param docFreq
	 * @return
	 * --------------------------------------------------*/
	static float idf(int maxDocs, int docFreq) {
		return (float) (Math.log(maxDocs / (double) (docFreq + 1)) + 1.0);
	}

	/**----------------------------------------------------
	 * extractKeywordメソッド
	 * ----------------------------------------------------
	 * @param line
	 * @return
	 * --------------------------------------------------*/
	static String extractKeyword(String line) {
		Pattern p = Pattern.compile("(" + Ranking.field + ":[a-z]+)");
		Matcher m = p.matcher(line);
		if (m.find()) {
			String[] str = m.group(1).split(":");
			return str[1];
		}
		return "";
 	}
}
