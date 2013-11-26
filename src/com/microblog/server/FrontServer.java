
package com.microblog.server;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import com.microblog.paxos.Dispenser;
import com.microblog.paxos.Paxos;
import com.microblog.paxos.Proposal;
import com.microblog.paxos.Receiver;

public class FrontServer extends Server{
	
	private Paxos paxosInstance;
	
	private boolean isStop;
	private static FrontServer server = null;
	public static int serverId = 0;
	public static String localAddr = null;
	public static int quorumSize = 3;
	public static int lastPosition = 0;
	public HashMap<Integer, String> route;
	public ArrayList<Proposal> GlobalLog;
	public ArrayList<String> localLog;
	private FrontServer () throws IOException	{
		super();
		isStop = false;
		paxosInstance = new Paxos();
		route = new HashMap<Integer, String>();
		setRoutingTable();
		super.bind(localAddr, 8000);
	}
	private FrontServer (String host, int port) throws IOException	{
		super(host, port);
		isStop = false;
		paxosInstance = new Paxos();
		route = new HashMap<Integer, String>();
		setRoutingTable();
		//serverSocket = new ServerSocket();
		//serverSocket.bind( new InetSocketAddress("127.0.0.1",8000) );
	}
	
	public static synchronized FrontServer getInstance() throws IOException	{
		if (server == null) {
			
            server = new FrontServer();
		}
		return server;
	}
	
	public static synchronized FrontServer getInstance(String host, int port) throws IOException {
        if (server == null) {
                server = new FrontServer(host, port);
        }
        return server;
	}
	
	public void setRoutingTable()	{
		
		try {
			BufferedReader reader = new BufferedReader( new FileReader(System.getProperty("user.dir") + "/route") ) ;
			String s;
			try {
				while ( (s = reader.readLine()) !=null)	{
					String[] par = s.split(":");
					route.put(Integer.valueOf(par[0]), par[1]);
					if (Integer.valueOf(par[0]).intValue() == serverId)
						localAddr = par[1];
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void fail()	{
		isStop = true;
	}
	
	public void unfail()	{
		isStop = false;
	}
	
	@Override
	public void clientWorker(Socket client)	{
	
		try {
			BufferedReader inputstream = new BufferedReader( new InputStreamReader( client.getInputStream()));
			PrintWriter outputstream = new PrintWriter (client.getOutputStream(), true);
			String msg = inputstream.readLine();
			System.out.println( "get msg from client:" + msg );
			if( !isStop )	{
				
				if (msg.matches("POST:.*"))	{
					System.out.println("exec post...");
					paxosInstance.addJob(msg);
				}
				
				else if (msg.matches("READ"))	{
					System.out.println("exec read...");
				}
				
				outputstream.println("success");
				
			}
			
			else	{
				System.out.println("fail...");
				outputstream.println("fail");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public String toString()	{
		
		return "my id:" + serverId + "	" + serverSocket;
	}
	
	
	public static void main( String[] args)	{

		serverId = Integer.valueOf( args[0] );
		try {
			Receiver receiver = new Receiver (FrontServer.getInstance().paxosInstance);
			Dispenser dispenser = new Dispenser(FrontServer.getInstance().paxosInstance);
			new Thread(FrontServer.getInstance()).start();
			new Thread(receiver).start();
			new Thread(dispenser).start();
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

