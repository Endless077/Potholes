package com.example.potholes.Service.Interface;

import com.example.potholes.Model.Pothole;

import java.net.Socket;
import java.util.List;

public interface IComunication {

    //GET
    List<Pothole> getNearPotholes(double latitudine, double longitudine, double range);
    List<Pothole> getAllPotoles();
    double getThreshold();

    //POST
    boolean insertNewPothole(double latitudine, double longitudine, String username);

    //PUT
    default void putPotole() { throw new UnsupportedOperationException(); }

    //DELETE
    default void deletePotole() { throw new UnsupportedOperationException(); }

    //SETUP
    Socket openConnection();
    boolean closeConnection();

}
