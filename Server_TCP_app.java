package com.distributedsystems;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.*;

public class Server_TCP_app {

    private static Logger lgm;                  //static class variables for global access
    public static Handler fhl;
    public static String cIp = null;
    public static int cPort = 0;
    public static int listen = 0;

    static                                  //static block for formatter output and logger
    {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS.%1$tL %1$Tp %2$s%n%4$s: %5$s%n");
        lgm = Logger.getLogger(String.valueOf(Server_TCP_app.class.getClass().getName()));
    }

    private static void filing_logging() throws Exception{        //handling output of logger into a file

        fhl = new FileHandler("./TCPServerlogs.log");
        SimpleFormatter simple = new SimpleFormatter();
        fhl.setFormatter(simple);
        lgm.addHandler(fhl);

    }

    public static void main(String[] args) throws Exception {

        filing_logging();                                  //start logging

        if (args.length == 1){                            //accept command line arguments, if none provided take defaults
            listen = Integer.parseInt(args[0]);
        }
        else{
            listen = 10110;
        }

        ServerSocket ss = new ServerSocket(listen);      //Accepting a connection from the client
        System.out.println("Server active...");
        System.out.println("Waiting for client to connect...");

        HashMap<String, String> hmap = new HashMap<>();    //Initialize Hashmap for Key-value store
                                                           //Hashmap only takes in a pair and is homogenous!
        Socket client1 = ss.accept();

        lgm.log(Level.INFO, "CONNECTION ESTABLISHED WITH A CLIENT");

        OutputStream os = client1.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);

        InputStream is = client1.getInputStream();               //Initialize I/O streams
        ObjectInputStream ois = new ObjectInputStream(is);

        InetSocketAddress sockaddr = (InetSocketAddress) client1.getRemoteSocketAddress();  //Get Client InetAddress and Port
        cIp = sockaddr.getAddress().getHostAddress();                                      //log the same in the log file
        cPort = sockaddr.getPort();

        while(true){
            ArrayList<Object> messages = new ArrayList<>();     //accept request from client
            messages = (ArrayList<Object>) ois.readObject();
            System.out.println((messages));

            boolean flag = false;
            flag = verify_packet(messages, flag);    //Check Integrity and validity of the datagram packet

            ArrayList<Object> response = new ArrayList<>();     //stream for responding back if datagram is invalid

            if (flag == true) {                              //proceed with operations if datagram is valid
                String operation = (String) messages.get(0);

                if (operation.equalsIgnoreCase("put")) {   //call for put-operation
                    put_pair(messages, hmap, oos);
                } else if (operation.equalsIgnoreCase("get")) {  //call for get-operation
                    get_pair(messages, hmap, oos);
                } else if (operation.equalsIgnoreCase("del")) {  //call for delete-operation
                    delete_pair(messages, hmap, oos);
                }
            }
            else{
                //log into logger and send the status back to the client
                lgm.log(Level.SEVERE, "ERR- Datagram " + messages + " received from client " + cIp + ":" + cPort + " is invalid!");
                response.add("ERR- Datagram " + messages + " is invalid!");
                System.out.println("ERR- Datagram " + messages + " is invalid!");
                oos.writeObject(response);
            }
            System.out.println(hmap);             //hashmap contents after each operation
        }
    }

    private static boolean verify_packet(ArrayList messages, boolean flag) throws Exception{

        flag = false;                                     //verify if packet contents are correct and in-order

        String operation = (String) messages.get(0);
        String key = (String) messages.get(1);

        if ((operation.equalsIgnoreCase("put")) && (key != "" ) && ((String) messages.get(2) != "")) {
            if (messages.size() == 3)
                flag = true;
        }
        else if ((operation.equalsIgnoreCase("get")) && (key != "")) {
            if (messages.size() == 2)
                flag = true;
        }
        else if ((operation.equalsIgnoreCase("del")) && (key != "")) {
            if (messages.size() == 2)
                flag = true;
        }

        return flag;
    }

    public static void put_pair(ArrayList messages, HashMap<String, String> hmap, ObjectOutputStream oos) throws Exception {

        ArrayList<Object> response = new ArrayList<>();      //Check for key in hashmap, if it already exists send a Warning
                                                             //If the key does not exist, insert the pair in the hashmap
        if (!hmap.containsKey((String) messages.get(1))) {
            hmap.put((String) messages.get(1), (String) messages.get(2));
            response.add("ACK-Added pair!");
            lgm.log(Level.INFO,"Request "+messages+" from client "+cIp+":"+cPort+" performed on Hashmap");
        }
        else{
            response.add("ERR-Could not add Key, already exists!");
            lgm.log(Level.WARNING,"Key "+messages.get(1)+ " already exists");
        }
        oos.writeObject(response);
    }

    public static void get_pair(ArrayList messages, HashMap<String, String> hmap, ObjectOutputStream oos) throws Exception{

        ArrayList<Object> response = new ArrayList<>();       //Check for key in hashmap, if it already exists fetch the pair and
        String k = (String) messages.get(1);                  //send to the client, else return and log an error
        String v;

        if (hmap.containsKey(k)){
            v = hmap.get(k);
            response.add("ACK-Fetched pair! " + k + ":" + v);
            lgm.log(Level.INFO,"Request "+messages+" from client "+cIp+":"+cPort+" performed on Hashmap");
        }
        else{
            response.add("ERR-Could not find Key to get, DNE!");
            lgm.log(Level.WARNING,"Key: "+messages.get(1)+ " DNE in Hashmap!");
        }
        oos.writeObject(response);
    }

    public static void delete_pair(ArrayList messages, HashMap<String, String> hmap, ObjectOutputStream oos) throws Exception{

        ArrayList<Object> response = new ArrayList<>();     //Check for key in hashmap, if it already exists delete the pair and
                                                            //send response to the client, else return and log an error
        if (hmap.containsKey((String) messages.get(1))){
            hmap.remove((String) messages.get(1));
            response.add("ACK-Deleted pair!");
            lgm.log(Level.INFO,"Request "+messages+" from client "+cIp+":"+cPort+" performed on Hashmap");
        }
        else{
            response.add("ERR-Could not find Key to delete, DNE!");
            lgm.log(Level.WARNING,"Key: "+messages.get(1)+ " to be deleted DNE in Hashmap");
        }
        oos.writeObject(response);
    }

    //        ss.close();                //No need to close connections, server always listens, when client exits
//        client1.close();
}


