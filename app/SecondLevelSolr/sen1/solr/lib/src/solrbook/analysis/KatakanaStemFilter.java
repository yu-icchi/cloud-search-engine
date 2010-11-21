package solrbook.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

public final class KatakanaStemFilter extends TokenFilter {

  static final char COMBINING_KATAKANA_HIRAGANA_VOICED_SOUND_MARK = '\u3099';
  static final char COMBINING_KATAKANA_HIRAGANA_SEMI_VOICED_SOUND_MARK = '\u309A';
  static final char KATAKANA_HIRAGANA_VOICED_SOUND_MARK = '\u309B';
  static final char KATAKANA_HIRAGANA_SEMI_VOICED_SOUND_MARK = '\u309C';
  static final char KATAKANA_HIRAGANA_PROLONGED_SOUND_MARK = '\u30FC';

  public KatakanaStemFilter(TokenStream in) {
    super(in);
  }

  /**
   * Returns the next input Token, after being stemmed
   */
  public Token next( Token token ) throws IOException {
    Token t = input.next( token );
    if (t == null)
      return null;
    String s = t.termText();
    int len = s.length();
    if (len > 3
        && s.charAt(len - 1) == KATAKANA_HIRAGANA_PROLONGED_SOUND_MARK
        && isKatakanaString(s)) {
      return token.reinit(s.substring(0, len - 1), t.startOffset(), t.endOffset(), t.type());
    }
    return t;
  }

  boolean isKatakanaString(String s) {
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.KATAKANA
          && c != COMBINING_KATAKANA_HIRAGANA_VOICED_SOUND_MARK
          && c != COMBINING_KATAKANA_HIRAGANA_SEMI_VOICED_SOUND_MARK
          && c != KATAKANA_HIRAGANA_VOICED_SOUND_MARK
          && c != KATAKANA_HIRAGANA_SEMI_VOICED_SOUND_MARK)
        return false;
    }
    return true;
  }
}
