//---------------------------------------------------------
//SolrConfigクラス
//
//修正できる「solrconfig.xml」のファイルを作成する
//---------------------------------------------------------
package master;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.serializer.OutputPropertiesFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class SolrConfig {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	//XML作成に必要な変数
	private static DocumentBuilderFactory _factory;
	private static DocumentBuilder _docBuilder;
	private static Document _document;
	private static Element _root;

	//デフォルトの出力先
	private static String _path = "demo/sampleSolrConfig.xml";
	//キャッシュのサイズ
	//private static String cacheSize = "512";
	//文書からトークナイヅされたあとの単語列先頭から検索対象にする値
	private static String maxFieldLength = "10000";
	//合成ファイルインデックス(true)、複数ファイルインデックス(false)
	private static String uesCompoundFile = "false";
	//インデックスファイルにフラッシュされる前にメモリ保存
	private static String ramBufferSizeMB = "32";
	//インデックスファイルセグメントのマージのタイミング
	private static String mergeFactor = "10";
	//インデックスファイルは更新するときにLuceneによってロックされる
	private static String lockType = "native";

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ (デフォルト)
	 */
	public SolrConfig() {
		this.init();
	}

	//-----------------------------------------------------
	//ゲッター・セッター
	//-----------------------------------------------------

	/**
	 * setPathメソッド
	 *
	 * @param path
	 */
	public void setPath(String path) {
		SolrConfig._path = path;
	}

	/**
	 * setUseCompoundFileメソッド
	 *
	 * @param bool
	 */
	public void setUseCompoundFile(boolean bool) {
		if (bool) {
			SolrConfig.uesCompoundFile = "true";
		} else {
			SolrConfig.uesCompoundFile = "false";
		}
	}

	/**
	 * setRamBufferSizeMBメソッド
	 *
	 * @param size
	 */
	public void setRamBufferSizeMB(int size) {
		SolrConfig.ramBufferSizeMB = Integer.toString(size);
	}

	/**
	 * setMergeFactorメソッド
	 *
	 * @param merge
	 */
	public void setMergeFactor(int merge) {
		SolrConfig.mergeFactor = Integer.toString(merge);
	}

	/**
	 * setMaxFieldLengthメソッド
	 *
	 * @param length
	 */
	public void setMaxFieldLength(int length) {
		SolrConfig.maxFieldLength = Integer.toString(length);
	}

	/**
	 * setCacheSizeメソッド
	 *
	 * @param size
	 */
	//public void setCacheSize(int size) {
	//	SolrConfig.cacheSize = Integer.toString(size);
	//}

	/**
	 * setLockTypeメソッド
	 *
	 * @param type
	 */
	public void setLockType(String type) {
		if (type.equals("single")) {
			SolrConfig.lockType = "single";
		} else if (type.equals("native")) {
			SolrConfig.lockType = "native";
		} else if (type.equals("simple")) {
			SolrConfig.lockType = "simple";
		}
	}

	//-----------------------------------------------------
	//メソッド
	//-----------------------------------------------------

	/**
	 * initメソッド (初期化)
	 */
	public void init() {
		try {
			//XML作成の初期設定
			_factory = DocumentBuilderFactory.newInstance();
			_docBuilder = _factory.newDocumentBuilder();
			_document = _docBuilder.newDocument();
			_root = _document.createElement("config");
			_document.appendChild(_root);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * abortOnConfigurationErrorメソッド
	 */
	public void abortOnConfigurationError() {
		_root.appendChild(setElement("abortOnConfigurationError", null, null, "${solr.abortOnConfigurationError:true}"));
	}

	/**
	 * libメソッド
	 */
	public void lib() {
		_root.appendChild(setElement("lib", "dir", "../../contrib/extraction/lib", null));
		String[] tags = {"dir","regex"};
		String[] tagdatas = {"../../dist/", "apache-solr-cell-\\d.*\\.jar"};
		_root.appendChild(setElement2("lib", tags, tagdatas, null));
		tagdatas[1] = "apache-solr-clustering-\\d.*\\.jar";
		_root.appendChild(setElement2("lib", tags, tagdatas, null));
		_root.appendChild(setElement("lib", "dir", "../../contrib/clustering/lib/downloads/", null));
		_root.appendChild(setElement("lib", "dir", "../../contrib/clustering/lib/", null));
		_root.appendChild(setElement("lib", "dir", "/total/crap/dir/ignored", null));
	}

	/**
	 * dataDirメソッド
	 */
	public void dataDir() {
		_root.appendChild(setElement("dataDir", null, null, "${solr.data.dir}"));
	}

	/**
	 * dataDirメソッド
	 *
	 * @param path
	 */
	public void dataDir(String path) {
		_root.appendChild(setElement("dataDir", null, null, "${solr.data.dir:" + path + "}"));
	}

	/**
	 * indexDefaultsメソッド
	 */
	public void indexDefaults() {
		Element indexDefaultsElement = _document.createElement("indexDefaults");
		indexDefaultsElement.appendChild(setElement("useCompoundFile", null, null, uesCompoundFile));
		indexDefaultsElement.appendChild(setElement("mergeFactor", null, null, mergeFactor));
		indexDefaultsElement.appendChild(setElement("ramBufferSizeMB", null, null, ramBufferSizeMB));
		indexDefaultsElement.appendChild(setElement("maxFieldLength", null, null, maxFieldLength));
		indexDefaultsElement.appendChild(setElement("writeLockTimeout", null, null, "10000"));
		indexDefaultsElement.appendChild(setElement("commitLockTimeout", null, null, "10000"));
		indexDefaultsElement.appendChild(setElement("lockType", null, null, lockType));
		_root.appendChild(indexDefaultsElement);
	}

	/**
	 * mainIndexメソッド
	 */
	public void mainIndex() {
		Element mainIndex = _document.createElement("mainIndex");
		mainIndex.appendChild(setElement("useCompoundFile", null, null, uesCompoundFile));
		mainIndex.appendChild(setElement("ramBufferSizeMB", null, null, ramBufferSizeMB));
		mainIndex.appendChild(setElement("mergeFactor", null, null, mergeFactor));
		mainIndex.appendChild(setElement("unlockOnStartup", null, null, "false"));
		mainIndex.appendChild(setElement("reopenReaders", null, null, "true"));
		//deletionPolicy
		Element deletionPolicyElement = _document.createElement("deletionPolicy");
		deletionPolicyElement.setAttribute("class", "solr.SolrDeletionPolicy");
		deletionPolicyElement.appendChild(setElement("str", "name", "maxCommitsToKeep", "1"));
		deletionPolicyElement.appendChild(setElement("str", "name", "maxOptimizedCommitsToKeep", "0"));
		mainIndex.appendChild(deletionPolicyElement);
		mainIndex.appendChild(setElement("infoStream", "file", "INFOSTREAM.txt", "false"));
		_root.appendChild(mainIndex);
	}

	/**
	 * jmxメソッド
	 */
	public void jmx() {
		_root.appendChild(setElement("jmx", null, null, null));
	}

	/**
	 * updateHandlerメソッド
	 */
	public void updateHandler() {
		_root.appendChild(setElement("updateHandler", "class", "solr.DirectUpdateHandler2", null));
	}

	/**
	 * queryメソッド
	 */
	public void query() {
		Element queryElement = _document.createElement("query");
		queryElement.appendChild(setElement("maxBooleanClauses", null, null, "1024"));
		String[] updateHandlerTag = {"class", "size", "initialSize", "autowarmCount"};
		String[] updateHandlerTagData = {"solr.FastLRUCache", "512", "512", "0"};
		queryElement.appendChild(setElement2("filterCache", updateHandlerTag, updateHandlerTagData, null));
		updateHandlerTagData[0] = "solr.LRUCache";
		queryElement.appendChild(setElement2("queryResultCache", updateHandlerTag, updateHandlerTagData, null));
		queryElement.appendChild(setElement2("documentCache", updateHandlerTag, updateHandlerTagData, null));
		queryElement.appendChild(setElement("enableLazyFieldLoading", null, null, "true"));
		queryElement.appendChild(setElement("queryResultWindowSize", null, null, "20"));
		queryElement.appendChild(setElement("queryResultMaxDocsCached", null, null, "200"));
		String[] listenerTag = {"event", "class"};
		String[] listenerTagData = {"newSearcher", "solr.QuerySenderListener"};
		Element listenerElement = setElement2("listener", listenerTag, listenerTagData, null);
		listenerElement.appendChild(setElement("arr", "name", "queries", null));
		queryElement.appendChild(listenerElement);
		listenerTagData[0] = "firstSearcher";
		Element listenerElement2 = setElement2("listener", listenerTag, listenerTagData, null);
		Element queries = setElement("arr", "name", "queries", null);
		Element lst1 = setElement("lst", null, null, null);
		lst1.appendChild(setElement("str", "name", "q", "solr rocks"));
		lst1.appendChild(setElement("str", "name", "start", "0"));
		lst1.appendChild(setElement("str", "name", "rows", "10"));
		queries.appendChild(lst1);
		Element lst2 = setElement("lst", null, null, null);
		lst2.appendChild(setElement("str", "name", "q", "static firstSearcher warming query from solrconfig.xml"));
		queries.appendChild(lst2);
		listenerElement2.appendChild(queries);
		queryElement.appendChild(listenerElement2);
		queryElement.appendChild(setElement("useColdSearcher", null, null, "false"));
		queryElement.appendChild(setElement("maxWarmingSearchers", null, null, "2"));
		_root.appendChild(queryElement);
	}

	/**
	 * requestDispatcherメソッド
	 */
	public void requestDispatcher() {
		Element requestDirspatcherElement = setElement("requestDispatcher", "handleSelect", "true", null);
		String[] requestParsersTag = {"enableRemoteStreaming", "multipartUploadLimitInKB"};
		String[] requestParsersData = {"true", "2048000"};
		requestDirspatcherElement.appendChild(setElement2("requestParsers", requestParsersTag, requestParsersData, null));
		String[] httpCachingTag = {"lastModifiedFrom", "etagSeed"};
		String[] httpCachingData = {"openTime", "Solr"};
		requestDirspatcherElement.appendChild(setElement2("httpCaching", httpCachingTag, httpCachingData, null));
		_root.appendChild(requestDirspatcherElement);
	}

	/**
	 * highlightingメソッド
	 */
	public void highlighting() {
		Element highlight = setElement("highlighting", null, null, null);
		String[] fragmenterTag = {"name", "class", "default"};
		String[] fragmenterData = {"gap", "org.apache.solr.highlight.GapFragmenter", "true"};
		Element fragmenter = setElement2("fragmenter", fragmenterTag, fragmenterData, null);
		Element fragmenterLst = setElement("lst", "name", "defualts", null);
		fragmenterLst.appendChild(setElement("int", "name", "hl.fragsize", "100"));
		fragmenter.appendChild(fragmenterLst);
		highlight.appendChild(fragmenter);
		String[] fragmenterTag2 = {"name", "class"};
		String[] fragmenterData2 = {"regex", "org.apache.solr.highlight.RegexFragmenter"};
		Element fragmenter2 = setElement2("fragmenter", fragmenterTag2, fragmenterData2, null);
		Element fragmenterLst2 = setElement("lst", "name", "defualts", null);
		fragmenterLst2.appendChild(setElement("int", "name", "hl.fragsize", "70"));
		fragmenterLst2.appendChild(setElement("float", "name", "hl.regex.slop", "0.5"));
		fragmenterLst2.appendChild(setElement("str", "name", "hl.regex.pattern", "[-\\w ,/\\n\\\"']{20,200}"));
		fragmenter2.appendChild(fragmenterLst2);
		highlight.appendChild(fragmenter2);
		String[] formatterData = {"html", "org.apache.solr.highlight.HtmlFormatter", "true"};
		Element formatter = setElement2("formatter", fragmenterTag, formatterData, null);
		Element formatterLst = setElement("lst", "name", "defualts", null);
		formatterLst.appendChild(setElement("str", "name", "hl.simple.pre","<![CDATA[<em>]]>"));
		formatterLst.appendChild(setElement("str", "name", "hl.simple.post","<![CDATA[</em>]]>"));
		formatter.appendChild(formatterLst);
		highlight.appendChild(formatter);
		_root.appendChild(highlight);
	}

	/**
	 * queryResponseWriterメソッド
	 */
	public void queryResponseWriter() {
		String[] tags2 = {"name", "class"};
		String[] datas2 = {"xslt", "org.apache.solr.request.XSLTResponseWriter"};
		Element queryResponseWriter = setElement2("queryResponseWriter", tags2, datas2, null);
		queryResponseWriter.appendChild(setElement("int", "name", "xsltCacheLifetimeSeconds", "5"));
		_root.appendChild(queryResponseWriter);
	}

	/**
	 * replicationHandlerSlaveメソッド
	 *
	 * @param host
	 * @param interval
	 */
	public void replicationHandlerSlave(String host, String interval) {
		String[] replicationTag = {"name", "class"};
		String[] replicationData = {"/replication", "solr.SearchHandler"};
		Element replication = setElement2("requestHandler", replicationTag, replicationData, null);
		Element replicationLst = setElement("lst", "name", "slave", null);
		replicationLst.appendChild(setElement("str", "name", "masterUrl", host));
		replicationLst.appendChild(setElement("str", "name", "pollInterval", interval));
		replicationLst.appendChild(setElement("str", "name", "compression", "internal"));
		replication.appendChild(replicationLst);
		_root.appendChild(replication);
	}

	/**
	 * replicationHandlerMasterメソッド
	 *
	 * @param replicateAfter
	 */
	public void replicationHandlerMaster(String... replicateAfter) {
		String[] replicationTag = {"name", "class"};
		String[] replicationData = {"/replication", "solr.SearchHandler"};
		Element replication = setElement2("requestHandler", replicationTag, replicationData, null);
		Element replicationLst = setElement("lst", "name", "master", null);
		for (String str : replicateAfter) {
			replicationLst.appendChild(setElement("str", "name", "replicateAfter", str));
		}
		replication.appendChild(replicationLst);
		_root.appendChild(replication);
	}

	/**
	 * replicationHandlerメソッド (Master/Slave)
	 *
	 * @param host
	 * @param interval
	 * @param data
	 */
	public void replicationHandler(String host, String interval, String... data) {
		String[] replicationTag = {"name", "class"};
		String[] replicationData = {"/replication", "solr.SearchHandler"};
		Element replication = setElement2("requestHandler", replicationTag, replicationData, null);
		//Master
		Element master = setElement("lst", "name", "master", null);
		for (String str : data) {
			master.appendChild(setElement("str", "name", "replicateAfter", str));
		}
		replication.appendChild(master);
		//Slave
		Element slave = setElement("lst", "name", "slave", null);
		slave.appendChild(setElement("str", "name", "masterUrl", host));
		slave.appendChild(setElement("str", "name", "pollInterval", interval));
		slave.appendChild(setElement("str", "name", "compression", "internal"));
		replication.appendChild(slave);
		_root.appendChild(replication);
	}

	/**
	 * standardHandlerメソッド
	 */
	public void standardHandler() {
		String[] tag = {"name", "class", "default"};
		String[] data = {"standard", "solr.SearchHandler", "true"};
		Element standard = setElement2("requestHandler", tag, data, null);
		Element standardLst = setElement("lst", "name", "defaults", null);
		standardLst.appendChild(setElement("str", "name", "echoParams", "explicit"));
		standard.appendChild(standardLst);
		_root.appendChild(standard);
	}

	/**
	 * adminHandlerメソッド
	 */
	public void adminHandler() {
		String[] adminTag = {"name", "class"};
		String[] adminData = {"/admin/", "org.apache.solr.handler.admin.AdminHandlers"};
		_root.appendChild(setElement2("requestHandler", adminTag, adminData, null));
	}

	/**
	 * pingHandlerメソッド
	 */
	public void pingHandler() {
		String[] pingTag = {"name", "class"};
		String[] pingData = {"/admin/ping", "PingRequestHandler"};
		Element ping = setElement2("requestHandler", pingTag, pingData, null);
		Element pingLst = setElement("lst", "name", "defaults", null);
		pingLst.appendChild(setElement("str", "name", "qt", "standard"));
		pingLst.appendChild(setElement("str", "name", "q", "solrpingquery"));
		pingLst.appendChild(setElement("str", "name", "echoParams", "all"));
		ping.appendChild(pingLst);
		_root.appendChild(ping);
	}

	/**
	 * debugHandlerメソッド
	 */
	public void debugHandler() {
		String[] debugTag = {"name", "class"};
		String[] debugData = {"/debug/dump", "solr.DumpRequestHandler"};
		Element debug = setElement2("requestHandler", debugTag, debugData, null);
		Element debugLst = setElement("lst", "name", "defaults", null);
		debugLst.appendChild(setElement("str", "name", "echoParams", "explicit"));
		debugLst.appendChild(setElement("str", "name", "echoHandler", "true"));
		debug.appendChild(debugLst);
		_root.appendChild(debug);
	}

	/**
	 * csvHandlerメソッド
	 */
	public void csvHandler() {
		String[] csvTag = {"name", "class", "startup"};
		String[] csvData = {"/update/csv", "solr.CSVRequestHandler", "lazy"};
		_root.appendChild(setElement2("requestHandler", csvTag, csvData, null));
	}

	public void jsonHandler() {
		String[] tag = {"name", "class", "startup"};
		String[] data = {"/update/json", "solr.JsonUpdateRequestHandler", "lazy"};
		_root.appendChild(setElement2("requestHandler", tag, data, null));
	}

	/**
	 * documentHandlerメソッド
	 */
	public void documentHandler() {
		String[] documentTag = {"name", "class"};
		String[] documentData = {"/analysis/document", "solr.DocumentAnalysisRequestHandler"};
		_root.appendChild(setElement2("requestHandler", documentTag, documentData, null));
	}

	/**
	 * fieldHandlerメソッド
	 */
	public void fieldHandler() {
		String[] fieldTag = {"name", "class"};
		String[] fieldData = {"/analysis/field", "solr.FieldAnalysisRequestHandler"};
		_root.appendChild(setElement2("requestHandler", fieldTag, fieldData, null));
	}

	/**
	 * javabinHandlerメソッド
	 */
	public void javabinHandler() {
		String[] tag = {"name", "class"};
		String[] data = {"/update/javabin", "solr.BinaryUpdateRequestHandler"};
		_root.appendChild(setElement2("requestHandler", tag, data, null));
	}

	/**
	 * xmlHandlerメソッド
	 */
	public void xmlHandler() {
		String[] tag = {"name", "class"};
		String[] data = {"/update", "solr.XmlUpdateRequestHandler"};
		_root.appendChild(setElement2("requestHandler", tag, data, null));
	}

	/**
	 * termsHandlerメソッド
	 */
	public void termsHandler() {
		//searchComponent
		String[] tag = {"name", "class"};
		String[] data = {"termsComponent", "org.apache.solr.handler.component.TermsComponent"};
		_root.appendChild(setElement2("searchComponent", tag, data, null));
		//requestHandler
		data[0] = "/terms";
		data[1] = "org.apache.solr.handler.component.SearchHandler";
		Element terms = setElement2("requestHandler", tag, data, null);
		Element termsLst1 = setElement("lst", "name", "defaults", null);
		termsLst1.appendChild(setElement("bool", "name", "terms", "true"));
		terms.appendChild(termsLst1);
		Element termsLst2 = setElement("arr", "name", "components", null);
		termsLst2.appendChild(setElement("str", null, null, "termsComponent"));
		terms.appendChild(termsLst2);
		_root.appendChild(terms);
	}

	/**
	 * fileWriteメソッド
	 */
	public void fileWrite() {
		try {
			//インデント設定
			TransformerFactory tfactory = TransformerFactory.newInstance();
			Transformer transformer = tfactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "2");	//"2"はスペースの数
			//保存するPATHを指定する
			File outfile = new File(_path);
			transformer.transform(new DOMSource(_document), new StreamResult(outfile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * fileWriterメソッド (パスを指定しXMLを保存する)
	 *
	 * @param path
	 */
	public void fileWrite(String path) {
		try {
			//インデント設定
			TransformerFactory tfactory = TransformerFactory.newInstance();
			Transformer transformer = tfactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "2");	//"2"はスペースの数
			//保存するPATHを指定する
			File outfile = new File(path);
			transformer.transform(new DOMSource(_document), new StreamResult(outfile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//-----------------------------------------------------
	//スタティックメソッド
	//-----------------------------------------------------

	/**
	 * setElementメソッド (XMLの要素を作成する)
	 *
	 * @param name
	 * @param tag
	 * @param tagdata
	 * @param data
	 * @return
	 */
	private static Element setElement(String name, String tag, String tagdata, String data) {
		Element element = _document.createElement(name);
		if (tag != null && tagdata != null) {
			element.setAttribute(tag, tagdata);
		}
		if (data != null) {
			Text text = _document.createTextNode(data);
			element.appendChild(text);
		}
		return element;
	}

	/**
	 * setElement2メソッド
	 *
	 * @param name
	 * @param tags
	 * @param tagdatas
	 * @param data
	 * @return
	 */
	private static Element setElement2(String name, String[] tags, String[] tagdatas, String data) {
		Element element = _document.createElement(name);
		if (tags != null && tagdatas != null) {
			if (tags.length == tagdatas.length) {
				for (int i = 0; i < tags.length; i++) {
					element.setAttribute(tags[i], tagdatas[i]);
				}
			}
		}
		if (data != null) {
			Text text = _document.createTextNode(data);
			element.appendChild(text);
		}
		return element;
	}

	//-----------------------------------------------------
	//テスト用
	//-----------------------------------------------------

	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		SolrConfig conf = new SolrConfig();
		conf.abortOnConfigurationError();
		conf.lib();
		conf.indexDefaults();
		conf.mainIndex();
		conf.jmx();
		conf.updateHandler();
		conf.query();
		conf.requestDispatcher();
		conf.standardHandler();
		conf.xmlHandler();
		conf.javabinHandler();
		conf.documentHandler();
		conf.fieldHandler();
		conf.debugHandler();
		conf.csvHandler();
		conf.replicationHandler("http://localhost:7100/solr/core0/replication", "10:00:00", "commit", "startup");
		conf.termsHandler();
		conf.adminHandler();
		conf.pingHandler();
		conf.highlighting();
		conf.queryResponseWriter();
		conf.fileWrite();
		System.out.println(System.currentTimeMillis() - time);
	}

}
