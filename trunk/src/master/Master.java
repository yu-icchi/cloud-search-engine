//---------------------------------------------------------
//Managerクラス
//
//LSEノードを管理するクラス（最終的には、ServletでREST形式で使えるようにする）
//---------------------------------------------------------
package master;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class Master {

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ
	 */
	public Master() {

	}

	//-----------------------------------------------------
	//制御用メソッド
	//-----------------------------------------------------

	/**
	 * attachメソッド (新規ノード追加した時の処理)
	 *
	 * @return
	 */
	public String attach() {

		return null;
	}

	/**
	 * dettachメソッド (１つのノードがダウンした時の処理)
	 *
	 * @return
	 */
	public String dettach() {
		return null;
	}

	/**
	 * restoreメソッド (ダウンしたノードを再起動させた時の処理)
	 *
	 * @return
	 */
	public String restore() {
		return null;
	}

	//-----------------------------------------------------
	//
	//------------------------------------------------------

	/**
	 * getURLBodyメソッド (URLを指定してデータを持ってくる)
	 *
	 * @param url
	 * @return
	 */
	private static String getURLBody(String url) {
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
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		return null;
	}
}