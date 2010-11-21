package solrbook.analysis;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

public final class POSFilter extends TokenFilter {
	
  private final Set<String> posSet;

  /**
   * Construct a filter which removes unspecified pos from the input
   * TokenStream.
   */
  public POSFilter(TokenStream in, String[] posArray) {
    super(in);
    posSet = makePOSSet( posArray );
  }

  /**
   * Construct a filter which removes unspecified pos from the input
   * TokenStream.
   */
  public POSFilter(TokenStream in, Set<String> posSet) {
    super(in);
    this.posSet = posSet;
  }

  /**
   * Builds a hashtable from an array of pos.
   */
  public final static Set<String> makePOSSet(String[] posArray) {
    Set<String> posSet = new HashSet<String>( posArray.length );
    for( String pos : posArray )
      posSet.add( pos );
    return posSet;
  }

  /**
   * Returns the next token in the stream, or null at EOS.
   * <p>
   * Removes a specified part of speech.
   */
  public final Token next() throws IOException {
    Token t;
    while (true) {
      t = input.next();
      if (t == null)
        return null;
      if( posSet == null )
        return t;
      if (!posSet.contains(t.type()))
        return t;
    }
  }
}
