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

	public float coord = 1.0f;

	static float _queryWeight = 0.0f;

	static float _fieldWeight = 0.0f;

	public static float _weight = 0.0f;

	/**
	 * weightメソッド (setメソッド)
	 *
	 * @param num
	 */
	public void weight(float num) {
		_weight += num;
	}

	/**
	 * weightメソッド (getメソッド)
	 *
	 * @return
	 */
	public float weight() {
		return _weight;
	}

	/**
	 * queryWeightメソッド
	 *
	 * @return
	 */
	public float queryWeight() {
		try {
			_queryWeight = queryWeight_idf * queryWeight_queryNurm;
			if (_queryWeight == 0.0f) {
				return 1.0f;
			}
			return _queryWeight;
		} catch (Exception e) {
			return 1.0f;
		}
	}

	/**
	 * fieldWeightメソッド
	 *
	 * @return
	 */
	public float fieldWeight() {
		try {
			_fieldWeight = fieldWeight_tf * fieldWeight_idf * fieldWeight_fieldNorm;
			if (_fieldWeight == 0.0f) {
				return 1.0f;
			}
			return _fieldWeight;
		} catch (Exception e) {
			return 1.0f;
		}
	}
}
