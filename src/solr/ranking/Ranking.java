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
	static String field = "text";

	//debugQueryの構造データを格納
	static String[] _data;

	//IDFの値を計算するためのデータ
	private int maxDocs;
	private Map<String, Integer> docFreq;

	/**
	 * コンストラクタ
	 */
	public Ranking() {

	}

	/**
	 * コンストラクタ (引数あり)
	 *
	 * @param docFreq
	 * @param maxDocs
	 */
	public Ranking(Map<String, Integer> docFreq, int maxDocs) {
		this.docFreq = docFreq;
		this.maxDocs = maxDocs;
	}

	/**
	 * setMaxDocsメソッド
	 *
	 * @param maxDocs
	 */
	public void setMaxDocs(int maxDocs) {
		this.maxDocs = maxDocs;
	}

	/**
	 * getmaxDocsメソッド
	 *
	 * @return
	 */
	public int getmaxDocs() {
		return maxDocs;
	}

	/**
	 * setDocFreqメソッド
	 *
	 * @param docFreq
	 */
	public void setDocFreq(Map<String, Integer> docFreq) {
		this.docFreq = docFreq;
	}

	/**
	 * getDocFreqメソッド
	 *
	 * @return
	 */
	public Map<String, Integer> getDocFreq() {
		return docFreq;
	}

	/**
	 * setFieldメソッド
	 *
	 * @param field
	 */
	public static void setField(String field) {
		Ranking.field = field;
	}

	/**
	 * getFieldメソッド
	 *
	 * @return
	 */
	public static String getField() {
		return field;
	}

	/**
	 * solrScoreメソッド (solrのスコアデータを格納する)
	 *
	 * @param data
	 */
	public void solrScore(String data) {

	}

	/**
	 * scoreメソッド
	 */
	public void score() {

	}

	/**
	 * rankingメソッド
	 */
	public void ranking() {

	}

	/**
	 * idfメソッド (グローバルIDFを付けるための計算式)
	 *
	 * @param maxDocs
	 * @param docFreq
	 * @return
	 */
	static float idf(int maxDocs, int docFreq) {
		return (float) (Math.log(maxDocs / (double) (docFreq + 1)) + 1.0);
	}

	/**
	 * extractKeywordメソッド
	 *
	 * @param line
	 * @return
	 */
	static String extractKeyword(String line) {
		Pattern p = Pattern.compile("(" + Ranking.field +":[a-z]+)");
		Matcher m = p.matcher(line);
		if (m.find()) {
			String[] str = m.group(1).split(":");
			return str[1];
		}
		return "";
	}
}
