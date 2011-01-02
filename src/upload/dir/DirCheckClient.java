package upload.dir;

import client.config.XMLConfig;

public class DirCheckClient {

	public static void main(String[] args) {

		String dir = null;

		try {
			XMLConfig config = new XMLConfig("src/localsearchengine/config/lse-config.xml");
			dir = config.getElement("dir");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		DirCheckDaemon check = new DirCheckDaemon(dir);
		check.start();
		try {
			//10sec
			Thread.sleep(10 * 1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		check.stop();
	}
}
