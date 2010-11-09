//---------------------------------------------------------
//Queryクラスのテスト
//---------------------------------------------------------
package location.query;

public class QueryConverterTest {

	public static void main(String[] args) throws Exception {

		//クエリー
		String q = "((前田^1.5　篠田) | (大島　板野)) -AKB48";

		QueryConverter query = new QueryConverter();

		//パーザーにクエリーを引数として与える
		query.parser(q);

		//抽出したタームのリスト
		System.out.println(query.getTermList());
		//正規化したクエリー
		System.out.println(query.getQuery());
	}
}
