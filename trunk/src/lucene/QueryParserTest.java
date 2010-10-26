package lucene;


import org.apache.lucene.analysis.WhitespaceAnalyzer;
//import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;

public class QueryParserTest {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		QueryParser qp = new QueryParser("id", new WhitespaceAnalyzer());
		//QueryParser qp = new QueryParser("id", new CJKAnalyzer());
		Query query = qp.parse("(あいうえお AND ipod) AND account:user1");
		query.setBoost(4.0f);
		System.out.println(query.toString());
		System.out.println(query.getBoost());
	}
}
