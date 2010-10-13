//---------------------------------------------------------
//Rankingクラス (未完成)
//
//Solrの検索時にdebugQuery=onにし、ランキング計算の情報を得る
//その内容からランキングを修正するプログラムである
//---------------------------------------------------------
package solr;

import java.util.Map;

public class Ranking {

	//debugQueryの構造データを格納
	static String[] _data;

	//IDFの値を計算するためのデータ
	static Map<String, Integer> _docFreq;
	static int _maxDocs;

	//スコアオブジェクト
	Score _score;

	/**----------------------------------------------------
	 * コンストラクタ
	 * --------------------------------------------------*/
	public Ranking() {
		_score = new Score();
	}

	/**----------------------------------------------------
	 * コンストラクタ (引数あり)
	 * ----------------------------------------------------
	 * @param docFreq
	 * @param maxDocs
	 * --------------------------------------------------*/
	public Ranking(Map<String, Integer> docFreq, int maxDocs) {
		_docFreq = docFreq;
		_maxDocs = maxDocs;
		_score = new Score();
	}

	/**----------------------------------------------------
	 * docFreqメソッド　(setメソッド)
	 * ----------------------------------------------------
	 * @param num
	 * --------------------------------------------------*/
	public void docFreq(Map<String, Integer> map) {
		_docFreq = map;
	}

	/**----------------------------------------------------
	 * maxDocsメソッド　(setメソッド)
	 * ----------------------------------------------------
	 * @param num
	 * --------------------------------------------------*/
	public void maxDocs(int num) {
		_maxDocs = num;
	}

	/**----------------------------------------------------
	 * debugDataメソッド (データを分割して保存)
	 * ----------------------------------------------------
	 * @param data
	 * --------------------------------------------------*/
	public void debugData(String data) {
		_data = data.split("\n");
		for (int i = 1; i < _data.length; i++) {
			String line = _data[i].trim();
			String[] a = line.split("=");

			if (a[1].indexOf("queryWeight") != -1) {

				//Keyword判断
				String[] sp = line.split("text:");
				System.out.println(sp[1]);

				//queryWeight idf
				String line2 = _data[i+1].trim();
				String[] a2 = line2.split("=");
				//System.out.println("queryWeight idf : " + _data[i+1].trim());
				_score.queryWeight_idf = Double.valueOf(a2[0]).floatValue();
				//System.out.println(a2[0]);

				//queryWeight queryNorm
				String line3 = _data[i+2].trim();
				String[] a3 = line3.split("=");
				//System.out.println("queryWeight queryNorm : " + _data[i+2].trim());
				_score.queryWeight_queryNurm = Double.valueOf(a3[0]).floatValue();
				//System.out.println(a3[0]);
			}

			if (a[1].indexOf("fieldWeight") != -1) {

				//Keyword判断

				//fieldWeight tf
				String line4 = _data[i+1].trim();
				String[] a4 = line4.split("=");
				//System.out.println("fieldWeight tf : " + _data[i+1].trim());
				_score.fieldWeight_tf = Double.valueOf(a4[0]).floatValue();
				//System.out.println(a4[0]);

				//fieldWeight idf
				String line5 = _data[i+2].trim();
				String[] a5 = line5.split("=");
				//System.out.println("fieldWeight idf : " + _data[i+2].trim());
				_score.fieldWeight_idf = Double.valueOf(a5[0]).floatValue();
				//System.out.println(a5[0]);

				//fieldWeight fieldNorm
				String line6 = _data[i+3].trim();
				String[] a6 = line6.split("=");
				//System.out.println("fieldWeight fieldNorm : " + _data[i+3].trim());
				_score.fieldWeight_fieldNorm = Double.valueOf(a6[0]).floatValue();
				//System.out.println(a6[0]);
			}

			if (a[1].indexOf("coord") != -1) {
				//coordを取得する
				String coordStr = _data[_data.length-1].trim();
				String[] coordStrArr = coordStr.split("=");
				//System.out.println("coord : " + coordStrArr[0]);
				_score.coord = Double.valueOf(coordStrArr[0]).floatValue();
			}
		}

	}

	/**----------------------------------------------------
	 * idfメソッド (グローバルIDFを付けるための計算式)
	 * ----------------------------------------------------
	 * @param maxDocs
	 * @param docFreq
	 * @return
	 * --------------------------------------------------*/
	public static float idf(int maxDocs, int docFreq) {
		return (float) (Math.log(maxDocs / (double) (docFreq + 1)) + 1.0);
	}

	/**----------------------------------------------------
	 * scoreメソッド (スコアを返す)
	 * ----------------------------------------------------
	 * @return
	 * --------------------------------------------------*/
	public float score() {
		System.out.println("queryWeight" + _score.queryWeight());
		System.out.println("fieldWeight" + _score.fieldWeight());
		return (_score.fieldWeight() * _score.queryWeight()) * _score.coord;
	}
}
