package upload.consistency;

import java.io.File;
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

		/*
		List<String> list = new ArrayList<String>();
		list.add("A"); list.add("B"); list.add("C"); list.add("D"); list.add("E"); list.add("F");
		list.add("G"); list.add("H"); list.add("I"); list.add("J"); list.add("K"); list.add("L");
		list.add("M"); list.add("N"); list.add("O"); list.add("P"); list.add("Q"); list.add("R");
		list.add("S"); list.add("T"); list.add("U"); list.add("V"); list.add("W"); list.add("X");
		list.add("Y"); list.add("Z");
		*/

		File file = new File("aozora");

		String[] list = file.list();

		XMLConfig config = new XMLConfig("demo/gse-config.xml");
		List<String> nodes = config.getNodes("node");
		System.out.println(nodes);


		ConsistentHashing hash = new ConsistentHashing();
		hash.addNode(nodes);
		hash.nodeList();

		List<String> node1 = new ArrayList<String>();
		List<String> node2 = new ArrayList<String>();
		List<String> node3 = new ArrayList<String>();

		for (int i = 0; i < 400; i++) {
			String node = hash.searchNode(list[i]);
			if (node.equals("192.168.220.131")) {
				node1.add(list[i]);
			}
			if (node.equals("192.168.220.132")) {
				node2.add(list[i]);
			}
			if (node.equals("192.168.220.133")) {
				node3.add(list[i]);
			}
		}

		System.out.println("192.168.220.131 : " + node1.size());
		System.out.println("192.168.220.132 : " + node2.size());
		System.out.println("192.168.220.133 : " + node3.size());

		hash.addNode("192.168.220.134");
		hash.nodeList();

		//System.out.println(hash.prevNode("192.168.220.134"));

		List<String> node11 = new ArrayList<String>();
		List<String> node22 = new ArrayList<String>();

		for (String str : node1) {
			String node = hash.searchNode(str);
			if (node.equals("192.168.220.131")) {
				node11.add(str);
			}
			if (node.equals("192.168.220.134")) {
				node22.add(str);
			}
		}

		System.out.println("192.168.220.131 : " + node11.size());
		System.out.println("192.168.220.134 : " + node22.size());

		System.out.println();

		//List<String> node4 = new ArrayList<String>();

		for (int i = 400; i < 800; i++) {
			String node = hash.searchNode(list[i]);
			if (node.equals("192.168.220.131")) {
				node11.add(list[i]);
			}
			if (node.equals("192.168.220.132")) {
				node2.add(list[i]);
			}
			if (node.equals("192.168.220.133")) {
				node3.add(list[i]);
			}
			if (node.equals("192.168.220.134")) {
				node22.add(list[i]);
			}
		}

		System.out.println("192.168.220.131 : " + node11.size());
		System.out.println("192.168.220.132 : " + node2.size());
		System.out.println("192.168.220.133 : " + node3.size());
		System.out.println("192.168.220.134 : " + node22.size());

		hash.addNode("192.168.220.135");
		hash.nodeList();

		List<String> node33 = new ArrayList<String>();
		List<String> node44 = new ArrayList<String>();

		int i = 0;

		for (String str : node2) {
			String node = hash.searchNode(str);
			if (node.equals("192.168.220.132")) {
				node33.add(str);
			}
			if (node.equals("192.168.220.135")) {
				node44.add(str);
			}
			if (node.equals("192.168.220.131")) {
				System.out.println(node + " : " + str);
			}
			if (node.equals("192.168.220.133")) {
				System.out.println(node + " : " + str);
			}
			if (node.equals("192.168.220.134")) {
				//System.out.println(node + " : " + str);
				i++;
			}
		}

		System.out.println("192.168.220.132 : " + node33.size());
		System.out.println("192.168.220.135 : " + node44.size());

		System.out.println(i);

		/*
		//仮想ノードなし
		ConsistentHashing2 hash2 = new ConsistentHashing2();
		hash2.addNode(nodes);
		hash2.nodeList();
		for (String str : list) {
			System.out.println(str + " : " + hash2.searchNode(str));
		}
		*/

	}
}
