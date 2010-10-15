package solr.ranking;

public class TermScore {

	/**
	 * プロパティ
	 */
	//
	private float queryWeight_idf;
	//
	private float queryWeight_norm;
	//
	private float fieldWeight_tf;
	//
	private float fieldWeight_idf;
	//
	private float fieldWeight_norm;


	/**
	 *
	 * @param queryWeight_idf
	 */
	public void setQueryWeight_idf(float queryWeight_idf) {
		this.queryWeight_idf = queryWeight_idf;
	}

	/**
	 *
	 * @return
	 */
	public float getQueryWeight_idf() {
		return queryWeight_idf;
	}

	/**
	 *
	 * @param queryWeight_norm
	 */
	public void setQueryWeight_norm(float queryWeight_norm) {
		this.queryWeight_norm = queryWeight_norm;
	}

	/**
	 *
	 * @return
	 */
	public float getQueryWeight_norm() {
		return queryWeight_norm;
	}

	/**
	 *
	 * @param fieldWeight_tf
	 */
	public void setFieldWeight_tf(float fieldWeight_tf) {
		this.fieldWeight_tf = fieldWeight_tf;
	}

	/**
	 *
	 * @return
	 */
	public float getFieldWeight_tf() {
		return fieldWeight_tf;
	}

	/**
	 *
	 * @param fieldWeight_idf
	 */
	public void setFieldWeight_idf(float fieldWeight_idf) {
		this.fieldWeight_idf = fieldWeight_idf;
	}

	/**
	 *
	 * @return
	 */
	public float getFieldWeight_idf() {
		return fieldWeight_idf;
	}

	/**
	 *
	 * @param fieldWeight_norm
	 */
	public void setFieldWeight_norm(float fieldWeight_norm) {
		this.fieldWeight_norm = fieldWeight_norm;
	}

	/**
	 *
	 * @return
	 */
	public float getFieldWeight_norm() {
		return fieldWeight_norm;
	}

}
