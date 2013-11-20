package com.microblog.client;

import java.io.IOException;

public class FrontClient extends Client {

	
	public FrontClient (String host, int port) throws IOException	{
		super(host, port);
	}
	
	
	public static void main ( String args[])	{
		
		try {
			FrontClient client = new FrontClient ("127.0.0.1",8000);
			client.send("read");
			System.out.println ( "response: "+ client.receive());
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
