package com.microblog.paxos;

public class BallotNumber {
	public int ballotNumber;
	public int senderId;
	
	public BallotNumber (int ballotNumber, int senderId)	{
		this.ballotNumber = ballotNumber;
		this.senderId	  = senderId;
	}

}
