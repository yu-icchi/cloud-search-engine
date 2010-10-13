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

	//スコアオブジェクト
	Score _score;

	/**----------------------------------------------------
	 * コンストラクタ
	 * --------------------------------------------------*/
	public Ranking() {
		_score = new Score();
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
				String line2 = _data[i+1].trim();
				String[] a2 = line2.split("=");
				System.out.println("queryWeight idf : " + _data[i+1].trim());
				_score.queryWeight_idf = Double a2[1];
				System.out.println("queryWeight queryNorm : " + _data[i+2].trim());
			}
			if (a[1].indexOf("fieldWeight") != -1) {
				System.out.println("fieldWeight tf : " + _data[i+1].trim());
				System.out.println("fieldWeight idf : " + _data[i+2].trim());
				System.out.println("fieldWeight fieldNorm : " + _data[i+3].trim());
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
