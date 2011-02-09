package solr;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
/*
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
*/

public class SolrJDebugQuery {

	public static void main(String[] args) throws Exception {
		/*
		SolrServer server = new CommonsHttpSolrServer("http://192.168.168.165:6365/solr");
	    SolrQuery solrquery = new SolrQuery();
	    solrquery.set("terms.fl", "text");
	    solrquery.set("terms.limit", "5");
	    //solrquery.setQuery("*:*");

	    QueryResponse response = server.query(solrquery);
	    SolrDocumentList docs = response.getResults();
	    System.out.println(docs);
	    */
		httpConnect("http://192.168.168.165:6365/solr/terms?terms.fl=text&terms.limit=5&version=1");
	}

	public static void httpConnect(String url) {
		HttpClient httpClient = new HttpClient();
		PostMethod method = new PostMethod(url);
		try {
			int status = httpClient.executeMethod(method);
			if (status != HttpStatus.SC_OK) {
				System.out.println("Method failed: " + method.getStatusText());
			}
			byte[] responseBody = method.getResponseBody();
			System.out.println(new String(responseBody));
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}

	}
}
