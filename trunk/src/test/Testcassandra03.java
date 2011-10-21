package test;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

public class Testcassandra03 {
	
    public static final String KEYSPACE = "Keyspace1";

    public static final String COLUMN_FAMILY = "Standard1";

    public static void main(String[] args) {
        TSocket transport = new TSocket("192.168.244.136", 9160);
        TProtocol protocol = new TBinaryProtocol(transport);
        try {
            transport.open();
        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }
        Cassandra.Client client = new Cassandra.Client(protocol);
        try {
            final String key = "hinamio";
            final String columnName = "name";
            final String value = "Shintani Hinamio";
            final long timestamp = System.currentTimeMillis();
            // ColumnPathは単一のカラムの位置を特定するためのクラス
            final ColumnPath columnPath = new ColumnPath(COLUMN_FAMILY);
            columnPath.setColumn(columnName.getBytes());
            // 1件カラムをインサート
            client.insert(KEYSPACE, key, columnPath, value.getBytes(),
                    timestamp, ConsistencyLevel.ONE);
        } catch (InvalidRequestException e) {
            throw new RuntimeException(e);
        } catch (UnavailableException e) {
            throw new RuntimeException(e);
        } catch (TimedOutException e) {
            throw new RuntimeException(e);
        } catch (TException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                transport.flush();
            } catch (TTransportException e) {
                throw new RuntimeException(e);
            } finally {
                transport.close();
            }
        }
        System.out.println("1件インサート完了.");
    }
}
