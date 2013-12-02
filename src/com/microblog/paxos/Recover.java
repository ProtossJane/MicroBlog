package com.microblog.paxos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;

import com.microblog.server.FrontServer;

public class Recover extends TimerTask {

	Sender sender;
	FrontServer server = FrontServer.getInstance();
	protected ArrayList<Paxos> multiPaxos;
	
	public Recover(Sender sender, ArrayList<Paxos> multiPaxos) throws IOException	{
		this.sender = sender;
		this.multiPaxos = multiPaxos;
	}
	@Override
	public void run() {
		
		//System.out.println("sending recover");
		String recoverInfo = "recover:" + FrontServer.serverId;
		for (Paxos paxos : multiPaxos)	
			recoverInfo += ":" + paxos.currentPosition ;
		
		if (!server.isStop() && server.getRecoverStatus())
			sender.broadCast( recoverInfo);
		
	}

}
