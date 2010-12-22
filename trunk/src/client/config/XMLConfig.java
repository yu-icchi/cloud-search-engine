//---------------------------------------------------------
//GSEConfigクラス
//
//"Global Search Engine"の設定ファイル情報を読み込む
//---------------------------------------------------------
package client.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLConfig {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	private static Document document;
	private static Element root;

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ
	 *
	 * @param path
	 * @throws Exception
	 */
	public XMLConfig(String path) throws Exception {
		File file = new File(path);
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		document = builder.parse(file);
		root = document.getDocumentElement();
	}

	//-----------------------------------------------------
	//設定データを取得するメソッド
	//-----------------------------------------------------

	/**
	 * getElementメソッド
	 *
	 * @param name
	 * @return
	 */
	public Map<String, String> getHost2Port(String name) {
		Map<String, String> map = new HashMap<String, String>();
		NodeList list = root.getElementsByTagName(name);
		for (int i = 0; i < list.getLength(); i++) {
			Element element = (Element) list.item(i);
			map.put("host", getChildren(element, "host"));
			map.put("port", getChildren(element, "port"));
		}
		return map;
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	public List<String> getNodes(String name) {
		List<String> nodes = new ArrayList<String>();
		NodeList list = root.getElementsByTagName(name);
		for (int i = 0; i < list.getLength(); i++) {
			Element cElement = (Element) list.item(i);
			nodes.add(cElement.getFirstChild().getNodeValue());
		}
		return nodes;
	}

	/**
	 * getChildrenメソッド
	 *
	 * @param element
	 * @param tag
	 * @return
	 */
	private static String getChildren(Element element, String tag) {
		NodeList data = element.getElementsByTagName(tag);
		Element cElement = (Element) data.item(0);
		return cElement.getFirstChild().getNodeValue();
	}

	public static void main(String[] args) throws Exception {
		XMLConfig config = new XMLConfig("demo/gse-config.xml");
		System.out.println(config.getHost2Port("location"));
		System.out.println(config.getHost2Port("account"));
		System.out.println(config.getNodes("node"));
	}
}
