package com.microblog.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Server implements Runnable {
	protected ServerSocket serverSocket;

	public Server() throws IOException	{
		serverSocket = new ServerSocket();
	}
	public Server (String host, int port) throws IOException	{
		
		serverSocket = new ServerSocket();
		serverSocket.bind( new InetSocketAddress(host,port) );
	}
	
	public void bind (String host, int port) throws IOException 	{
		serverSocket.bind( new InetSocketAddress(host,port) );
	}
	
	public void clientWorker (Socket client)	{
		
		
	}
	
	@Override
	public void run() {
		while (true)	{

			try {
				Socket client = serverSocket.accept();
				clientWorker(client);
	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
