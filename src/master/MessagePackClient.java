package master;

import org.msgpack.rpc.client.EventLoop;
import org.msgpack.rpc.client.TCPClient;

public class MessagePackClient {

	public static void main(String[] args) {
		EventLoop loop = new EventLoop();
		TCPClient c = new TCPClient("localhost", 5632, loop);
		try {
			c.call("hello", 1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
