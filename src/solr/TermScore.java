//---------------------------------------------------------
//TermScoreクラス (未完成)
//---------------------------------------------------------
package solr;

public class TermScore {

	public float queryWeight_idf;

	public float queryWeight_queryNorm;

	public float fieldWeight_termFreq;

	public float fieldWeight_idf;

	public float fieldWeight_fieldNorm;
}
