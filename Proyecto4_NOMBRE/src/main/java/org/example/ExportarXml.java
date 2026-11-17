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
import java.util.List;

public class ExportarXml {

    // LISTA DE ANIMALES
    private static class ListaAnimales {

        private List<Animal> lista;

        public ListaAnimales(List<Animal> lista) {
            this.lista = lista;
        }
    }

    // LISTA DE ADOPTANTES
    private static class ListaAdoptantes {

        private List<Adoptante> lista;

        public ListaAdoptantes(List<Adoptante> lista) {
            this.lista = lista;
        }
    }

    // LISTA DE ADOPCIONES
    private static class ListaAdopciones {

        private List<Adopcion> lista;

        public ListaAdopciones(List<Adopcion> lista) {
            this.lista = lista;
        }
    }

    // EXPORTAR ANIMALES
    public static void exportarAnimales(MongoDatabase db) throws IOException {

        AnimalDao animalDao = new AnimalDao(db);

        ListaAnimales lista = new ListaAnimales(animalDao.listar());

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

        ListaAdoptantes lista = new ListaAdoptantes(adoptanteDao.listar());

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

        ListaAdopciones lista = new ListaAdopciones(adopcionDao.listar());

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