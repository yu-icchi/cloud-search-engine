package solr.ranking;

public class RankingTest {

	public static void main(String[] args) {

		System.out.println(Ranking.getField());

		Ranking.setField("id");

		System.out.println(Ranking.getField());
	}

}
