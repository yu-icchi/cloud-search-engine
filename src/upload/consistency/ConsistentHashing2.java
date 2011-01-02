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

public class ConsistentHashing2 {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	//LSE(Solrサーバ)の物理的ノード格納リング
	private static NavigableMap<BigInteger, String> continuum = new TreeMap<BigInteger, String>();

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ(デフォルト)
	 */
	public ConsistentHashing2() {

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
		}
	}

	/**
	 * nextNodeメソッド
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
	 * prevNodeメソッド
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
	 * searchNodeメソッド (格納する文書のアドレスを調べる)
	 *
	 * @param key
	 * @return
	 */
	public String searchNode(String key) {
		return search(continuum, getHash(key));
	}

	/**
	 * nodeListメソッド (ノードリスト一覧を表示する)
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
	 * @throws NoSuchAlgorithmException
	 */
	private static BigInteger getHash(String value) {
		byte[] byteHash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			byteHash = digest.digest(value.getBytes());
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
