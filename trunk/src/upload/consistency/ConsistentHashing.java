//---------------------------------------------------------
//ConsistentHashing クラス
//
//LSEのSolrサーバにスケーラブルに文書を格納するためのハッシュ方法
//---------------------------------------------------------
package upload.consistency;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashing {

	/**
	 * テスト用　Main
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int numOfReplicants = 100;
		String[] nodeLists = {"n1", "n2", "n3", "n4"};
		NavigableMap<BigInteger, String> circle = new TreeMap<BigInteger, String>();
		SortedMap<String, List> nodesmap = new TreeMap<String, List>();
		List<String> alphalist = Arrays.asList(
	           "A","B","C","D","E","F","G","H","I","J","K","L","M",
	           "N","O","P","Q","R","S","T","U","V","W","X","Y","Z");
		for( int i = 0; i < nodeLists.length; i++ ){
			nodesmap.put( nodeLists[i], new LinkedList<String>());
		}

		for( String node : nodesmap.keySet() ){
			circle.put( getHash(node), node );
			for (int i = 0; i < numOfReplicants; i++) {
				circle.put(getHash(node + "_" + i), node);
			}
		}

		for( String str : alphalist ){
			String node = search(circle, getHash(str));
			if( !nodesmap.containsKey(node)){
				List<String> asciiList = new LinkedList<String>();
				asciiList.add(str);
				nodesmap.put(node, asciiList);
			}else{
				nodesmap.get(node).add(str);
			}
		}

		for( String node : nodesmap.keySet()){
			System.out.print(node + " ");
			List<String> asciiList = nodesmap.get(node);
			for( String strascii : asciiList ){
				System.out.print(strascii + " ");
			}
			System.out.print("\n");
		}
	}

	static BigInteger getHash(String value) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("md5");
		byte[] byteHash = digest.digest(value.getBytes());
		return new BigInteger(byteHash);
	}

	static String search(NavigableMap<BigInteger, String> circle, BigInteger rec ) {
		BigInteger key = circle.ceilingKey(rec);
		if (key == null) {
			return (String) circle.get(circle.firstKey());
		} else {
			return (String) circle.get(key);
		}
	}
}
