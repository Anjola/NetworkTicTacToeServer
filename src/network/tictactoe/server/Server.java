package network.tictactoe.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.MINUTES;

public class Server {

	// constants
	public static final int DEFAULT_PORT = 20000;
	public static final int MAX_PACKET_SIZE = 512;

	// port number to listen on
	protected int port;

	// set of clientEndPoints
	
	//maps a groupName to a group. Stores all groups.
	protected static final Map<String, Group> groups = Collections.synchronizedMap(
			new HashMap<String, Group>()); 
	//stores the list of taken names so the server knowns names not in usse 
	protected static final Set<String> names = Collections.synchronizedSet(new HashSet<String>());
	protected static final Map<String, User> clients = Collections.synchronizedMap(new HashMap<String, User>());
	//maps a client to it's pending messages. Messages still deliverable on quitting group
	protected static final Map<String, List<String>> messageQueue = Collections.synchronizedMap(new HashMap<String, List<String>>());
	//stores all messages that have not been acknowledged. maps an ackId to the Scheduled sender 
	protected static final Map<String, ScheduledExecutorService> pendingAck = 
			Collections.synchronizedMap(new HashMap<String, ScheduledExecutorService>());
	//clean up task 
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	//for unique id generation. synchronized int
	protected static final AtomicLong  clientID = new AtomicLong();
	//for unique id generation for agames
	protected static final AtomicLong  gameID = new AtomicLong();
	// constructor
	Server(int port) {
		this.port = port;
	}

	// start up the server
	public void start() {
		DatagramSocket socket = null;
		try {
			// create a datagram socket, bind to port port. See
			// http://docs.oracle.com/javase/tutorial/networking/datagrams/ for
			// details.

			socket = new DatagramSocket(port);
			 
			//timer that wipes message queue every 15 minutes. 
			scheduler.scheduleAtFixedRate(
					 new Runnable()
					 {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							messageQueue.clear();
						}
						 
					 }
					 , 15,15,MINUTES);

			// receive packets in an infinite loop
			while (true) {
				// create an empty UDP packet
				byte[] buf = new byte[Server.MAX_PACKET_SIZE];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				// call receive (this will poulate the packet with the received
				// data, and the other endpoint's info)
				socket.receive(packet);
				// start up a worker thread to process the packet (and pass it
				// the socket, too, in case the
				// worker thread wants to respond)
				WorkerThread t = new WorkerThread(packet, socket);
				t.start();
			}
		} catch (IOException e) {
			// we jump out here if there's an error, or if the worker thread (or
			// someone else) closed the socket
			e.printStackTrace();
		} finally {
			if (socket != null && !socket.isClosed())
				socket.close();
		}
	}
	
	//add a new client to the client list 
	public static boolean addClient(String clientID,User client)
	{
		if(clients.containsKey(clientID))
			return false;
		clients.put(clientID,client);
		return true;
	}
	
	//remove a client from the list of existing clients. 
	public static boolean removeClient(String clientID)
	{
		for(Group group: groups.values())
		{
			//remove from all groups. 
			removeClientFromGroup(group.name,clients.get(clientID));
		}
		if(clients.containsKey(clientID)){
			clients.remove(clientID);
			return true;
		}
		return false;
		
	}
	//with maxmembers
	public static boolean addClientToGroup(String name,
			User client, int maxMembers) {
		if(groups.containsKey(name)) {
			Group group = groups.get(name);
			//can change member size
			group.MaxMembers = maxMembers;
			return group.addMember(client);
			
		} else {
			//create group 
			Group group = new Group(name,maxMembers);
			if(group.addMember(client))
			{
				groups.put(name, group);
				return true;
			}
			return false;
		}
	}	
	//add client to group
	public static boolean addClientToGroup(String name, User client)
	{
		//group full returns false;
		if(groups.containsKey(name)) {
			Group group = groups.get(name);
			return group.addMember(client);
			
		} else {
			//create group 
			Group group = new Group(name);
			if(group.addMember(client))
			{
				groups.put(name, group);
				return true;
			}
			return false;
		}
	}
	
	//removeClientFromGroup
	public static boolean removeClientFromGroup(String groupName,User client)
	{
		Group group = groups.get(groupName);
		if(group!=null && group.removeMember(client))
		{
			//no more members
			if(group.members.isEmpty())
			{
				groups.remove(groupName);
				return true;
			}
		}
		return false;
		
	}
	
	// main method
	public static void main(String[] args) {
		int port = Server.DEFAULT_PORT;

		// check if port was given as a command line argument
		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.out.println("Invalid port specified: " + args[0]);
				System.out.println("Using default port " + port);
			}
		}

		// instantiate the server
		Server server = new Server(port);

		System.out
				.println("Starting server. Connect with netcat (nc -u localhost "
						+ port
						+ ") or start multiple instances of the client app to test the server's functionality.");

		// start it
		server.start();

	}


}
