//---------------------------------------------------------
//Queryクラスのテスト
//---------------------------------------------------------
package location.query;

public class LocationQueryConverterTest {

	public static void main(String[] args) {

		//クエリー
		String q = "((前田敦子^1.5　篠田麻里子) | (大島優子　板野友美)) -AKB48";

		LocationQueryConverter query = new LocationQueryConverter();

		try {

			//パーザーにクエリーを引数として与える
			query.parser(q);

			//抽出したタームのリスト
			System.out.println(query.getTermList());
			//正規化したクエリー
			System.out.println(query.getQuery());

		} catch (Exception e) {

			//エラーを表示させる
			System.out.println("Error");

		}
	}
}
