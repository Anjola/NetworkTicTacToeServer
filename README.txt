ASSIGNMENT 3 UDP GROUP SERVER
To connect to the server using netcat, 
use (nc -u localhost 20000) from a terminal on a device that has
netcat installed.

************************************************************************
COMMANDS AND PROTOCOL
************************************************************************
REGISTER,<Optional User ID>
	Creates a new user on the received endpoint. Including the client ID 
	indicates that this is a returning user from a different endpoint. 
	Changes the endpoint of the user with argument passed in 
	
NAME,<User ID>,<name>
	Gives the corresponding user the passed in name. 
	
JOIN,<User ID>,<Group name>,<Optional Max members>
	adds the corresponding user to the requested group or creates the 
	group if it does not already exist. The optional parameter can be 
	used to specify how many users max can be in the group. 

QUIT,<User ID>,<Group name>
	removes the user from the given group if the user is in it. Deletes
	the group if there are no more users there in. User can still receive
	messages that sent prior to quitting group. 

MSG,<User ID>,<Group name>,<Message>
	Sends the provided message to all the users in the specified group 
	Saves undelivered messages in a message queue that users can later
	poll to see the messages they missed while offline. 
	
POLL,<User ID>
	Sends any undelivered messages to the specified user. 

LISTGROUPS,<User ID>,<Optional Prefix>
	Sends a list of all the groups to the requesting user. Uses matching 
	prefix if provided for the listing. 

LISTMYGROUPS,<User ID>,<Optional Prefix>
	Sends a list of all the groups the user belongs to. Uses matching 
	prefix if provided by client for the listing. 

ACK,<Acknowlege ID>
	Clients way of telling server it received the message that has the 
	specified acknowlege ID(ClientID:RequestNumber)

SHUTDOWN
	Client wants server to shutdown. Only works if cient is on localhost
	

************************************************************************
EXAMPLE LOG
************************************************************************
REGISTER
1:1 REGISTERED
ACK,1:1
+SUCCESS1:1Acknowleged
NAME,1,Bob
1:2 +SUCCESS
NAME,1,Frank
1:2 +SUCCESS
1:3 +ERROR,Already have a name
ACK 1:2
1:2 +SUCCESS
1:3 +ERROR,Already have a name
ACK 1:3
1:2 +SUCCESS
ACK,1:3
1:3 +ERROR,Already have a name
+SUCCESS1:3Acknowleged
1:2 +SUCCESS
ACK,1:2
+SUCCESS1:2Acknowleged
REGISTER
2:1 REGISTERED
REGISTER
3:1 REGISTERED
REGISTER
4:1 REGISTERED
ACK,2:1
2:1 REGISTERED
+SUCCESS2:1Acknowleged
ACK,3:1
3:1 REGISTERED
+SUCCESS3:1Acknowleged
ACK,4:1
4:1 REGISTERED
+SUCCESS4:1Acknowleged
NAME,2,Bob
2:2 +ERROR,name already taken
ACK,2:2
2:2 +ERROR,name already taken
+SUCCESS2:2Acknowleged
NAME,2,Alice
2:3 +SUCCESS
NAME,3,Frank
3:2 +SUCCESS
2:3 +SUCCESS
ACK,2:3
+SUCCESS2:3Acknowleged
ACK,3:2
3:2 +SUCCESS
+SUCCESS3:2Acknowleged
JOIN,1,MyGroup,2
1:4 +SUCCESS
ACK 1:4
ACK,1:4
+SUCCESS1:4Acknowleged
JOIN,2,MyGroup
2:4 +SUCCESS,joined group MyGroup
ACK,2:4
+SUCCESS2:4Acknowleged
JOIN,3,MyGroup
3:3 +ERROR,group full/client already in group
ACK,3:3
+SUCCESS3:3Acknowleged
MSG,2,MyGroup,Hello People!
1:5 From Aliceto MyGroup: Hello People!
2:5 From Aliceto MyGroup: Hello People!
2:6 +SUCCESS,message sent to MyGroup
ACK,1:5
+SUCCESS1:5Acknowleged
ACK 2:5
2:5 From Aliceto MyGroup: Hello People!
2:6 +SUCCESS,message sent to MyGroup
ACK,2:5
2:6 +SUCCESS,message sent to MyGroup
2:5 From Aliceto MyGroup: Hello People!
+SUCCESS2:5Acknowleged
ACK,2:6
+SUCCESS2:6Acknowleged
POLL,3
3:4 +SUCCESS,no messages
ACK,3:4
+SUCCESS3:4Acknowleged
LISTGROUPS,1,My
1:6 +SUCESS
MyGroup
ACK,1:6
1:6 +SUCESS
MyGroup
+SUCCESS1:6Acknowleged
LISTMYGROUPS,2
2:7 +SUCESS
MyGroup
ACK,2:7
+SUCCESS2:7Acknowleged
QUIT,1
1:7 +ERROR,no group specified. Check parameters
Shutdown
1:7 +ERROR,no group specified. Check parameters

BAD REQUEST
SHUTDOWN
1:7 +ERROR,no group specified. Check parameters
Shutting Down Server

 
