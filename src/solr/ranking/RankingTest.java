package solr.ranking;

public class RankingTest {

	public static void main(String[] args) {

		System.out.println(DistributedSimilarity.extractKeywordList("fieldWeight(text:kumofs in 0)"));

		System.out.println(DistributedSimilarity.idf(3, 2));
		System.out.println(DistributedSimilarity.idf(3, 1));
	}

}
