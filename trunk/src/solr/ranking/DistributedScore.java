//---------------------------------------------------------
//Scoreクラス
//---------------------------------------------------------
package solr.ranking;

public class DistributedScore {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	//
	private float coord = 0.0f;
	//
	private int overlap = 0;
	//
	private int maxOverlap = 0;
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
	 * @param overlap
	 */
	public void setOverlap(int overlap) {
		this.overlap = overlap;
	}

	/**
	 *
	 * @return
	 */
	public int getOverlap() {
		return overlap;
	}

	/**
	 *
	 * @param maxOverlap
	 */
	public void setMaxOverlap(int maxOverlap) {
		this.maxOverlap = maxOverlap;
	}

	/**
	 *
	 * @return
	 */
	public int getMaxOverlap() {
		return maxOverlap;
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

	public float coord() {
		if (this.overlap != 0) {
			return (float) this.overlap / this.maxOverlap;
		} else {
			return this.coord;
		}
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
		return (float) (coord() * (1.0 / Math.sqrt(this.queryNormSum)) * this.weight);
	}

}
