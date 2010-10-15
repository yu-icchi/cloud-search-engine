//---------------------------------------------------------
//Scoreクラス (未完成)
//---------------------------------------------------------
package solr.ranking;

public class Score {

	/**
	 * プロパティ
	 */
	//
	private float coord;
	//
	private float weight = 0.0f;

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
	 * @return
	 */
	public float score() {
		return this.weight * this.coord;
	}

}
