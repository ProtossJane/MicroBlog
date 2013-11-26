package com.microblog.paxos;

public class Proposal {
	public BallotNumber ballotNumber;
	public Message message;
	
	public Proposal (BallotNumber ballotNumber, Message message)	{
		this.ballotNumber = ballotNumber;
		this.message = message;
		
	}
	
	public String toString ()	{
		return ballotNumber.toString() + ":" + message;
	}
}
