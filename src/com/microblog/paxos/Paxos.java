package com.microblog.paxos;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class Paxos {
	
	protected Sender sender;
	protected LinkedList<String> jobQueue;
	protected Proposer proposer;
	protected Accepter accepter;
	
	public Paxos (HashMap<Integer, String> route) throws IOException	{
		sender = new Sender(route);
		jobQueue = new LinkedList<String>();
		proposer = new Proposer(sender);
	}
	
	public synchronized void addJob( String job )	{
		jobQueue.add(job);
	}
	
	public synchronized String popJob ()	{
		return jobQueue.poll();
	}
}
