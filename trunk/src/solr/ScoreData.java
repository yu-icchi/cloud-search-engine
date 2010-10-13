//---------------------------------------------------------
//ScoreDataクラス(未完成)
//
//Rankingクラスで使用するScoreのデータ構造管理用のクラス
//---------------------------------------------------------
package solr;

public class ScoreData {

	public float queryWeight;

	public float queryWeight_idf;

	public float queryWeight_queryNurm;

	public float fieldWeight;

	public float fieldWeight_tf;

	public float fieldWeight_idf;

	public float fieldWeight_fieldNorm;

}
