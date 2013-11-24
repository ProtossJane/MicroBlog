package com.microblog.paxos;

public class Dispenser implements Runnable{

	protected Paxos paxosInstance;
	
	public Dispenser( Paxos paxos)	{
		this.paxosInstance = paxos;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
