package upload.consistency;

import java.util.ArrayList;
import java.util.List;

import client.config.XMLConfig;

public class ConsistentHashingTest {

	/**
	 * メイン
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		List<String> list = new ArrayList<String>();
		list.add("A"); list.add("B"); list.add("C"); list.add("D"); list.add("E"); list.add("F");
		list.add("G"); list.add("H"); list.add("I"); list.add("J"); list.add("K"); list.add("L");
		list.add("M"); list.add("N"); list.add("O"); list.add("P"); list.add("Q"); list.add("R");
		list.add("S"); list.add("T"); list.add("U"); list.add("V"); list.add("W"); list.add("X");
		list.add("Y"); list.add("Z");

		XMLConfig config = new XMLConfig("demo/gse-config.xml");
		List<String> nodes = config.getNodes("node");
		System.out.println(nodes);

		/*
		ConsistentHashing hash = new ConsistentHashing();
		hash.addNode(nodes);
		hash.nodeList();

		for (String str : list) {
			String node = hash.searchNode(str);
			System.out.println(str + " : " + node);
		}

		System.out.println();
		*/

		//仮想ノードなし
		ConsistentHashing2 hash2 = new ConsistentHashing2();
		hash2.addNode(nodes);
		hash2.nodeList();
		for (String str : list) {
			System.out.println(str + " : " + hash2.searchNode(str));
		}


	}
}
