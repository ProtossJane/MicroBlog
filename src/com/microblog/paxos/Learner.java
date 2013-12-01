package com.microblog.paxos;

import java.io.IOException;
import java.util.HashMap;

import com.microblog.server.FrontServer;

public class Learner {
	
	protected Sender sender;
	protected int quorumSize = FrontServer.quorumSize;
	protected HashMap<BallotNumber, Integer> proposals = new HashMap<BallotNumber, Integer>();
	protected HashMap<Integer, Integer> recoverRespond = new HashMap<Integer, Integer>();
	protected boolean recoverReady = false;
	protected FrontServer server = FrontServer.getInstance();
	
	
	
	public Learner (Sender sender)	throws IOException{
		
		this.sender = sender;
		
	}
	
	public void receiveAccepted ( Proposal acceptedProposal )	{
		
		if ( acceptedProposal.ballotNumber.positionId <= server.currentPosition)
			return;
		
		if ( !proposals.containsKey( acceptedProposal.ballotNumber) )	{
			proposals.put( new BallotNumber(acceptedProposal.ballotNumber), 0);
			System.out.println("accected new proposal " + proposals.get(acceptedProposal.ballotNumber));
		}
		
		int count = proposals.get(acceptedProposal.ballotNumber).intValue() + 1;
		proposals.put(acceptedProposal.ballotNumber, count);
		
		if (count == quorumSize)	{
			sender.broadCast("decide:" + acceptedProposal);
			if (acceptedProposal.ballotNumber.positionId > server.currentPosition + 1) 	{//gap
				
				if(acceptedProposal.ballotNumber.positionId > server.paxosInstance.maxPosition )
					server.paxosInstance.maxPosition = acceptedProposal.ballotNumber.positionId;
				server.paxosInstance.isRecover	= true;
				server.paxosInstance.addDecide(acceptedProposal.ballotNumber.positionId, acceptedProposal);
				return;
			}
			server.GlobalLog.add(acceptedProposal.ballotNumber.positionId, acceptedProposal);
			server.currentPosition = acceptedProposal.ballotNumber.positionId;
			server.paxosInstance.maxPosition = acceptedProposal.ballotNumber.positionId;
			System.out.println("write proposal " + acceptedProposal);
		}
	}
	
	public void receiveDecide ( Proposal decidedProposal)	{
		
		if ( decidedProposal.ballotNumber.positionId == server.currentPosition + 1 )	{
			server.GlobalLog.add(decidedProposal);
			server.currentPosition += 1;
		}
		
		else if ( decidedProposal.ballotNumber.positionId > server.currentPosition + 1 )	{
			server.paxosInstance.addDecide( decidedProposal.ballotNumber.positionId, decidedProposal);
			if( decidedProposal.ballotNumber.positionId > server.paxosInstance.maxPosition )
				server.paxosInstance.maxPosition = decidedProposal.ballotNumber.positionId;
		}
		
	}
	
	public void receiveRecoverRespond(int senderId, int positionId)	{
		
		if ( !recoverRespond.containsKey(senderId) || recoverRespond.get(senderId).intValue() < positionId )
			recoverRespond.put(senderId, positionId);
		if (positionId > server.paxosInstance.maxPosition)
			server.paxosInstance.maxPosition = positionId;
		
		if ( recoverRespond.size() >= FrontServer.quorumSize )
			recoverReady = true;
		
	}
	
}