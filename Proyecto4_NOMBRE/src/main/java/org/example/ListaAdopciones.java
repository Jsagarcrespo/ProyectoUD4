package org.example;

import org.example.modelos.Adopcion;
import java.util.ArrayList;
import java.util.List;

public class ListaAdopciones {

    private List<Adopcion> lista = new ArrayList<>();

    public ListaAdopciones() {
    }

    public void add(Adopcion adopcion) {
        lista.add(adopcion);
    }

    public List<Adopcion> getListaAdopciones() {
        return lista;
    }
}
