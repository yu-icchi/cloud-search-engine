package lucene;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;


import upload.consistency.ConsistentHashing;

public class IndexReaderToWriter {

	/**
	 *
	 *
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {

		ConsistentHashing hash = new ConsistentHashing();

		hash.addNode("localhost:8081", "localhost:8082", "localhost:8083", "localhost:8084", "localhost:8085");
		hash.addNode("localhost:8086", "localhost:8087", "localhost:8088", "localhost:8089", "localhost:8090");

		IndexWriter writer = new IndexWriter("app/SecondLevelSolr/solr1/solr/data/index", new WhitespaceAnalyzer(), true);

		Document doc = null;
		IndexReader reader = IndexReader.open("app/SecondLevelSolr/solr2/solr/data/index");
		for (int i = 0; i < reader.maxDoc(); i++) {
			doc = reader.document(i);
			String node = hash.searchNode(doc.get("id"));
			if (node.equals("localhost:8082")) {
				writer.addDocument(doc);
			}
		}
		reader.clone();
		writer.close();

		reader = IndexReader.open("app/SecondLevelSolr/solr1/solr/data/index");
		for (int i = 0; i < reader.maxDoc(); i++) {
			doc = reader.document(i);
			System.out.println(doc);
		}
		reader.clone();
	}
}
