package com.microblog.paxos;

import java.io.IOException;
import java.io.PrintWriter;

import com.microblog.server.FrontServer;
import com.microblog.server.Post;

public class PaxosWorker implements Runnable{

	protected Post currentPost;
	protected FrontServer server = FrontServer.getInstance();
	protected Paxos paxosInstance;
	//protected ArrayList<Paxos> multiPaxos;
	
	public PaxosWorker( Paxos paxos) throws IOException	{
		this.paxosInstance = paxos;
		//this.multiPaxos = multiPaxos;
		
	}
	
	
	@Override
	public void run() {
		while ( true )	{
			
			
			if ( server.isStop() )	{
				respondPost (currentPost, "fail");
				currentPost = null;
			}
			
			if(!server.getRecoverStatus() && !server.isStop())	{
				if ( isPostFinished() )	{
						if (currentPost != null)	{
							if( currentPost.message != null && paxosInstance.localLog.get(currentPost.position).message.senderId != FrontServer.serverId)
								respondPost(currentPost , "fail");
							else
								respondPost(currentPost , "success");
						}
					if (!paxosInstance.isPostEmpty() )	{						
						currentPost = paxosInstance.popPost();
						preparePost (currentPost);

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
							default:
								break;
						}
				}
			
			}
			
			else if( server.getRecoverStatus() && !server.isStop())	{
				
				if ( !paxosInstance.recoverQueue.isEmpty() )	{
					
					String recoverJob	=	paxosInstance.popRecoverJob();
					//System.out.println("get recover queue " + recoverJob);
					String[] types		= recoverJob.split(":",2);
					if ( types.length == 2)
						switch ( types[0] )	{
						
							case "decide" :
								respondDecide( types[1] );
								break;
							default:
								break;
							
						}
				}
				
			
			}
			
			if ( !paxosInstance.postQueue.isEmpty() )	{
				if (System.currentTimeMillis() - paxosInstance.postQueue.peek().timeStamp > 10000 )	{
					respondPost (paxosInstance.postQueue.peek(), "fail");
					//System.out.println("************************************post " +  paxosInstance.postQueue.peek() + " Time out ");
					//paxosInstance.postQueue.poll();
				}
			}
			
			if ( currentPost!=null && System.currentTimeMillis() - currentPost.timeStamp > 10000 )	{
				respondPost (currentPost, "fail");
				//System.out.println("************************************post " +  currentPost + " Time out ");
				//currentPost = null;
			}
		}
		
	}
	
public void preparePost (Post post)	{
		
		post.position = paxosInstance.currentPosition + 1;
		//System.out.println("process post ..." + post);
		paxosInstance.proposer.setProposal( new Message ( FrontServer.serverId, post.message));
		paxosInstance.proposer.prepare();
	}
	
	public void respondPost (Post currentPost, String status)	{
		
		if ( currentPost!=null && currentPost.socket!= null )
			try {
				PrintWriter outputstream = new PrintWriter (currentPost.socket.getOutputStream(), true);
				outputstream.println(status);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public void respondPrepare( String parameter )	{
		//System.out.println("respond prepare");
		String[] parameters = parameter.split(":");
		if (parameters.length == 3)	{
			paxosInstance.accepter.receivePrepare( new BallotNumber( Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2])));
			
		}
	}
	
	
	public void respondPromise( String parameter)	{
		//System.out.println("respond promise");
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
		//System.out.println("respond accept request");
		String[] parameters = parameter.split(":", 5);
		if (parameters.length == 5)	{
			BallotNumber bal = new BallotNumber (Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2])) ;
			Message message = new Message (Integer.parseInt(parameters[3]), parameters[4]);
			//Paxos paxos = getPaxosInstance (bal.senderId);
			paxosInstance.accepter.receiveAcceptRequest(new Proposal(bal, message));
		}
		
	}
	
	public void respondAccepted(String parameter)	{
		
		//System.out.println("respond accepted");
		String[] parameters = parameter.split(":", 5);
		if (parameters.length == 5)	{
			BallotNumber bal = new BallotNumber (Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2])) ;
			Message message = new Message (Integer.parseInt(parameters[3]), parameters[4]);
			//Paxos paxos = getPaxosInstance (bal.senderId);
			paxosInstance.learner.receiveAccepted(new Proposal(bal, message));
		}
		
	}
	
	
	public void respondDecide(String parameter)	{
		
		//System.out.println("respond decide");
		String[] parameters = parameter.split(":", 5);
		if (parameters.length == 5)	{
			BallotNumber bal = new BallotNumber (Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2])) ;
			Message message = new Message (Integer.parseInt(parameters[3]), parameters[4]);
			//Paxos paxos = getPaxosInstance (bal.senderId);
			paxosInstance.learner.receiveDecide(new Proposal(bal, message));
		}
		
	}
	
	
	public boolean isPostFinished ()	{
		if (currentPost == null || paxosInstance.currentPosition >= currentPost.position || currentPost.message == null)
			return true;
		return false;
			
	}
	
}



