package com.microblog.paxos;

import java.io.IOException;
import java.util.HashSet;

import com.microblog.server.FrontServer;


public class Proposer {
	
	protected boolean isPendding = false;
	protected Sender sender;
	protected String message				   = null;
	protected BallotNumber bal				   = new BallotNumber(0, FrontServer.serverId, 0);
	protected BallotNumber acceptedBal 		   = null;
	protected HashSet<Integer> promiseReceived = null;
	protected FrontServer server 			   = FrontServer.getInstance();
	public Proposer (Sender sender) throws IOException	{
		this.sender = sender;
	}
	
	public void setProposal (String message)	{
		this.message = message;
	}
	
	public void prepare()	{
		isPendding = true;
		promiseReceived.clear();
		bal.proposalId += 1;
		bal.positionId = FrontServer.lastPosition + 1;
		sender.broadCast("prepare:" + bal.toString() );
	}
	
	public void receivePromise (BallotNumber bal, BallotNumber ballotNumber, int senderId, String message)	{
		if ( !bal.equals(this.bal) || promiseReceived.contains(senderId) )	{
			return;
		}
		
		promiseReceived.add(senderId);
		if (ballotNumber != null)
			if (acceptedBal == null || ballotNumber.compareTo(acceptedBal) > 0) {
				acceptedBal = ballotNumber;
				this.message = message;
			}
		
		if (promiseReceived.size() == server.quorumSize)
			sender.broadCast("accept:" + bal.toString() + ":" + message);
		
	}
	
	public boolean isPendding ()	{
		return isPendding;
	}

}
