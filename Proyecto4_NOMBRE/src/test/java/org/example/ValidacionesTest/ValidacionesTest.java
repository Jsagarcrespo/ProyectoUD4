package org.example;

import org.example.modelos.*;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ValidacionesTest {

    // ANIMAL CORRECTO
    @Test
    void animalCorrecto() {

        Animal animal = new Animal(1, "Luna", "Perro", "Labrador", 3, "Hembra", "Animal tranquilo", new Date(), "disponible");

        assertDoesNotThrow(animal::validar);
    }

    // ANIMAL CON EDAD INCORRECTA
    @Test
    void animalEdadNegativa() {

        Animal animal = new Animal(1, "Luna", "Perro", "Labrador", -3, "Hembra", "Animal tranquilo", new Date(), "disponible");

        assertThrows(IllegalArgumentException.class, animal::validar);
    }

    // ANIMAL CON ESTADO INCORRECTO
    @Test
    void animalEstadoIncorrecto() {

        Animal animal = new Animal(1, "Luna", "Perro", "Labrador", 3, "Hembra", "Animal tranquilo", new Date(), "incorrecto");

        assertThrows(IllegalArgumentException.class, animal::validar);
    }

    // ADOPTANTE CORRECTO
    @Test
    void adoptanteCorrecto() {

        Adoptante adoptante = new Adoptante("12345678Z", "Adoptante 1", "Apellido 1", "600000001", "Calle A", "adoptante@example.com", new Date());

        assertDoesNotThrow(adoptante::validar);
    }

    // DNI INCORRECTO
    @Test
    void adoptanteDniIncorrecto() {

        Adoptante adoptante = new Adoptante("123ABC", "Adoptante 1", "Apellido 1", "600000001", "Calle A", "adoptante@example.com", new Date());

        assertThrows(IllegalArgumentException.class, adoptante::validar);
    }

    // ADOPCION CORRECTA
    @Test
    void adopcionCorrecta() {

        Adopcion adopcion = new Adopcion(1, 1, "12345678Z", new Date(), null, "pendiente", "Solicitud recibida");

        assertDoesNotThrow(adopcion::validar);
    }

    // ADOPCION APROBADA SIN FECHA DE RESOLUCION
    @Test
    void adopcionSinFechaResolucion() {

        Adopcion adopcion = new Adopcion(1, 1, "12345678Z", new Date(), null, "aprobada", "Solicitud aprobada");

        assertThrows(IllegalArgumentException.class, adopcion::validar);
    }

    // REFUGIO CORRECTO
    @Test
    void refugioCorrecto() {

        Refugio refugio = new Refugio("Refugio Animal", "Asociacion Animal", "G00000000", "Calle A", "600000000", "refugio@example.com", new Date());

        assertDoesNotThrow(refugio::validar);
    }

    // REFUGIO SIN NOMBRE
    @Test
    void refugioSinNombre() {

        Refugio refugio = new Refugio("", "Asociacion Animal", "G00000000", "Calle A", "600000000", "refugio@example.com", new Date());

        assertThrows(IllegalArgumentException.class, refugio::validar);
    }
}