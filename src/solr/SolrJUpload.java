//---------------------------------------------------------
//SolrJUploadクラス(未完成)
//
//SolrJを利用してSolrにアップする
//---------------------------------------------------------
package solr;

import java.io.File;
import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;

public class SolrJUpload {

	public static void main(String[] args) {
		try {
			String fileName = "demo/sample.doc";
			String solrId = "sample.doc";

			indexFilesSolrCell(fileName, solrId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param fileName
	 * @param solrId
	 * @throws IOException
	 * @throws SolrServerException
	 */
	public static void indexFilesSolrCell(String fileName, String solrId) throws IOException, SolrServerException {
		String url = "http://localhost:8983/solr";
		SolrServer solr = new CommonsHttpSolrServer(url);

		ContentStreamUpdateRequest up = new ContentStreamUpdateRequest("/update/extract");

		up.addFile(new File(fileName));

		up.setParam("literal.id", solrId);
		up.setParam("uprefix", "attr_");
		up.setParam("fmap.content", "attr_content");

		up.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);

		solr.request(up);

		QueryResponse rsp = solr.query(new SolrQuery("*:*"));

		System.out.println(rsp);
	}
}
