//---------------------------------------------------------
//IndexReaderTestクラス
//
// Solr(Lucene)のインデックスを調べて、必要なドキュメント情報を抜き出す
//---------------------------------------------------------
package lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

public class IndexReaderTest {

	/**
	 * main
	 *
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		IndexReader reader = IndexReader.open("app/SecondLevelSolr/solr1/solr/data/index");
		for (int i = 0; i < reader.maxDoc(); i++) {
			Document doc = reader.document(i);
			System.out.println(doc.get("id"));
		}
		reader.clone();
	}
}
