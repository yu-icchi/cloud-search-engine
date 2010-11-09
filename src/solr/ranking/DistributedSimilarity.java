//---------------------------------------------------------
//DistributedSimilarityクラス
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


public class DistributedSimilarity {

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

	//queryNormを計算する時に使用する変数 (呼び出しを1回にし計算コストを抑える)
	static float sumOfSquaredWeightsValue = 0.0f;
	static Map<String, Float> boostMap = new HashMap<String, Float>();

	//スコアリスト
	static List<Map<String, DistributedScore>> scoreList;

	//-----------------------------------------------------
	//コンストラクタの定義
	//-----------------------------------------------------

	/**
	 * コンストラクタ (デフォルト)
	 */
	public DistributedSimilarity() {

	}

	/**
	 * コンストラクタ (引数あり)
	 *
	 * @param docFreq (Map)
	 * @param maxDocs (int)
	 */
	public DistributedSimilarity(Map<String, Integer> docFreq, int maxDocs) {
		DistributedSimilarity.docFreq = docFreq;
		DistributedSimilarity.maxDocs = maxDocs;
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
		DistributedSimilarity.maxDocs = maxDocs;
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
		DistributedSimilarity.docFreq = docFreq;
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
		DistributedSimilarity.field = field;
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
	@SuppressWarnings("rawtypes")
	public void solrScoreImport(Map data) {

		//スコアリスト
		scoreList = new ArrayList<Map<String, DistributedScore>>();

		//インデックスIDを取得する
		Iterator it = data.keySet().iterator();

		//複数のDocumentに対して処理する
		while (it.hasNext()) {
			String id = (String) it.next();
			//System.out.println(data.get(id));
			//スコアクラスのMap
			Map<String, DistributedScore> map = new HashMap<String, DistributedScore>();
			//解析し、修正したスコアを返す
			//System.out.println(data.get(id));
			DistributedScore score = scoreAnalyze((String) data.get(id));
			//Mapに格納
			map.put(id, score);
			//スコアリストに追加する
			scoreList.add(map);
		}

		//sumOfSqueredWeightsの計算
		sumOfSquaredWeightsValue = sumOfSquaredWeights();
		//System.out.println(sumOfSquaredWeights());

	}

	/**
	 * scoreメソッド
	 *
	 * @param num
	 * @return
	 */
	public Map<String, DistributedScore> score(int num) {
		return scoreList.get(num);
	}

	/**
	 * rankingメソッド (付焼刃)
	 * ランキングの高い順から格納される
	 *
	 * @return MapをListでまとめたモノ
	 */
	public List<Map<String, Float>> ranking() {
		//出力結果を返す変数
		List<Map<String, Float>> resultList = new  ArrayList<Map<String, Float>>();

		for (int i = 0; i < scoreList.size(); i++) {
			Map<String, DistributedScore> map = scoreList.get(i);
			Iterator<String> it = map.keySet().iterator();
			//次の要素へ
			while (it.hasNext()) {
				//idを取り出す
				String id = it.next();
				DistributedScore score = map.get(id);
				score.setQueryNormSum(sumOfSquaredWeightsValue);
				System.out.println("QueryNorm:" + (float) (1.0 / Math.sqrt((double) score.getQueryNormSum())));
				//System.out.println(id + " : " + score.score());
				Map<String, Float> resultMap = new HashMap<String, Float>();
				resultMap.put(id, score.score());
				resultList.add(resultMap);
			}
		}

		return resultList;
	}

	//-----------------------------------------------------
	//staticメソッドの定義
	//-----------------------------------------------------

	/**
	 * scoreAnalyzeメソッド (solrのスコアデータを解析する)
	 *
	 * @param data
	 * @return DistributedScore
	 */
	static DistributedScore scoreAnalyze(String data) {

		//スコアクラス
		DistributedScore score = new DistributedScore();

		//スコア計算に必要な変数
		//tf値
		float tf = 0.0f;
		//idf値
		float idf = 0.0f;
		//デフォルトで"1.0"にしておく
		float boost = 1.0f;
		//norm値
		float norm = 0.0f;

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
					String tmp = _data[i+1].trim();
					//boost値が存在するか調べる
					if (tmp.indexOf("boost") != -1) {
						boost = extractWeight(i+1);
						//System.out.println("boost:" + boost);
						boostMap.put(key, boost);
					}
				}

			}

