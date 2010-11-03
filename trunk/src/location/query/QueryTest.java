//---------------------------------------------------------
//Queryクラスのテスト
//---------------------------------------------------------
package location.query;

public class QueryTest {

	public static void main(String[] args) throws Exception {

		//クエリー
		String q = "((前田 大島) OR (優子 前田)) -AKB48";

		Query query = new Query();

		//パーザーにクエリーを引数として与える
		query.parser(q);

		//抽出したタームのリスト
		System.out.println(query.getTermList());
		//正規化したクエリー
		System.out.println(query.getQuery());
	}
}
