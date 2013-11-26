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
	
	public BallotNumber ( BallotNumber bal)	{
		
		this.proposalId	  = bal.proposalId;
		this.senderId	  = bal.senderId;
		this.positionId   = bal.positionId;
		
	}
	
	int compareTo (BallotNumber bal)	{
		if ( this.proposalId > bal.proposalId || (this.proposalId == bal.proposalId && this.senderId > bal.senderId))
			return 1;
		else if (this.proposalId == bal.proposalId && this.senderId == bal.senderId)
			return 0;
		else return -1;
	}
	
	public String toString ()	{
		return proposalId + ":" + senderId + ":" + positionId;
	}
}
