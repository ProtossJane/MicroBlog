package com.microblog.paxos;

public class Message {
	
	String message;
	int senderId;
	
	public Message (int senderId, String message )	{
		
		this.senderId = senderId;
		this.message = message;
		
	}

	public String toString ()	{
		return senderId + ":" + message;
	}
}
