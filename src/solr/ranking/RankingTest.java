package solr.ranking;

public class RankingTest {

	public static void main(String[] args) {

		System.out.println(DistributedSimilarity.extractKeywordList("fieldWeight(text:\"前田 敦子\" in 1)"));

		System.out.println(DistributedSimilarity.idf(4, 1));
		System.out.println(DistributedSimilarity.idf(3, 1));
	}

}
