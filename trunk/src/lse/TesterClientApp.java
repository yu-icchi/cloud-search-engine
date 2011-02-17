package lse;

import org.msgpack.rpc.Client;
import org.msgpack.rpc.loop.EventLoop;


public class TesterClientApp {

	/**
	 * main
	 * テスト用のクライアントプログラム
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		EventLoop loop = EventLoop.defaultEventLoop();

		Client cli = new Client("192.168.220.137", 1988, loop);
		LSE iface = cli.proxy(LSE.class);

		String msg = iface.hello("akb48 ske48 nmb48");
		System.out.println(msg);

		iface.startAll();

		cli.close();
		loop.shutdown();
	}

}
