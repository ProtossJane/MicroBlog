package com.microblog.paxos;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements Runnable{
	
	protected Paxos paxosInstance;
	protected ServerSocket serverSocket;
	
	public Receiver (Paxos paxosInstance) throws IOException	{
		
		this.paxosInstance = paxosInstance;
		serverSocket = new ServerSocket();
		serverSocket.bind( new InetSocketAddress("127.0.0.1",9000) );	//Paxos messenger listen to 127.0.0.1:9000 
		
	}
	@Override
	public void run() {

		try {
			Socket client = serverSocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
