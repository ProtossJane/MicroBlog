package com.microblog.paxos;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;

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
	protected Timer	recoverTimer;
	
	public Paxos (HashMap<Integer, String> route) throws IOException	{
		sender 		= new Sender(route);
		jobQueue 	= new LinkedList<String>();
		postQueue 	= new LinkedList<Post>();
		recoverQueue = new LinkedList<String>();
		proposer 	= new Proposer (sender);
		accepter 	= new Accepter (sender);
		learner 	= new Learner (sender);
		decideBuffer = new HashMap <Integer, Proposal> ();
		recoverTimer = new Timer();
		recoverTimer.scheduleAtFixedRate(new Recover( sender), 2000, 5000);
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
	
	public synchronized Proposal popDecide ( Integer position)	{
		return decideBuffer.get(position);
	}
	
	public synchronized void addRecoverJob (String recoverMsg)	{
		recoverQueue.add(recoverMsg);
	}
	
	public synchronized String popRecoverJob ()	{
		return recoverQueue.poll();
	}
}
