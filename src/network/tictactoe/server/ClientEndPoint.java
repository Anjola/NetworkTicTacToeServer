package network.tictactoe.server;

import java.net.InetAddress;

public class ClientEndPoint {
	protected final InetAddress address;
	protected final int port;
	
	public ClientEndPoint(InetAddress addr, int port) {
		this.address = addr;
		this.port = port;
	}

	@Override
	public int hashCode() {
		// the hashcode is the exclusive or (XOR) of the port number and the hashcode of the address object
		return this.port ^ this.address.hashCode();
	}
	
	
}
