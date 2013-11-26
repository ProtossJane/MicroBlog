package com.microblog.paxos;

public class Message {
	
	String message;
	int senderId;
	
	public Message (String message, int senderId)	{
		this.message = message;
		this.senderId = senderId;
	}

	public String toString ()	{
		return senderId + ":" + message;
	}
}
