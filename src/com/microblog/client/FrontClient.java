package com.microblog.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FrontClient extends Client {

	private static FrontClient client;
	
	public FrontClient (String host, int port) throws IOException	{
		super(host, port);
	}
	
	
	public static void main ( String args[])	{
		
		try {
			client = new FrontClient ("127.0.0.1",8200);
			CLI();
			//client.send("POST:test");
		
			//client.send("accepted:1:1:0:1:test");
			//System.out.println ( "response: "+ client.receive());
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	public static void CLI ()	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String command = null;
		
		while (true)	{
			try	{
				command = br.readLine();
				parser(command);
				
			}
			
			catch (IOException e)	{
				System.out.println("IO error");
			}
		
		}
	}
	
	public static void parser (String command) throws IOException	{
		
		if ( command.matches("POST:.*") )	{
			client.send(command);
		}
		
		else if( command.matches("READ"))	{
			client.send(command);
			System.out.println(client.receive());
		}
		
		else if (command.matches("exit"))	{
			
			System.exit(0);
		}
		
	}
}
