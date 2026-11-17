package org.example.Idao;

import org.example.modelos.Adoptante;

import java.util.List;

public interface IAdoptanteDao {

    // CRUD
    Adoptante create(Adoptante adoptante);

    Adoptante find(String dni);

    Adoptante update(Adoptante adoptante);

    void delete(String dni);

    List<Adoptante> listar();

    // PREFERENCIAS
    void anadirPreferencia(String dni, String preferencia);

    void eliminarPreferencia(String dni, String preferencia);

    // BUSQUEDAS AVANZADAS
    List<Adoptante> buscarPorNombre(String nombre);

    List<Adoptante> buscarPorApellidos(String apellidos);

    List<Adoptante> buscarPorPreferencia(String preferencia);
}