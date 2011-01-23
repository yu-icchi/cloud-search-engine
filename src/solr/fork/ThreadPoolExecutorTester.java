package solr.fork;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorTester {

	private static class MyTask implements Callable<Map<String, String>> {

		@SuppressWarnings("unused")
		private int index;

		public MyTask(int index) {
			this.index = index;
		}

		@Override
		public Map<String, String> call() throws Exception {
			//SolrClinet
			SolrClient client = new SolrClient("http://localhost:6365/solr/");
			Map<String, String> map = client.getExplain("芥川");
			return map;
		}

	}

	public static void main(String[] args) {

        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
        ThreadPoolExecutor exec = new ThreadPoolExecutor(10, 10, 0, TimeUnit.MILLISECONDS, queue);

        final List<Future<Map<String, String>>> result = new ArrayList<Future<Map<String, String>>>();

        // キューへの順次追加
        try {
            for (int i = 0; i < 10; i++) {
                if (queue.remainingCapacity() > 0) {
                    result.add(exec.submit(new MyTask(i)));
                } else {
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            exec.shutdown();
        }

        // 結果表示
        for (int i = 0; i < result.size(); i++) {
        	try {
        		System.out.println(i + ": Explain is " + result.get(i).get().keySet());
        	} catch (InterruptedException e) {
        		throw new RuntimeException(e);
        	} catch (ExecutionException e) {
        		throw new RuntimeException(e);
        	}
        }

        System.out.println("finished.");
    }

}
