package solr.ping;

public class SolrKeepAlive {

	public static void main(String[] args) {
		PingTask check = new PingTask();
		check.start();
		check.addServer("192.168.220.137:8983");
		check.addServer("192.168.220.139:8983");
		check.addServer("192.168.220.140:8983");
		try {
			while (true);
		} catch (Exception e) {
			check.stop();
		}
	}
}
