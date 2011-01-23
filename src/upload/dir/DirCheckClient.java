package upload.dir;

import upload.consistency.ConsistentHashing2;
import client.config.XMLConfig;

public class DirCheckClient {

	public static void main(String[] args) {

		DirCheckDaemon check = null;
		try {
			XMLConfig config = new XMLConfig(args[0]);
			System.out.println(config.getHost2Port("location"));
			System.out.println(config.getElement("dir"));
			String dir = config.getElement("dir");

			ConsistentHashing2 hash = new ConsistentHashing2();
			hash.addNode(config.getNodes("node"));
			hash.nodeList();

			check = new DirCheckDaemon(dir);
			check.start();
			Thread.sleep(10 * 1000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		check.stop();
	}
}
