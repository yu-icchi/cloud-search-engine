//---------------------------------------------------------
//Scoreクラス (未完成)
//---------------------------------------------------------
package solr.ranking;

public class DistributedScore {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	//
	private float coord = 0.0f;
	//
	private float weight = 0.0f;
	//
	private float queryNormSum = 0.0f;

	//-----------------------------------------------------
	//get・setメソッド
	//-----------------------------------------------------

	/**
	 *
	 * @param coord
	 */
	public void setCoord(float coord) {
		this.coord = coord;
	}

	/**
	 *
	 * @return
	 */
	public float getCoord() {
		return coord;
	}

	/**
	 *
	 * @param weight
	 */
	public void setWeight(float weight) {
		this.weight += weight;
	}

	/**
	 *
	 * @return
	 */
	public float getWeight() {
		return weight;
	}

	/**
	 *
	 * @param queryNormSum
	 */
	public void setQueryNormSum(float queryNormSum) {
		this.queryNormSum = queryNormSum;
	}

	/**
	 *
	 * @return
	 */
	public float getQueryNormSum() {
		return queryNormSum;
	}

	//-----------------------------------------------------
	//publicメソッド
	//-----------------------------------------------------

	/**
	 * scoreメソッド (スコア計算をして結果を返す)
	 *
	 * @return
	 */
	public float score() {
		return (float) (this.coord * (1.0 / Math.sqrt(this.queryNormSum)) * this.weight);
	}

}
