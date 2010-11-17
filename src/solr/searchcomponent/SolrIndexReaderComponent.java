//---------------------------------------------------------
//SolrIndexReaderComponentクラス
//
//indexの内容を表示するだけのサーチコンポーネント
//---------------------------------------------------------
package solr.searchcomponent;

import java.io.IOException;

import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;

public class SolrIndexReaderComponent extends SearchComponent {

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getSource() {
		return null;
	}

	@Override
	public String getSourceId() {
		return null;
	}

	@Override
	public String getVersion() {
		return null;
	}

	@Override
	public void prepare(ResponseBuilder arg0) throws IOException {

	}

	@Override
	public void process(ResponseBuilder arg0) throws IOException {

	}

}
