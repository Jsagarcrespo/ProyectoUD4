package org.example;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class GestionIndices {

    public static void crearIndices(MongoDatabase db) {

        MongoCollection<Document> animales = db.getCollection("animales");

        animales.createIndex(new Document("especie", 1));
        animales.createIndex(new Document("raza", 1));
        animales.createIndex(new Document("edad", 1));
        animales.createIndex(new Document("estado", 1));


        MongoCollection<Document> adoptantes = db.getCollection("adoptantes");

        adoptantes.createIndex(new Document("preferencias", 1));


        MongoCollection<Document> adopciones = db.getCollection("adopciones");

        adopciones.createIndex(new Document("idAnimal", 1));
        adopciones.createIndex(new Document("dniAdoptante", 1));
        adopciones.createIndex(new Document("fechaSolicitud", 1));
        adopciones.createIndex(new Document("estado", 1));
    }
}