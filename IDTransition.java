package dnsrelay;

import java.net.InetAddress;

public class IDTransition {
        private int port;	
    
	private int srcID;
	
	private InetAddress addr;
	public IDTransition(int srcID, int port, InetAddress addr) {
		this.srcID = srcID;
		this.port = port;
		this.addr = addr;
	}
	
        public int getPort() {
		return this.port;
	}
        
	public int getSrcID() {
		return this.srcID;
	}

	

	public InetAddress getAddr() {
		return this.addr;
	}
	
	
}