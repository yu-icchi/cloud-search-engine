package upload.solrserver;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;

public class SolrManagerThread {
	public static void main(String[] args) {
		HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
		httpClient.getHostConfiguration().setHost("localhost", 8983, "http");
		String[] urlisToGet = {
			"/solr/admin",
			"/solr/admin/ping"
		};
		GetThread[] threads = new GetThread[urlisToGet.length];
		for (int i = 0; i < threads.length; i++) {
			GetMethod get = new GetMethod(urlisToGet[i]);
			get.setFollowRedirects(true);
			threads[i] = new GetThread(httpClient, get, i + 1);
		}
		for (int i = 0; i < 2; i++) {
			threads[i].start();
		}
	}

	static class GetThread extends Thread {
		private HttpClient httpClient;
		private GetMethod method;
		private int id;

		public GetThread(HttpClient httpClient, GetMethod method, int id) {
			this.httpClient = httpClient;
			this.method = method;
			this.id = id;
		}

		public void run() {
			try {
				System.out.println(id + " - about to get something from " + method.getURI());
				httpClient.executeMethod(method);
				System.out.println(id + " - get executed");
				byte[] bytes = method.getResponseBody();
				System.out.println(id + " - " + bytes.length + " bytes read");
			} catch (Exception e) {
				System.out.println(id + " - error: " + e);
			} finally {
				method.releaseConnection();
				System.out.println(id + " - connection released");
			}
		}
	}
}