			//fieldWeightの部分を探す
			if (str[1].indexOf("fieldWeight") != -1) {
				//Keyword判定
				String key = extractKeyword(line);
				//keyの値が空文字で無ければ、値を格納する (アカウント判断部分を削除するためにif文を使う)
				if (key != "") {
					//TFを取得し、格納する
					tf = extractWeight(i+1);
					//System.out.println("tf:" + tf);
					//IDFを計算し、格納する
					idf = idf(DistributedSimilarity.maxDocs, DistributedSimilarity.docFreq.get(key));
					//System.out.println("idf:" + idf);
					//Normを取得し、格納する
					norm = extractWeight(i+3);
					//System.out.println("norm:" + norm);
					//スコアクラスに総合スコアの重みを格納
					score.setWeight(tf * (idf * idf) * boost * norm);
					//System.out.println("Weight1:" + score.getWeight());
					//System.out.println("QueryNormSum:" + (float) (1.0 / Math.sqrt(score.getQueryNormSum())));
					//オーバーラップ変数
					overlap++;
				}
			}

		}

		//coordを計算し、格納する
		score.setCoord((float) overlap / maxOverlap);
		//System.out.println("Coord:" + score.getCoord());

		//System.out.println("Weight2:" + score.getWeight());

		//ひとつのDocumentのスコア
		return score;
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
	 * sumOfSqueredWeightsメソッド
	 *
	 * @return
	 */
	static float sumOfSquaredWeights() {

		//初期値
		float sum = 0.0f;
		//boostの初期値
		float boost = 1.0f;

		//インデックスIDを取得する
		Iterator<String> it = docFreq.keySet().iterator();

		//複数のDocumentに対して処理する
		while (it.hasNext()) {
			//クエリーのキーワードを取り出す
			String id = it.next();
			//idf計算
			float idf = idf(maxDocs, docFreq.get(id));
			//クエリー時に含まれるboost値を与える
			if (boostMap.get(id) != null) {
				boost = boostMap.get(id);
			}
			//Σ(idf(t)*t.getBoost)^2の計算
			sum += (idf * boost) * (idf * boost);
		}

		return sum;
	}

	/**
	 * extractKeywordメソッド
	 * Ranking.fieldでしていした、Solrのインデックスフィールドの部分だけ調べる
	 *
	 * @param line
	 * @return
	 */
	static String extractKeyword(String line) {
		//検索対象のフィールドを指定する 【queryWeight(text:◯◯◯) or queryWeight(text:◯◯◯^◯.◯)】【fieldWeight(text:◯◯◯ in ◯)】
		//正規表現で調べる、英数字・数字・ラテン文字・ひらがな・カタカナ・漢字
		Pattern p = Pattern.compile("\\((" + DistributedSimilarity.field +":[\\w]*[\\p{InBasicLatin}]*[\\p{InHiragana}]*[\\p{InKatakana}]*[\\p{InCJKUnifiedIdeographs}]*)" +
									"|(" + DistributedSimilarity.field +":[\\w]*[\\p{InBasicLatin}]*[\\p{InHiragana}]*[\\p{InKatakana}]*[\\p{InCJKUnifiedIdeographs}]*)^[0-9]\\.[0-9]\\)");
		Matcher m = p.matcher(line);
		if (m.find()) {
			//コロンかスペースかハットで区切る
			String[] str = m.group(1).split(":|\\s|\\^|\\)");
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
