/* Generated By:JJTree: Do not edit this line. ASTAnd.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=location.query.BaseNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package location.query.parser;

public
class ASTAnd extends SimpleNode {
  public ASTAnd(int id) {
    super(id);
  }

  public ASTAnd(QueryParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(QueryParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=dbfeed110cba8a6e63fc35b42dbe7883 (do not edit this line) */