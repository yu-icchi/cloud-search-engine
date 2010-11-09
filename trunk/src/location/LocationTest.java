package location;

import java.util.List;
import java.util.Map;

import location.query.QueryConverter;
import location.query.parser.ParseException;

public class LocationTest {

	/**
	 * @param args
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws ParseException {

		//クエリーの設定
		String query = "solr | ipod | 前田";

		//クエリーの解析
		QueryConverter queryConverter = new QueryConverter();
		queryConverter.parser(query);

		//データベース(Locationサーバ)にアクセス
		Location location = new Location();
		//正規化したクエリーを与える
		location.query(queryConverter.getQuery());
		//クエリーのタームを与える
		Map<String, Object> map = location.get(queryConverter.getTermList());

		//結果
		System.out.println("LocationTest: " + map);
		List<String> list = (List<String>) map.get("url");
		System.out.println("maxDocs: " + map.get("maxDocs"));
		System.out.println("url: " + list);
		System.out.println("maxDocs: " + map.get("docFreq"));
	}

}
