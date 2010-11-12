//---------------------------------------------------------
//SolrManagerクラス
//
//Jakarta Commons HTTP Client を利用したマルチ版のHTTP接続による監視
//---------------------------------------------------------
package upload.manager;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class SolrManager {

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ(デフォルト)
	 */
	public SolrManager() {

	}

	//-----------------------------------------------------
	//publicメソッド
	//-----------------------------------------------------

	/**
	 * keeperメソッド (監視をする)
	 */
	public static void keeper() {
		String[] urls = {"http://localhost:8983/solr/admin/ping", "http://localhost:7574/solr/admin/ping"};
		for (int i = 0; i < urls.length; i++) {
			System.out.println(observer(urls[i]));
		}
	}

	public static boolean observer(String url) {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
		try {
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				//System.out.println("Method failed: " + method.getStatusLine());
			}
			byte[] responseBody = method.getResponseBody();
			System.out.println(new String(responseBody));
			return true;
		} catch(Exception e) {
			return false;
		} finally {
			method.releaseConnection();
		}
	}

	//-----------------------------------------------------
	//
	//-----------------------------------------------------

	public static void main(String[] args) throws Exception {
		keeper();
	}
}