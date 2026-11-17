package org.example;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class DatabaseConnection implements AutoCloseable {

    private MongoClient mongo;
    private MongoDatabase db;

    // CONEXION A LA BASE DE DATOS PRINCIPAL
    public DatabaseConnection() {

        this("refugio");
    }

    // CONEXION A UNA BASE DE DATOS CONCRETA
    public DatabaseConnection(String nombreBD) {

        mongo = new MongoClient("localhost", 27017);

        db = mongo.getDatabase(nombreBD);
    }

    // DEVOLVER BASE DE DATOS
    public MongoDatabase getDatabase() {

        return db;
    }

    // CERRAR CONEXION
    @Override
    public void close() {

        mongo.close();
    }
}