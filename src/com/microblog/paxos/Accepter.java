package com.microblog.paxos;

import java.io.IOException;

import com.microblog.server.FrontServer;

public class Accepter {
	
	protected Sender sender;
	protected static BallotNumber promisedBal  = null;
	protected Proposal acceptedProposal = null;
	protected FrontServer server 		= FrontServer.getInstance();
	
	public Accepter (Sender sender)	throws IOException {
		
		this.sender = sender;
		
	}
	
	public void receivePrepare( BallotNumber bal )	{
		
		if (bal.positionId <= server.paxosInstance.maxPosition)
			return; //or signal the sender for recovering
		
		
		if( promisedBal == null )	{
			promisedBal = bal;		
			sender.send("promise:" + bal + ":" + server.serverId + ":" + acceptedProposal, bal.senderId);
			System.out.println("acceptor processing...promise bal: "+ bal +" accept proposal:" + acceptedProposal);
		}
		
		else if ( bal.compareTo(promisedBal) == 0 || bal.compareTo(promisedBal) > 0 )	{
			
			promisedBal = bal;
			if ( acceptedProposal!=null && bal.positionId > acceptedProposal.ballotNumber.positionId)	
				acceptedProposal = null;
			
			sender.send("promise:" + bal + ":" + server.serverId + ": "+ acceptedProposal, bal.senderId);
			System.out.println("acceptor processing...promise bal: "+ bal +" accept proposal:" + acceptedProposal);
		}
		
		
		
	}
	
	public void receiveAcceptRequest( Proposal proposal)	{
		
		if (promisedBal == null || proposal.ballotNumber.compareTo(promisedBal) == 0 || proposal.ballotNumber.compareTo(promisedBal) > 0 )	{
			
			promisedBal = proposal.ballotNumber;
			acceptedProposal = proposal;
			sender.broadCast("accepted:" + acceptedProposal);
		}
		
	}
	
}
