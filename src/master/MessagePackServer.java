package master;

import org.msgpack.rpc.server.TCPServer;

public class MessagePackServer {

	public int hello(int a) { return a; };

	public static void main(String[] args) {
		TCPServer s = new TCPServer("0.0.0.0", 5632, new MessagePackServer());
		try {
			s.serv();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
