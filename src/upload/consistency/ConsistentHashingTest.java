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

		for (String str : list) {
			String node = hash.searchNode(str);
			//System.out.println(str + " : " + node);
			if (node.equals("192.168.220.131")) {
				node1.add(str);
			}
			if (node.equals("192.168.220.132")) {
				node2.add(str);
			}
			if (node.equals("192.168.220.133")) {
				node3.add(str);
			}
		}
		//String node = hash.searchNode("A");

		System.out.println("192.168.220.131 : " + node1.size());
		System.out.println("192.168.220.132 : " + node2.size());
		System.out.println("192.168.220.133 : " + node3.size());

		String kumo = hash.searchNode("demo/kumofs.txt");
		System.out.println(kumo);

		hash.addNode("192.168.220.134");
		hash.nodeList();

		List<String> node11 = new ArrayList<String>();
		List<String> node22 = new ArrayList<String>();

		for (String str : node1) {
			String node = hash.searchNode(str);
			//System.out.println(str + " : " + node);
			if (node.equals("192.168.220.131")) {
				node11.add(str);
			}
			if (node.equals("192.168.220.134")) {
				node22.add(str);
			}
		}

		System.out.println("192.168.220.131 : " + node11.size());
		System.out.println("192.168.220.134 : " + node22.size());

		hash.addNode("192.168.220.135");
		hash.nodeList();


		List<String> node33 = new ArrayList<String>();
		List<String> node44 = new ArrayList<String>();

		for (String str : node2) {
			String node = hash.searchNode(str);
			//System.out.println(str + " : " + node);
			if (node.equals("192.168.220.132")) {
				node33.add(str);
			}
			if (node.equals("192.168.220.135")) {
				node44.add(str);
			}
		}

		System.out.println("192.168.220.132 : " + node33.size());
		System.out.println("192.168.220.135 : " + node44.size());

		hash.addNode("192.168.220.136");
		hash.nodeList();

		List<String> node55 = new ArrayList<String>();
		List<String> node66 = new ArrayList<String>();

		for (String str : node2) {
			String node = hash.searchNode(str);
			//System.out.println(str + " : " + node);
			if (node.equals("192.168.220.132")) {
				node55.add(str);
			}
			if (node.equals("192.168.220.136")) {
				node66.add(str);
			}
		}

		System.out.println("192.168.220.132 : " + node55.size());
		System.out.println("192.168.220.136 : " + node66.size());

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
