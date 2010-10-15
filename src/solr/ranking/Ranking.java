//---------------------------------------------------------
//Rankingクラス (未完成)
//
//Solrの検索時にdebugQuery=onにし、スコア計算の情報を取得する
//その内容からランキングを修正する
//---------------------------------------------------------
package solr.ranking;

import java.util.List;
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

	//スコア計算用クラス
	static Score _score;

	//-----------------------------------------------------
	//コンストラクタの定義
	//-----------------------------------------------------

	/**
	 * コンストラクタ (デフォルト)
	 */
	public Ranking() {
		_score = new Score();
	}

	/**
	 * コンストラクタ (引数あり)
	 *
	 * @param docFreq (Map)
	 * @param maxDocs (int)
	 */
	public Ranking(Map<String, Integer> docFreq, int maxDocs) {
		this.docFreq = docFreq;
		this.maxDocs = maxDocs;
		_score = new Score();
	}

	//-----------------------------------------------------
	//get・set メソッドの定義
	//-----------------------------------------------------

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

	//-----------------------------------------------------
	//publicメソッドの定義
	//-----------------------------------------------------

	/**
	 * solrScoreメソッド (solrのスコアデータを修正し、格納する)
	 *
	 * @param data
	 */
	public void solrScore(String data) {

		//queryの重み変数
		float queryWeight = 0.0f;
		//fieldの重み変数
		float fieldWeight = 0.0f;

		//改行で分割する
		_data = data.split("\n");

		for (int i = 0; i < _data.length; i++) {

			//一行毎に処理をする
			String line = _data[i].trim();
			String[] str = line.split("=");

			//queryWeightの部分を探す
			if (str[1].indexOf("queryWeight") != -1) {
				//Keyword判定
				String key = extractKeyword(line);
				//keyの値が空文字で無ければ、値を格納する (アカウント判断部分を削除するためにif文を使う)
				if (key != "") {
					//IDFを計算し、格納する
					float idf = idf(this.maxDocs, this.docFreq.get(key));
					//Normを取得し、格納する
					float norm = extractWeight(i+2);
					//queryの重み計算
					queryWeight = idf * norm;
				}
			}

			//fieldWeightの部分を探す
			if (str[1].indexOf("fieldWeight") != -1) {
				//Keyword判定
				String key = extractKeyword(line);
				//keyの値が空文字で無ければ、値を格納する (アカウント判断部分を削除するためにif文を使う)
				if (key != "") {
					//TFを取得し、格納する
					float tf = extractWeight(i+1);
					//IDFを計算し、格納する
					float idf = idf(this.maxDocs, this.docFreq.get(key));
					//Normを取得し、格納する
					float norm = extractWeight(i+3);
					//fieldの重み計算
					fieldWeight = tf * idf * norm;
					//スコアクラスに総合スコアの重みを格納
					_score.setWeight(queryWeight * fieldWeight);
				}
			}

			//coordの部分を探す
			if (str[1].indexOf("coord") != -1) {
				//coordを取得し、格納する
				_score.setCoord(extractWeight(i));
			}

		}
	}

	/**
	 * scoreメソッド
	 *
	 * @param num
	 * @return
	 */
	public float score(int num) {
		return 0.0f;
	}

	/**
	 * rankingメソッド
	 * ランキングの高い順から格納される
	 *
	 * @return MapをListでまとめたモノ
	 */
	public List<Map<String, Float>> ranking() {
		return null;
	}

	//-----------------------------------------------------
	//staticメソッドの定義
	//-----------------------------------------------------

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
	 * Ranking.fieldでしていした、Solrのインデックスフィールドの部分だけ調べる
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

	/**
	 * extractNumメソッド
	 *
	 * @param indexNum
	 * @return
	 */
	static float extractWeight(int indexNum) {
		String line = _data[indexNum].trim();
		String[] array = line.split("=");
		return Float.valueOf(array[0]).floatValue();
	}
}
