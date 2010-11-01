package location.query;


public class QueryTest {

	public static void main(String[] args) throws Exception {

		String q = "(ipod AND solr) OR (前田 NOT 大島)";

		Query query = new Query();

		query.parser(q);

		System.out.println(query.getTermList());
		System.out.println(query.getQuery());
	}
}
