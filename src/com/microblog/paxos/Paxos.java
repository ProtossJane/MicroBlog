package com.microblog.paxos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Paxos {
	
	protected Sender sender;
	//protected volatile LinkedList<String> jobQueue;
	//protected volatile LinkedList<Post>	 postQueue;
	//protected volatile LinkedList<String> recoverQueue;
	protected Proposer proposer;
	protected Accepter accepter;
	protected Learner learner;
	protected volatile int	maxPosition = -1;
	protected volatile int currentPosition = -1;
	//protected volatile boolean isRecover = true;
	protected volatile HashMap< Integer,Proposal > decideBuffer;
	protected ArrayList<Proposal> localLog = new ArrayList<Proposal> ();
	
	
	public Paxos ( Sender sender) throws IOException	{
		this.sender 		= sender;
		//jobQueue 	= new LinkedList<String>();
		//postQueue 	= new LinkedList<Post>();
		//recoverQueue = new LinkedList<String>();
		proposer 	= new Proposer (sender, this);
		accepter 	= new Accepter (sender, this);
		learner 	= new Learner (sender, this);
		decideBuffer = new HashMap <Integer, Proposal> ();
		
	}
	
	/*public synchronized void setRecoverStatus ()	{
		isRecover = true;
	}
	
	public synchronized boolean getRecoverStatus () {
		return isRecover;
	}
	*/
	/*public synchronized void addPost (Post post)	{
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
	}*/
	
	public synchronized void addDecide( Integer position, Proposal decidedProposal )	{
		decideBuffer.put(position, decidedProposal);
	}
	
	public synchronized Proposal popDecide ( Integer position)	{
		return decideBuffer.get(position);
	}
	
	/*public synchronized void addRecoverJob (String recoverMsg)	{
		recoverQueue.add(recoverMsg);
	}
	
	public synchronized String popRecoverJob ()	{
		return recoverQueue.poll();
	}*/
	
	public void setMaxPosition (int position)	{
		if( position > maxPosition)
			maxPosition = position;
	}
	
	public boolean noGap ()	{
		return currentPosition == maxPosition;
	}
}
