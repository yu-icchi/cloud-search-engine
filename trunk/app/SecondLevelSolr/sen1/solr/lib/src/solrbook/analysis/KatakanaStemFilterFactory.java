package solrbook.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.BaseTokenFilterFactory;

public class KatakanaStemFilterFactory extends BaseTokenFilterFactory {

  public TokenStream create(TokenStream input) {
    return new KatakanaStemFilter(input);
  }
}
