
package com.microblog.server;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.microblog.paxos.Dispenser;
import com.microblog.paxos.Paxos;
import com.microblog.paxos.Proposal;
import com.microblog.paxos.Receiver;
import com.microblog.paxos.Sender;

public class FrontServer extends Server{
	
	public Paxos paxosInstance;
	public ArrayList<Paxos> multiPaxos;
	public boolean isOptimized = false;
	private boolean isStop;
	private volatile boolean isRecover = true;
	private static FrontServer server ;
	public static int serverId = 0;
	public static String localAddr = null;
	public static int quorumSize = 3;
	//public int currentPosition = -1;
	public HashMap<Integer, String> route;
	public ArrayList<Proposal> GlobalLog = new ArrayList<Proposal> ();
	
	public volatile LinkedList<String> jobQueue;
	public volatile LinkedList<Post>	 postQueue;
	public volatile LinkedList<String> recoverQueue;
	
	
	
	private FrontServer () throws IOException	{
		super();
		localAddr = Inet4Address.getLocalHost().getHostAddress();
		isStop = false;
		route = new HashMap<Integer, String>();
		setRoutingTable();
		super.bind(localAddr, 8000);
		multiPaxos = new ArrayList<Paxos>();
		jobQueue 	= new LinkedList<String>();
		postQueue 	= new LinkedList<Post>();
		recoverQueue = new LinkedList<String>();
	}
	
	private FrontServer (String host, int port) throws IOException	{
		super(host, port);
		localAddr = Inet4Address.getLocalHost().getHostAddress();
		isStop = false;
		route = new HashMap<Integer, String>();
		setRoutingTable();
		multiPaxos = new ArrayList<Paxos>();
		jobQueue 	= new LinkedList<String>();
		postQueue 	= new LinkedList<Post>();
		recoverQueue = new LinkedList<String>();
	}
	
	public static synchronized FrontServer getInstance() throws IOException	{
		if (server == null) {
			System.out.println("init server" );
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
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void initPaxos(Sender sender) throws IOException	{
		if (!isOptimized)	{
			multiPaxos.add( new Paxos(sender, serverId) );
			paxosInstance = multiPaxos.get(0);
		}
		else	{
			for ( int i = 0; i< route.size(); ++i)
				multiPaxos.add( new Paxos(sender, i+1) );
			paxosInstance = multiPaxos.get(serverId -1);
		}
		//paxosInstance.addPost(new Post("test", -1, null));
	}
	
	public void fail()	{
		isStop = true;
	}
	
	public void unfail()	{
		
		if ( isStop)	{
			isRecover = true;
			isStop = false;
		}
	}
	
	public boolean isStop ()	{
		return isStop;
	}
	
	public synchronized void setRecoverStatus (boolean status)	{
		isRecover = status;
	}
	
	public synchronized boolean getRecoverStatus () {
		return isRecover;
	}
	
	public synchronized void addPost (Post post)	{
		postQueue.add(post);
	}
	
	public synchronized Post popPost ()	{
		return postQueue.poll();
	}
	
	public synchronized boolean isPostEmpty()	{
		return postQueue.isEmpty();
	}
	
	public synchronized void addJob( String job )	{
		jobQueue.add(job);
	}
	
	public synchronized String popJob ()	{
		return jobQueue.poll();
	}
	
	public synchronized boolean isJobEmpty()	{
		return jobQueue.isEmpty();
	}
	
	public synchronized void addRecoverJob (String recoverMsg)	{
		recoverQueue.add(recoverMsg);
	}
	
	public synchronized String popRecoverJob ()	{
		return recoverQueue.poll();
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
					msg = msg.replaceFirst("POST:", "");
					Post post = new Post (msg, -1, client, System.currentTimeMillis());
					paxosInstance.addPost(post);
					
				}
				
				else if (msg.matches("READ"))	{
					//System.out.println("exec read...");
					String blogs = "";
					
					for (int i = 0; i < GlobalLog.size(); ++i )	{
						System.out.println( GlobalLog.get(i) );
						blogs += GlobalLog.get(i).message.message + ":"; 
					}
					outputstream.println(blogs);
				}
				
				//outputstream.println("success");
				
			}
			
			else	{
				//System.out.println("fail...");
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
		boolean option = false;
		if (args.length == 2)	{
			option = args[1].equals("1")? true:false ;
		}
		try {
			FrontServer server = FrontServer.getInstance();
			Sender sender = new Sender (server.route);
			server.isOptimized = option;
			server.initPaxos(sender);
			System.out.println("optimized mode:"+option);
			Receiver receiver = new Receiver (server.paxosInstance);
			Dispenser dispenser = new Dispenser(server.paxosInstance, server.multiPaxos, sender);
			for ( Paxos p : server.multiPaxos)
				new Thread(p.getWorker()).start();
			new Thread(receiver).start();
			new Thread(dispenser).start();
			new Thread(server).start();
			
			CLI(server);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	

		
	public static void CLI (FrontServer server)	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String command = null;
		
		while (true)	{
			try	{
				command = br.readLine();
				parser(command, server);
				
			}
			
			catch (IOException e)	{
				System.out.println("IO error");
			}
		
		}
	}
	
	public static void parser (String command, FrontServer server)	{
		
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
			String blogs = "";
			for (Proposal p : server.GlobalLog)	{
				System.out.println(p.message.message);
				//blogs += p.message.message + ":"; 
			}
			//System.out.println( blogs );
		}
		else if(command.matches("serverId"))
		{
			System.out.println(serverId);
		}
		
		else if (command.matches("exit"))	{
			
			System.exit(0);
		}
		
	}

}


