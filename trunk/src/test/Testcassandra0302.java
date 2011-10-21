package test;

import java.io.UnsupportedEncodingException;
import java.util.*;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class Testcassandra0302 {
	public static final String UTF8 = "UTF-8";

	public static void main(String[] args) throws InvalidRequestException, NotFoundException, UnavailableException, TimedOutException,
			TException, UnsupportedEncodingException {

		TTransport transport = new TSocket("192.168.244.136", 9160);
		TProtocol  protocol  = new TBinaryProtocol(transport);
		Cassandra.Client client = new Cassandra.Client(protocol);

		transport.open();
		try {
			String keyspace     = "Keyspace1";	//キースペース名
			String columnFamily = "Standard1";	//カラムファミリー名(テーブル名)
			String columnname    = "name";	//カラム名
			String key          = "hinamio";	//キー

			// カラムを指定してデータを取得
			ColumnPath colPathAge = new ColumnPath(columnFamily).setColumn(columnname.getBytes(UTF8));
			Column col = client.get(keyspace, key, colPathAge, ConsistencyLevel.ONE).getColumn();

			long mill = System.currentTimeMillis()*1000;
			
			// 取ってきたデータを表示してみる
			System.out.println(col);
			System.out.println(new String(col.getName() , UTF8));
			System.out.println(new String(col.getValue(), UTF8));
			System.out.println(col.getTimestamp());
			System.out.println(col.getTimestamp()*1000);
			System.out.println(mill);
			System.out.println(mill/1000);
			System.out.println(mill/1000-col.getTimestamp());
		} finally {
			transport.close();
		}
	}


}
