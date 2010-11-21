package solrbook.analysis;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.solr.analysis.BaseTokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SenTokenizerFactory extends BaseTokenizerFactory {
  
  private static final Logger log = LoggerFactory.getLogger( SenTokenizerFactory.class );
  private static final String PROP_SEN_HOME = "sen.home";
  private static final String FS = System.getProperty("file.separator");
  private static final String SEN_XML = FS + "conf" + FS + "sen.xml";
  private String configFile;

  public void init(Map<String, String> args) {
    // Try JNDI
    try {
      Context c = new InitialContext();
      configFile = (String)c.lookup("java:comp/env/sen/home") + SEN_XML; 
    } catch (NoInitialContextException e) {
      log.info("JNDI not configured for Solr (NoInitialContextEx)");
    } catch (NamingException e) {
      log.info("No /sen/home in JNDI");
    } catch( RuntimeException ex ) {
      log.warn("Odd RuntimeException while testing for JNDI: " 
          + ex.getMessage());
    } 

    // Now try system property
    if( configFile == null ){
      configFile = System.getProperty(PROP_SEN_HOME) + SEN_XML;
    }
    
    log.info( "config file for SenTikenizer is " + configFile );
  }
  
  public Tokenizer create(Reader input) {
    try {
      return new SenTokenizer( input, configFile );
    } catch (IOException e) {
      throw new RuntimeException( "cannot initialize SenTokenizer : " + e.toString() );
    }
  }
}
