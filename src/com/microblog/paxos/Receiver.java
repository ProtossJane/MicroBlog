package com.microblog.paxos;

import java.io.IOException;

import com.microblog.server.Server;

public class Receiver extends Server{
	
	protected Paxos paxosInstance;
	
	public Receiver (Paxos paxosInstance, String host, int port) throws IOException	{
		
		super( "127.0.0.1",9000 );	//Paxos messenger listen to 127.0.0.1:9000 
		this.paxosInstance = paxosInstance;
		
		
	}
	
}
