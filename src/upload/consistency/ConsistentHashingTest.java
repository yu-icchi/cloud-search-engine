package upload.consistency;

public class ConsistentHashingTest {

	/**
	 * メイン
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		ConsistentHashing hash = new ConsistentHashing();

		hash.addNode("http://localhost:8081/solr/", "http://localhost:8082/solr/");
		hash.nodeList();
		System.out.println(hash.searchNode("demo/sample.text"));
		hash.nextNode(hash.searchNode("demo/sample.text"));
	}
}
