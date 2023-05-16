# Key-Value-Store (2021)

> Tools - Java, Socket Programming

## About

Key-Value Store based on a Client-Server architecture, that is single-threaded and leverages Socket Programming (both TCP and UDP sockets) for inter-communication, built to implement the learnings made from coursework in Distributed Systems. The TCP and UDP applications define a logger to store the logs into a separate file for the server and the client.
- For the TCP part, the pre-defined operations are performed and then the user is asked for the input of whatever it wants from the list of choices. The user has the option to perform three operations on the server. The user input is received at the server, the input message is checked for consistency and correctness. If the message is invalid, a log is made, and the client moves on to providing the next input. If the message is valid, the respective operation is performed on the key-value store as directed by the user input. The acknowledgement of the same is sent back to the client, and the client logs the same on its side.
- For the UDP part, firstly, the pre-defined operations are performed. Next, input is sought from the user. Once the input message is made into a packet ready for sending to the server, the client signals the server to be prepared to accept the incoming packet of the said type. The user message is broken into individual chunks and then transmitted to the server. The server then checks the validity of the message received from the client. If it is valid, it moves on to performing the required operations on the key-value store and responds with the respective acknowledgement (same as the TCP application).

## Project-Structure

**TCP**:

- *Server_TCP_app.java* Contains the java code for the TCP server application. This file accepts one optional command-line argument: the port it is supposed to listen on. If this file is run without the shell scripts and not inside any docker container (java from cmd prompt), there is additional support for falling to the default port to listen on; if no command-line input is provided. This file takes in one optional command-line argument in the following format: `java Server_TCP_app <server_listening_port>`

- *Client_TCP_app.java* Contains the java code for the TCP client application. This file accepts two optional command-line arguments: the address of the server and the port it is supposed to send the request to the server. If this file is run without the shell scripts and not inside any docker container (java from cmd prompt), there is additional support for falling to the default server address and port to communicate on; if no command-line input is provided. This file takes in two optional command-line arguments in the following format: `java Client_TCP_app <server_address> <server_listening_port>`

- *Dockerfile* This contains the parameters to generate and create images for generating both builds the TCP-client and the TCP-server. JDK 17 from the alpine image has been used.

- *tcpserver.sh* This file contains all the configurations needed to start from scratch by creating the docker images for the client and the server and assigning them to the default 'host' network. After this, the script launches the container for the TCP server. Note that the default 'host' network has been used to avoid exposing any ports, as, on a custom network, the required ports need to be exposed manually. This has been done with the aim to reduce any complexities during the execution. The script terminates with deleting the container from the environment to avoid any conflicts. This file takes in one command-line argument in the following format: `./tcpserver.sh <server_listening_port>`

- *tcpclient.sh* This file contains all the configurations needed to start the TCP client container that has been created in the previous step. The client container also gets attached to the default 'host' network. This script terminates with deleting the container from the environment to avoid any conflicts. This file takes in two command-line arguments in the following format: `./tcpclient.sh <server_address> <server_listening_port>`

**UDP**:

- *Server_UDP_app.java* Contains the java code for the UDP server application. This file accepts two optional command-line arguments: the port it is supposed to listen on and the port it is supposed to use to send responses on. Suppose this file is run without the shell scripts or not inside any docker container (java from cmd prompt). In that case, there is additional support for falling to the default communication ports if no command-line input is provided. This file takes in two optional command-line arguments in the following format: `java Server_UDP_app <server_listening_port> <client_listening_port>`

- *Client_UDP_app.java* Contains the java code for the UDP client application. This file accepts two optional command-line arguments: the listening port for the client and the port it is supposed to use to send the requests to the server. Suppose this file is run without the shell scripts or not inside any docker container (bare java from cmd prompt). In that case, there is additional support for falling to the default communication ports if no command-line input is provided. This file takes in two optional command-line arguments in the following format: `java Client_UDP_app <client_listening_port><server_listening_port>`

- *Dockerfile* This contains the parameters to generate and create images for generating both builds the udp-client and the udp-server. JDK 17 from the alpine image has been used.

- *udpserver.sh* This file contains all the configurations needed to start from scratch by creating the docker images for the client and the server and assigning them to the default 'host' network. After this, the script launches the container for the UDP server. Note that the default 'host' network has been used to avoid exposing any ports, as, on a custom network, the required ports need to be exposed manually. This has been done with the aim to reduce any complexities during the execution. The script terminates with deleting the container from the environment to avoid any conflicts. This file takes in two command-line arguments in the following format: `./udpserver.sh <server_listening_port> <client_listening_port>`

- *udpclient.sh* This file contains all the configurations needed to start the UDP client container that has been created in the previous step. The client container also gets attached to the default 'host' network. This script terminates with deleting the container from the environment to avoid any conflicts. This file takes in two command-line arguments in the following format: `./udpclient.sh <client_listening_port> <server_listening_port>`


