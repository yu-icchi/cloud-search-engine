//---------------------------------------------------------
//CassandraClientクラス(Account用)
//---------------------------------------------------------
package account;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class CassandraClient {

	static final String KEYSPACE = "Keyspace1";
	static final String COLUMN_FAMILY = "Standard1";
	static final String SUPER_COLUMN = "Super1";
	static final String TERM = "term";
	static TTransport transport = null;
	static Cassandra.Client client = null;
	static String host;
	static int port;
	
	/**----------------------------------------------------
	 * コンストラクタ(HOSTとPORTを指定する)
	 * ----------------------------------------------------
	 *  @param _host (String)ホストの指定
	 *  @param _port (int)ポート番号の指定
	 * --------------------------------------------------*/
	public CassandraClient(String _host, int _port) {
		host = _host;
		port = _port;
		//接続する
		client = openConnection();
	}
	
	/**----------------------------------------------------
	 * connectメソッド(接続先を指定する)
	 * ----------------------------------------------------
	 *  @param (String) hostの指定す
	 *  @param (int) portの指定する
	 * --------------------------------------------------*/
	public void connect(String _host, int _port) {
		host = _host;
		port = _port;
	}
	
	/**----------------------------------------------------
	 * openConnectionメソッド(Cassandraに接続する)
	 * --------------------------------------------------*/
	static Cassandra.Client openConnection() {
		try {
			transport = new TSocket(host, port);
			TProtocol protocol = new TBinaryProtocol(transport);
			Cassandra.Client client = new Cassandra.Client(protocol);
			transport.open();
			return client;
		} catch(TTransportException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**----------------------------------------------------
	 * closeConnectionメソッド(Cassandraの接続を切る)
	 * --------------------------------------------------*/
	public void closeConnection() {
		try {
			transport.flush();
			transport.close();
		} catch(TTransportException e) {
			e.printStackTrace();
		}
	}
	
	/**----------------------------------------------------
	 * insertMaxDocメソッド
	 * ----------------------------------------------------
	 *  @param id (String) IDを指定する
	 *  @param pass (String) PASSを指定する
	 * --------------------------------------------------*/
	public void insert(String id, String pass) {
		try {
			//ColumnPathの作成
			ColumnPath columnPath = new ColumnPath(COLUMN_FAMILY);
			columnPath.setColumn(id.getBytes("utf-8"));
			//レコードを挿入
			client.insert(KEYSPACE, "Account", columnPath, pass.getBytes("utf-8"), System.currentTimeMillis(), ConsistencyLevel.ONE);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**----------------------------------------------------
	 * getメソッド(タームに対して全ての内容を取得)
	 * ----------------------------------------------------
	 *  @param (String) keyであるタームを指定する
	 *  @return (List<Map<String, String>>) Listを返す
	 * --------------------------------------------------*/
	public boolean get(String id, String pass) {
		try {
			ColumnPath columnPath = new ColumnPath(COLUMN_FAMILY);
			columnPath.setColumn(id.getBytes("utf-8"));
			ColumnOrSuperColumn cosc = client.get(KEYSPACE, "Account", columnPath, ConsistencyLevel.ONE);
			Column column = cosc.getColumn();
			String name = new String(column.getName(), "utf-8");
			String value = new String(column.getValue(), "utf-8");
			System.out.println(name + ":" + value);
			if (value.equals(pass)) {
				return true;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**----------------------------------------------------
	 * deleteメソッド
	 * ----------------------------------------------------
	 *  @param (String) keyを指定する
	 * --------------------------------------------------*/
	public void delete(String key) {
		try {
			ColumnPath columnPath = new ColumnPath(COLUMN_FAMILY);
			client.remove(KEYSPACE, key, columnPath, System.currentTimeMillis(), ConsistencyLevel.ALL);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**----------------------------------------------------
	 * deleteメソッド
	 * ----------------------------------------------------
	 *  @param (String) keyを指定する
	 *  @param (String) urlを指定する
	 * --------------------------------------------------*/
	public void delete(String key, String url) {
		try {
			//ColumnPathの作成
			ColumnPath columnPath = new ColumnPath(COLUMN_FAMILY);
			columnPath.setColumn(url.getBytes("utf-8"));
			//レコードを削除する
			client.remove(KEYSPACE, key, columnPath, System.currentTimeMillis(), ConsistencyLevel.ALL);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
