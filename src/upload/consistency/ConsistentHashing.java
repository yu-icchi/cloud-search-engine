//---------------------------------------------------------
//ConsistentHashing クラス
//
//LSEのSolrサーバにスケーラブルに文書を格納するためのハッシュ方法
//---------------------------------------------------------
package upload.consistency;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ConsistentHashing {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	//LSE(Solrサーバ)のノード格納
	private static NavigableMap<BigInteger, String> circle = new TreeMap<BigInteger, String>();

	//レプリケーション(仮想ノード)数
	private static int REPLICANTS = 100;

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ(デフォルト)
	 */
	public ConsistentHashing() {

	}

	/**
	 * コンストラクタ(仮想ノード数を指定する)
	 *
	 * @param replicants
	 */
	public ConsistentHashing(int replicants) {
		ConsistentHashing.REPLICANTS = replicants;
	}

	//-----------------------------------------------------
	//publicメソッド
	//-----------------------------------------------------

	/**
	 * addNodeメソッド (ノードの追加する)
	 *
	 * @param node ノードのアドレスを指定する
	 * @throws NoSuchAlgorithmException
	 */
	public void addNode(String node) throws NoSuchAlgorithmException {
		circle.put(getHash(node), node);
		//仮想ノードを追加する
		for (int i = 1; i <= REPLICANTS; i++) {
			circle.put(getHash(node + "_" + i), node);
		}

	}

	/**
	 * addNodeメソッド (複数のノードを一遍に登録する)
	 *
	 * @param nodes
	 * @throws NoSuchAlgorithmException
	 */
	public void addNode(String... nodes) throws NoSuchAlgorithmException {
		for (int i = 0; i < nodes.length; i++) {
			circle.put(getHash(nodes[i]), nodes[i]);
			//仮想ノードを追加する
			for (int j = 1; j <= REPLICANTS; j++) {
				circle.put(getHash(nodes[i] + "_" + j), nodes[i]);
			}

		}
	}

	/**
	 * addNodeメソッド (Listでノードを一遍に登録する)
	 *
	 * @param nodes
	 * @throws NoSuchAlgorithmException
	 */
	public void addNode(List<String> nodes) throws NoSuchAlgorithmException {
		for (String node : nodes) {
			circle.put(getHash(node), node);
			//仮想ノードを追加する
			for (int i = 1; i <= REPLICANTS; i++) {
				circle.put(getHash(node + "_" + i), node);
			}
		}
	}

	/**
	 * delNodeメソッド (ノードの削除する)
	 *
	 * @param node
	 * @throws NoSuchAlgorithmException
	 */
	public void delNode(String node) throws NoSuchAlgorithmException {
		circle.remove(getHash(node));
		//仮想ノードを追加する
		for (int i = 1; i <= REPLICANTS; i++) {
			circle.remove(getHash(node + "_" + i));
		}

	}

	/**
	 * delNodeメソッド (複数のノードを一遍に削除する)
	 *
	 * @param nodes
	 * @throws NoSuchAlgorithmException
	 */
	public void delNode(String... nodes) throws NoSuchAlgorithmException {
		for (int i = 0; i < nodes.length; i++) {
			circle.remove(getHash(nodes[i]));
			//仮想ノードを追加する
			for (int j = 1; j <= REPLICANTS; j++) {
				circle.remove(getHash(nodes[i] + "_" + j));
			}

		}
	}

	/**
	 * nextNodeメソッド
	 *
	 * @param key
	 * @throws NoSuchAlgorithmException
	 */
	public void nextNode(String key) throws NoSuchAlgorithmException {
		BigInteger foo = circle.higherKey(getHash(key));
		if (foo == null) {
			System.out.println(circle.get(circle.firstKey()));
		} else {
			System.out.println(circle.get(foo));
		}
	}

	/**
	 * searchNodeメソッド (格納する文書のアドレスを調べる)
	 *
	 * @param key
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public String searchNode(String key) throws NoSuchAlgorithmException {
		return search(circle, getHash(key));
	}

	/**
	 * nodeListメソッド (ノードリスト一覧を表示する)
	 */
	public void nodeList() {
		System.out.println(circle);
	}

	//-----------------------------------------------------
	//privateメソッド
	//-----------------------------------------------------

	/**
	 * getHashメソッド
	 *
	 * @param value
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private static BigInteger getHash(String value) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("md5");
		byte[] byteHash = digest.digest(value.getBytes());
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
