package manager;

public class ExThread1 extends Thread {
	public void run() {
		for (int i = 0; i <= 10; i++) {
			System.out.println(getName() + ":" + i);
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		ExThread1 thread1 = new ExThread1();
		ExThread1 thread2 = new ExThread1();
		
		thread1.start();
		thread2.start();
	}
}
