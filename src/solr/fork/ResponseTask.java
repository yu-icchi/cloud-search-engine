package solr.fork;

import java.util.concurrent.Callable;

import org.apache.solr.common.SolrDocumentList;

public class ResponseTask implements Callable<SolrDocumentList> {

	private String url;
	private String query;

	public ResponseTask(String url, String query) {
		this.url = url;
		this.query = query;
	}

	@Override
	public SolrDocumentList call() throws Exception {
		//SolrClinet
		SolrClient client = new SolrClient(this.url);
		SolrDocumentList list = client.getResponse(this.query);
		return list;
	}

}
