package solr.ping;

public class PingTask implements Runnable {

	@Override
	public void run() {
		try {
			System.out.println("Hello World");
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
