package org.example;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.dao.*;
import org.example.modelos.*;

import java.util.Date;

public class App {

    public static void main(String[] args) {

        try (DatabaseConnection conexion = new DatabaseConnection("refugio_pruebas")) {

            MongoDatabase db = conexion.getDatabase();

            AnimalDao animalDao = new AnimalDao(db);
            AdoptanteDao adoptanteDao = new AdoptanteDao(db);
            AdopcionDao adopcionDao = new AdopcionDao(db);
            RefugioDao refugioDao = new RefugioDao(db);

            Date fecha = new Date();

            // PRUEBA DEL REFUGIO
            Refugio refugio = new Refugio("Refugio Animal", "Asociacion Animal", "G00000000", "Calle A", "600000000", "refugio@example.com", fecha);

            if (refugioDao.find() == null) {
                refugioDao.create(refugio);
            }

            refugio.setDireccion("Calle B");
            refugioDao.update(refugio);

            System.out.println(refugioDao.find());

            // CREAR ANIMAL
            Animal animal = new Animal(9001, "Luna", "Perro", "Labrador", 3, "Hembra", "Animal tranquilo", fecha, "disponible");

            animalDao.create(animal);

            // BUSCAR ANIMAL
            System.out.println(animalDao.find(9001));

            // MODIFICAR ANIMAL
            animal.setEdad(4);
            animalDao.update(animal);

            // AÑADIR HISTORIAL MEDICO
            Document registroMedico = new Document("fecha", fecha).append("descripcion", "Revision veterinaria");

            animalDao.anadirRegistroMedico(9001, registroMedico);

            System.out.println(animalDao.consultarHistorialMedico(9001));

            // CREAR ADOPTANTE
            Adoptante adoptante = new Adoptante("12345678Z", "Adoptante 1", "Apellido 1", "600000001", "Calle A", "adoptante@example.com", fecha);

            adoptanteDao.create(adoptante);

            // MODIFICAR ADOPTANTE
            adoptante.setTelefono("600000002");
            adoptanteDao.update(adoptante);

            // AÑADIR PREFERENCIA
            adoptanteDao.anadirPreferencia("12345678Z", "Perro");

            System.out.println(adoptanteDao.find("12345678Z"));

            // CREAR ADOPCION PENDIENTE
            Adopcion adopcion = new Adopcion(9001, 9001, "12345678Z", fecha, null, "pendiente", "Solicitud recibida");

            adopcionDao.create(adopcion);

            System.out.println(adopcionDao.find(9001));

            // APROBAR ADOPCION
            adopcion.setEstado("aprobada");
            adopcion.setFechaResolucion(fecha);

            adopcionDao.update(adopcion);

            // COMPROBAR EL ESTADO DEL ANIMAL
            System.out.println("Estado del animal: " + animalDao.find(9001).getEstado());

            // AÑADIR SEGUIMIENTO
            Document seguimiento = new Document("fecha", fecha).append("observaciones", "Adaptacion correcta");

            adopcionDao.anadirSeguimiento(9001, seguimiento);

            System.out.println(adopcionDao.consultarSeguimiento(9001));

            // COMPROBAR INTEGRIDAD DE LAS REFERENCIAS
            try {

                animalDao.delete(9001);

                throw new IllegalStateException("No se deberia poder eliminar el animal");

            } catch (IllegalArgumentException e) {

                System.out.println(e.getMessage());
            }

            // ELIMINAR LOS DATOS DE PRUEBA
            adopcionDao.delete(9001);

            System.out.println("Estado del animal: " + animalDao.find(9001).getEstado());

            animalDao.delete(9001);
            adoptanteDao.delete("12345678Z");

            System.out.println("Pruebas finalizadas correctamente");

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());
        }
    }
}