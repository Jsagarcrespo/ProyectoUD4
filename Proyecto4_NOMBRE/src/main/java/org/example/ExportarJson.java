package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoDatabase;

import org.example.dao.AnimalDao;
import org.example.dao.AdoptanteDao;
import org.example.dao.AdopcionDao;

import org.example.modelos.Animal;
import org.example.modelos.Adoptante;
import org.example.modelos.Adopcion;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExportarJson {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    // EXPORTAR ANIMALES
    public static void exportarAnimales(MongoDatabase db) throws IOException {

        AnimalDao animalDao = new AnimalDao(db);

        List<Animal> lista = animalDao.listar();

        Type tipo = new TypeToken<List<Animal>>() {
        }.getType();

        new File("ficheros").mkdirs();

        try (FileWriter writer = new FileWriter("ficheros/animales.json", StandardCharsets.UTF_8)) {
            gson.toJson(lista, tipo, writer);
        }
    }

    // EXPORTAR ADOPTANTES
    public static void exportarAdoptantes(MongoDatabase db) throws IOException {

        AdoptanteDao adoptanteDao = new AdoptanteDao(db);

        List<Adoptante> lista = adoptanteDao.listar();

        Type tipo = new TypeToken<List<Adoptante>>() {
        }.getType();

        new File("ficheros").mkdirs();

        try (FileWriter writer = new FileWriter("ficheros/adoptantes.json", StandardCharsets.UTF_8)) {
            gson.toJson(lista, tipo, writer);
        }
    }

    // EXPORTAR ADOPCIONES
    public static void exportarAdopciones(MongoDatabase db) throws IOException {

        AdopcionDao adopcionDao = new AdopcionDao(db);

        List<Adopcion> lista = adopcionDao.listar();

        Type tipo = new TypeToken<List<Adopcion>>() {
        }.getType();

        new File("ficheros").mkdirs();

        try (FileWriter writer = new FileWriter("ficheros/adopciones.json", StandardCharsets.UTF_8)) {
            gson.toJson(lista, tipo, writer);
        }
    }
}
