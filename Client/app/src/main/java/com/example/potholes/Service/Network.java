package com.example.potholes.Service;

import android.app.Activity;
import android.util.Log;

import com.example.potholes.Model.Pothole;
import com.example.potholes.Service.Interface.ICommunication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Network implements ICommunication {

    private final String LOG = "Network";

    public static String NICKNAME = "";
    public static double THRESHOLD = 1;

    static final String ADDRESS = "192.168.110.89";
    static final int PORT = 5678;

    private Activity guiRef;

    public Network() {}

    public Network(Activity a) {
        this.guiRef = a;
    }

    //Methods GET
    @Override
    public List<Pothole> getNearPotholes(double latitudine, double longitudine, double range) {
        Log.i(LOG, "Get Near Potholes by "
                + NICKNAME + " at lat "
                + latitudine + " and long "
                + longitudine + " with range "
                + range);

        String operation = "getNear";
        ArrayList<Pothole> potholes = new ArrayList<>();

        try {
            //Connessione aperta
            Log.i(LOG,"Open connection...");
            Socket socket = openConnection();

            //Richiede operazione getNear
            Log.i(LOG,"Request output stream...");
            socket.getOutputStream().write(operation.getBytes());

            //Apro un buffer reader
            Log.i(LOG,"Request input stream...");
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));

            //Invio informazioni al server
            Log.i(LOG,"Sending information...");
            String tracking = NICKNAME +":"+latitudine+":"+longitudine;
            socket.getOutputStream().write(tracking.getBytes());

            //Attesa informazioni
            Log.i(LOG,"Waiting information...");
            Thread.sleep(1000);

            //Recupero informazioni
            Log.i(LOG,"Reading information...");
            String responseBuffer;
            while(reader.ready()){
                responseBuffer = reader.readLine();
                responseBuffer = responseBuffer.replace("\u0000", "");
                if(responseBuffer.isEmpty()){
                    break;
                }
                String[] fields = responseBuffer.split(":");
                String nickname = fields[0];
                double latitude = Double.parseDouble(fields[1]);
                double longitude = Double.parseDouble(fields[2]);

                potholes.add(new Pothole(nickname, latitude, longitude));
            }

            //Connessione chiusa
            Log.i(LOG,"Open connection...");
            closeConnection(socket);

        }catch (Exception e) {
            Log.e(LOG,"Errore in getNearPotholes");
            Handler.handleException(e,guiRef);
        }
        return potholes;
    }

    @Override
    public List<Pothole> getAllPotoles() {
        Log.i(LOG, "Get All Potholes by " + NICKNAME);

        String operation = "getAll";
        ArrayList<Pothole> potholes = new ArrayList<>();

        try {
            //Connessione aperta
            Log.i(LOG,"Opening connection...");
            Socket socket = openConnection();

            //Richiede operazione getAll
            Log.i(LOG,"Request output stream...");
            socket.getOutputStream().write(operation.getBytes());

            //Apro un buffer reader
            Log.i(LOG,"Request input stream...");
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));

            //Attesa informazioni
            Log.i(LOG,"Waiting information...");
            Thread.sleep(1000);

            //Recupero informazioni
            Log.i(LOG,"Reading information...");
            String responseBuffer;
            while(reader.ready()){
                responseBuffer = reader.readLine();
                responseBuffer = responseBuffer.replace("\u0000", "");
                if(responseBuffer.isEmpty()){
                    break;
                }
                String[] fields = responseBuffer.split(":");
                String nickname = fields[0];
                double latitude = Double.parseDouble(fields[1]);
                double longitude = Double.parseDouble(fields[2]);

                potholes.add(new Pothole(nickname, latitude, longitude));
            }

            //Connessione chiusa
            Log.i(LOG,"Open connection...");
            closeConnection(socket);

        }catch (Exception e) {
            Log.e(LOG,"Errore in getAllPotholes");
            Handler.handleException(e,guiRef);
        }
        return potholes;
    }

    @Override
    public double getThreshold() {
        Log.i(LOG, "Get Thrashold by " + NICKNAME);

        String operation = "threshold";
        String result = "1";

        try {

            //Connessione aperta
            Log.i(LOG,"Open connection...");
            Socket socket = openConnection();

            //Apro un buffer reader
            Log.i(LOG,"Request input stream...");
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));

            //Richiede operazione thrashold
            Log.i(LOG,"Request output stream...");
            socket.getOutputStream().write(operation.getBytes());

            //Attesa informazioni
            Log.i(LOG,"Waiting information...");
            Thread.sleep(1000);

            //Recupero informazioni
            Log.i(LOG,"Reading information...");
            if(reader.ready()){
                result = reader.readLine();
                result = result.replace("\u0000", "");
                result = result.replace(":", "");
            }

            //Connessione chiusa
            Log.i(LOG,"Closing connection...");
            closeConnection(socket);

            THRESHOLD = Double.parseDouble(result);

        }catch (Exception e) {
            Log.e(LOG,"Errore in getThrashold");
            Handler.handleException(e,guiRef);
        }

        return THRESHOLD;
    }

    //Methods POST
    @Override
    public void insertNewPothole(double latitudine, double longitudine) {
        Log.i(LOG, "Post new Pothole by "
                + NICKNAME + " at lat "
                + latitudine + " and long "
                + longitudine);

        String operation = "post";

        try {

            //Connessione aperta
            Log.i(LOG,"Opening connection...");
            Socket socket = openConnection();

            //Richiede operazione thrashold
            Log.i(LOG,"Request output stream...");
            socket.getOutputStream().write(operation.getBytes());

            //Invio nuovi dati
            Log.i(LOG,"Sending data...");
            String newData = NICKNAME +":"+latitudine+":"+longitudine;
            socket.getOutputStream().write(newData.getBytes());

            //Connessione chiusa
            Log.i(LOG,"Closing connection...");
            closeConnection(socket);

        }catch(Exception e) {
            Log.e(LOG,"Errore in insertNewPothole");
            Handler.handleException(e,guiRef);
        }
    }

    //Methods SETUP
    @Override
    public Socket openConnection() throws IOException {
        //InetAddress serverAddr = InetAddress.getByName(ADDRESS);
        return new Socket(ADDRESS, PORT);
    }

    @Override
    public void closeConnection(Socket socket) throws IOException {
        socket.close();
    }
}
