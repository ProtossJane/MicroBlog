package com.microblog.paxos;

import java.util.LinkedList;

public class Paxos {
	
	protected Sender sender;
	protected LinkedList<String> jobQueue;
	
	public Paxos (Sender sender)	{
		this.sender = sender;
		jobQueue = new LinkedList<String>();
	}
	
	public synchronized void addJob( String job )	{
		jobQueue.add(job);
	}
	
	public synchronized String popJob ()	{
		return jobQueue.poll();
	}
}
