package com.microblog.paxos;

import java.io.IOException;
import java.util.HashMap;

import com.microblog.server.FrontServer;

public class Learner {
	
	protected Sender sender;
	protected int quorumSize = FrontServer.quorumSize;
	protected HashMap<BallotNumber, Integer> proposals = new HashMap<BallotNumber, Integer>();
	//protected HashMap<Integer, Integer> recoverRespond = new HashMap<Integer, Integer>();
	//protected boolean recoverReady = false;
	protected FrontServer server = FrontServer.getInstance();
	protected Paxos paxosInstance;
	
	
	public Learner (Sender sender, Paxos paxosInstance)	throws IOException{
		
		this.sender = sender;
		this.paxosInstance = paxosInstance;
	}
	
	public void receiveAccepted ( Proposal acceptedProposal )	{
		
		if ( acceptedProposal.ballotNumber.positionId <= paxosInstance.currentPosition)
			return;
		
		if ( paxosInstance.accepter.promisedBal == null || acceptedProposal.ballotNumber.compareTo( paxosInstance.accepter.promisedBal) > 0)
			paxosInstance.accepter.promisedBal = acceptedProposal.ballotNumber;
		
		if ( !proposals.containsKey( acceptedProposal.ballotNumber) )	{
			proposals.put( new BallotNumber(acceptedProposal.ballotNumber), 0);
			//System.out.println("accected new proposal " + proposals.get(acceptedProposal.ballotNumber));
		}
		
		int count = proposals.get(acceptedProposal.ballotNumber).intValue() + 1;
		proposals.put(acceptedProposal.ballotNumber, count);
		
		if (count == quorumSize)	{
			sender.broadCast("decide:" + acceptedProposal);
			if (acceptedProposal.ballotNumber.positionId > paxosInstance.currentPosition + 1) 	{//gap
				
				if(acceptedProposal.ballotNumber.positionId > paxosInstance.maxPosition )
					paxosInstance.maxPosition = acceptedProposal.ballotNumber.positionId;
				server.setRecoverStatus(true);
				paxosInstance.addDecide(acceptedProposal.ballotNumber.positionId, acceptedProposal);
				return;
			}
			server.GlobalLog.add(acceptedProposal);
			paxosInstance.localLog.add(acceptedProposal);
			paxosInstance.currentPosition = acceptedProposal.ballotNumber.positionId;
			paxosInstance.maxPosition = acceptedProposal.ballotNumber.positionId;
<<<<<<< HEAD
			System.out.println("***********write proposal********** \n" + acceptedProposal+"\n\n");
=======
			//System.out.println("***********write proposal********** " + acceptedProposal);
>>>>>>> parent of 2135ebe... Revert "clean output"
			//System.out.println("id "+ paxosInstance.paxosId + " current position " + paxosInstance.currentPosition);
		}
	}
	
	public void receiveDecide ( Proposal decidedProposal)	{
		
		if ( paxosInstance.accepter.promisedBal == null || decidedProposal.ballotNumber.compareTo( paxosInstance.accepter.promisedBal) > 0)
			paxosInstance.accepter.promisedBal = decidedProposal.ballotNumber;
		
		if ( decidedProposal.ballotNumber.positionId == paxosInstance.currentPosition + 1 )	{
			server.GlobalLog.add(decidedProposal);
			paxosInstance.localLog.add(decidedProposal);
			paxosInstance.currentPosition += 1;
			paxosInstance.maxPosition = Math.max(paxosInstance.currentPosition, paxosInstance.maxPosition);
<<<<<<< HEAD
			System.out.println("***********write proposal********** \n" + decidedProposal+"\n\n");
=======
			//System.out.println("***********write proposal********** " + decidedProposal);
>>>>>>> parent of 2135ebe... Revert "clean output"
			//System.out.println("id "+paxosInstance.paxosId + " current position " + paxosInstance.currentPosition);
		}
		
		else if ( decidedProposal.ballotNumber.positionId > paxosInstance.currentPosition + 1 )	{
			paxosInstance.addDecide( decidedProposal.ballotNumber.positionId, decidedProposal);
			server.setRecoverStatus(true);
			if( decidedProposal.ballotNumber.positionId > paxosInstance.maxPosition )
				paxosInstance.maxPosition = decidedProposal.ballotNumber.positionId;
		}
		if (!paxosInstance.decideBuffer.isEmpty())
			processDecideBuffer ();
	}
	/*
	public void receiveRecoverRespond(int senderId, int positionId)	{
		
		if ( !recoverRespond.containsKey(senderId) || recoverRespond.get(senderId).intValue() < positionId )
			recoverRespond.put(senderId, positionId);
		if (positionId > paxosInstance.maxPosition)
			paxosInstance.maxPosition = positionId;
		
		if ( recoverRespond.size() >= FrontServer.quorumSize )
			recoverReady = true;
		
	}*/
	
	public void processDecideBuffer ()	{
		
		Proposal decidedProposal = paxosInstance.popDecide( paxosInstance.currentPosition + 1 );
		
			while ( decidedProposal != null)	{
				paxosInstance.localLog.add(decidedProposal);
				server.GlobalLog.add(decidedProposal);
				paxosInstance.currentPosition += 1;
<<<<<<< HEAD
				System.out.println("***********write proposal********** \n" + decidedProposal+"\n\n");
=======
				//System.out.println("***********write proposal********** " + decidedProposal);
>>>>>>> parent of 2135ebe... Revert "clean output"
				decidedProposal = paxosInstance.popDecide( paxosInstance.currentPosition + 1 );
			}
		
		
	}
	
}
