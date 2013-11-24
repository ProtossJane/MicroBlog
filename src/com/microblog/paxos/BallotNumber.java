package com.microblog.paxos;

public class BallotNumber {
	public int proposalId;
	public int senderId;
	public int positionId;
	
	public BallotNumber (int proposalId, int senderId, int positionId)	{
		this.proposalId	  = proposalId;
		this.senderId	  = senderId;
		this.positionId   = positionId;
	}
	public String toString ()	{
		return proposalId + ":" + senderId + ":" + positionId;
	}
}
