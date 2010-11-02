package location.query;


public class QueryTest {

	public static void main(String[] args) throws Exception {

		String q = "前田　大島 | 渡辺 -松井";

		Query query = new Query();

		query.parser(q);

		System.out.println(query.getTermList());
		System.out.println(query.getQuery());
	}
}
