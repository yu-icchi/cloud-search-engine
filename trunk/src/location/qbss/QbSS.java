//---------------------------------------------------------
//QbSS (Query based Server Selection)クラス
//
//検索クエリからアクセス先サーバを絞る
//---------------------------------------------------------
package location.qbss;

import java.io.*;

import location.qbss.parser.*;

public class QbSS implements QbSSParserVisitor {

	/**
	 * mainメソッド
	 *
	 * @param ags
	 * @throws Exception
	 */
	public static void main(String[] ags) throws Exception {
		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(in);
		String line;
		while ((line = reader.readLine()) != null) {
			QbSSParser parser = new QbSSParser(new StringReader(line));
			QbSS visitor = new QbSS();
			ASTStart start = parser.Start();
			System.out.println(start.jjtAccept(visitor, null));
		}
	}

	//-----------------------------------------------------
	//プロパティ設定
	//-----------------------------------------------------

	static String _query;

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストタクタ
	 */
	public QbSS() {

	}

	/**
	 * コンストタクタ(引数あり)
	 *
	 * @param query
	 */
	public QbSS(String query) {
		QbSS._query = query;
	}

	//-----------------------------------------------------
	//オーバーライド定義
	//-----------------------------------------------------

	/**
	 * ここには来ない
	 */
	@Override
	public Object visit(SimpleNode node, Object data) {
		return null;
	}

	/**
	 * Start(開始記号)
	 */
	@Override
	public Object visit(ASTStart node, Object data) {
		return node.jjtGetChild(0).jjtAccept(this, null);
	}

	/**
	 * AND演算の場合
	 */
	@Override
	public Object visit(ASTAnd node, Object data) {
		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();
		System.out.println(left + " and " + right);
		return node.jjtGetChild(0).jjtAccept(this, null);
	}

	/**
	 * OR演算の場合
	 */
	@Override
	public Object visit(ASTOr node, Object data) {
		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();
		System.out.println(left + " or " + right);
		return node.jjtGetChild(0).jjtAccept(this, null);
	}

	/**
	 * NOT演算の場合
	 */
	@Override
	public Object visit(ASTNot node, Object data) {
		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();
		System.out.println(left + " not " + right);
		return node.jjtGetChild(0).jjtAccept(this, null);
	}

	/**
	 * Word
	 */
	@Override
	public Object visit(ASTWord node, Object data) {
		String value = node.nodeValue;
		return value;
	}

}
