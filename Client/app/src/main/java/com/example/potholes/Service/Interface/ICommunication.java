package com.example.potholes.Service.Interface;

import com.example.potholes.Model.Pothole;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public interface ICommunication {

    //GET
    List<Pothole> getNearPotholes(double latitudine, double longitudine, double range) throws IOException;
    List<Pothole> getAllPotoles() throws IOException;
    double getThreshold() throws IOException;

    //POST
    void insertNewPothole(double latitudine, double longitudine) throws IOException;

    //PUT
    default void putPotole() { throw new UnsupportedOperationException(); }

    //DELETE
    default void deletePotole() { throw new UnsupportedOperationException(); }

    //SETUP
    Socket openConnection() throws IOException;
    void closeConnection(Socket socket) throws IOException;

}
