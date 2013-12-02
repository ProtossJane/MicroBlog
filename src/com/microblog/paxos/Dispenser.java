package com.microblog.paxos;

import java.io.IOException;
import java.io.PrintWriter;
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
			
			/*if ( server.isStop() )	{
				respondPost (currentPost, "fail");
				currentPost = null;
			}*/
			
			if(!server.getRecoverStatus() && !server.isStop())	{
				/*if ( isPostFinished() )	{
						if (currentPost != null)	{
							if( currentPost.message != null && paxosInstance.localLog.get(currentPost.position).message.senderId != FrontServer.serverId)
								respondPost(currentPost , "fail");
							else
								respondPost(currentPost , "success");
						}
					if (!server.isPostEmpty() )	{						
						currentPost = server.popPost();
						preparePost (currentPost);

					}
				}*/
				
				
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
					System.out.println("get recover queue " + recoverJob);
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
			
			/*if ( !server.postQueue.isEmpty() )	{
				if (System.currentTimeMillis() - server.postQueue.peek().timeStamp > 10000 )	{
					//respondPost (paxosInstance.postQueue.peek(), "fail");
					//System.out.println("************************************post " +  paxosInstance.postQueue.peek() + " Time out ");
					//paxosInstance.postQueue.poll();
				}
			}*/
			
			/*if ( currentPost!=null && System.currentTimeMillis() - currentPost.timeStamp > 10000 )	{
				//respondPost (currentPost, "fail");
				//System.out.println("************************************post " +  currentPost + " Time out ");
				//currentPost = null;
			}*/
		}
		
	}
	
	
	/*public void preparePost (Post post)	{
		
		post.position = paxosInstance.currentPosition + 1;
		System.out.println("process post ..." + post);
		paxosInstance.proposer.setProposal( new Message ( FrontServer.serverId, post.message));
		paxosInstance.proposer.prepare();
	}*/
	
	/*public void respondPost (Post currentPost, String status)	{
		
		if ( currentPost!=null && currentPost.socket!= null )
			try {
				PrintWriter outputstream = new PrintWriter (currentPost.socket.getOutputStream(), true);
				outputstream.println(status);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}*/
	
	public void respondPrepare( String currentJob )	{//fix this
		//System.out.println("respond prepare");
		String[] parameters = currentJob.split(":");
		int paxosId = FrontServer.serverId;
		if (parameters.length == 4)	
			paxosId = Integer.parseInt(parameters[2]);
			//paxosInstance.accepter.receivePrepare( new BallotNumber( Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2])));
			
		Paxos paxos = getPaxosInstance(paxosId);
		paxos.addJob(currentJob);
		
	}
	
	
	public void respondPromise( String currentJob)	{
		/*System.out.println("respond promise");
		String[] parameters = parameter.split(":", 9);
		if (parameters.length == 9)	{
			BallotNumber bal = new BallotNumber (Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2])) ;
			BallotNumber acceptedBallotNumber = new BallotNumber (Integer.parseInt(parameters[4]), Integer.parseInt(parameters[5]), Integer.parseInt(parameters[6])) ;
			Message message = new Message (Integer.parseInt(parameters[7]), parameters[8]);
			int senderId = Integer.parseInt(parameters[3]);
			Proposal acceptedProposal = new Proposal (acceptedBallotNumber, message);
			paxosInstance.proposer.receivePromise(bal, acceptedProposal, senderId );
		}
		else if ( parameters.length == 5)	{
			BallotNumber bal = new BallotNumber (Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2])) ;
			int senderId = Integer.parseInt(parameters[3]);
			paxosInstance.proposer.receivePromise(bal, null, senderId );
		}*/
		
		paxosInstance.addJob(currentJob);
	}
	
	
	public void respondAccept(String currentJob)	{
		System.out.println("respond accept request");
		String[] parameters = currentJob.split(":", 6);
		if (parameters.length == 6)	{
			//BallotNumber bal = new BallotNumber (Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2])) ;
			//Message message = new Message (Integer.parseInt(parameters[3]), parameters[4]);
			Paxos paxos = getPaxosInstance (Integer.parseInt(parameters[2]));
			paxos.addJob(currentJob);
			//paxos.accepter.receiveAcceptRequest(new Proposal(bal, message));
		}
		
	}
	
	public void respondAccepted(String currentJob)	{
		
		System.out.println("respond accepted");
		String[] parameters = currentJob.split(":", 6);
		if (parameters.length == 6)	{
			//BallotNumber bal = new BallotNumber (Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2])) ;
			//Message message = new Message (Integer.parseInt(parameters[3]), parameters[4]);
			//Paxos paxos = getPaxosInstance (bal.senderId);
			//paxos.learner.receiveAccepted(new Proposal(bal, message));
			Paxos paxos = getPaxosInstance (Integer.parseInt(parameters[2]));
			paxos.addJob(currentJob);
		}
		
	}
	
	
	public void respondDecide(String currentJob)	{
		
		System.out.println("respond decide");
		String[] parameters = currentJob.split(":", 6);
		if (parameters.length == 6)	{
			//BallotNumber bal = new BallotNumber (Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2])) ;
			//Message message = new Message (Integer.parseInt(parameters[3]), parameters[4]);
			//Paxos paxos = getPaxosInstance (bal.senderId);
			//paxos.learner.receiveDecide(new Proposal(bal, message));
			Paxos paxos = getPaxosInstance (Integer.parseInt(parameters[2]));
			paxos.addJob(currentJob);
			if (server.getRecoverStatus())
				paxos.addRecoverJob(currentJob);
		}
		
	}
	
	public void respondRecover(String parameter) {
		if ( server.getRecoverStatus() )
		System.out.println("respond recover");
		String[] parameters = parameter.split(":");
		String recoverPositionInfo = "recover_respond:"+ FrontServer.serverId;
		int dest = Integer.parseInt(parameters[0]);
		
		for (Paxos paxos : multiPaxos)	
			recoverPositionInfo += ":" + paxos.currentPosition ;
		paxosInstance.sender.send(recoverPositionInfo, dest);
		
		for (int i = 1 ; i < parameters.length; ++i)	{
			Paxos paxos = multiPaxos.get(i -1);
			int position = Integer.parseInt(parameters[i]);
			//System.out.println("***in recover****id "+server.serverId + " current position " + paxosInstance.currentPosition);
			for (int j = position + 1; j <= paxos.currentPosition; ++j)
				paxosInstance.sender.send("decide:"+paxos.localLog.get(j).toString(), dest);
		}
		//int recoverPosition = Integer.parseInt(parameters[1]);
		//paxosInstance.sender.send("recover_respond:" + FrontServer.serverId +":" + server.currentPosition, dest);
		//for (int i = recoverPosition + 1; i <= server.currentPosition; ++i)	
			//paxosInstance.sender.send("decide:"+server.GlobalLog.get(i).toString(), dest);
		
	}
	
	public void processRecoverRespond (String parameter)	{
		if ( server.getRecoverStatus()  )
		System.out.println("process recover respond");
		String[] parameters = parameter.split(":");
		int senderId = Integer.parseInt(parameters[0]);
		recoverRespond.add(senderId);
		
		for (int i = 1 ; i < parameters.length; ++i)	
			multiPaxos.get(i-1).setMaxPosition(Integer.parseInt(parameters[i]));
		
		if ( recoverRespond.size() >= FrontServer.quorumSize )
			recoverReady = true;
		//int positionId = Integer.parseInt(parameters[1]);
		
		//paxosInstance.learner.receiveRecoverRespond(senderId, positionId);
		
	}
	
	public Paxos getPaxosInstance (int paxosId)	{
		if (server.isOptimized)
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
	
	/*public boolean isPostFinished ()	{
		if (currentPost == null || paxosInstance.currentPosition >= currentPost.position || currentPost.message == null)
			return true;
		return false;
			
	}*/
	
}
