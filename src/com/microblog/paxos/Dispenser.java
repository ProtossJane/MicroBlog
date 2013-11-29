package com.microblog.paxos;

import java.io.IOException;
import java.io.PrintWriter;

import com.microblog.server.FrontServer;
import com.microblog.server.Post;

public class Dispenser implements Runnable{

	
	protected Post currentPost;
	protected FrontServer server = FrontServer.getInstance();
	protected Paxos paxosInstance;
	
	public Dispenser( Paxos paxos) throws IOException	{
		this.paxosInstance = paxos;
		
	}
	@Override
	public void run() {
		
		while ( true )	{
			
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
				
				//System.out.println("post queue length: " + paxosInstance.postQueue.isEmpty());

				if (!paxosInstance.isPostEmpty() )	{
					
					currentPost = paxosInstance.popPost();
					currentPost.position = server.lastPosition + 1;
					System.out.println("get post queue..." + currentPost);
					paxosInstance.proposer.setProposal( new Message (currentPost.message, FrontServer.serverId));
					paxosInstance.proposer.prepare();
					
				}
			}
			
			if ( !paxosInstance.isJobEmpty() )	{
				
				String currentJob 	= paxosInstance.popJob();
				String[] types		= currentJob.split(":",2);
				if ( types.length == 2)
					switch ( types[0] )	{
						case "prepare":
							System.out.println("receive prepare");
							break;
					
						default:
							break;
					}
				
			}
			
		}
		
	}
	
	public void respondPrepare( String parameter )	{
		String[] parameters = parameter.split(":");
		if (parameters.length == 3)	{
			
		}
	}
	
	public boolean isPostFinished ()	{
		if (currentPost == null || server.lastPosition >= currentPost.position)
			return true;
		return false;
			
	}
	
}
