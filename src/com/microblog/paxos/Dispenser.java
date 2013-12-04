package com.microblog.paxos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;

import com.microblog.server.FrontServer;
import com.microblog.server.Post;

public class Dispenser implements Runnable{

	//protected Post currentPost;
	protected FrontServer server = FrontServer.getInstance();
	protected Paxos paxosInstance;
	protected ArrayList<Paxos> multiPaxos;
	protected Timer	recoverTimer;
	protected HashSet<Integer> recoverRespond = new HashSet<Integer>();
	protected boolean recoverReady = false;
	
	public Dispenser( Paxos paxos, ArrayList<Paxos> multiPaxos, Sender sender) throws IOException	{
		this.paxosInstance = paxos;
		this.multiPaxos = multiPaxos;
		recoverTimer = new Timer();
		recoverTimer.scheduleAtFixedRate(new Recover(  sender, multiPaxos), 2000, 10000);
	}
	@Override
	public void run() {
		
		while ( true )	{
						
			if(!server.getRecoverStatus() && !server.isStop())	{
			
				if ( !server.isJobEmpty() )	{
					
					String currentJob 	= server.popJob();
					String[] types		= currentJob.split(":",2);
					if ( types.length == 2)
						switch ( types[0] )	{
							case "prepare":
								respondPrepare( currentJob );
								break;
							case "promise":
								respondPromise( currentJob );
								break;
							case "accept":
								respondAccept( currentJob );
								break;
							case "accepted":
								respondAccepted( currentJob );
								break;
							case "decide":
								respondDecide( currentJob );
								break;
							case "recover":
								respondRecover( types[1] );
								break;
							default:
								break;
						}
				}
			
			}
			
			else if( server.getRecoverStatus() && !server.isStop())	{
				
				if ( !server.recoverQueue.isEmpty() )	{
					
					String recoverJob	=	server.popRecoverJob();
					//System.out.println("get recover queue " + recoverJob);
					String[] types		= recoverJob.split(":",2);
					if ( types.length == 2)
						switch ( types[0] )	{
						
							case "recover":
								respondRecover( types[1] );
								break;
							case "recover_respond" :
								processRecoverRespond( types[1] );
								break;
							case "decide" :
								respondDecide( recoverJob );
								break;
							default:
								break;
							
						}
				}
				
				if ( recoverReady && isRecoverFinished ())	{
					
					System.out.println("***Recover Done****");
					server.setRecoverStatus(false);
					recoverReady = false;
					recoverRespond.clear();
					paxosInstance.postQueue.addFirst(new Post (null, paxosInstance.currentPosition + 1, null, System.currentTimeMillis()));
					
				}
			}

		}
		
	}
	
	
	public void respondPrepare( String currentJob )	{//fix this
		
		String[] parameters = currentJob.split(":");
		int paxosId = FrontServer.serverId;
		if (parameters.length == 4)		{
			
			paxosId = Integer.parseInt(parameters[2]);
			//System.out.println("parse prepare get id " + paxosId);
		}
			//paxosInstance.accepter.receivePrepare( new BallotNumber( Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2])));
			
		Paxos paxos = getPaxosInstance(paxosId -1 );
		paxos.addJob(currentJob);
		//System.out.println("dispense prepare to paxos#" + paxosId);
	}
	
	
	public void respondPromise( String currentJob)	{
		paxosInstance.addJob(currentJob);
	}
	
	
	public void respondAccept(String currentJob)	{
		//System.out.println("respond accept request");
		String[] parameters = currentJob.split(":", 6);
		if (parameters.length == 6)	{
			Paxos paxos = getPaxosInstance (Integer.parseInt(parameters[2]) -1 );
			paxos.addJob(currentJob);
		}
		
	}
	
	public void respondAccepted(String currentJob)	{
		
		//System.out.println("respond accepted");
		String[] parameters = currentJob.split(":", 6);
		if (parameters.length == 6)	{
			Paxos paxos = getPaxosInstance (Integer.parseInt(parameters[2]) -1);
			paxos.addJob(currentJob);
		}
		
	}
	
	
	public void respondDecide(String currentJob)	{
		
		//System.out.println("respond decide");
		String[] parameters = currentJob.split(":", 6);
		if (parameters.length == 6)	{
			Paxos paxos = getPaxosInstance (Integer.parseInt(parameters[2]) -1);
			paxos.addJob(currentJob);
			if (server.getRecoverStatus())
				paxos.addRecoverJob(currentJob);
		}
		
	}
	
	public void respondRecover(String parameter) {
		//if ( server.getRecoverStatus() )
		//System.out.println("respond recover");
		String[] parameters = parameter.split(":");
		String recoverPositionInfo = "recover_respond:"+ FrontServer.serverId;
		int dest = Integer.parseInt(parameters[0]);
		
		for (Paxos paxos : multiPaxos)	
			recoverPositionInfo += ":" + paxos.currentPosition ;
		paxosInstance.sender.send(recoverPositionInfo, dest);
		
		for (int i = 1 ; i < parameters.length; ++i)	{
			Paxos paxos = multiPaxos.get(i -1);
			int position = Integer.parseInt(parameters[i]);
			
			for (int j = position + 1; j <= paxos.currentPosition; ++j)
				paxosInstance.sender.send("decide:"+paxos.localLog.get(j).toString(), dest);
		}
	}
	
	public void processRecoverRespond (String parameter)	{
		//if ( server.getRecoverStatus()  )
		//System.out.println("process recover respond");
		String[] parameters = parameter.split(":");
		int senderId = Integer.parseInt(parameters[0]);
		recoverRespond.add(senderId);
		
		for (int i = 1 ; i < parameters.length; ++i)	
			multiPaxos.get(i-1).setMaxPosition(Integer.parseInt(parameters[i]));
		
		if ( recoverRespond.size() >= FrontServer.quorumSize )
			recoverReady = true;
		
	}
	
	public Paxos getPaxosInstance (int paxosId)	{
		if (FrontServer.isOptimized)
			return multiPaxos.get(paxosId);
		else
			return paxosInstance;
	}
	
	public void processDecideBuffer ()	{
		for ( Paxos paxos : multiPaxos)	
			paxos.learner.processDecideBuffer();
		
	}
	
	public boolean isRecoverFinished ()	{
		boolean isFinished = true;
		for ( Paxos paxos : multiPaxos )
			isFinished = isFinished && paxos.noGap();
		return isFinished;
	}
	
	
}
