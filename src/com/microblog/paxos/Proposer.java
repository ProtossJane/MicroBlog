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
	protected Paxos paxosInstance;
	protected BallotNumber acceptedBal 		   = null;
	protected BallotNumber bal				   = new BallotNumber(0, FrontServer.serverId, 0);
	
	public Proposer (Sender sender, Paxos paxosInstance) throws IOException	{
		this.sender = sender;
		this.paxosInstance = paxosInstance;
	}
	
	public void setProposal (Message message)	{
		this.message = message;
	}
	
	public void prepare()	{
		isPendding = true;
		promiseReceived.clear();
		if (paxosInstance.accepter.promisedBal!=null)
			bal.proposalId = paxosInstance.accepter.promisedBal.proposalId + 1;
		else
			bal.proposalId += 1;
		bal.positionId = paxosInstance.currentPosition + 1;
		//System.out.println("ID "+ FrontServer.serverId + " broadcast prepare:"+bal);
		sender.broadCast("prepare:" + bal );
		//sender.send("prepare:" + bal, 1);
	}
	
	public void receivePromise (BallotNumber bal, Proposal acceptedProposal, int senderId)	{
		//System.out.println("compare this bal: " + this.bal + " with receive bal: " + bal);
		
		if ( !bal.equals(this.bal) || promiseReceived.contains(senderId) )	{
			//System.out.println("throw promise " + bal);
			return;
		}
		
		promiseReceived.add(senderId);
		if (acceptedProposal != null)
			if (acceptedBal == null || acceptedProposal.ballotNumber.compareTo(acceptedBal) > 0) {
				acceptedBal = acceptedProposal.ballotNumber;
				this.message = acceptedProposal.message;
			}
		
		if (promiseReceived.size() == FrontServer.quorumSize)	{
			System.out.println("reach majority for " + bal);
			
			if ( message.message != null && isPendding == true)
				sender.broadCast("accept:" + bal + ":" + message);
			isPendding = false;
		}
		
		//System.out.println("# of promise:" + promiseReceived.size());
	}
	
	public boolean isPendding ()	{
		return isPendding;
	}

}
