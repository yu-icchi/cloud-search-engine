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
		Query._query = (String) start.jjtAccept(visitor, null);
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
		String word = "";
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			word += node.jjtGetChild(i).jjtAccept(this, null);
		}
		return word;
	}

	/**
	 * AND演算
	 */
	@Override
	public Object visit(ASTAnd node, Object data) {
		/*
		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();
		return left + " AND " + right;
		*/
		return " AND ";
	}

	/**
	 * OR演算
	 */
	@Override
	public Object visit(ASTOr node, Object data) {
		/*
		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();
		return left + " OR " + right;
		*/
		return " OR ";
	}

	/**
	 * NOT演算
	 */
	@Override
	public Object visit(ASTNot node, Object data) {
		/*
		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();
		return left + " NOT " + right;
		*/
		return " NOT ";
	}

	/**
	 * WORD
	 */
	@Override
	public Object visit(ASTWord node, Object data) {
		String value = node.nodeValue;
		//重複を許さないぞ！！
		if (!Query._termList.contains(value)) {
			Query._termList.add(value);
		}
		return value;
	}

	/**
	 * LP
	 */
	@Override
	public Object visit(ASTLp node, Object data) {
		return node.nodeValue;
	}

	/**
	 * RP
	 */
	@Override
	public Object visit(ASTRp node, Object data) {
		return node.nodeValue;
	}

}
