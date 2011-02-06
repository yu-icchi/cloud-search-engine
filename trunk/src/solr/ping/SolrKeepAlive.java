package solr.ping;

public class SolrKeepAlive {

	public static void main(String[] args) throws InterruptedException {
		PingTask task = new PingTask();
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		Thread.sleep(1000L * 60);
	}
}
