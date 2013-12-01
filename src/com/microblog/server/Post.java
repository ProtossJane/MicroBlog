package com.microblog.server;

import java.net.Socket;

public class Post {

	public String message;
	public Socket socket;
	public int position;
	public long timeStamp;
	
	public Post (String message, int position, Socket socket)	{
		this.message  	= message;
		this.position 	= position;
		this.socket   	= socket;
		this.timeStamp	= 0; 
	}
	
	public String toString ()	{
		
		return "["+ message + "]" + " at " + position;
	}
}
