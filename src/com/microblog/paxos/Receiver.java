package com.microblog.paxos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

import com.microblog.server.FrontServer;
import com.microblog.server.Server;

public class Receiver extends Server{
	
	protected Paxos paxosInstance;
	
	public Receiver (Paxos paxosInstance) throws IOException	{
		
		super( FrontServer.localAddr , 9000 );	//Paxos messenger listen to 127.0.0.1:9000 
		this.paxosInstance = paxosInstance;
		
	}
	
	@Override
	public void clientWorker(Socket client)	{

			System.out.println("invoke client worker");
			try {
				BufferedReader inputstream = new BufferedReader( new InputStreamReader( client.getInputStream()));
				String msg = inputstream.readLine();
				System.out.println( "get msg from paxos:" + msg );
				if( !FrontServer.getInstance().isStop() )	{
					paxosInstance.addJob(msg);
				}
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
	
	}
}
