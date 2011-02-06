package solr.fork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import solr.ranking.DistributedSimilarity;

public class SolrFork {

	//プロパティ
	private BlockingQueue<Runnable> queue;
	private ThreadPoolExecutor exec;
	private List<Future<Map<String, String>>> explain;
	private List<Future<SolrDocumentList>> result;

	/**
	 * コンストラクタ
	 */
	public SolrFork() {
		queue = new LinkedBlockingQueue<Runnable>();
		explain = new ArrayList<Future<Map<String, String>>>();
		result = new ArrayList<Future<SolrDocumentList>>();
	}

	/**
	 * searchExplain処理
	 *
	 * @param nodes
	 * @param query
	 */
	public void searchExplain(List<String> nodes, String query) {
		exec = new ThreadPoolExecutor(10, 10, 0, TimeUnit.MILLISECONDS, queue);
		try {
			for (String node : nodes) {
				//キューに順番に追加する
				explain.add(exec.submit(new ExplainTask(node, query)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			exec.shutdown();
		}
	}

	/**
	 * searchResponse
	 *
	 * @param nodes
	 * @param query
	 */
	public void searchResponse(List<String> nodes, String query) {
		exec = new ThreadPoolExecutor(10, 10, 0, TimeUnit.MILLISECONDS, queue);
		try {
			for (String node : nodes) {
				//キューに順番に追加する
				result.add(exec.submit(new ResponseTask(node, query)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			exec.shutdown();
		}
	}

	/**
	 * getExplain
	 *
	 * @return
	 */
	public Map<String, String> getExplain() {
		Map<String, String> debugList = new HashMap<String, String>();
		// 結果表示
        for (int i = 0; i < explain.size(); i++) {
        	try {
        		//System.out.println(i + ": Explain is " + explain.get(i).get().keySet() + " : " + explain.get(i).get().size());
        		//System.out.println(explain.get(i).get());
        		Map<String, String> map = explain.get(i).get();
        		Iterator<String> it = map.keySet().iterator();
        		while (it.hasNext()) {
        			String id = it.next();
        			//System.out.println(id);
        			debugList.put(id, map.get(id));
        		}
        	} catch (InterruptedException e) {
        		throw new RuntimeException(e);
        	} catch (ExecutionException e) {
        		throw new RuntimeException(e);
        	}
        }
        return debugList;
	}

	/**
	 * getResponse
	 *
	 * @return
	 */
	public Map<String, SolrDocument> getResponse() {
		Map<String, SolrDocument> document = new HashMap<String, SolrDocument>();
		// 結果表示
        for (int i = 0; i < result.size(); i++) {
        	try {
        		//System.out.println(i + ": Result is NumFound " + result.get(i).get());
        		for (int j = 0; j < result.get(i).get().getNumFound(); j++) {
        			//System.out.println(result.get(i).get().get(j).get("id").toString());
        			//System.out.println(result.get(i).get().get(j));
        			document.put(result.get(i).get().get(j).get("id").toString(), result.get(i).get().get(j));
        		}
        	} catch (InterruptedException e) {
        		throw new RuntimeException(e);
        	} catch (ExecutionException e) {
        		throw new RuntimeException(e);
        	}
        }
        return document;
	}

	/**
	 * @test
	 *
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		//分散検索
		SolrFork test = new SolrFork();
		//アクセス先アドレス
		List<String> nodes = new ArrayList<String>();
		nodes.add("http://192.168.220.131:6365/solr/core0/");
		nodes.add("http://192.168.220.132:6365/solr/core0/");
		nodes.add("http://192.168.220.133:6365/solr/core0/");
		//アクセス先とキーワード
		test.searchExplain(nodes, "芥川");
		Map<String, String> debugList = test.getExplain();
		//ランキング修正
		Map<String, Integer> docFreq2 = new HashMap<String, Integer>();
		docFreq2.put("芥川", 208);
		docFreq2.put("夏目", 82);
		DistributedSimilarity ranking = new DistributedSimilarity(docFreq2, 1211);
		ranking.solrScoreImport(debugList);
		List<Map<String, Object>> documentResult = ranking.ranking();
		System.out.println(documentResult);

		String ids = "";
		int rows = 10;

		for (int i = 0; i < rows -1 ; i++) {
			String id = documentResult.get(i).get("id").toString();
			ids +=  "id:" + id + " ";
		}

		ids += "id:" + documentResult.get(rows - 1).get("id").toString();
		//詳細検索
		//アクセス先とidの列挙
		test.searchResponse(nodes, ids);
		Map<String, SolrDocument> doc = test.getResponse();
		//System.out.println(doc);

		//トップ10件の表示
		for (int i = 0; i < rows; i++) {
			System.out.println("---------- " + (i+1) + " ----------");
			System.out.println("score : " + documentResult.get(i).get("score"));
			System.out.println("id : " + documentResult.get(i).get("id"));
			List<String> msg = (List<String>) doc.get(documentResult.get(i).get("id")).get("text");
			System.out.println(msg.get(0) + " " + msg.get(1));
			System.out.println();
		}
    }

}
