package com.distributedsystems;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class Server_UDP_app {

    private static Logger lgm;                  //static class variables for global access
    public static Handler fhl;
    public static String cIp = null;
    public static int listen = 0;
    public static int send = 0;

    static                                  //static block for formatter output and logger
    {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS.%1$tL %1$Tp %2$s%n%4$s: %5$s%n");
        lgm = Logger.getLogger(String.valueOf(Server_UDP_app.class.getClass().getName()));
    }

    private static void filing_logging() throws Exception {        //handling output of logger into a file

        fhl = new FileHandler("./UDPServerlogs.log");
        SimpleFormatter simple = new SimpleFormatter();
        fhl.setFormatter(simple);
        lgm.addHandler(fhl);

    }

    public static void main(String[] args) throws Exception {

        filing_logging();                                  //start logging

        if (args.length == 2){                            //accept command line arguments, if none provided take defaults
            listen = Integer.parseInt(args[0]);
            send = Integer.parseInt(args[1]);
        }
        else{
            listen = 9806;
            send = 9807;
        }

        System.out.println("Server active...");             //Creating server datagram socket to accept request
        HashMap<String, String> hmap = new HashMap<>();    //Initialize Hashmap for Key-value store
        DatagramSocket ds = new DatagramSocket(listen);    //Hashmap only takes in a pair and is homogenous !

        System.out.println("Waiting for client request...");

        while (true) {

            byte[] sig = accept_request(ds);                 //signalling mechanism to make server ready for an input
            String signal = converter(sig);                  //such that packets are not dropped

            ArrayList<Object> messages = new ArrayList<>();

            if (signal.equals("1")) {                          //start accepting a put-type packet

                for (int i = 0; i <= 2; i++) {
                    byte[] temp = accept_request(ds);
                    //TimeUnit.SECONDS.sleep(1);        //a pause could be used for added redundancy!!
                    String packet = converter(temp);
                    System.out.println(packet);
                    messages.add(packet);
                }

                System.out.println(messages);
            } else if (signal.equals("2") || signal.equals("3")) {    //start accepting a get or del-type packet

                for (int i = 0; i <= 1; i++) {
                    byte[] temp = accept_request(ds);
                    //TimeUnit.SECONDS.sleep(1);
                    String packet = converter(temp);
                    System.out.println(packet);
                    messages.add(packet);
                }
                System.out.println(messages);
            }

            if (messages.size() != 0){                //Check Integrity and validity of the datagram packet
                boolean flag = false;
                flag = verify_packet(messages, flag);

                if (flag == true) {                              //proceed with operations if datagram is valid
                    String operation = (String) messages.get(0);

                    if (operation.equalsIgnoreCase("put")) {   //call for put-operation
                        put_pair(messages, hmap, ds);
                    } else if (operation.equalsIgnoreCase("get")) {  //call for get-operation
                        get_pair(messages, hmap, ds);
                    } else if (operation.equalsIgnoreCase("del")) {  //call for delete-operation
                        delete_pair(messages, hmap, ds);
                    }
                }
                else{
                    String query = "Datagram packet is invalid!";
                    System.out.println(query);              //if datagram is malformed not adhering to the set-standard
                    respond_request(query, ds);
                    lgm.log(Level.SEVERE,"ERR- Datagram "+messages+" received from client " +InetAddress.getLocalHost()+
                            ":"+listen+ " is invalid!");
                }
                System.out.println(hmap);             //hashmap contents after each operation
            }
        }
    }

    public static byte[] accept_request(DatagramSocket ds) throws Exception {    //accept a datagram from the sender(client)

        byte[] buffer = new byte[10000];

        DatagramPacket stream = new DatagramPacket(buffer, buffer.length);
        ds.receive(stream);

        return buffer;
    }

    public static void respond_request(String query, DatagramSocket ds) throws Exception {   //send a datagram to the client

        InetAddress ip = InetAddress.getLocalHost();
//        int port_Num = 9807;

        byte[] buffer = query.getBytes(StandardCharsets.UTF_8);

        DatagramPacket udp_packet = new DatagramPacket(buffer, buffer.length ,ip, send);
        ds.send(udp_packet);

    }

    public static String converter(byte[] arr) throws Exception {          //convert byte array into a usable String type

        if (arr == null)
            return null;
        StringBuilder temp = new StringBuilder();
        int i = 0;
        while (arr[i] != 0) {
            temp.append((char) arr[i]);
            i++;
        }

        String result = temp.toString();
        return result;
    }

    private static boolean verify_packet(ArrayList messages, boolean flag) throws Exception {

        flag = false;                                         //verify if packet contents are correct and in-order
                                                              //integrity checker
        if (messages.size()==3 || messages.size()==2) {       //decided the flow of the main program
            String operation = (String) messages.get(0);
            String key = (String) messages.get(1);

            if ((operation.equalsIgnoreCase("put")) && (key != "") && ((String) messages.get(2) != "")) {
                if (messages.size() == 3)
                    flag = true;
            } else if ((operation.equalsIgnoreCase("get")) && (key != "")) {
                if (messages.size() == 2)
                    flag = true;
            } else if ((operation.equalsIgnoreCase("del")) && (key != "")) {
                if (messages.size() == 2)
                    flag = true;
            }
        }
        return flag;
    }

    public static void put_pair(ArrayList messages, HashMap<String, String> hmap, DatagramSocket ds) throws Exception {
                                                                //Check for key in hashmap, if it already exists send a Warning
                                                                //If the key does not exist, insert the pair in the hashmap
        if (!hmap.containsKey((String) messages.get(1))) {
            hmap.put((String) messages.get(1), (String) messages.get(2));
            String query = "ACK-Added pair!";
            respond_request(query, ds);                            //log information of received data from the client in the logs
            lgm.log(Level.INFO,"Request "+messages+" from client "+InetAddress.getLocalHost()+":"+listen+" performed on Hashmap");
        }
        else{
            String query = "ERR-Could not add Key, already exists!";
            respond_request(query, ds);                                          //else let know the client that key exists already
            lgm.log(Level.WARNING,"Key "+messages.get(1)+ " already exists");
        }

    }

    public static void get_pair(ArrayList messages, HashMap<String, String> hmap, DatagramSocket ds) throws Exception{
                                                              //Check for key in hashmap, if it already exists fetch the pair and
        String k = (String) messages.get(1);                  //send to the client, else return and log an error
        String v;

        if (hmap.containsKey(k)){
            v = hmap.get(k);
            System.out.println(k +" : "+v);
            String query = "ACK-Fetched pair! " +k+ ":" +v;
            respond_request(query, ds);                        //log information of received data from the client in the logs
            lgm.log(Level.INFO,"Request "+messages+" from client "+InetAddress.getLocalHost()+":"+listen+" performed on Hashmap");
        }
        else{
            String query = "ERR-Could not find Key to get, DNE!";
            respond_request(query, ds);                                 //else let know the client that key does not exist
            lgm.log(Level.WARNING,"Key: "+messages.get(1)+ " DNE in Hashmap!");
        }
    }

    public static void delete_pair(ArrayList messages, HashMap<String, String> hmap, DatagramSocket ds) throws Exception{

                                                            //Check for key in hashmap, if it already exists delete the pair and
                                                            //send response to the client, else return and log an error
        if (hmap.containsKey((String) messages.get(1))){
            hmap.remove((String) messages.get(1));
            String query = "ACK-Deleted pair!";
            respond_request(query, ds);                   //log information of received data from the client in the logs
            lgm.log(Level.INFO,"Request "+messages+" from client "+InetAddress.getLocalHost()+":"+listen+" performed on Hashmap");
        }
        else{
            String query = "ERR-Could not find Key to delete, DNE!";
            respond_request(query, ds);                                //else let know the client that key does not exist
            lgm.log(Level.WARNING,"Key: "+messages.get(1)+ " to be deleted DNE in Hashmap");
        }

    }
}