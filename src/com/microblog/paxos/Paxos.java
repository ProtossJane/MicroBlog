package com.microblog.paxos;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import com.microblog.server.Post;

public class Paxos {
	
	protected Sender sender;
	protected volatile LinkedList<String> jobQueue;
	protected volatile LinkedList<Post>	 postQueue;
	protected volatile LinkedList<String> recoverQueue;
	protected Proposer proposer;
	protected Accepter accepter;
	protected Learner learner;
	protected int	maxPosition = -1;
	protected boolean isRecover = false;
	protected volatile HashMap< Integer,Proposal > decideBuffer;
	
	public Paxos (HashMap<Integer, String> route) throws IOException	{
		sender 		= new Sender(route);
		jobQueue 	= new LinkedList<String>();
		postQueue 	= new LinkedList<Post>();
		recoverQueue = new LinkedList<String>();
		proposer 	= new Proposer (sender);
		accepter 	= new Accepter (sender);
		learner 	= new Learner (sender);
		decideBuffer = new HashMap <Integer, Proposal> ();
	}
	
	public synchronized void addPost (Post post)	{
		postQueue.add(post);
	}
	
	public synchronized Post popPost ()	{
		return postQueue.poll();
	}
	
	public synchronized boolean isPostEmpty()	{
		return postQueue.isEmpty();
	}
	
	public synchronized void addJob( String job )	{
		jobQueue.add(job);
	}
	
	public synchronized String popJob ()	{
		return jobQueue.poll();
	}
	
	public synchronized boolean isJobEmpty()	{
		return jobQueue.isEmpty();
	}
	
	public synchronized void addDecide( Integer position, Proposal decidedProposal )	{
		decideBuffer.put(position, decidedProposal);
	}
	
	public synchronized Proposal popRecover ( Integer position)	{
		return decideBuffer.get(position);
	}
	
	public synchronized void addRecover (String recoverMsg)	{
		recoverQueue.add(recoverMsg);
	}
}
