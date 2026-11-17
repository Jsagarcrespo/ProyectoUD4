package org.example.Idao;

import org.bson.Document;
import org.example.modelos.Adopcion;

import java.util.Date;
import java.util.List;

public interface IAdopcionDao {

    // CRUD
    Adopcion create(Adopcion adopcion);

    Adopcion find(int id);

    Adopcion update(Adopcion adopcion);

    void delete(int id);

    List<Adopcion> listar();

    // SEGUIMIENTO
    void anadirSeguimiento(int id, Document registro);

    List<Document> consultarSeguimiento(int id);

    // BUSQUEDAS AVANZADAS
    List<Adopcion> buscarPorAnimal(int idAnimal);

    List<Adopcion> buscarPorAdoptante(String dni);

    List<Adopcion> buscarPorFecha(Date desde, Date hasta);

    List<Adopcion> buscarPorEstado(String estado);
}
