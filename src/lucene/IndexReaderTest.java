//---------------------------------------------------------
//IndexReaderTestクラス
//
// Solr(Lucene)のインデックスを調べて、必要なドキュメント情報を抜き出す
//---------------------------------------------------------
package lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import upload.consistency.ConsistentHashing;

public class IndexReaderTest {

	/**
	 * main
	 *
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {

		ConsistentHashing hash = new ConsistentHashing();

		hash.addNode("localhost:8081", "localhost:8082", "localhost:8083", "localhost:8084", "localhost:8085");
		hash.addNode("localhost:8086", "localhost:8087", "localhost:8088", "localhost:8089", "localhost:8090");

		IndexReader reader = IndexReader.open("app/SecondLevelSolr/solr1/solr/data/index");
		for (int i = 0; i < reader.maxDoc(); i++) {
			Document doc = reader.document(i);
			System.out.println(doc.get("id"));
			String node = hash.searchNode(doc.get("id"));
			System.out.println(node);
		}
		reader.clone();
	}
}
