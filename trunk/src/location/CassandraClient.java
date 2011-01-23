//---------------------------------------------------------
//CassandraClientクラス(GlobalIDF用)
//---------------------------------------------------------
package location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Deletion;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.KeySlice;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class CassandraClient {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	static final String KEYSPACE = "Keyspace1";
	static final String COLUMN_FAMILY = "Standard1";
	static final String SUPER_COLUMN = "Super1";
	static final String TERM = "term";
	static TTransport transport = null;
	static Cassandra.Client client = null;
	static String host;
	static int port;

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ(引数あり)
	 *
	 * @param _host
	 * @param _port
	 */
	public CassandraClient(String _host, int _port) {
		//ホスト名の指定
		host = _host;
		//ポート番号の指定
		port = _port;
		//接続する
		client = openConnection();
	}

	//-----------------------------------------------------
	//接続関連メソッド
	//-----------------------------------------------------

	/**
	 * connectメソッド(接続先を指定する)
	 *
	 *  @param (String) hostの指定す
	 *  @param (int) portの指定する
	 */
	public void connect(String _host, int _port) {
		host = _host;
		port = _port;
	}

	/**
	 * openConnectionメソッド(Cassandraに接続する)
	 */
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

	/**
	 * closeConnectionメソッド(Cassandraの接続を切る)
	 */
	public void closeConnection() {
		try {
			transport.flush();
			transport.close();
		} catch(TTransportException e) {
			e.printStackTrace();
		}
	}

	//-----------------------------------------------------
	//データ格納メソッド
	//-----------------------------------------------------

	public void insertNodes(String host, String type,  String data) {
		try {
			//ColumnPathの作成
			ColumnPath columnPath = new ColumnPath(COLUMN_FAMILY);
			columnPath.setColumn(type.getBytes("utf-8"));
			//レコードを挿入
			client.insert(KEYSPACE, host, columnPath, data.getBytes("utf-8"), System.currentTimeMillis(), ConsistencyLevel.QUORUM);
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * insertMaxDocメソッド
	 *
	 *  @param (String) urlを指定する
	 *  @param (String) dataを指定する
	 */
	public void insertMaxDoc(String url, String data) {
		try {
			//ColumnPathの作成
			ColumnPath columnPath = new ColumnPath(COLUMN_FAMILY);
			columnPath.setColumn(url.getBytes("utf-8"));
			//レコードを挿入
			client.insert(KEYSPACE, "MaxDocs", columnPath, data.getBytes("utf-8"), System.currentTimeMillis(), ConsistencyLevel.QUORUM);
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * insertMapメソッド(Map<String, String>をデータベースに格納する)
	 *
	 *  @param (String) keyを指定する
	 *  @param (Map<(String)カラム名 , (String)値>) Map型のデータを指定する
	 */
	public void insertMap(String key, Map<String, String> data) {
		try {
			Map<String, List<ColumnOrSuperColumn>> cfmap = new HashMap<String, List<ColumnOrSuperColumn>>();
			List<ColumnOrSuperColumn> columns = new ArrayList<ColumnOrSuperColumn>();
			Column column;
			ColumnOrSuperColumn columnOrSuperColumn;
			long timestamp = System.currentTimeMillis();
			for (Map.Entry<String, String> e : data.entrySet()) {
				//追加するカラムデータ
				column = new Column(e.getKey().getBytes("utf-8"), e.getValue().getBytes("utf-8"), timestamp);
				columnOrSuperColumn = new ColumnOrSuperColumn();
				columnOrSuperColumn.setColumn(column);
				columns.add(columnOrSuperColumn);
			}
			cfmap.put(COLUMN_FAMILY, columns);
			client.batch_insert(KEYSPACE, key, cfmap, ConsistencyLevel.QUORUM);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * insertIDFメソッド
	 *
	 *  @param (List<Map<String, String>>) dataを入れる
	 */
	public void insertDocFreq(List<Map<String, String>> data) {
		try {
			Map<String, Map<String, List<Mutation>>> map = new HashMap<String, Map<String, List<Mutation>>>();
			long timestamp = System.currentTimeMillis();
			for (int i = 0; i < data.size(); i++) {
				List<Mutation> mutationList = new ArrayList<Mutation>();
				String term = data.get(i).get("term");
				String doc = data.get(i).get("docFreq");
				String url = data.get(i).get("url");
				//登録するMutationオブジェクトのListを生成
				//mutationList.add(toMutation("idf", idf, timestamp));
				//mutationList.add(toMutation("url", url, timestamp));
				mutationList.add(toMutation(url, doc, timestamp));
				//カラムファミリとMutationのリストのMapを作成する
				Map<String, List<Mutation>> columnFamilyMap = new HashMap<String, List<Mutation>>();
				columnFamilyMap.put(COLUMN_FAMILY, mutationList);
				map.put(new String(term), columnFamilyMap);
			}
			System.out.println("insertDocFreq");
			client.batch_mutate(KEYSPACE, map, ConsistencyLevel.ONE);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * insertSuperColumnメソッド
	 *
	 *  @param data
	 */
	public void insertSuperColumn(List<Map<String, String>> data) {
		try {
			//格納用のデータ構造
			Map<String, Map<String, List<Mutation>>> mutationMap = new HashMap<String, Map<String, List<Mutation>>>();
			//タイムスタンプ
			long timestamp = System.currentTimeMillis();
			Map<String, List<Mutation>> colMap = new HashMap<String, List<Mutation>>();
			//タームに対して処理する
			for (int i = 0; i < data.size(); i++) {
				String term = data.get(i).get("term");
				String doc = data.get(i).get("docFreq");
				String url = data.get(i).get("url");
				addMutationSuperColumn(colMap, SUPER_COLUMN, term.getBytes("utf-8"), url.getBytes("utf-8"), doc.getBytes("utf-8"), timestamp);
			}
			mutationMap.put(TERM, colMap);
			client.batch_mutate(KEYSPACE, mutationMap, ConsistencyLevel.QUORUM);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * addMutationSuperColumn関数
	 *
	 * @param map
	 * @param columnFamily
	 * @param superName
	 * @param name
	 * @param value
	 * @param timestamp
	 */
	static void addMutationSuperColumn(Map<String, List<Mutation>> map, String columnFamily, byte[] superName, byte[] name, byte[] value, long timestamp) {
		Column column = new Column(name, value, timestamp);
		SuperColumn superColumn = new SuperColumn().setName(superName);
		superColumn.addToColumns(column);
		ColumnOrSuperColumn cs = new ColumnOrSuperColumn().setSuper_column(superColumn);
		Mutation mutation = new Mutation().setColumn_or_supercolumn(cs);
		List<Mutation> list = map.get(columnFamily);
		if (list == null) {
			list = new ArrayList<Mutation>();
			map.put(columnFamily, list);
		}
		list.add(mutation);
	}

	/**
	 * toMutationメソッド(カラム名、値、タイムスタンプからMutationオブジェクトを生成する)
	 *
	 *  @param (String) nameを指定する
	 *  @param (String) valueを指定する
	 *  @param (long) timestampを指定する
	 *  @return (Mutation) Mutationオブジェクトを返す
	 */
	static Mutation toMutation(final String name, final String value, final long timestamp) {
		try {
			Mutation mutation = new Mutation();
			ColumnOrSuperColumn columnOrSuperColumn = new ColumnOrSuperColumn();
			columnOrSuperColumn.setColumn(new Column(name.getBytes("utf-8"), value.getBytes("utf-8"), timestamp));
			mutation.setColumn_or_supercolumn(columnOrSuperColumn);
			return mutation;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//-----------------------------------------------------
	//データ取得メソッド
	//-----------------------------------------------------

	/**
	 * getメソッド(複数カラムを取得する)
	 *
	 *   @param (String) keyであるタームを指定する
	 *   @return (List<Map<String, String>>) ListでまとめたMapオブジェクトを返す
	 */
	public List<Map<String, String>> getMap(String key) {
		try {
			ColumnParent parent = new ColumnParent(COLUMN_FAMILY);
			SlicePredicate predicate = new SlicePredicate();
			predicate.setColumn_names(Arrays.asList("idf".getBytes(), "url".getBytes()));
			List<ColumnOrSuperColumn> ret = client.get_slice(KEYSPACE, key, parent, predicate, ConsistencyLevel.QUORUM);
			//結果を出力する変数
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			for (ColumnOrSuperColumn csc : ret) {
				Map<String, String> map = new HashMap<String, String>();
				Column column = csc.getColumn();
				String name = new String(column.getName());
				String value = new String(column.getValue());
				map.put("name", name);
				map.put("value", value);
				list.add(map);
			}
			return list;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, String> getNodes(String host) {
		try {
			SlicePredicate slicePredicate = new SlicePredicate();
			SliceRange sliceRange = new SliceRange();
			sliceRange.setStart(new byte[] {});
			sliceRange.setFinish(new byte[] {});
			slicePredicate.setSlice_range(sliceRange);
			ColumnParent columnParent = new ColumnParent(COLUMN_FAMILY);
			List<ColumnOrSuperColumn> results = client.get_slice(KEYSPACE, host, columnParent, slicePredicate, ConsistencyLevel.QUORUM);
			Map<String, String> map = new HashMap<String, String>();
			for (ColumnOrSuperColumn csc : results) {
				Column column = csc.getColumn();
				String name = new String(column.getName());
				String value = new String(column.getValue());
				map.put(name, value);
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * getメソッド(タームに対して全ての内容を取得)
	 *
	 *  @param (String) keyであるタームを指定する
	 *  @return (List<Map<String, String>>) Listを返す
	 */
	public List<Map<String, String>> get(String key) {
		try {
			SlicePredicate slicePredicate = new SlicePredicate();
			SliceRange sliceRange = new SliceRange();
			sliceRange.setStart(new byte[] {});
			sliceRange.setFinish(new byte[] {});
			slicePredicate.setSlice_range(sliceRange);
			ColumnParent columnParent = new ColumnParent(COLUMN_FAMILY);
			List<ColumnOrSuperColumn> results = client.get_slice(KEYSPACE, key, columnParent, slicePredicate, ConsistencyLevel.QUORUM);
			//結果を出力する変数
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			for (ColumnOrSuperColumn c : results) {
				if (c.getColumn() != null) {
					String name = new String(c.getColumn().getName(), "utf-8");
					String value = new String(c.getColumn().getValue(), "utf-8");
					Map<String, String> map = new HashMap<String, String>();
					map.put("url", name);
					map.put("docFreq", value);
					list.add(map);
				}
			}
			return list;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * getメソッド(入力Termが複数の場合)
	 *
	 *  @param keys 複数のTerm入力
	 *  @return
	 */
	public List<Map<String, String>> get(ArrayList<String> keys) {
		try {
			//ColumnParentにはColumnFamily名またはColumnFamily/SuperColumn名を指定する
			ColumnParent columnParent = new ColumnParent(COLUMN_FAMILY);
			SliceRange sliceRange = new SliceRange();
			//取得カラムの範囲を指定する。（全部指定のため、空のbyte配列を指定する）
			sliceRange.setStart(new byte[0]);
			sliceRange.setFinish(new byte[0]);

			SlicePredicate slicePredicate = new SlicePredicate();
			slicePredicate.setSlice_range(sliceRange);
			//multigetメソッドを使う
			Map<String, List<ColumnOrSuperColumn>> results = client.multiget_slice(KEYSPACE,
					keys, columnParent, slicePredicate, ConsistencyLevel.QUORUM);
			//出力する
			//結果を出力する変数
			List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
			for (Map.Entry<String, List<ColumnOrSuperColumn>> entry : results.entrySet()) {
				String key = entry.getKey();
				List<ColumnOrSuperColumn> list = entry.getValue();
				for (int i = 0; i < list.size(); i++) {
					ColumnOrSuperColumn result = list.get(i);
					Column col = result.column;
					String name = new String(col.name, "utf-8");
					String value = new String(col.value, "utf-8");
					Map<String, String> map = new HashMap<String, String>();
					map.put("url", name);
					map.put("docFreq", value);
					map.put("key", key);
					lists.add(map);
				}
			}
			return lists;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, Object> getMap(ArrayList<String> keys) {
		try {
			//ColumnParentにはColumnFamily名またはColumnFamily/SuperColumn名を指定する
			ColumnParent columnParent = new ColumnParent(COLUMN_FAMILY);
			SliceRange sliceRange = new SliceRange();
			//取得カラムの範囲を指定する。（全部指定のため、空のbyte配列を指定する）
			sliceRange.setStart(new byte[0]);
			sliceRange.setFinish(new byte[0]);

			SlicePredicate slicePredicate = new SlicePredicate();
			slicePredicate.setSlice_range(sliceRange);
			//multigetメソッドを使う
			Map<String, List<ColumnOrSuperColumn>> results = client.multiget_slice(KEYSPACE,
					keys, columnParent, slicePredicate, ConsistencyLevel.QUORUM);
			//出力する
			//結果を出力する変数
			List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
			for (Map.Entry<String, List<ColumnOrSuperColumn>> entry : results.entrySet()) {
				String key = entry.getKey();
				List<ColumnOrSuperColumn> list = entry.getValue();
				for (int i = 0; i < list.size(); i++) {
					ColumnOrSuperColumn result = list.get(i);
					Column col = result.column;
					String name = new String(col.name, "utf-8");
					String value = new String(col.value, "utf-8");
					Map<String, String> map = new HashMap<String, String>();
					map.put("url", name);
					map.put("docFreq", value);
					map.put("key", key);
					lists.add(map);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * getSuperColumnメソッド
	 *
	 *  @param key 複数のTerm入力
	 *  @return (List<Map<String, String>>) Listを返す
	 */
	public List<Map<String, String>> getSuperColumn(String key) {
		try {
			//カラムの取得範囲を全部にする
			SlicePredicate slicePredicate = new SlicePredicate();
			SliceRange sliceRange = new SliceRange();
			sliceRange.setStart(new byte[0]);
			sliceRange.setFinish(new byte[0]);
			slicePredicate.setSlice_range(sliceRange);
			//スーパーカラムを指定する
			ColumnParent columnParent = new ColumnParent(SUPER_COLUMN);
			//探しているスーパーカラム名を指定する
			columnParent.setSuper_column(key.getBytes("utf-8"));
			List<ColumnOrSuperColumn> results = client.get_slice(KEYSPACE, TERM, columnParent, slicePredicate, ConsistencyLevel.QUORUM);
			//結果を出力する変数
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			for (ColumnOrSuperColumn c : results) {
				if (c.getColumn() != null) {
					String name = new String(c.getColumn().getName(), "utf-8");
					String value = new String(c.getColumn().getValue(), "utf-8");
					Map<String, String> map = new HashMap<String, String>();
					map.put("url", name);
					map.put("docFreq", value);
					list.add(map);
				}
			}
			return list;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * getSuperColumnメソッド(入力Termが複数の場合)
	 *
	 *  @param keys 複数のTerm入力
	 *  @return
	 */
	public List<Map<String, String>> getSuperColumn(ArrayList<String> keys) {
		try {
			ColumnParent columnParent = new ColumnParent(SUPER_COLUMN);
			SlicePredicate slicePredicate = new SlicePredicate();
			for (String str : keys) {
				slicePredicate.addToColumn_names(str.getBytes("utf-8"));
			}
			List<ColumnOrSuperColumn> results = client.get_slice(KEYSPACE, TERM, columnParent, slicePredicate, ConsistencyLevel.QUORUM);
			//結果を出力する変数
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			for (ColumnOrSuperColumn cs : results) {
				SuperColumn superColumn = cs.getSuper_column();
				String key = new String(superColumn.getName(), "utf-8");
				List<Column> column = superColumn.getColumns();
				for (Column c : column) {
					String name = new String(c.getName(), "utf-8");
					String value = new String(c.getValue(), "utf-8");
					Map<String, String> map = new HashMap<String, String>();
					map.put("url", name);
					map.put("docFreq", value);
					map.put("key", key);
					list.add(map);
				}
			}
			return list;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * getMaxDocsメソッド
	 *
	 *  @return (List<Map<String, String>>) Listを返す
	 */
	public List<Map<String, String>> getMaxDocs() {
		try {
			SlicePredicate slicePredicate = new SlicePredicate();
			SliceRange sliceRange = new SliceRange();
			sliceRange.setStart(new byte[] {});
			sliceRange.setFinish(new byte[] {});
			slicePredicate.setSlice_range(sliceRange);
			ColumnParent columnParent = new ColumnParent(COLUMN_FAMILY);
			List<ColumnOrSuperColumn> results = client.get_slice(KEYSPACE, "MaxDocs", columnParent, slicePredicate, ConsistencyLevel.QUORUM);
			//結果を出力する変数
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			for (ColumnOrSuperColumn c : results) {
				if (c.getColumn() != null) {
					String name = new String(c.getColumn().getName(), "utf-8");
					String value = new String(c.getColumn().getValue(), "utf-8");
					Map<String, String> map = new HashMap<String, String>();
					map.put("url", name);
					map.put("maxDocs", value);
					list.add(map);
				}
			}
			return list;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * termsメソッド(登録しているタームリストを返す)
	 *
	 * @return
	 */
	public ArrayList<String> terms() {
		ArrayList<String> list = new ArrayList<String>();
		try {
			ColumnParent columnParent = new ColumnParent(SUPER_COLUMN);
			//登録しているターム数を調べる
			int count = client.get_count(KEYSPACE, TERM, columnParent, ConsistencyLevel.QUORUM);
			SlicePredicate slicePredicate = new SlicePredicate();
			SliceRange sliceRange = new SliceRange();
			sliceRange.setStart(new byte[0]);
			sliceRange.setFinish(new byte[0]);
			sliceRange.setCount(count);
			slicePredicate.setSlice_range(sliceRange);
			List<ColumnOrSuperColumn> results = client.get_slice(KEYSPACE, TERM, columnParent, slicePredicate, ConsistencyLevel.QUORUM);
			for (ColumnOrSuperColumn cs : results) {
				SuperColumn superColumn = cs.getSuper_column();
				//System.out.println(new String(superColumn.getName(), "utf-8"));
				list.add(new String(superColumn.getName(), "utf-8"));
			}
			return list;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * termsLengthメソッド
	 *
	 * @return
	 */
	public int termsLength() {
		try {
			//スーパーカラムを指定する
			ColumnParent columnParent = new ColumnParent(SUPER_COLUMN);
			//登録しているターム数を調べる
			int count = client.get_count(KEYSPACE, TERM, columnParent, ConsistencyLevel.QUORUM);
			return count;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	//-----------------------------------------------------
	//データ削除メソッド
	//-----------------------------------------------------

	/**
	 * deleteメソッド
	 *
	 *  @param (String) keyを指定する
	 */
	public void delete(String key) {
		try {
			ColumnPath columnPath = new ColumnPath(COLUMN_FAMILY);
			client.remove(KEYSPACE, key, columnPath, System.currentTimeMillis(), ConsistencyLevel.QUORUM);
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * deleteメソッド
	 *
	 *  @param (String) keyを指定する
	 *  @param (String) urlを指定する
	 */
	public void delete(String key, String url) {
		try {
			//ColumnPathの作成
			ColumnPath columnPath = new ColumnPath(COLUMN_FAMILY);
			columnPath.setColumn(url.getBytes("utf-8"));
			//レコードを削除する
			client.remove(KEYSPACE, key, columnPath, System.currentTimeMillis(), ConsistencyLevel.QUORUM);
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * deleteURLメソッド
	 *
	 * @param url
	 */
	public void deleteURL(String url) {
		try {
			//keyの範囲を決める (「""」はALLと指定している)
			KeyRange keyRange = new KeyRange();
			keyRange.setStart_key("");
			keyRange.setEnd_key("");
			//問題はsetCountの数、デフォルトだと100になっているgetKeyCountみたいなのがない
			keyRange.setCount(10000000);

			ColumnParent columnParent = new ColumnParent(COLUMN_FAMILY);
			//columnを複数取得する設定
			SlicePredicate slicePredicate = new SlicePredicate();
			SliceRange sliceRange = new SliceRange();
			sliceRange.setStart(new byte[0]);
			sliceRange.setFinish(new byte[0]);
			slicePredicate.setSlice_range(sliceRange);
			//ConsistencyLevel.QUORUMで行なうと時間がかかり過ぎるため、ConsistencyLevel.ONEにする
			List<KeySlice> list = client.get_range_slices(KEYSPACE, columnParent, slicePredicate, keyRange, ConsistencyLevel.ONE);

			//key毎にURLがあるか調べて削除する
			for (KeySlice slice : list) {
				String key = slice.getKey();
				//ColumnPathの作成
				ColumnPath columnPath = new ColumnPath(COLUMN_FAMILY);
				columnPath.setColumn(url.getBytes("utf-8"));
				//レコードを削除する
				client.remove(KEYSPACE, key, columnPath, System.currentTimeMillis(), ConsistencyLevel.QUORUM);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * deleteSuperColumnメソッド
	 *
	 * @param term (String) 削除するスーパーカラム名を指定する
	 * @param url (String) カラムの一部分を削除する
	 */
	public void deleteSuperColumn(String term, String url) {
		try {
			//ColumnPathの作成
			ColumnPath deletePath = new ColumnPath(SUPER_COLUMN);
			deletePath.setSuper_column(term.getBytes("utf-8"));
			deletePath.setColumn(url.getBytes("utf-8"));
			//レコードを削除する
			client.remove(KEYSPACE, TERM, deletePath, System.currentTimeMillis(), ConsistencyLevel.QUORUM);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * deleteSuperColumnメソッド(このメソッドの場合だと、term全て削除する)
	 */
	public void deleteSuperColumnAll() {
		try {
			ColumnPath columnPath = new ColumnPath(SUPER_COLUMN);
			client.remove(KEYSPACE, TERM, columnPath, System.currentTimeMillis(), ConsistencyLevel.QUORUM);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param map
	 * @param columnFamily
	 * @param superName
	 * @param timestamp
	 */
	static void addDeletionSuperColumn(Map<String, List<Mutation>> map, String columnFamily, byte[] superName, long timestamp) {
		Deletion deletion = new Deletion(timestamp).setSuper_column(superName);
		Mutation mutation = new Mutation().setDeletion(deletion);
		List<Mutation> list = map.get(columnFamily);
		if (list == null) {
			list = new ArrayList<Mutation>();
			map.put(columnFamily, list);
		}
		list.add(mutation);
	}

	/**
	 *
	 */
	public void describe() {
		try {
			Set<String> set = client.describe_keyspaces();
			System.out.println(set);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * searchメソッド
	 *
	 * @param start
	 * @param end
	 */
	public void search(String start, String end) {
		try {
			KeyRange keyRange = new KeyRange();
			keyRange.setStart_key(start);
			keyRange.setEnd_key(end);
			keyRange.setCount(10000000);
			System.out.println(keyRange.getCount());

			ColumnParent columnParent = new ColumnParent(COLUMN_FAMILY);

			SlicePredicate slicePredicate = new SlicePredicate();
			SliceRange sliceRange = new SliceRange();
			sliceRange.setStart(new byte[0]);
			sliceRange.setFinish(new byte[0]);
			slicePredicate.setSlice_range(sliceRange);

			List<KeySlice> list = client.get_range_slices(KEYSPACE, columnParent, slicePredicate, keyRange, ConsistencyLevel.QUORUM);

			System.out.println(list.size());

			for (KeySlice slice : list) {
				String key = slice.getKey();
				List<ColumnOrSuperColumn> columns = slice.getColumns();
				for (ColumnOrSuperColumn cs : columns) {
					Column col = cs.getColumn();
					System.out.println(key + " : " + new String(col.getName(), "utf-8"));
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

}
