package com.microblog.paxos;

public class Proposal {
	public BallotNumber ballotNumber;
	public String message;
	
	public Proposal (BallotNumber ballotNumber, String message, int positionId)	{
		this.ballotNumber = ballotNumber;
		this.message = message;
		
	}
	
	public String toString ()	{
		return ballotNumber.toString() + ":" + message;
	}
}
