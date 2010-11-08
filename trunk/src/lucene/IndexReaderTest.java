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
		IndexReader reader = IndexReader.open("app/SecondLevelSolr/solr2/solr/data/index");
		Document doc = reader.document(1);
		System.out.println(doc.get("id"));
		reader.clone();
	}
}
