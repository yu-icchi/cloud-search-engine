//---------------------------------------------------------
//SolrJUpdateClient クラス
//
//SolrJを利用して更新をする(最終的にはServletにする)
//---------------------------------------------------------
package client;

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
	public static void main(String args) throws Exception {
		//Account
		String account = "user1";
		//ファイルのパス
		String filePath = "demo/sample.txt";
		//複数のサーバ
		String[] server = {"http://localhost:8983/solr", "http://localhost:7574/solr"};
		//ConsistentHashingで格納するサーバを決める
		ConsistentHashing hash = new ConsistentHashing();
		hash.addNode(server);
		String node = hash.searchNode(filePath);
		//インデックスにデータを格納させる
		Crawler crawler = new Crawler(account, filePath, node);
		boolean flag = crawler.setIndex();
		if (flag) {
			//正常
		} else {
			//エラー
		}
	}

}
