package com.microblog.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class Client {
	
	protected Socket socket;
	protected BufferedReader inputstream;
	protected PrintWriter outputstream;
	
	public Client ()
	{
		socket = new Socket();
	}
	
	public Client( String host, int port) throws UnknownHostException, IOException	{
		
		socket = new Socket(InetAddress.getByName(host), port);
		inputstream = new BufferedReader ( new InputStreamReader( socket.getInputStream() ));
		outputstream = new PrintWriter ( socket.getOutputStream(), true);
		
	}
	
	public void send (String s){
		outputstream.println(s);
	}
	
	public String receive() throws IOException	{
		return inputstream.readLine();
	}

}
