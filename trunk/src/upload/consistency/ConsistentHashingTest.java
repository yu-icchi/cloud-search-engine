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

		hash.addNode("localhost:8983", "localhost:7574");
		hash.addNode("localhost:6365", "localhost:4541");
		hash.nodeList();
		System.out.println(hash.searchNode("H"));
		System.out.println(hash.searchNode("A"));
		System.out.println(hash.searchNode("C"));
		System.out.println(hash.searchNode("G"));

		System.out.println("next Node");
		hash.nextNode("localhost:6365");

	}
}
