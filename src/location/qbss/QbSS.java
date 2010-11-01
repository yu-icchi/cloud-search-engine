//---------------------------------------------------------
//QbSS (Query based Server Selection)クラス
//
//検索クエリからアクセス先サーバを絞る
//---------------------------------------------------------
package location.qbss;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import location.qbss.parser.*;

public class QbSS implements QbSSParserVisitor {

	//-----------------------------------------------------
	//プロパティ設定
	//-----------------------------------------------------

	//クエリーデータ
	static String _query;

	//Cassandraからのデータ
	static Map<String, List<String>> _data;

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
	 * @param data
	 */
	public QbSS(String query, Map<String, List<String>> data) {
		QbSS._query = query;
		QbSS._data = data;
	}

	//-----------------------------------------------------
	//get・setメソッド
	//-----------------------------------------------------

	/**
	 * setDataメソッド
	 *
	 * @param data
	 */
	public static void setData(Map<String, List<String>> data) {
		QbSS._data = data;
	}

	/**
	 * setQueryメソッド
	 *
	 * @param query
	 */
	public static void setQuery(String query) {
		QbSS._query = query;
	}

	//-----------------------------------------------------
	//publicメソッド
	//-----------------------------------------------------

	/**
	 * parserメソッド (クエリーの解析を行いQbSSの結果を返す)
	 *
	 * @return
	 * @throws ParseException
	 */
	public Object parser() throws ParseException {
		QbSSParser parser = new QbSSParser(new StringReader(_query));
		QbSS visitor = new QbSS();
		ASTStart start = parser.Start();
		String query = start.jjtAccept(visitor, null).toString();
		return _data.get(query);
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
	 * AND演算
	 */
	@Override
	public Object visit(ASTAnd node, Object data) {
		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();
		//System.out.println(left + " and " + right);

		//URLリストのAND演算処理 (総当り法)
		List<String> leftList = _data.get(left);
		List<String> rightList = _data.get(right);
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < leftList.size(); i++) {
			if (rightList.contains(leftList.get(i))) {
				//System.out.println(leftList.get(i));
				list.add(leftList.get(i));
			}
		}
		_data.put(left + "AND" + right, list);

		return left + "AND" + right;
	}

	/**
	 * OR演算
	 */
	@Override
	public Object visit(ASTOr node, Object data) {
		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();
		//System.out.println(left + " or " + right);

		//URLリストのOR演算処理 (総当り法)
		List<String> leftList = _data.get(left);
		List<String> rightList = _data.get(right);
		for (int i = 0; i < leftList.size(); i++) {
			if (!rightList.contains(leftList.get(i))) {
				rightList.add(leftList.get(i));
			}
		}
		//System.out.println(rightList);
		_data.put(left + "OR" + right, rightList);

		return left + "OR" + right;
	}

	/**
	 * NOT演算
	 */
	@Override
	public Object visit(ASTNot node, Object data) {
		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();
		//System.out.println(left + " not " + right);

		//URLリストのNOT演算処理（総当り法）
		List<String> leftList = _data.get(left);
		List<String> rightList = _data.get(right);
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < leftList.size(); i++) {
			if (!rightList.contains(leftList.get(i))) {
				//System.out.println(leftList.get(i));
				list.add(leftList.get(i));
			}
		}
		_data.put(left + "NOT" + right, list);

		return left + "NOT" + right;
	}

	/**
	 * Word
	 */
	@Override
	public Object visit(ASTWord node, Object data) {
		String value = node.nodeValue;
		return value;
	}

	/**
	 * Quote
	 */
	@Override
	public Object visit(ASTQuote node, Object data) {
		int n = node.jjtGetNumChildren();
		System.out.println("Quote: " + n);
		return "";
	}



}
