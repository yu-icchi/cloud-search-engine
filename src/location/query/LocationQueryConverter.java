//---------------------------------------------------------
//Queryクラス
//
//QbSSParserを利用して、正規化とタームの抽出を行う
//---------------------------------------------------------
package location.query;

import java.io.StringReader;
import java.util.ArrayList;

import analysis.CJKAnalyzerExtract;
import analysis.SenAnalyzerExtract;

import location.query.parser.*;

public class LocationQueryConverter implements QueryParserVisitor {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	//ターム抽出を格納する
	private static ArrayList<String> _termList = new ArrayList<String>();

	//クエリ
	private static String _query = "";

	//Analyzer
	private static String _analyzer = null;

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ (デフォルト)
	 */
	public LocationQueryConverter() {

	}

	/**
	 * コンストラクタ (Analyzerの指定)
	 *
	 * @param type
	 */
	public LocationQueryConverter(String type) {
		LocationQueryConverter._analyzer = type;
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
		return LocationQueryConverter._query;
	}

	/**
	 * setQueryメソッド
	 *
	 * @param query
	 */
	public void setQuery(String query) {
		LocationQueryConverter._query = query;
	}

	/**
	 * getTermListメソッド
	 *
	 * @return
	 */
	public ArrayList<String> getTermList() {
		return LocationQueryConverter._termList;
	}

	/**
	 * setTermListメソッド
	 *
	 * @param array
	 */
	public void setTermList(ArrayList<String> array) {
		LocationQueryConverter._termList = array;
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
		LocationQueryConverter visitor = new LocationQueryConverter();
		ASTStart start = parser.Start();
		LocationQueryConverter._query = (String) start.jjtAccept(visitor, null);
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
		String word = value;
		//重複を許さないぞ！！
		if (!LocationQueryConverter._termList.contains(value)) {
			//t.getBoostaaでの"^"以降を除く
			if (value.contains("^")) {
				//^が存在するまでの文字列を取り出す
				word = value.substring(0, value.indexOf("^"));
			}
			//タームリストに追加する
			LocationQueryConverter._termList.add(word);
		}

		//Analyzerによって変更させる
		if (LocationQueryConverter._analyzer.equals("sen")) {
			//LocationのSen用に変更
			SenAnalyzerExtract extract = new SenAnalyzerExtract();
			try {
				word = extract.qbssExtract(word);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (LocationQueryConverter._analyzer.equals("cjk")) {
			//LocationのNgram用に変更
			CJKAnalyzerExtract extract = new CJKAnalyzerExtract();
			try {
				word = extract.qbssExtract(word);
				//System.out.println(word);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return word;
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
