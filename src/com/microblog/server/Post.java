package com.microblog.server;

import java.net.Socket;

public class Post {

	public String message;
	public Socket socket;
	public int position;
	
	public Post (String message, int position, Socket socket)	{
		this.message  = message;
		this.position = position;
		this.socket   = socket;
	}
	
	public String toString ()	{
		
		return "["+ message + "]" + " at " + position;
	}
}
