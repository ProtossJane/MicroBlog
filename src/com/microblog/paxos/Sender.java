package com.microblog.paxos;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

import com.microblog.client.Client;

public class Sender {
	protected HashMap<Integer, String> route;
	protected HashMap<Integer,paxosClient> clientList;
	public Sender (HashMap<Integer, String> route)	{
		
		this.route = route;
		clientList = new HashMap<Integer, paxosClient>();
		/*for (int i = 0 ; i < route.size(); ++i)	{
			try {
				clientList.add(new paxosClient(route.get(i+1), 9000 ) );
			} catch (IOException e) {
				//System.out.println( "cant connect to server ");
				//e.printStackTrace();
			}
		}*/
	}

	
	public void send (String message, int dest)	{
		
		/*if ( !clientList.containsKey(dest))
			try {
				System.out.println("init channel to "+ "server "+dest);
				clientList.put(dest, new paxosClient(route.get(dest), 9000 ));
			} catch (IOException e) {
				System.out.println("can not connet to " + route.get(dest));
				return;
				//e.printStackTrace();
			}
		System.out.println("sending " + message);
		clientList.get(dest).send(message);*/
		
		try {
			new paxosClient(route.get(dest), 9000 ).send(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void broadCast (String message)	{
		
		for (int i = 1; i <= 5; ++i)	{
			send(message, i);
		}
		
	}
	
	class paxosClient extends Client {
		
		public paxosClient (String host, int port) throws IOException {
			super(host, port);
		}
		
	}

}
