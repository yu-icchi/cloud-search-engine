//---------------------------------------------------------
//Queryクラスのテスト
//---------------------------------------------------------
package location.query;

public class LocationQueryConverterTest {

	public static void main(String[] args) {

		//クエリー
		String q = "高城亜樹はAKB48メンバーである";

		LocationQueryConverter query = new LocationQueryConverter("sen");

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
