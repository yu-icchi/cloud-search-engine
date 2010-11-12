package lucene;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;

public class IndexReaderToWriter {

	/**
	 *
	 *
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		Document doc = null;
		IndexReader reader = IndexReader.open("app/SecondLevelSolr/solr2/solr/data/index");
		for (int i = 0; i < reader.maxDoc(); i++) {
			doc = reader.document(i);
			System.out.println(doc);
		}
		reader.clone();
/*
		IndexWriter writer = new IndexWriter("app/SecondLevelSolr/solr1/solr/data/index", new WhitespaceAnalyzer(), false);
		writer.addDocument(doc);
		writer.close();
*/
		reader = IndexReader.open("app/SecondLevelSolr/solr1/solr/data/index");
		for (int i = 0; i < reader.maxDoc(); i++) {
			doc = reader.document(i);
			System.out.println(doc);
		}
		reader.clone();
	}
}
