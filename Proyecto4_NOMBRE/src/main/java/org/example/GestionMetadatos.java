package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class GestionMetadatos {

    // OBTENER METADATOS
    public static String obtenerMetadatos(MongoDatabase db) {

        StringBuilder texto = new StringBuilder();

        texto.append("===== INFORMACION DE LA BASE DE DATOS =====\n\n");

        texto.append("Base de datos: ").append(db.getName()).append("\n");
        texto.append("Gestor: MongoDB\n");

        texto.append("\n===== COLECCIONES =====\n");

        // Recorremos las colecciones de la base de datos.
        for (String nombre : db.listCollectionNames()) {

            MongoCollection<Document> coleccion = db.getCollection(nombre);

            texto.append("\n---------------------------------\n");

            texto.append("Coleccion: ").append(nombre).append("\n");

            texto.append("Numero de documentos: ").append(coleccion.countDocuments()).append("\n");

            texto.append("\nINDICES:\n");

            // Recorremos los indices de cada coleccion.
            for (Document indice : coleccion.listIndexes()) {

                texto.append("\nNombre: ").append(indice.getString("name")).append("\n");

                Document campos = indice.get("key", Document.class);

                // Recorremos los campos del indice.
                for (String campo : campos.keySet()) {

                    texto.append("Campo: ").append(campo).append("\n");

                    String tipo = campos.get(campo).toString();

                    if (tipo.equals("1")) {

                        texto.append("Tipo: Ascendente\n");

                    } else if (tipo.equals("-1")) {

                        texto.append("Tipo: Descendente\n");

                    } else {

                        texto.append("Tipo: ").append(tipo).append("\n");
                    }
                }

                if (campos.size() > 1) {

                    texto.append("Indice compuesto\n");
                }
            }
        }

        return texto.toString();
    }

    // EXPORTAR METADATOS
    public static void exportar(String texto) throws IOException {

        Files.createDirectories(Path.of("ficheros"));

        Files.writeString(Path.of("ficheros", "Metadatos.txt"), texto, StandardCharsets.UTF_8);
    }
}