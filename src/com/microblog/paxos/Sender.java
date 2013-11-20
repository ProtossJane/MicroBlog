package com.microblog.paxos;

import java.io.IOException;

import com.microblog.client.Client;

public class Sender extends Client{
	
	public Sender (String host, int port) throws IOException {
		super(host, port);
	}

}
