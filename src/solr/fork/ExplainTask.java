package solr.fork;

import java.util.Map;
import java.util.concurrent.Callable;

public class ExplainTask implements Callable<Map<String, String>> {

	private String url;
	private String query;

	public ExplainTask(String url, String query) {
		this.url = url;
		this.query = query;
	}

	@Override
	public Map<String, String> call() throws Exception {
		//SolrClinet
		SolrClient client = new SolrClient(this.url);
		Map<String, String> map = client.getExplain(this.query);
		return map;
	}

}
