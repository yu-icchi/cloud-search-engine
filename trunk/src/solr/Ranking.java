//---------------------------------------------------------
//Rankingクラス (未完成)
//
//Solrの検索時にdebugQuery=onにし、ランキング計算の情報を得る
//その内容からランキングを修正するプログラムである
//---------------------------------------------------------
package solr;

public class Ranking {

	//debugQueryの構造データを格納
	static String[] _data;

	//IDFの値を計算するためのデータ
	static int _decFreq;
	static int _maxDocs;

	/**----------------------------------------------------
	 * コンストラクタ
	 * --------------------------------------------------*/
	public Ranking() {

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
			//System.out.println(line);
			String a[] = line.split("=");
			//System.out.println("score : " + a[0]);
			if (a[1].indexOf("(MATCH) weight") != -1) {
				System.out.println(a[1].substring(21));
			}
			if (a[1].indexOf("idf") != -1) {
				//System.out.println(line);
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
		return 0.0f;
	}
}
