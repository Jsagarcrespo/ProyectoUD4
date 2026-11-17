package org.example;

import com.mongodb.client.MongoDatabase;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.collections.MapConverter;

import org.bson.Document;

import org.example.dao.AnimalDao;
import org.example.dao.AdoptanteDao;
import org.example.dao.AdopcionDao;

import org.example.modelos.Animal;
import org.example.modelos.Adoptante;
import org.example.modelos.Adopcion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportarXml {

    // EXPORTAR ANIMALES
    public static void exportarAnimales(MongoDatabase db) throws IOException {

        AnimalDao animalDao = new AnimalDao(db);

        ListaAnimales lista = new ListaAnimales();

        for (Animal animal : animalDao.listar()) {
            lista.add(animal);
        }

        XStream xstream = new XStream();

        xstream.alias("Animales", ListaAnimales.class);
        xstream.alias("Animal", Animal.class);

        xstream.addImplicitCollection(ListaAnimales.class, "lista");

        xstream.registerConverter(new MapConverter(xstream.getMapper(), Document.class));

        new File("ficheros").mkdirs();

        try (FileOutputStream fichero = new FileOutputStream("ficheros/animales.xml")) {

            xstream.toXML(lista, fichero);
        }
    }

    // EXPORTAR ADOPTANTES
    public static void exportarAdoptantes(MongoDatabase db) throws IOException {

        AdoptanteDao adoptanteDao = new AdoptanteDao(db);

        ListaAdoptantes lista = new ListaAdoptantes();

        for (Adoptante adoptante : adoptanteDao.listar()) {
            lista.add(adoptante);
        }

        XStream xstream = new XStream();

        xstream.alias("Adoptantes", ListaAdoptantes.class);
        xstream.alias("Adoptante", Adoptante.class);

        xstream.addImplicitCollection(ListaAdoptantes.class, "lista");

        new File("ficheros").mkdirs();

        try (FileOutputStream fichero = new FileOutputStream("ficheros/adoptantes.xml")) {

            xstream.toXML(lista, fichero);
        }
    }

    // EXPORTAR ADOPCIONES
    public static void exportarAdopciones(MongoDatabase db) throws IOException {

        AdopcionDao adopcionDao = new AdopcionDao(db);

        ListaAdopciones lista = new ListaAdopciones();

        for (Adopcion adopcion : adopcionDao.listar()) {
            lista.add(adopcion);
        }

        XStream xstream = new XStream();

        xstream.alias("Adopciones", ListaAdopciones.class);
        xstream.alias("Adopcion", Adopcion.class);

        xstream.addImplicitCollection(ListaAdopciones.class, "lista");

        xstream.registerConverter(new MapConverter(xstream.getMapper(), Document.class));

        new File("ficheros").mkdirs();

        try (FileOutputStream fichero = new FileOutputStream("ficheros/adopciones.xml")) {

            xstream.toXML(lista, fichero);
        }
    }
}
