package network.tictactoe.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Group {
	int MaxMembers;
	final int DEFAULT_MEMBER_SIZE = 20;
	Set<User> members = Collections.synchronizedSet(new HashSet<User>());
	String name;
	
	public Group(String name, int MaxMembers)
	{
		this.name = name;
		this.MaxMembers = MaxMembers;
	}
	
	//use default size
	public Group(String name)
	{
		this.name = name;
		this.MaxMembers = DEFAULT_MEMBER_SIZE;
	}
	
	public boolean addMember(User client)
	{
		if(members.size() < MaxMembers)
		{
			members.add(client);
			return true;
		}
		return false;
		
	}
	
	public boolean removeMember(User client)
	{
		if(members.contains(client))
		{
			members.remove(client);
			return true;
		}
		return false;
	}
	

}
