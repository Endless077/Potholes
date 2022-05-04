package com.example.potholes.Service;

import com.example.potholes.Model.Pothole;
import com.example.potholes.Service.Interface.IComunication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class Comunication {

    static final String ADDRESS = "localhost";
    static final int PORT = 8080;

    private Comunication() {}

    //Methods GET
    public static List<Pothole> getNearPotholes(double latitudine, double longitudine, double range) {
        return null;
    }

    public static List<Pothole> getAllPotoles() {
        return null;
    }

    public static double getThreshold() {
        return 0;
    }

    //Methods POST
    public static boolean insertNewPothole(double latitudine, double longitudine, String username) {
        return false;
    }

    //Methods PUT
    public static void putPotole() { throw new UnsupportedOperationException(); }

    //Methods DELETE
    public static void deletePotole() { throw new UnsupportedOperationException(); }

    //Methods SETUP
    private Socket openConnection() throws IOException {
        InetAddress serverAddr = InetAddress.getByName(ADDRESS);
        return new Socket(serverAddr, PORT);
    }

    private boolean closeConnection(Socket socket) {
        try {
            socket.close();
            return true;
        }catch (IOException e) {
            return false;
        }
    }


//    Socket sock;
//
//    public void writeSocket(String message) throws IOException {
//        InetAddress serverAddr;
//        serverAddr = InetAddress.getByName("localhost");
//        sock = new Socket(serverAddr, 8080);
//        PrintWriter out = new PrintWriter(
//                new BufferedWriter(
//                        new OutputStreamWriter(
//                                sock.getOutputStream())), true);
//
//        out.println(message);
//        out.println("abs");
//
//        //risposta server
//
//        out.close();
//    }


}
