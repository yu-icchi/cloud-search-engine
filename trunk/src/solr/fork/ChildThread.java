package solr.fork;

public class ChildThread implements Runnable {

    public void run() {
        System.out.println("The thread[" + Thread.currentThread().getId()
                + "] started.");
        try {
            Thread.sleep(Math.round(Math.random() * 10000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("The thread[" + Thread.currentThread().getId()
                + "] finished.");
    }

}
