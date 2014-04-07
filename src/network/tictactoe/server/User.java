package network.tictactoe.server;

//import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

//main actor. allows multiple users from same endpoint
public class User {
	//unique ID used for communication
	private long id;
	public String name;
	//public Timestamp lastSeen; 
	public ClientEndPoint endpoint;
	//public boolean isActive
	public final AtomicInteger currentReqID;
	
	public User(ClientEndPoint endpoint){
		this.endpoint = endpoint;
		this.id = Server.clientID.incrementAndGet();
		//this.isActive = true;
		this.currentReqID = new AtomicInteger();
	}
	
	//id to string
	public String getID()
	{
		return ""+id;
	}
	
	
	
	
}
