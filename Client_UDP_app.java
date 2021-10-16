package com.distributedsystems;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Client_UDP_app {

    private static Logger lgm;                    //static class variables for global access
    public static Handler fhl;
    public static int listen = 0;
    public static int send = 0;

    static {                                      //static block for formatter output and logger
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS.%1$tL %1$Tp %2$s%n%4$s: %5$s%n");
        lgm = Logger.getLogger(String.valueOf(Client_UDP_app.class.getClass()));
    }

    private static void filing_logging() throws Exception {     //handling output of logger into a file

        fhl = new FileHandler("./UDPClientlogs.log");
        SimpleFormatter simple = new SimpleFormatter();
        fhl.setFormatter(simple);
        lgm.addHandler(fhl);

    }

    public static void main(String[] args) throws Exception {

        filing_logging();                                //start logging

        if (args.length == 2){
            listen = Integer.parseInt(args[0]);          //accept command line arguments, if none provided take defaults
            send = Integer.parseInt(args[1]);
        }
        else{
            listen = 9807;
            send = 9806;
        }

        DatagramSocket ds = new DatagramSocket(listen);        //Creating client datagram socket to accept requests
        ds.setSoTimeout(10000);                                //timeout if no response from the server

        System.out.println("Performing pre-defined 5 Puts, 5 Gets, 5 Deletes before handing over to the user...");

        for (int i = 1; i <= 5; i++) {                     //5 pre-defined operations as required by the question

            String k = String.valueOf(i);
            char v = (char) ((char) i + 64);
            Signal_server("1", ds);
            put_pair(k, String.valueOf(v), ds);
        }

        for (int i = 1; i <= 5; i++) {

            String k = String.valueOf(i);
            Signal_server("2", ds);
            get_pair(k, ds);
        }

        for (int i = 1; i <= 5; i++) {

            String k = String.valueOf(i);
            Signal_server("3", ds);
            delete_pair(k, ds);
        }

        while (true) {                                //user-input is accepted here-on
            TimeUnit.SECONDS.sleep(1);
            Scanner console = new Scanner(System.in);
            System.out.println("Enter your choice...");
            System.out.println("1). Send a PUT request.");
            System.out.println("2). Send a GET request.");
            System.out.println("3). Send a DELETE request.");
            System.out.println("4). End.");
            int choice = console.nextInt();

            Scanner sec = new Scanner(System.in);         //signalling used for added redundancy
            switch (choice) {
                case 1:
                    Signal_server("1", ds);                   //signal server to accept a put-type request
                    System.out.println("Enter Key to send");
                    String key_put = sec.nextLine();
                    System.out.println("Enter Value to send");
                    String val_put = sec.nextLine();
                    put_pair(key_put, val_put, ds);              //insert the pair
                    break;

                case 2:
                    Signal_server("2", ds);                    //signal server to accept a get-type request
                    System.out.println("Enter Key to fetch");
                    String key_get = sec.nextLine();
                    get_pair(key_get, ds);                       //fetch the pair
                    break;

                case 3:
                    Signal_server("3", ds);                   //signal server to accept a delete-type request
                    System.out.println("Enter Key to delete");
                    String key_del = sec.nextLine();
                    delete_pair(key_del, ds);                   //delete the pair
                    break;

                case 4:                                         //if user wants to terminate client
                    System.exit(0);

                default:
                    System.out.println("Invalid choice!!");
                    break;
            }

        }
    }

    public static void Signal_server(String i, DatagramSocket ds)throws Exception{   //signalling mechanism invoked

        Datagram_creator_sender(i, ds);

    }

    public static void Datagram_creator_sender(String request, DatagramSocket ds) throws Exception {

        InetAddress ip = InetAddress.getLocalHost();                         //create the required datagram packet and
//        int port_Num = 9806;                                               //send to server

        byte[] buffer = request.getBytes(StandardCharsets.UTF_8);

        DatagramPacket udp_packet = new DatagramPacket(buffer, buffer.length, ip, send);
        ds.send(udp_packet);

    }

    public static byte[] response_collector(DatagramSocket ds) throws Exception {

        byte[] buffer = new byte[10000];                                //Catching a response from the server

        DatagramPacket stream = new DatagramPacket(buffer, buffer.length);
        ds.receive(stream);

        return buffer;
    }

    public static String converter(byte[] arr) throws Exception {    //convert byte array into a usable String type

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

    public static void put_pair(String key, String val, DatagramSocket ds) throws Exception {

        ArrayList<String> requests;                          //function to send the key-value pair to the server
        requests = new ArrayList<String>();
        requests.add("put");
        requests.add(key);
        requests.add(val);
        System.out.println("Sending Put req" + requests);

        for (int i =0; i< requests.size();i++){
            Datagram_creator_sender(requests.get(i), ds);
        }
                                                                     //log necessary information in the logger and accept
        lgm.info("PUT-request sent to the server "+ requests);  //server's response

        byte[] temp = response_collector(ds);
        String response = converter(temp);
        System.out.println(response);

        lgm.info("Server response "+ response);

    }

    public static void get_pair(String key, DatagramSocket ds) throws Exception {

        ArrayList<String> requests;                     //function to fetch the key-value pair from the server
        requests = new ArrayList<String>();
        requests.add("get");
        requests.add(key);
        System.out.println("Sending Get req" + requests);

        for (int i =0; i< requests.size();i++){
            Datagram_creator_sender(requests.get(i), ds);
        }

        lgm.info("GET-request sent to the server " + requests); //log necessary information in the logger and accept
                                                                     //server's response
        byte[] temp = response_collector(ds);
        String response = converter(temp);
        System.out.println(response);

        lgm.info("Server response " + response);

    }

    public static void delete_pair(String key, DatagramSocket ds) throws Exception{

        ArrayList<String> requests;                             //function to delete the pair at the server
        requests = new ArrayList<String>();
        requests.add("del");
        requests.add(key);
        System.out.println("Sending Delete req" + requests);

        for (int i =0; i< requests.size();i++){
            Datagram_creator_sender(requests.get(i), ds);
        }

        lgm.info("DEL-request sent to the server "+ requests);   //log necessary information in the logger and accept
                                                                      //server's response
        byte[] temp = response_collector(ds);
        String response = converter(temp);
        System.out.println(response);

        lgm.info("Server response "+ response);

    }
}

//REF
//https://stackoverflow.com/questions/49186029/how-do-i-modify-a-log-format-with-simple-formatter
//https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html