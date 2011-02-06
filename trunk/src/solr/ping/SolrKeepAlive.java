package solr.ping;

public class SolrKeepAlive {

	public static void main(String[] args) {
		PingTask check = new PingTask();
		check.start();
		check.addServer("192.168.220.131:6365");
		check.addServer("192.168.220.132:6365");
		check.addServer("192.168.220.133:6365");
		try {
			while (true);
		} catch (Exception e) {
			check.stop();
		}
	}
}
