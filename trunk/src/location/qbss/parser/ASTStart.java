/* Generated By:JJTree: Do not edit this line. ASTStart.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=location.qbss.BaseNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package location.qbss.parser;

public
class ASTStart extends SimpleNode {
  public ASTStart(int id) {
    super(id);
  }

  public ASTStart(QbSSParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(QbSSParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=cf8baa92256e883543dade2c703a9c00 (do not edit this line) */