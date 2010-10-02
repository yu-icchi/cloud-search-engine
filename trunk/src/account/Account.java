//---------------------------------------------------------
//Accountクラス
//---------------------------------------------------------
package account;

public class Account {
	
	//ID
	static String _id = null;
	//PASS
	static String _pass = null;
	//ホスト名
	static String _host = "localhost";
	//ポート番号
	static int _port = 9160;
	
	/**----------------------------------------------------
	 * コンストラクタ(デフォルト)
	 * --------------------------------------------------*/
	public Account() {
		
	}
	
	/**----------------------------------------------------
	 * コンストラクタ(引数あり)
	 * ----------------------------------------------------
	 * @param id (String) IDの指定をする
	 * @param pass (String) PASSの指定をする
	 * --------------------------------------------------*/
	public Account(String id, String pass) {
		_id = id;
		_pass = pass;
	}
	
	/**----------------------------------------------------
	 * connectメソッド
	 * ----------------------------------------------------
	 * @param id (String) IDの指定をする
	 * @param pass (String) PASSの指定をする
	 * --------------------------------------------------*/
	public void connect(String id, String pass) {
		_id = id;
		_pass = pass;
	}
	
	/**----------------------------------------------------
	 * openConnection関数
	 * --------------------------------------------------*/
	static void openConnection() {
		//IDとPASSが設定されているか？
		if (_id != null && _pass != null) {
			
		}
	}
	
	/**----------------------------------------------------
	 * accountSetメソッド
	 * --------------------------------------------------*/
	public void accountSet() {
		CassandraClient cc = new CassandraClient(_host, _port);
		cc.insert(_id, _pass);
		cc.closeConnection();
	}
	
	/**----------------------------------------------------
	 * accountGetメソッド
	 * --------------------------------------------------*/
	public void accountGet() {
		CassandraClient cc = new CassandraClient(_host, _port);
		System.out.println(cc.get(_id, _pass));
		cc.closeConnection();
	}
}
