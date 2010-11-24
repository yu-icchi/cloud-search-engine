package upload.manager;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class SolrConfigActionTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO 自動生成されたメソッド・スタブ
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod("http://localhost:7100/solr/core0/admin/file/?file=solrconfig.xml");
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
		try {
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				//System.out.println("Method failed: " + method.getStatusLine());
			}
			byte[] responseBody = method.getResponseBody();
			File file = new File("demo/solrconfig.xml");
			FileOutputStream out = new FileOutputStream(file);
			out.write(responseBody);
			System.out.println(new String(responseBody));
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
	}

}
