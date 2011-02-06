//---------------------------------------------------------
//DistributedSimilarityクラス
//
//Solrの検索時にdebugQuery=onにし、スコア計算の情報を取得する
//その内容からランキングを修正する
//---------------------------------------------------------
package solr.ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	//static float sumOfSquaredWeightsValue2 = 0.0f;
	static Map<String, Float> boostMap = new HashMap<String, Float>();

	static int maxOverlap = 0;

	//static List<Float> idfList = new ArrayList<Float>();

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
	@SuppressWarnings("unchecked")
	public void solrScoreImport(Map data) {

		//スコアリスト
		scoreList = new ArrayList<Map<String, DistributedScore>>();

		//インデックスIDを取得する
		Iterator it = data.keySet().iterator();

		int i = 0;

		//複数のDocumentに対して処理する
		while (it.hasNext()) {
			String id = (String) it.next();
			//System.out.println(data.get(id));
			//スコアクラスのMap
			Map<String, DistributedScore> map = new HashMap<String, DistributedScore>();
			//解析し、修正したスコアを返す
			//System.out.println(data.get(id));
			DistributedScore score = scoreAnalyze((String) data.get(id), i);
			//Mapに格納
			map.put(id, score);
			//スコアリストに追加する
			scoreList.add(map);
			i++;
		}

		//sumOfSqueredWeightsの計算
		//sumOfSquaredWeightsValue = sumOfSquaredWeights();
		//System.out.println(sumOfSquaredWeightsValue2);
		//System.out.println("QueryNormSum:" + (float) (1.0 / Math.sqrt(sumOfSquaredWeightsValue2)));

	}

	/**
	 * solrScoreメソッド (SolrからScoreデータを持ってくる)
	 *
	 * @param datas
	 */
	public void solrScoreImport(List<Map<String, String>> datas) {

		//スコアリスト
		scoreList = new ArrayList<Map<String, DistributedScore>>();
		//スコア修正
		for (Map<String, String> data : datas) {
			//インデックスIDを取得する
			Iterator<String> it = data.keySet().iterator();
			int i = 0;
			//複数のDocumentに対して処理する
			while (it.hasNext()) {
				//ID取得
				String id = it.next();
				//スコアクラスのMap
				Map<String, DistributedScore> map = new HashMap<String, DistributedScore>();
				DistributedScore score = scoreAnalyze(data.get(id), i);
				//Mapに格納
				map.put(id, score);
				//スコアリストに追加する
				scoreList.add(map);
				i++;
			}
		}
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
	public List<Map<String, Object>> ranking() {
		//出力結果を返す変数
		List<Map<String, Object>> resultList = new  ArrayList<Map<String, Object>>();

		for (int i = 0; i < scoreList.size(); i++) {
			Map<String, DistributedScore> map = scoreList.get(i);
			Iterator<String> it = map.keySet().iterator();
			//次の要素へ
			while (it.hasNext()) {
				//idを取り出す
				String id = it.next();
				DistributedScore score = map.get(id);
				score.setQueryNormSum(sumOfSquaredWeightsValue);
				score.setMaxOverlap(maxOverlap);
				//System.out.println("maxOverlap: " + maxOverlap);
				//System.out.println("CoordNum: " + score.coord());
				//System.out.println("QueryNorm:" + (float) (1.0 / Math.sqrt((double) score.getQueryNormSum())));
				//System.out.println(id + " : " + score.score());
				Map<String, Object> resultMap = new HashMap<String, Object>();
				resultMap.put("id", id);
				resultMap.put("score", score.score());
				resultList.add(resultMap);
			}
		}

		//ソート
		Collections.sort(resultList, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> map1, Map<String, Object> map2) {
				String p1 = map1.get("score").toString();
				String p2 = map2.get("score").toString();
				return p2.compareTo(p1);
			}
		});

		//結果を返す
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
	static DistributedScore scoreAnalyze(String data, int flag) {

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

		//改行で分割する
		_data = data.split("\n");

		for (int i = 1; i < _data.length; i++) {

			//一行毎に処理をする
			String line = _data[i].trim();
			String[] str = line.split("=");

			//System.out.println(line);

			//queryWeightの部分を探す
			if (str[1].indexOf("queryWeight") != -1) {
				//Keyword判定
				List<String> key = extractKeywordList(line);
				//keyの値が空文字で無ければ、値を格納する (アカウント判断部分を削除するためにif文を使う)
				if (key != null) {
					String tmp = _data[i+1].trim();
					//boost値が存在するか調べる
					if (tmp.indexOf("boost") != -1) {
						boost = extractWeight(i+1);
						//System.out.println("boost:" + boost);
						for (String k_str : key) {
							boostMap.put(k_str, boost);
						}
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
					idf = 0.0f;
					for (String term : extractKeywordList(line)) {
						idf += idf(DistributedSimilarity.maxDocs, DistributedSimilarity.docFreq.get(term));
					}
					//idfList.add(idf);
					//System.out.println("Ranking-idf: " + idf);
					if (flag == 0) {
						sumOfSquaredWeightsValue += (idf * boost) * (idf * boost);
					}
					//System.out.println((idf * boost) * (idf * boost));
					//idf = idf(DistributedSimilarity.maxDocs, DistributedSimilarity.docFreq.get(key));
					//System.out.println("idf:" + idf);
					//Normを取得し、格納する
					norm = extractWeight(i+3);
					//System.out.println("norm:" + norm);
					//スコアクラスに総合スコアの重みを格納
					score.setWeight(tf * (idf * idf) * boost * norm);
					//System.out.println("Weight1:" + score.getWeight());
					//System.out.println("QueryNormSum:" + (float) (1.0 / Math.sqrt(score.getQueryNormSum())));
					//オーバーラップ変数
					maxOverlap++;
				}
			}

			//coordの部分を探す、無ければ"1"にする
			if (str[1].indexOf("coord") != -1) {
				float coord = extractWeight(i);
				//System.out.println("Coord:" + extractCoord(_data[i]));
				score.setCoord(coord);
				score.setOverlap(extractCoord(_data[i]));
			} else {
				score.setCoord(1);
			}

		}

		//coordを計算し、格納する
		//score.setCoord((float) overlap / maxOverlap);
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

	static List<String> idfTermList(String line) {
		System.out.println(extractKeyword(line));
		return null;
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
				System.out.println(id + " : " + boostMap.get(id));
			}
			//Σ(idf(t)*t.getBoost)^2の計算
			sum += (idf * boost) * (idf * boost);
		}

		/*
		for (float idf : idfList) {
			//Σ(idf(t)*t.getBoost)^2の計算
			sum += (idf * boost) * (idf * boost);
		}
		*/

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
	 * extractCoordメソッド
	 *
	 * @param line
	 * @return
	 */
	static int extractCoord(String line) {
		//coord(◯/◯)の分子を見つける
		Pattern p = Pattern.compile("\\(([0-9]/[0-9])\\)");
		Matcher m = p.matcher(line);
		if (m.find()) {
			//"/"で区切る
			String[] str = m.group(1).split("/");
			//分子だけを取り出す
			return Integer.valueOf(str[0]);
		}
		//無ければ"1"を返す
		return 1;
	}

	/**
	 * extractKeywordListaメソッド
	 *
	 * @param line
	 * @return
	 */
	static List<String> extractKeywordList(String line) {
		//出力用のList変数
		List<String> list = new ArrayList<String>();
		//パターン【fieldWeight(text:"◯◯ ◯◯ ◯◯ ◯◯" in ◯)】
		Pattern p = Pattern.compile("\\((" + DistributedSimilarity.field +":\"*([\\w]*[\\p{InBasicLatin}]*[\\p{InHiragana}]*[\\p{InKatakana}]*[\\p{InCJKUnifiedIdeographs}]*)*\"*)");
		Matcher m = p.matcher(line);
		if (m.find()) {
			//コロンかスペースかハットで区切る
			String[] str = m.group(1).split(":|\\s|\\^|\\)|\"");
			for (int i = 1; i < str.length; i++) {
				//"in"の文字で終了
				if (str[i].equals("in")) {
					break;
				}
				//何も無い文字は飛ばす
				if (str[i].equals("")) {
					continue;
				}
				//必要な文字だけ格納
				list.add(str[i]);
			}
		}

		return list;
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
