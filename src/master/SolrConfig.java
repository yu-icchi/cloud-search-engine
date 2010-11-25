//---------------------------------------------------------
//SolrConfigクラス
//
//
//---------------------------------------------------------
package master;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SolrConfig {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	private static DocumentBuilderFactory _factory;
	private static DocumentBuilder _docBuilder;
	private static Document _document;

	//デフォルトの出力先
	private static final String _path = "demo/sampleSolrConfig.xml";

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ (デフォルト)
	 */
	public SolrConfig() {
		//XML作成の初期設定
		_factory = DocumentBuilderFactory.newInstance();
		try {
			_docBuilder = _factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		_document = _docBuilder.newDocument();
	}

	//-----------------------------------------------------
	//メソッド
	//-----------------------------------------------------

	/**
	 * xmlDocumentメソッド (XMLの内容を編集する)
	 */
	public void xmlDocument() {
		try {
			//solrconfigノード作成
			Element root = _document.createElement("solrconfig");
			//ノードをDocumentに追加
			_document.appendChild(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * readerXMLメソッド (URLを指定し、XMLを読み込む)
	 *
	 * @param url
	 */
	public void readerXML(String url) {

	}

	/**
	 * showメソッド (メモリ上にあるXMLの内容を表示する)
	 */
	public void show() {

	}

	/**
	 * fileWriterメソッド (パスを指定しXMLを保存する)
	 *
	 * @param path
	 */
	public void fileWrite() {
		try {
			//ディレクトリにXMLファイルを書き出す
			TransformerFactory tfactory = TransformerFactory.newInstance();
			Transformer transformer = tfactory.newTransformer();
			//保存するPATHを指定する
			File outfile = new File(_path);
			transformer.transform(new DOMSource(_document), new StreamResult(outfile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//-----------------------------------------------------
	//テスト用
	//-----------------------------------------------------
	public static void main(String[] args) {
		SolrConfig conf = new SolrConfig();
		conf.xmlDocument();
		conf.fileWrite();
	}
}
