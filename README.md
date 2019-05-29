# 1020-baal_NFP103_2019
ACCOV project
This project i did after i finished the ACCOV Cours at CNAM (Conservatoire national des arts et métiers)
this project is similar to IRC (Internet Relay Chat).It is desired to create a client / server system that makes it possible to communicate with several simultaneously on
the network.
/n/r
Client side:
------------
1-the client sends messages (text) to the server.
2-the client accepts the following commands typed:
  2.1.1-who:List all clients connected to server
  2.1.2-connect:Connect to the server ex: connect -u root -p root -h 192.168.0.10:5555
  2.1.3-help:Print a help message
  2.1.4-quit:Exit application
  2.2.1-to_usr:chat peer-to-peer with another user ex: to_usr pc1 message
  2.3.1-to_group:chat with memebers group ex: to_group grp1 message
  2.3.2-create_grp:create group ex: create_grp nameOfYourGroup
  2.3.3-delete_grp:delete group if you are admin of group ex: delete_grp nameOfGroup
  2.3.4-exit_grp:exit group ex: exit_grp nameOfGroup
  2.3.5-list_grp:list all groups founded on the server
  2.3.6-members:list all members in groups ex: members nameOfGroup
  2.4.1-send_to:send file to someone connected to the server ex: send_to pc1 pathOfYourFile
 
 Server side:
 ------------
1-the server broadcasts all messages (text) it receives from one of these clients to all other clients
connected and known.
2-the server accepts the following commands:
  2.1.1-who:List all clients connected to server
  2.1.2-help:Print a help message
  2.1.3-kill:kill user ex: kill pc1
  2.1.4-quit:Exit application
  2.1.5-start:Configure a machine to listen on a specific port ex: start 5555
  3.1.1-delete_grp:delete group if you are admin of group ex: delete_grp nameOfGroup
  3.1.2-list_grp:list all groups founded on the server
  3.1.3-members:list all members in groups ex: members nameOfGroup

I use Log4j for logging and Junit 5 for testing.
