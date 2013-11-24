package com.microblog.paxos;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

import com.microblog.client.Client;

public class Sender {
	protected HashMap<Integer, String> route;
	protected ArrayList<paxosClient> clientList;
	public Sender (HashMap<Integer, String> route) throws IOException	{
		
		this.route = route;
		for (int i = 0 ; i < route.size(); ++i)	{
			clientList.add(new paxosClient(route.get(i+1), 9000 ) );
		}
	}

	
	public void send (String message, int dest)	{
		clientList.get(dest).send(message);
	}
	
	public void broadCast (String message)	{
		for (paxosClient client : clientList)
			client.send(message);
		
	}
	
	class paxosClient extends Client {
		
		public paxosClient (String host, int port) throws IOException {
			super(host, port);
		}
		
	}

}
