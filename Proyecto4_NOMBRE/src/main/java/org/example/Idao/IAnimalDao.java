package org.example.Idao;

import org.bson.Document;
import org.example.modelos.Animal;

import java.util.List;

public interface IAnimalDao {

    // OPERACIONES CRUD
    Animal create(Animal animal);

    Animal find(int id);

    Animal update(Animal animal);

    void delete(int id);

    List<Animal> listar();

    // HISTORIAL MEDICO
    void anadirRegistroMedico(int id, Document registro);

    List<Document> consultarHistorialMedico(int id);

    // BUSQUEDAS AVANZADAS
    List<Animal> buscarPorNombre(String nombre);

    List<Animal> buscarPorEspecie(String especie);

    List<Animal> buscarPorRaza(String raza);

    List<Animal> buscarPorEdad(int edad);

    List<Animal> buscarPorEstado(String estado);
}