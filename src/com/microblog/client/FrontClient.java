package com.microblog.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class FrontClient extends Client {

	//private static FrontClient client;
	//public static HashMap<Integer, String> route = new HashMap<Integer, String>();
	
	public FrontClient (String host, int port) throws IOException	{
		
		super(host, port);
		
	}
	
	public static void setRoutingTable(HashMap<Integer, String> route)	{
			
			try {
				BufferedReader reader = new BufferedReader( new FileReader(System.getProperty("user.dir") + "/route") ) ;
				String s;
				try {
					while ( (s = reader.readLine()) !=null)	{
						String[] par = s.split(":");
						route.put(Integer.valueOf(par[0]), par[1]);
						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	
	public static void main ( String args[])	{
		
		HashMap<Integer, String> route = new HashMap<Integer, String>();
		setRoutingTable( route );
		try {
			
			FrontClient client = new FrontClient ( route.get(Integer.parseInt(args[0])),8000 + Integer.parseInt(args[2]));
			//CLI();
			client.send(args[1]);
			//System.out.println(client.receive());
			//client.send("accepted:1:1:0:1:test");
			//System.out.println ( "response: "+ client.receive());
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	/*public static void CLI ()	{
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
			//System.out.println(client.receive());
		}
		
		else if (command.matches("exit"))	{
			
			System.exit(0);
		}
		
	}*/
}
