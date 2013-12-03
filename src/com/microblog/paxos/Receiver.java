package com.microblog.paxos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.microblog.server.FrontServer;
import com.microblog.server.Server;

public class Receiver extends Server{
	
	protected Paxos paxosInstance;
	protected FrontServer server = FrontServer.getInstance();
	public Receiver (Paxos paxosInstance) throws IOException	{
		
		super( FrontServer.localAddr , 9000 );	//Paxos messenger listen to 127.0.0.1:9000 
		this.paxosInstance = paxosInstance;
		
	}
	
	@Override
	public void clientWorker(Socket client)	{

			try {
				BufferedReader inputstream = new BufferedReader( new InputStreamReader( client.getInputStream()));
				String msg = inputstream.readLine();
				
				if( !server.isStop() )	{
					if ( server.getRecoverStatus())	{

						if ( msg.matches("recover_respond:.*") || msg.matches( "decide:.*") || msg.matches("recover:.*"))
							server.addRecoverJob(msg);
					}
					
					if ( !(msg.matches("recover_respond:.*") || msg.matches( "decide:.*") || msg.matches("recover:.*")) )
					System.out.println( "get msg from paxos:" + msg );
					server.addJob(msg);
					
				}
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
	
	}
}