## Sample Execution

There are two possible execution methods for this project as follows:

### Method 1 (CMD prompt)

- Executing TCP application simply on the CMD prompt:
  - javac Server_TCP_app.java Client_TCP_app.java
  - java Server_TCP_app
  - java Client_TCP_app (On another terminal).
  - The respective logs for the server and client fall into the working directory in a separate file.

**Client_TCP_app output**
<p align="center">
<img src="https://github.com/divitvasu/Key-Value-Store/assets/30820920/5d13dee3-2f2c-463e-ab07-0ff3f4fc14ae" alt="Image" width="550" height="300">
<img src="https://github.com/divitvasu/Key-Value-Store/assets/30820920/e6ad9986-b384-4aa6-b04e-23f82541d77e" alt="Image" width="550" height="500">
</p>

**Server_TCP_app output**
<p align="center">
<img src="https://github.com/divitvasu/Key-Value-Store/assets/30820920/66c324dd-18f3-44a4-81b7-3185deaeae0b" alt="Image" width="550" height="300">
</p>

**TCP Server Logs output**
<p align="center">
<img src="https://github.com/divitvasu/Key-Value-Store/assets/30820920/4b6e45d4-dbf1-4973-a520-8728afb96050" alt="Image" width="450" height="550">
</p>

- Executing UDP application simply on the CMD prompt:
  - javac Server_UDP_app.java Client_UDP_app.java
  - java Server_UDP_app
  - java Client_UDP_app (On another terminal).
  - The respective logs for the server and client fall into the working directory in a separate file.

**Client_UDP_app output**
<p align="center">
<img src="https://github.com/divitvasu/Key-Value-Store/assets/30820920/6de9d189-901e-4fc0-b9a6-46a9abcbea10" alt="Image" width="550" height="300">
<img src="https://github.com/divitvasu/Key-Value-Store/assets/30820920/f087bdac-b3c1-4ca6-aaa2-238855c0676b" alt="Image" width="550" height="500">
</p>

**Server_UDP_app output**
<p align="center">
<img src="https://github.com/divitvasu/Key-Value-Store/assets/30820920/dd6bf212-301e-47db-b720-ff70cfba420b" alt="Image" width="550" height="400">
</p>

**UDP Server Logs output**
<p align="center">
<img src="https://github.com/divitvasu/Key-Value-Store/assets/30820920/476c8f20-0bea-4c41-8936-29057082a670" alt="Image" width="450" height="550">
</p>

### Method 2 (Docker and shell script)

- Executing TCP application using docker and Linux Shell:
  - ./tcpserver.sh 55998
  - ./tcpclient.sh localhost 55998
  - The respective logs fall inside the docker images.
 
**TCP Server script output**
<p align="center">
<img src="https://github.com/divitvasu/Key-Value-Store/assets/30820920/37b047bf-1dd0-4e0b-b2bd-26b005678049" alt="Image" width="550" height="550">
</p>

**TCP Client script output**
<p align="center">
<img src="https://github.com/divitvasu/Key-Value-Store/assets/30820920/2cf7595c-b034-4e63-a7a8-27a6e178bb12" alt="Image" width="650" height="150">
</p>

- Executing UDP application using docker and Linux Shell:
  - ./udpserver.sh 33679 33997
  - ./udpclient.sh 33997 33679
  - The respective logs fall inside the docker images.

**UDP Server script output**
<p align="center">
<img src="https://github.com/divitvasu/Key-Value-Store/assets/30820920/4e1b9a2e-a003-4073-99cc-6ba06fae0a80" alt="Image" width="550" height="400">
</p>

**UDP Client script output**
<p align="center">
<img src="https://github.com/divitvasu/Key-Value-Store/assets/30820920/bdaffea9-c821-43e8-ba83-e9cb625e00d5" alt="Image" width="500" height="400">
</p>

## Final Thoughts

TCP is a connection-oriented protocol and guarantees that the message gets delivered, as it performs a 3-way handshake. On the other hand, UDP is a connection-less protocol and does not guarantee the delivery of messages. TCP works by opening a socket. On the other hand, UDP only uses the receiver's address and port and sends the packet (one-shot). The UDP protocol allows for a max data packet size of 65535 bytes = 65Kb. For sending large files and effective and reliant communication, TCP is better suited. However, for tasks that involve signalling and do not require a guarantee of delivery, UDP is the best fit, as it is much faster. Both these protocols have their own merits and demerits.

To overcome some UDP shortcomings and make the code somewhat more robust, my code uses a signalling mechanism to convey the server of the type of packet to expect from the client before the transmission actually begins. Such a mechanism could be helpful in cases where the format of the packets getting exchanged can be generalized.
