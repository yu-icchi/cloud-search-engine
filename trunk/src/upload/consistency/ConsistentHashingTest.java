package upload.consistency;

import java.util.ArrayList;
import java.util.List;

public class ConsistentHashingTest {

	/**
	 * メイン
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		ConsistentHashing hash = new ConsistentHashing();

		hash.addNode("localhost:8081", "localhost:8082", "localhost:8083", "localhost:8084", "localhost:8085");
		hash.nodeList();

		List<String> list = new ArrayList<String>();
		list.add("A");
		list.add("B");
		list.add("C");
		list.add("D");
		list.add("E");
		list.add("F");

		for (String str : list) {
			System.out.println(str);
			String node = hash.searchNode(str);
			System.out.println(node);
			String nextNode = hash.nextNode(node);
			System.out.println("nextNode: " + nextNode);
			System.out.println("next+nextNode: " + hash.nextNode(nextNode));
		}

		hash.addNode("localhost:8086", "localhost:8087", "localhost:8088", "localhost:8089", "localhost:8090");
		hash.nodeList();

		for (String str : list) {
			System.out.println(str);
			String node = hash.searchNode(str);
			System.out.println(node);
			String nextNode = hash.nextNode(node);
			System.out.println("nextNode: " + nextNode);
			System.out.println("next+nextNode: " + hash.nextNode(nextNode));
		}

		System.out.println("\nnextNode: " + hash.nextNode("localhost:8081"));
	}
}
