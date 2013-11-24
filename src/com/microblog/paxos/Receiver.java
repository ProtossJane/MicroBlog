package com.microblog.paxos;

import java.io.IOException;
import java.util.HashMap;

import com.microblog.server.FrontServer;
import com.microblog.server.Server;

public class Receiver extends Server{
	
	protected Paxos paxosInstance;
	
	public Receiver (Paxos paxosInstance) throws IOException	{
		
		super( FrontServer.localAddr , 9000 );	//Paxos messenger listen to 127.0.0.1:9000 
		this.paxosInstance = paxosInstance;
		
		
	}
	
}
