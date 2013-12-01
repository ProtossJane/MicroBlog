package com.microblog.paxos;

import java.io.IOException;
import java.util.TimerTask;

import com.microblog.server.FrontServer;

public class Recover extends TimerTask {

	Sender sender;
	FrontServer server = FrontServer.getInstance();
	public Recover(Sender sender) throws IOException	{
		this.sender = sender;
	}
	@Override
	public void run() {
		
		System.out.println("sending recover");
		sender.broadCast( "recover:" + FrontServer.serverId + server.currentPosition);
		
	}

}
