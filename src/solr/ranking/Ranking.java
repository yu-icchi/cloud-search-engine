//---------------------------------------------------------
//Rankingクラス (未完成)
//
//Solrの検索時にdebugQuery=onにし、スコア計算の情報を取得する
//その内容からランキングを修正する
//---------------------------------------------------------
package solr.ranking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Ranking {

	/**
	 * プロパティ
	 */
	//検索指定フィール
	private static String field = "text";

	//debugQueryの構造データを格納
	static String[] _data;

	//IDFの値を計算するためのデータ
	static int maxDocs;
	static Map<String, Integer> docFreq;

	//スコアリスト
	static List<Map<String, Float>> scoreList;

	//-----------------------------------------------------
	//コンストラクタの定義
	//-----------------------------------------------------

	/**
	 * コンストラクタ (デフォルト)
	 */
	public Ranking() {

	}

	/**
	 * コンストラクタ (引数あり)
	 *
	 * @param docFreq (Map)
	 * @param maxDocs (int)
	 */
	public Ranking(Map<String, Integer> docFreq, int maxDocs) {
		Ranking.docFreq = docFreq;
		Ranking.maxDocs = maxDocs;
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
		Ranking.maxDocs = maxDocs;
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
		Ranking.docFreq = docFreq;
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
	 * solrScoreメソッド (SolrからScoreデータを持ってくる)
	 *
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public void solrScore(Map data) {

		//スコアリスト
		scoreList = new ArrayList<Map<String, Float>>();

		//インデックスIDを取得する
		Iterator it = data.keySet().iterator();

		//複数のDocumentに対して処理する
		while (it.hasNext()) {
			String id = (String) it.next();
			//System.out.println(data.get(id));
			//スコアMap
			Map<String, Float> map = new HashMap<String, Float>();
			//解析し、修正したスコアを返す
			//System.out.println(data.get(id));
			float score = scoreAnalyze((String) data.get(id));
			//Mapに格納
			map.put(id, score);
			//スコアリストに追加する
			scoreList.add(map);
		}

	}

	/**
	 * scoreメソッド
	 *
	 * @param num
	 * @return
	 */
	public Map<String, Float> score(int num) {
		return scoreList.get(num);
	}

	/**
	 * rankingメソッド
	 * ランキングの高い順から格納される
	 *
	 * @return MapをListでまとめたモノ
	 */
	public List<Map<String, Float>> ranking() {
		return scoreList;
	}

	//-----------------------------------------------------
	//staticメソッドの定義
	//-----------------------------------------------------

	/**
	 * scoreAnalyzeメソッド (solrのスコアデータを解析する)
	 *
	 * @param data
	 */
	static float scoreAnalyze(String data) {

		//スコアクラス
		Score score = new Score();

		//queryの重み変数
		float queryWeight = 1.0f;
		//fieldの重み変数
		float fieldWeight = 1.0f;

		//coord計算用のoverlap
		int overlap = 0;
		//coord計算用のmaxOverlap
		int maxOverlap = docFreq.size();

		//改行で分割する
		_data = data.split("\n");

		for (int i = 1; i < _data.length; i++) {

			//一行毎に処理をする
			String line = _data[i].trim();
			String[] str = line.split("=");

			//System.out.println(str[1]);

			//queryWeightの部分を探す
			if (str[1].indexOf("queryWeight") != -1) {
				//Keyword判定
				String key = extractKeyword(line);
				//keyの値が空文字で無ければ、値を格納する (アカウント判断部分を削除するためにif文を使う)
				if (key != "") {
					//IDFを計算し、格納する
					float idf = idf(Ranking.maxDocs, Ranking.docFreq.get(key));
					//float idf = extractWeight(i+1);
					//Normを取得し、格納する
					float norm = extractWeight(i+2);
					//queryの重み計算
					queryWeight = idf * norm;
					//System.out.println(queryWeight);
				}
			}

			//fieldWeightの部分を探す
			if (str[1].indexOf("fieldWeight") != -1) {
				//Keyword判定
				String key = extractKeyword(line);
				//System.out.println(line);
				//System.out.println(key);
				//keyの値が空文字で無ければ、値を格納する (アカウント判断部分を削除するためにif文を使う)
				if (key != "") {
					//TFを取得し、格納する
					float tf = extractWeight(i+1);
					//IDFを計算し、格納する
					float idf = idf(Ranking.maxDocs, Ranking.docFreq.get(key));
					//float idf = extractWeight(i+2);
					//Normを取得し、格納する
					float norm = extractWeight(i+3);
					//fieldの重み計算
					fieldWeight = tf * idf * norm;
					//System.out.println(fieldWeight);
					//スコアクラスに総合スコアの重みを格納
					score.setWeight(queryWeight * fieldWeight);
					//オーバーラップ変数
					overlap++;
				}
			}

			/*
			//coordの部分を探す
			if (str[1].indexOf("coord") != -1) {
				//coordを取得し、格納する
				score.setCoord(extractWeight(i));
			}
			*/
		}

		//coordを計算し、格納する
		//System.out.println((float) overlap / maxOverlap + " = coord(" + overlap + "/" + maxOverlap + ")");
		score.setCoord((float) overlap / maxOverlap);

		//ひとつのDocumentのスコア
		//System.out.println(score.score());
		return score.score();
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
	 * Ranking.fieldでしていした、Solrのインデックスフィールドの部分だけ調べる
	 *
	 * @param line
	 * @return
	 */
	static String extractKeyword(String line) {
		//検索対象のフィールドを指定する 【queryWeight(text:◯◯◯)】【fieldWeight(text:◯◯◯ in ◯)】
		//英数字・数字・ラテン文字・ひらがな・カタカナ・漢字
		Pattern p = Pattern.compile("\\((" + Ranking.field +":[\\w]*[\\p{InBasicLatin}]*[\\p{InHiragana}]*[\\p{InKatakana}]*[\\p{InCJKUnifiedIdeographs}]*)\\)");
		Matcher m = p.matcher(line);
		if (m.find()) {
			//コロンかスペースで区切る
			String[] str = m.group(1).split(":|\\s");
			//Keywordだけを取り出す
			return str[1];
		}
		//無ければ空文字を返す
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
