/* Generated By:JJTree: Do not edit this line. ASTAnd.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=location.qbss.BaseNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package location.qbss.parser;

public
class ASTAnd extends SimpleNode {
  public ASTAnd(int id) {
    super(id);
  }

  public ASTAnd(QbSSParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(QbSSParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=e74785fee511e377182f2e0cdf8af7a9 (do not edit this line) */
