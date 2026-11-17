package org.example.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public abstract class dao<T> {

    protected MongoDatabase db;
    protected MongoCollection<Document> coleccion;

    // CONSTRUCTOR
    public dao(MongoDatabase db, String nombreColeccion) {

        this.db = db;

        // Seleccionamos la coleccion correspondiente.
        coleccion = db.getCollection(nombreColeccion);
    }

    // Convierte un documento MongoDB en un objeto Java.
    protected abstract T convertir(Document documento);

    // INSERTAR DOCUMENTO
    protected void insertar(Document documento) {

        // Comprobamos si el identificador ya existe.
        if (coleccion.find(new Document("_id", documento.get("_id"))).first() != null) {

            throw new IllegalArgumentException("El identificador ya existe");
        }

        coleccion.insertOne(documento);
    }

    // BUSCAR POR IDENTIFICADOR
    protected T buscarUno(Object id) {

        Document documento = coleccion.find(new Document("_id", id)).first();

        if (documento == null) {
            return null;
        }

        return convertir(documento);
    }

    // ACTUALIZAR DOCUMENTO
    protected void actualizar(Object id, Document cambios) {

        Document filtro = new Document("_id", id);

        if (coleccion.updateOne(filtro, cambios).getMatchedCount() == 0) {

            throw new IllegalArgumentException("El documento no existe");
        }
    }

    // ELIMINAR DOCUMENTO
    protected void eliminar(Object id) {

        Document filtro = new Document("_id", id);

        if (coleccion.deleteOne(filtro).getDeletedCount() == 0) {

            throw new IllegalArgumentException("El documento no existe");
        }
    }

    // BUSCAR VARIOS DOCUMENTOS
    protected List<T> buscar(Document filtro) {

        List<T> lista = new ArrayList<>();

        for (Document documento : coleccion.find(filtro)) {

            lista.add(convertir(documento));
        }

        return lista;
    }
}