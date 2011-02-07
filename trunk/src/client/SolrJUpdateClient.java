//---------------------------------------------------------
//SolrJUpdateClient クラス
//
//SolrJを利用して更新をする(最終的にはServletにする)
//---------------------------------------------------------
package client;

import location.Location;
import upload.Crawler;
import upload.consistency.ConsistentHashing;

public class SolrJUpdateClient {

	/**
	 * Cloud-Search-Engineの更新部分
	 * 		1.アップロードするファイルを決める
	 * 		2.Consistent Hashingにより分散するLSEの格納場所を決める
	 * 		3.拡張子から自動的に読み込むクラスを選び、テキストを抽出し、インデックスに格納する
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//Account
		String account = "test1 test2";
		//ファイルのパス
		String filePath = "demo/data.txt";
		//複数のサーバ
		String[] server = {"http://192.168.168.164:6365/solr/"};
		//ConsistentHashingで格納するサーバを決める
		ConsistentHashing hash = new ConsistentHashing();
		hash.addNode(server);
		String node = hash.searchNode(filePath);
		System.out.println("インデックス格納先ノード: " + node);
		//インデックスにデータを格納させる
		Crawler crawler = new Crawler(account, filePath, node);
		boolean flag = crawler.setIndex();
		if (flag) {
			//LocationServerを更新させる
			//Location location = new Location("192.168.220.133", 9160);
			//location.set(node);
			//正常
			System.out.println("成功");
		} else {
			//エラー
			System.out.println("エラー");
		}
	}

}
