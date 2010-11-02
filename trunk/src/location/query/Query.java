//---------------------------------------------------------
//Queryクラス
//
//QbSSParserを利用して、正規化とタームの抽出を行う
//---------------------------------------------------------
package location.query;

import java.io.StringReader;
import java.util.ArrayList;

import location.query.parser.*;

public class Query implements QueryParserVisitor {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	//ターム抽出を格納する
	static ArrayList<String> _termList = new ArrayList<String>();

	//クエリ
	static String _query = "";

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ (デフォルト)
	 */
	public Query() {

	}

	//-----------------------------------------------------
	//get・setメソッド
	//-----------------------------------------------------

	/**
	 * getQueryメソッド
	 *
	 * @return
	 */
	public String getQuery() {
		return Query._query;
	}

	/**
	 * setQueryメソッド
	 *
	 * @param query
	 */
	public void setQuery(String query) {
		Query._query = query;
	}

	/**
	 * getTermListメソッド
	 *
	 * @return
	 */
	public ArrayList<String> getTermList() {
		return Query._termList;
	}

	/**
	 * setTermListメソッド
	 *
	 * @param array
	 */
	public void setTermList(ArrayList<String> array) {
		Query._termList = array;
	}

	//-----------------------------------------------------
	//publicメソッド
	//-----------------------------------------------------

	/**
	 * parserメソッド
	 *
	 * @param query
	 * @throws ParseException
	 */
	public void parser(String query) throws ParseException {
		QueryParser parser = new QueryParser(new StringReader(query));
		Query visitor = new Query();
		ASTStart start = parser.Start();
		start.jjtAccept(visitor, null);
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
	 * Start (開始記号)
	 */
	@Override
	public Object visit(ASTStart node, Object data) {
		return node.jjtGetChild(0).jjtAccept(this, null);
	}

	/**
	 * AND演算
	 */
	@Override
	public Object visit(ASTAnd node, Object data) {
		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();
		System.out.println(left + " AND " + right);
		if (!left.equals("")) {
			Query._termList.add(left);
		}
		if (!right.equals("")) {
			Query._termList.add(right);
		}

		Query._query += left + " AND " + right;

		return "";
	}

	/**
	 * OR演算
	 */
	@Override
	public Object visit(ASTOr node, Object data) {
		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();
		System.out.println(left + " OR " + right);
		if (!left.equals("")) {
			Query._termList.add(left);
		}
		if (!right.equals("")) {
			Query._termList.add(right);
		}

		Query._query += left + " OR " + right;

		return "";
	}

	/**
	 * NOT演算
	 */
	@Override
	public Object visit(ASTNot node, Object data) {
		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();
		System.out.println(left + " NOT " + right);
		if (!left.equals("")) {
			Query._termList.add(left);
		}
		if (!right.equals("")) {
			Query._termList.add(right);
		}

		Query._query += left + " NOT " + right;

		return "";
	}

	/**
	 * LP "("
	 */
	@Override
	public Object visit(ASTLp node, Object data) {
		String value = node.nodeValue;
		return value;
	}

	/**
	 * RP ")"
	 */
	@Override
	public Object visit(ASTRp node, Object data) {
		String value = node.nodeValue;
		return value;
	}

	/**
	 * WORD
	 */
	@Override
	public Object visit(ASTWord node, Object data) {
		String value = node.nodeValue;
		return value;
	}

	//-----------------------------------------------------
	//staticメソッド
	//-----------------------------------------------------

	static void sumSet() {

	}

}
