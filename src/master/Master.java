//---------------------------------------------------------
//Managerクラス
//
//LSEノードを管理するクラス（最終的には、ServletでREST形式で使えるようにする）
//---------------------------------------------------------
package master;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

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


	public static void main(String[] args) {
		httpConnect("http://www.google.com");
	}
}