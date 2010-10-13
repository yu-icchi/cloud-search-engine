//---------------------------------------------------------
//ScoreDataクラス(未完成)
//
//Rankingクラスで使用するScoreのデータ構造管理用のクラス
//---------------------------------------------------------
package solr;

public class Score {

	public float queryWeight_idf;

	public float queryWeight_queryNurm;

	public float fieldWeight_tf;

	public float fieldWeight_idf;

	public float fieldWeight_fieldNorm;

	/**----------------------------------------------------
	 * queryWeightメソッド
	 * ----------------------------------------------------
	 * @return
	 * --------------------------------------------------*/
	public float queryWeight() {
		try {
			return queryWeight_idf * queryWeight_queryNurm;
		} catch (Exception e) {
			return 0.0f;
		}
	}

	/**----------------------------------------------------
	 * fieldWeightメソッド
	 * ----------------------------------------------------
	 * @return
	 * --------------------------------------------------*/
	public float fieldWeight() {
		try {
			return fieldWeight_tf * fieldWeight_idf * fieldWeight_fieldNorm;
		} catch (Exception e) {
			return 0.0f;
		}
	}
}
