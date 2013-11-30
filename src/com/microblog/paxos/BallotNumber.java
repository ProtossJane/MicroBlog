package com.microblog.paxos;

public class BallotNumber {
	public int proposalId;
	public int senderId;
	public int positionId;
	
	public BallotNumber (int proposalId, int senderId, int positionId)	{
		this.proposalId	  = proposalId;
		this.senderId	  = senderId;
		this.positionId   = positionId;
	}
	
	public BallotNumber ( BallotNumber bal)	{
		
		this.proposalId	  = bal.proposalId;
		this.senderId	  = bal.senderId;
		this.positionId   = bal.positionId;
		
	}
	
	int compareTo (BallotNumber bal)	{
		if ( this.proposalId > bal.proposalId || (this.proposalId == bal.proposalId && this.senderId > bal.senderId))
			return 1;
		else if (this.proposalId == bal.proposalId && this.senderId == bal.senderId)
			return 0;
		else return -1;
	}
	
	
	
	/*@Override
	public int hashCode()	{
	
		
	}*/
	
	public String toString ()	{
		return proposalId + ":" + senderId + ":" + positionId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + positionId;
		result = prime * result + proposalId;
		result = prime * result + senderId;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BallotNumber))
			return false;
		BallotNumber other = (BallotNumber) obj;
		if (positionId != other.positionId)
			return false;
		if (proposalId != other.proposalId)
			return false;
		if (senderId != other.senderId)
			return false;
		return true;
	}
}
