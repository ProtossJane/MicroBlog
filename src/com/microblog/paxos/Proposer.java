package com.microblog.paxos;

import java.io.IOException;
import java.util.HashSet;

import com.microblog.server.FrontServer;


public class Proposer {
	
	protected boolean isPendding = false;
	protected Sender sender;
	protected Message message				   = null;
	protected HashSet<Integer> promiseReceived = new HashSet<Integer>();
	protected FrontServer server 			   = FrontServer.getInstance();
	protected BallotNumber acceptedBal 		   = null;
	protected BallotNumber bal				   = new BallotNumber(0, FrontServer.serverId, 0);
	
	public Proposer (Sender sender) throws IOException	{
		this.sender = sender;
	}
	
	public void setProposal (Message message)	{
		this.message = message;
	}
	
	public void prepare()	{
		isPendding = true;
		promiseReceived.clear();
		bal.proposalId += 1;
		bal.positionId = server.lastPosition + 1;
		System.out.println("ID "+ FrontServer.serverId + " broadcast prepare:"+bal);
		//sender.broadCast("prepare:" + bal );
		sender.send("prepare:" + bal, 1);
	}
	
	public void receivePromise (BallotNumber bal, Proposal acceptedProposal, int senderId)	{
		if ( !bal.equals(this.bal) || promiseReceived.contains(senderId) )	{
			return;
		}
		
		promiseReceived.add(senderId);
		if (acceptedProposal != null)
			if (acceptedBal == null || acceptedProposal.ballotNumber.compareTo(acceptedBal) > 0) {
				acceptedBal = acceptedProposal.ballotNumber;
				this.message = acceptedProposal.message;
			}
		
		if (promiseReceived.size() == FrontServer.quorumSize)
			sender.broadCast("accept:" + bal + ":" + message);
		
	}
	
	public boolean isPendding ()	{
		return isPendding;
	}

}
