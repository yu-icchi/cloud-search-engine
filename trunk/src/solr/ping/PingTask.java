package solr.ping;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import location.Location;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.SolrPingResponse;

public class PingTask {

	protected boolean fStop;

	private Location location = new Location();

	protected List<String> serverList = new ArrayList<String>();

	public void start() {
		fStop = false;
		Thread thread = new Thread(new KeepAlive());
		thread.setDaemon(true);
		thread.start();
	}

	public void addServer(String address) {
		serverList.add(address);
		location.setNodes("nodelist", address, "active");
	}

	public void addServer(String[] address) {
		for (String str : address) {
			serverList.add(str);
			location.setNodes("nodelist", str, "active");
		}
	}

	public void stop() {
		fStop = true;
	}

	protected void check() {
		SolrServer server;
		SolrPingResponse res = null;
		Iterator<String> it = serverList.iterator();
		String url = null;
		while (it.hasNext()) {
			String address = it.next();
			System.out.println(address);
			try {
				url = "http://" + address + "/solr/core0/";
				server = new CommonsHttpSolrServer(url);
				res = server.ping();
				System.out.println(res);
			} catch (MalformedURLException e) {
				System.out.println("URLがおかしいよ");
				location.deleteNodes("nodelist", address);
				it.remove();
			} catch (SolrServerException e) {
				System.out.println("サーバにアクセスできん");
				location.setNodes("nodelist", address, "fault");
				it.remove();
			} catch (IOException e) {
				System.out.println("読み込めん");
				location.deleteNodes("nodelist", address);
				it.remove();
			}
		}
	}

	protected class KeepAlive implements Runnable {

		public void run() {
			while (!fStop) {
				try {
					//秒数を決める
					Thread.sleep(1000L * 5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				check();
			}
		}
	}
}
