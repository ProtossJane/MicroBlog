package com.microblog.paxos;

import java.io.IOException;
import java.io.PrintWriter;

import com.microblog.server.FrontServer;
import com.microblog.server.Post;

public class Dispenser implements Runnable{

	// todo: post time out
	protected Post currentPost;
	protected FrontServer server = FrontServer.getInstance();
	protected Paxos paxosInstance;
	
	public Dispenser( Paxos paxos) throws IOException	{
		this.paxosInstance = paxos;
		
	}
	@Override
	public void run() {
		
		while ( true )	{
			if(!paxosInstance.isRecover && !server.isStop())	{
				if ( isPostFinished() )	{
					try {
						if (currentPost != null)	{
							PrintWriter outputstream = new PrintWriter (currentPost.socket.getOutputStream(), true);
							if( server.GlobalLog.get(currentPost.position).message.senderId != FrontServer.serverId)
								outputstream.println("fail");
							else
								outputstream.println("success");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

					if (!paxosInstance.isPostEmpty() )	{
						
						currentPost = paxosInstance.popPost();
						currentPost.position = server.currentPosition + 1;
						System.out.println("get post queue..." + currentPost);
						paxosInstance.proposer.setProposal( new Message ( FrontServer.serverId, currentPost.message));
						paxosInstance.proposer.prepare();
						
					}
				}
				
				if ( !paxosInstance.isJobEmpty() )	{
					
					String currentJob 	= paxosInstance.popJob();
					String[] types		= currentJob.split(":",2);
					if ( types.length == 2)
						switch ( types[0] )	{
							case "prepare":
								respondPrepare( types[1] );
								break;
							case "promise":
								respondPromise( types[1] );
								break;
							case "accept":
								respondAccept( types[1] );
								break;
							case "accepted":
								respondAccepted( types[1] );
								break;
							case "decide":
								respondDecide( types[1] );
								break;
							case "recover":
								respondRecover( types[1] );
								break;
							default:
								break;
						}
					
				}
			
			}
			
			else if( paxosInstance.isRecover && !server.isStop())	{
				
				if ( !paxosInstance.recoverQueue.isEmpty() )	{
					String recoverJob	=	server.paxosInstance.popRecoverJob();
					String[] types		= recoverJob.split(":",2);
					if ( types.length == 2)
						switch ( types[1] )	{
							case "recoverrespond" :
								processRecoverRespond( types[2] );
								break;
							case "decide" :
								respondDecide( types[2] );
								break;
							default:
								break;
							
						}
					
				}
				
				if ( paxosInstance.learner.recoverReady && paxosInstance.maxPosition == server.currentPosition)	{
					paxosInstance.isRecover = false;
					paxosInstance.learner.recoverReady = false;
					paxosInstance.learner.recoverRespond.clear();
					
				}
				
			}
		}
		
	}
	
	
	public void respondPrepare( String parameter )	{
		System.out.println("respond prepare");
		String[] parameters = parameter.split(":");
		if (parameters.length == 3)	{
			paxosInstance.accepter.receivePrepare( new BallotNumber( Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2])));
			
		}
	}
	
	
	public void respondPromise( String parameter)	{
		System.out.println("respond promise");
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
		}
	}
	
	
	public void respondAccept(String parameter)	{
		System.out.println("respond accept request");
		String[] parameters = parameter.split(":", 5);
		if (parameters.length == 5)	{
			BallotNumber bal = new BallotNumber (Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2])) ;
			Message message = new Message (Integer.parseInt(parameters[3]), parameters[4]);
			paxosInstance.accepter.receiveAcceptRequest(new Proposal(bal, message));
		}
		
	}
	
	public void respondAccepted(String parameter)	{
		
		System.out.println("respond accepted");
		String[] parameters = parameter.split(":", 5);
		if (parameters.length == 5)	{
			BallotNumber bal = new BallotNumber (Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2])) ;
			Message message = new Message (Integer.parseInt(parameters[3]), parameters[4]);
			paxosInstance.learner.receiveAccepted(new Proposal(bal, message));
		}
		
	}
	
	
	public void respondDecide(String parameter)	{
		
		System.out.println("respond decide");
		String[] parameters = parameter.split(":", 5);
		if (parameters.length == 5)	{
			BallotNumber bal = new BallotNumber (Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2])) ;
			Message message = new Message (Integer.parseInt(parameters[3]), parameters[4]);
			paxosInstance.learner.receiveDecide(new Proposal(bal, message));
		}
		
	}
	
	public void respondRecover(String parameter) {
		
		System.out.println("respond recover");
		String[] parameters = parameter.split(":", 2);
		int dest = Integer.parseInt(parameters[0]);
		int recoverPosition = Integer.parseInt(parameters[1]);
		if (  recoverPosition < server.currentPosition)	{
			paxosInstance.sender.send("respondrecover:" + FrontServer.serverId +":" + server.currentPosition, dest);
			for (int i = recoverPosition; i <= server.currentPosition; ++i)	
				paxosInstance.sender.send(server.GlobalLog.get(i).toString(), dest);
		}
	}
	
	public void processRecoverRespond (String parameter)	{
		
		System.out.println("process recover respond");
		String[] parameters = parameter.split(":", 3);
		int senderId = Integer.parseInt(parameters[1]);
		int positionId = Integer.parseInt(parameters[2]);
		
		paxosInstance.learner.receiveRecoverRespond(senderId, positionId);
		
	}
	
	public boolean isPostFinished ()	{
		if (currentPost == null || server.currentPosition >= currentPost.position)
			return true;
		return false;
			
	}
	
}
