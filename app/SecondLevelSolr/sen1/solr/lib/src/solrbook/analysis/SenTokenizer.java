package solrbook.analysis;

import java.io.IOException;
import java.io.Reader;

import net.java.sen.StreamTagger;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;

public class SenTokenizer extends Tokenizer {
  private StreamTagger tagger = null;
  private String configFile;

  public SenTokenizer(Reader in, String configFile) throws IOException {
    super( in );
    this.configFile = configFile;
    tagger = new StreamTagger(input, this.configFile);
  }
  
  public Token next( Token token ) throws IOException {
    if (!tagger.hasNext()) return null;
    net.java.sen.Token t = tagger.next();

    if (t == null) return next( token );

    token.reinit(t.getBasicString(),
        correctOffset( t.start() ),
        correctOffset( t.end() ),
        t.getPos());
    return token;
  }

  public void reset( Reader in ) throws IOException {
    super.reset( in );
    tagger = new StreamTagger(input, this.configFile);
  }
}
