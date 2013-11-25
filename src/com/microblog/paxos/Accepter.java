package com.microblog.paxos;

import com.microblog.server.FrontServer;

public class Accepter {
	
	protected Sender sender;
	protected BallotNumber promisedBal = null;
	protected Proposal acceptedProposal = null;
	
	public Accepter (Sender sender)	{
		
		this.sender = sender;
		
	}
	
	public void receivePrepare( BallotNumber bal )	{
		
		if (bal.positionId <= FrontServer.lastPosition )
			return; //or signal the sender for recovering
		
		if( promisedBal == null )	{
			promisedBal = bal;
			sender.send("promise:" + bal + ":" + acceptedProposal, bal.senderId);
		}
		
		else if ( bal.compareTo(promisedBal) == 0 || bal.compareTo(promisedBal) > 0 )	{
			
			if ( bal.positionId > FrontServer.lastPosition + 1)	
				acceptedProposal = null;
			promisedBal = bal;
			sender.send("promise:" + bal.toString() + ":" + acceptedProposal, bal.senderId);
			
		}
		
	}
	
	public void receiveAcceptRequest( Proposal proposal)	{
		
		if (promisedBal == null || proposal.ballotNumber.compareTo(promisedBal) == 0 || proposal.ballotNumber.compareTo(promisedBal) > 0 )	{
			
			promisedBal = proposal.ballotNumber;
			acceptedProposal = proposal;
			sender.broadCast("accept:" + acceptedProposal);
		}
		
	}
	
}
