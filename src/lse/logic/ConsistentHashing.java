//---------------------------------------------------------
//コンシステントハッシングの実装クラス
//
//シングルトンパターンによりコーディング
//---------------------------------------------------------
package lse.logic;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ConsistentHashing {

	//-----------------------------------------------------
	//シングルトンパターン
	//-----------------------------------------------------
	private static ConsistentHashing instance = new ConsistentHashing();

	private ConsistentHashing() {}

	//インスタンスを返す
	public static ConsistentHashing getInstance() {
		return instance;
	}

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	//LSE(Solrサーバ)の仮想ノード付き格納リング
	private static NavigableMap<BigInteger, String> circle = new TreeMap<BigInteger, String>();
	//LSE(Solrサーバ)の物理的ノード格納リング
	private static NavigableMap<BigInteger, String> continuum = new TreeMap<BigInteger, String>();

	//レプリケーション(仮想ノード)数 デフォルトでは100個
	private int replicants = 100;

	//-----------------------------------------------------
	//ゲッター・セッター
	//-----------------------------------------------------
	/**
	 * レプリカ数の指定
	 */
	public void setReplicants(int replicants) {
		this.replicants = replicants;
	}

	/**
	 * レプリカ数の呼び出し
	 * @return
	 */
	public int getReplicants() {
		return replicants;
	}

	//-----------------------------------------------------
	//publicメソッド
	//-----------------------------------------------------

	/**
	 * addNodeメソッド (ノードの追加する)
	 *
	 * @param node ノードのアドレスを指定する
	 */
	public void addNode(String node) {
		//物理ノードに追加する
		continuum.put(getHash(node), node);
		//仮想ノードに追加する
		for (int i = 0; i < replicants; i++) {
			circle.put(getHash(node + "_" + i), node);
		}
	}

	/**
	 * addNodeメソッド (複数のノードを一遍に登録する)
	 *
	 * @param nodes
	 */
	public void addNode(String... nodes) {
		for (int i = 0; i < nodes.length; i++) {
			//物理ノードに追加する
			continuum.put(getHash(nodes[i]), nodes[i]);
			//仮想ノードに追加する
			for (int j = 0; j < replicants; j++) {
				circle.put(getHash(nodes[i] + "_" + j), nodes[i]);
			}
		}
	}

	/**
	 * addNodeメソッド (Listでノードを一遍に登録する)
	 *
	 * @param nodes
	 */
	public void addNode(List<String> nodes) {
		for (String node : nodes) {
			//物理ノードに追加する
			continuum.put(getHash(node), node);
			//仮想ノードに追加する
			for (int i = 0; i < replicants; i++) {
				circle.put(getHash(node + "_" + i), node);
			}
		}
	}

	/**
	 * delNodeメソッド (ノードの削除する)
	 *
	 * @param node
	 */
	public void delNode(String node) {
		//物理ノードに追加する
		continuum.remove(getHash(node));
		//仮想ノードに追加する
		for (int i = 0; i < replicants; i++) {
			circle.remove(getHash(node + "_" + i));
		}
	}

	/**
	 * delNodeメソッド (複数のノードを一遍に削除する)
	 *
	 * @param nodes
	 */
	public void delNode(String... nodes) {
		for (int i = 0; i < nodes.length; i++) {
			//物理ノードに追加する
			continuum.remove(getHash(nodes[i]));
			//仮想ノードに追加する
			for (int j = 0; j < replicants; j++) {
				circle.remove(getHash(nodes[i] + "_" + j));
			}
		}
	}

	/**
	 * nextNodeメソッド（物理ノード側で調べる）
	 * 指定したノードより1つ次のノードを返す
	 *
	 * @param key
	 * @return
	 */
	public String nextNode(String key) {
		BigInteger hash = continuum.higherKey(getHash(key));
		if (hash == null) {
			return continuum.get(continuum.firstKey());
		} else {
			return continuum.get(hash);
		}
	}

	/**
	 * prevNodeメソッド（物理ノード側で調べる）
	 * 指定したノードより1つ前のノードを返す
	 *
	 * @param key
	 * @return
	 */
	public String prevNode(String key) {
		BigInteger hash = continuum.lowerKey(getHash(key));
		if (hash == null) {
			return continuum.get(continuum.lastKey());
		} else {
			return continuum.get(hash);
		}
	}

	/**
	 * isNodeメソッド（物理ノード側で調べる）
	 * 存在するノードであるか
	 *
	 * @param key
	 * @return
	 */
	public boolean isNode(String key) {
		return continuum.containsValue(key);
	}

	/**
	 * searchNodeメソッド (格納する文書のアドレスを調べる)
	 * 仮想ノードに対して調べる
	 *
	 * @param key
	 * @return
	 */
	public String searchNode(String key) {
		return search(circle, getHash(key));
	}

	/**
	 * nodeListメソッド (物理ノードリスト一覧を表示する)
	 */
	public void nodeList() {
		System.out.println(continuum);
	}

	//-----------------------------------------------------
	//privateメソッド
	//-----------------------------------------------------

	/**
	 * getHashメソッド
	 *
	 * @param value
	 * @return
	 */
	private static BigInteger getHash(String value) {
		byte[] byteHash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			byteHash = digest.digest(value.getBytes());
			return new BigInteger(byteHash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new BigInteger(byteHash);
	}

	/**
	 * searchメソッド
	 *
	 * @param circle
	 * @param rec
	 * @return
	 */
	private static String search(NavigableMap<BigInteger, String> circle, BigInteger rec ) {
		BigInteger key = circle.ceilingKey(rec);
		if (key == null) {
			return (String) circle.get(circle.firstKey());
		} else {
			return (String) circle.get(key);
		}
	}
}
