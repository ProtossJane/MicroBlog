
package com.microblog.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.microblog.paxos.Paxos;

public class Server extends Thread{
	
	private Paxos paxosInstance;
	private ServerSocket serverSocket;
	private int myId;
	private boolean isStop;
	private static Server server = null;
	public static int numOfNodes = 5;
	
	private Server () throws IOException	{
		
		myId = (int)Math.floor(Math.random() * numOfNodes); //need to fix
		isStop = false;
		serverSocket = new ServerSocket();
		serverSocket.bind( new InetSocketAddress("127.0.0.1",8000) );
	}
	
	public static synchronized Server getInstance() throws IOException {
        if (server == null) {
                server = new Server();
        }
        return server;
	}
	
	public void fail()	{
		isStop = true;
	}
	
	public void unfail()	{
		isStop = false;
	}
	
	@Override
	public void run()	{
		
		while (true)	{
			
			try {
				System.out.println("I am listening...");
				Socket client = serverSocket.accept();
				BufferedReader inputstream = new BufferedReader( new InputStreamReader( client.getInputStream()));
				PrintWriter outputstream = new PrintWriter (client.getOutputStream(), true);
				System.out.println( "get msg from client:" + inputstream.readLine() );
				if( !isStop )	{
					System.out.println("exec...");
					
					outputstream.println("success");
					
				}
				
				else	{
					System.out.println("fail...");
					outputstream.println("fail");
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
				
	}
	
	
	public String toString()	{
		
		return "my id:" + myId + "	" + serverSocket;
	}
	
	
	public static void main( String[] args)	{

		try {
			Server.getInstance().start();
			CLI();
		} catch (IOException e1) {
			e1.printStackTrace();
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
	
	public static void parser (String command)	{
		
		if ( command.matches("\\s*fail\\s*") )	{
			server.fail();
		}
		
		else if( command.matches("\\s*unfail\\s*"))	{
			server.unfail();
		}
		
		else if ( command.matches("\\s*post\\s*(.*)") )	{
			//todo
		}
		
		else if( command.matches("\\s*read\\s*"))	{
			//todo
		}
		
	}

}


