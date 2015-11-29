package coen445.server;
/**
 * Created by Ahmed on 15-10-25.
 */

import java.io.*;
import java.net.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server{

    public static final int BUFFER_SIZE = 1024;
    private static DatagramSocket serverSocket;
    public static ConcurrentHashMap<InetAddress,ParticipantData> IpToData;


    public Server() {

    }

    private void setup()  {

        System.out.println("Please Configure Server");
        System.out.println("Enter the server port number");

        int serverPort;
        serverPort = getServerPortFromUser();

        InetAddress serverIPAddress;
        serverIPAddress = getServerInetAddress();

        // should make this thread safe, not more than one object should use this at one time
        createServerSocket(serverPort, serverIPAddress);
        IpToData = new ConcurrentHashMap<InetAddress,ParticipantData>();

    }

    private int getServerPortFromUser() {
        String userInput;
        int serverPort = 0;
        while (serverPort == 0 || serverPort < 1024) {

            System.out.println("Please enter a number greater than 1024");

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                userInput = bufferedReader.readLine();
                serverPort = Integer.parseInt(userInput);
            } catch (NumberFormatException ex) {
                System.out.println("This is not a number");
            } catch (IOException e) {


                e.printStackTrace();
            }


        }
        return serverPort;
    }

    private InetAddress getServerInetAddress() {
        InetAddress serverIPAddress = null;

        try {
            serverIPAddress = InetAddress.getLocalHost();
            System.out.println("InetAddress = " + serverIPAddress);
        } catch (UnknownHostException e) {
            System.out.println("Server IP address is not known");
            e.printStackTrace();
        }
        return serverIPAddress;
    }

    private void createServerSocket(int serverPort, InetAddress serverIPAddress) {
        try {
            serverSocket = new DatagramSocket(serverPort,serverIPAddress);
            System.out.println("Server setup was successful");

        } catch (SocketException e) {
            System.out.println("Could not create server socket");
            e.printStackTrace();
        }
    }


    private void displayServerInfo() {

        System.out.println("Server Port is set to: " + serverSocket.getLocalPort());
        System.out.println("Server Ip is set to: " + serverSocket.getLocalAddress());
        System.out.println("");
    }



    public void listen(){

        byte[] receiveData = new byte[BUFFER_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);


        while(true){

            try {

                serverSocket.receive(receivePacket);

            } catch (IOException e) {
                e.printStackTrace();
            }


            InetAddress IPAddress = receivePacket.getAddress();
            System.out.println("RECEIVED Address: " + IPAddress);
            int port = receivePacket.getPort();
            System.out.println("RECEIVED Port: " + port);


            UDPMessage message;
            message = getUdpMessage(receiveData);

            ResponseThread myResponseThread = new ResponseThread(message,IPAddress,port, serverSocket);

            Thread t = new Thread(myResponseThread);
            t.start();

        }
    }

    private UDPMessage getUdpMessage(byte[] receiveData) {
        UDPMessage message = null;
        ByteArrayInputStream in = new ByteArrayInputStream(receiveData);
        try {

            ObjectInputStream is = new ObjectInputStream(in);
            message = (UDPMessage) is.readObject();

            System.out.println("RECEIVED Type: "+ message);
            System.out.println(" ");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return message;
    }

    public static byte[] getBytes(UDPMessage message) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(outputStream);
        os.writeObject(message);
        System.out.println("From Server, creating message object: " + message.toString());
        return outputStream.toByteArray();
    }

    public static void main(String[] args) throws Exception {

        Server myServer = new Server();

        myServer.setup();
        myServer.displayServerInfo();
        myServer.listen();

    }

}
