package org.example;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.dao.*;
import org.example.modelos.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DaoJUnitTest {

    private DatabaseConnection conexion;
    private MongoDatabase db;

    // ANTES DE CADA TEST
    @BeforeEach
    void conectar() {

        // Creamos una base de datos independiente para cada prueba.
        String nombreBD = "refugio_junit_" + UUID.randomUUID().toString().replace("-", "");

        conexion = new DatabaseConnection(nombreBD);
        db = conexion.getDatabase();
    }

    // DESPUES DE CADA TEST
    @AfterEach
    void cerrar() {

        // Eliminamos solamente la base de datos temporal del test.
        try {

            if (db != null && db.getName().startsWith("refugio_junit_")) {
                db.drop();
            }

        } finally {

            if (conexion != null) {
                conexion.close();
            }
        }
    }

    // PRUEBA CRUD ANIMAL
    @Test
    void probarAnimal() {

        AnimalDao dao = new AnimalDao(db);

        Animal animal = new Animal(1, "Luna", "Perro", "Labrador", 3, "Hembra", "Animal tranquilo", new Date(), "disponible");

        // CREAR
        dao.create(animal);

        // BUSCAR
        assertNotNull(dao.find(1));
        assertEquals("Luna", dao.find(1).getNombre());

        // MODIFICAR
        animal.setEdad(4);
        dao.update(animal);

        assertEquals(4, dao.find(1).getEdad());

        // BUSQUEDAS
        assertEquals(1, dao.buscarPorNombre("Lun").size());
        assertEquals(1, dao.buscarPorEspecie("Perro").size());
        assertEquals(1, dao.buscarPorRaza("Labrador").size());
        assertEquals(1, dao.buscarPorEdad(4).size());
        assertEquals(1, dao.buscarPorEstado("disponible").size());

        // HISTORIAL MEDICO
        Document registro = new Document("fecha", new Date()).append("descripcion", "Revision veterinaria");

        dao.anadirRegistroMedico(1, registro);

        assertEquals(1, dao.consultarHistorialMedico(1).size());

        // ELIMINAR
        dao.delete(1);

        assertNull(dao.find(1));
    }

    // PRUEBA CRUD ADOPTANTE
    @Test
    void probarAdoptante() {

        AdoptanteDao dao = new AdoptanteDao(db);

        Adoptante adoptante = new Adoptante("12345678Z", "Adoptante 1", "Apellido 1", "600000001", "Calle A", "adoptante@example.com", new Date());

        // CREAR
        dao.create(adoptante);

        // BUSCAR
        assertNotNull(dao.find("12345678Z"));

        // MODIFICAR
        adoptante.setTelefono("600000002");
        dao.update(adoptante);

        assertEquals("600000002", dao.find("12345678Z").getTelefono());

        // AÑADIR PREFERENCIAS
        dao.anadirPreferencia("12345678Z", "Perro");
        dao.anadirPreferencia("12345678Z", "Perro");

        // $addToSet no permite preferencias duplicadas.
        assertEquals(1, dao.find("12345678Z").getPreferencias().size());

        // BUSQUEDAS
        assertEquals(1, dao.buscarPorNombre("Adoptante").size());
        assertEquals(1, dao.buscarPorApellidos("Apellido").size());
        assertEquals(1, dao.buscarPorPreferencia("Perro").size());

        // ELIMINAR PREFERENCIA
        dao.eliminarPreferencia("12345678Z", "Perro");

        assertTrue(dao.find("12345678Z").getPreferencias().isEmpty());

        // ELIMINAR ADOPTANTE
        dao.delete("12345678Z");

        assertNull(dao.find("12345678Z"));
    }

    // PRUEBA CRUD ADOPCION
    @Test
    void probarAdopcion() {

        AnimalDao animalDao = new AnimalDao(db);
        AdoptanteDao adoptanteDao = new AdoptanteDao(db);
        AdopcionDao adopcionDao = new AdopcionDao(db);

        Date fecha = new Date();

        // CREAMOS EL ANIMAL
        Animal animal = new Animal(1, "Luna", "Perro", "Labrador", 3, "Hembra", "Animal tranquilo", fecha, "disponible");

        animalDao.create(animal);

        // CREAMOS EL ADOPTANTE
        Adoptante adoptante = new Adoptante("12345678Z", "Adoptante 1", "Apellido 1", "600000001", "Calle A", "adoptante@example.com", fecha);

        adoptanteDao.create(adoptante);

        // CREAMOS LA ADOPCION
        Adopcion adopcion = new Adopcion(1, 1, "12345678Z", fecha, null, "pendiente", "Solicitud recibida");

        adopcionDao.create(adopcion);

        assertNotNull(adopcionDao.find(1));

        // NO SE PUEDE AÑADIR SEGUIMIENTO SI ESTA PENDIENTE
        assertThrows(IllegalArgumentException.class, () -> {

            adopcionDao.anadirSeguimiento(1, new Document("observaciones", "Prueba"));
        });

        // APROBAR ADOPCION
        adopcion.setEstado("aprobada");
        adopcion.setFechaResolucion(fecha);

        adopcionDao.update(adopcion);

        assertEquals("aprobada", adopcionDao.find(1).getEstado());

        // COMPROBAR QUE EL ANIMAL PASA A ADOPTADO
        assertEquals("adoptado", animalDao.find(1).getEstado());

        // AÑADIR SEGUIMIENTO
        Document seguimiento = new Document("fecha", fecha).append("observaciones", "Adaptacion correcta");

        adopcionDao.anadirSeguimiento(1, seguimiento);

        assertEquals(1, adopcionDao.consultarSeguimiento(1).size());

        // BUSQUEDAS AVANZADAS
        assertEquals(1, adopcionDao.buscarPorAnimal(1).size());

        assertEquals(1, adopcionDao.buscarPorAdoptante("12345678Z").size());

        assertEquals(1, adopcionDao.buscarPorEstado("aprobada").size());

        assertEquals(1, adopcionDao.buscarPorFecha(fecha, fecha).size());

        // NO PERMITIR ELIMINAR EL ANIMAL
        assertThrows(IllegalArgumentException.class, () -> animalDao.delete(1));

        // NO PERMITIR ELIMINAR EL ADOPTANTE
        assertThrows(IllegalArgumentException.class, () -> adoptanteDao.delete("12345678Z"));

        // IMPEDIR UNA SEGUNDA ADOPCION APROBADA
        Adopcion segunda = new Adopcion(2, 1, "12345678Z", fecha, fecha, "aprobada", "Segunda adopcion");

        assertThrows(IllegalArgumentException.class, () -> adopcionDao.create(segunda));

        // ELIMINAR ADOPCION
        adopcionDao.delete(1);

        assertNull(adopcionDao.find(1));

        // EL ANIMAL VUELVE A ESTAR DISPONIBLE
        assertEquals("disponible", animalDao.find(1).getEstado());
    }

    // PRUEBA ADOPCION CON REFERENCIAS INCORRECTAS
    @Test
    void adopcionSinAnimalNiAdoptante() {

        AdopcionDao dao = new AdopcionDao(db);

        Adopcion adopcion = new Adopcion(1, 99, "12345678Z", new Date(), null, "pendiente", "Solicitud");

        assertThrows(IllegalArgumentException.class, () -> dao.create(adopcion));

        assertNull(dao.find(1));
    }

    // PRUEBA REFUGIO
    @Test
    void probarRefugio() {

        RefugioDao dao = new RefugioDao(db);

        Refugio refugio = new Refugio("Refugio Animal", "Asociacion Animal", "G00000000", "Calle A", "600000000", "refugio@example.com", new Date());

        // CREAR
        dao.create(refugio);

        assertNotNull(dao.find());

        // MODIFICAR
        refugio.setDireccion("Calle B");
        dao.update(refugio);

        assertEquals("Calle B", dao.find().getDireccion());
    }
}