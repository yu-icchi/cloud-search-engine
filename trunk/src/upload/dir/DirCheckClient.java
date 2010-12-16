package upload.dir;

public class DirCheckClient {

	public static void main(String[] args) {
		DirCheckDaemon check = new DirCheckDaemon("demo");
		check.start();
		try {
			//10sec
			Thread.sleep(60 * 1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		check.stop();
	}
}
