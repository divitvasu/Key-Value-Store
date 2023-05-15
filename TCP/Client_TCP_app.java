import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class Client_TCP_app {

    private static Logger lgm;           //static class variables for global access
    public static Handler fhl;
    public static int port_Num = 0;
    public static String server_IP = null;

    static                                //static block for formatter output and logger
    {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS.%1$tL %1$Tp %2$s%n%4$s: %5$s%n");
        lgm = Logger.getLogger(String.valueOf(Client_TCP_app.class.getClass()));
    }

    private static void filing_logging() throws Exception{      //handling output of logger into a file

        fhl = new FileHandler("./TCPClientlogs.log");
        SimpleFormatter simple = new SimpleFormatter();
        fhl.setFormatter(simple);
        lgm.addHandler(fhl);

    }

    public static void main(String args[]) throws Exception {

        filing_logging();                         //start logging

        if (args.length == 2){
            server_IP = args[0];
            port_Num = Integer.parseInt(args[1]);     //accept command line arguments, if none provided take defaults
        }
        else{
            server_IP = "127.0.0.1";
            port_Num = 10110;
        }


        Socket sc = new Socket(server_IP, port_Num);       //creating socket to connect with server
        sc.setSoTimeout(10000);                            //dealing with an unresponsive server

        lgm.log(Level.INFO,"CONNECTION ESTABLISHED WITH SERVER AND I/O OBJECTS INITIALISED SUCCESSFULLY");

        OutputStream os = sc.getOutputStream();                 //Initialize I/O streams
        ObjectOutputStream oos = new ObjectOutputStream(os);

        InputStream is = sc.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);

        System.out.println("Performing pre-defined 5 Puts, 5 Gets, 5 Deletes before handing over to the user...");

        for (int i = 1; i <= 5; i++) {                         //5 pre-defined operations as required by the question

            String k = String.valueOf(i);
            char v = (char) ((char) i + 64);
            put_pair(k, String.valueOf(v), oos, ois);
        }

        for (int i = 1; i <= 5; i++) {

            String k = String.valueOf(i);
            get_pair(k, oos, ois);
        }

        for (int i = 1; i <= 5; i++) {

            String k = String.valueOf(i);
            delete_pair(k, oos, ois);
        }

        while(true){                                //user-input is accepted here-on

            TimeUnit.SECONDS.sleep(1);
            Scanner console = new Scanner(System.in);
            System.out.println("Enter your choice...");
            System.out.println("1). Send a PUT request.");
            System.out.println("2). Send a GET request.");
            System.out.println("3). Send a DELETE request.");
            System.out.println("4). End.");
            int choice = console.nextInt();

            Scanner sec = new Scanner(System.in);
            switch (choice) {
                case 1:

                    System.out.println("Enter Key to send");
                    String key_put = sec.nextLine();
                    System.out.println("Enter Value to send");
                    String val_put = sec.nextLine();
                    put_pair(key_put, val_put, oos, ois);     //insert the pair
                    break;

                case 2:
                    System.out.println("Enter Key to fetch");
                    String key_get = sec.nextLine();
                    get_pair(key_get, oos, ois);             //fetch the pair
                    break;

                case 3:
                    System.out.println("Enter Key to delete");
                    String key_del = sec.nextLine();
                    delete_pair(key_del, oos, ois);           //delete the pair
                    break;

                case 4:
                    System.exit(0);                   //if user wants to terminate client

                default:
                    System.out.println("Invalid choice!!");
                    break;
            }

        }
        //       System.out.println("Closing socket and terminating program.");
//        sc.close();

    }

    public static void put_pair(String key, String val, ObjectOutputStream oos, ObjectInputStream ois) throws Exception{

        ArrayList<String> requests;                    //function to send the key-value pair to the server
        requests = new ArrayList<String>();
        requests.add("put");
        requests.add(key);
        requests.add(val);
        System.out.println("Sending Put req" + requests);
        oos.writeObject(requests);

        lgm.info("PUT-request sent to the server "+ requests);     //log necessary information in the logger and accept
                                                                        //server's response
        ArrayList<Object> response = new ArrayList<>();

        response = (ArrayList<Object>) ois.readObject();
        System.out.println(response);

        lgm.info("Server response "+ response);

    }

    public static void get_pair(String key, ObjectOutputStream oos, ObjectInputStream ois) throws Exception{

        ArrayList<String> requests;                                       //function to fetch the key-value pair from the server
        requests = new ArrayList<String>();
        requests.add("get");
        requests.add(key);
        System.out.println("Sending Get req" + requests);
        oos.writeObject(requests);

        lgm.info("GET-request sent to the server "+ requests);      //log necessary information in the logger and accept
                                                                         //server's response
        ArrayList<Object> response = new ArrayList<>();
        response = (ArrayList<Object>) ois.readObject();
        System.out.println(response);

        lgm.info("Server response "+ response);

    }

    public static void delete_pair(String key, ObjectOutputStream oos, ObjectInputStream ois) throws Exception{

        ArrayList<String> requests;                                 //function to delete the pair at the server
        requests = new ArrayList<String>();
        requests.add("del");
        requests.add(key);
        System.out.println("Sending Delete req" + requests);
        oos.writeObject(requests);

        lgm.info("DEL-request sent to the server "+ requests);       //log necessary information in the logger and accept
                                                                          //server's response
        ArrayList<Object> response = new ArrayList<>();
        response = (ArrayList<Object>) ois.readObject();
        System.out.println(response);

        lgm.info("Server response "+ response);

    }
}


//REF
//https://stackoverflow.com/questions/49186029/how-do-i-modify-a-log-format-with-simple-formatter
//https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html