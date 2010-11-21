package solrbook.analysis;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.BaseTokenFilterFactory;
import org.apache.solr.common.ResourceLoader;
import org.apache.solr.util.plugin.ResourceLoaderAware;

public class POSFilterFactory extends BaseTokenFilterFactory implements ResourceLoaderAware {
	
  private Set<String> posSet;

  public void inform(ResourceLoader loader) {
    String denyPOSFile = args.get("deny");
    if (denyPOSFile != null) {
      try {
        List<String> alist = loader.getLines(denyPOSFile);
        posSet = POSFilter.makePOSSet((String[])alist.toArray(new String[0]));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public TokenStream create(TokenStream input) {
    return new POSFilter(input,posSet);
  }
}
