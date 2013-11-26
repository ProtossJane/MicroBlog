package com.microblog.paxos;

import java.io.IOException;
import java.util.HashMap;

import com.microblog.server.FrontServer;

public class Learner {
	
	protected Sender sender;
	protected int quorumSize = FrontServer.quorumSize;
	protected HashMap<BallotNumber, Integer> proposals = null;
	protected FrontServer server = FrontServer.getInstance();
	
	public Learner (Sender sender)	throws IOException{
		
		this.sender = sender;
		
	}
	
	public void receiveAccepted ( Proposal acceptedProposal )	{
		
		if ( acceptedProposal.ballotNumber.positionId <= server.lastPosition)
			return;
		
		if ( !proposals.containsKey( acceptedProposal.ballotNumber) )
			proposals.put( new BallotNumber(acceptedProposal.ballotNumber), 0);
		
		int count = proposals.get(acceptedProposal.ballotNumber).intValue() + 1;
		proposals.put(acceptedProposal.ballotNumber, count);
		
		if (count == quorumSize)	{
			if (acceptedProposal.ballotNumber.positionId > server.lastPosition + 1) 	{//gap
				// recover
			}
			server.GlobalLog.add(acceptedProposal.ballotNumber.positionId, acceptedProposal);
			server.lastPosition = acceptedProposal.ballotNumber.positionId;
			sender.broadCast("decide:" + acceptedProposal);
			
		}
	}
	
	public void receiveDecide ( Proposal decidedProposal)	{
		
	
		
	}
}
