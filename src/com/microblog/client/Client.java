package com.microblog.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
	
	private Socket socket;
	private BufferedReader inputstream;
	private PrintWriter outputstream;
	
	public Client () throws IOException	{
		
		socket = new Socket(InetAddress.getByName("127.0.0.1"), 8000);
		//socket.connect(new InetSocketAddress("127.0.0.1",8000) );
		inputstream = new BufferedReader ( new InputStreamReader( socket.getInputStream() ));
		outputstream = new PrintWriter ( socket.getOutputStream(), true);
	}
	
	public void send (String s){
		outputstream.println(s);
	}
	
	public String receive() throws IOException	{
		return inputstream.readLine();
	}
	
	public static void main ( String args[])	{
		
		try {
			Client client = new Client ();
			client.send("read");
			System.out.println ( "response: "+ client.receive());
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
