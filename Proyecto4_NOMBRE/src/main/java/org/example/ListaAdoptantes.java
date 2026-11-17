package org.example;

import org.example.modelos.Adoptante;
import java.util.ArrayList;
import java.util.List;

public class ListaAdoptantes {

    private List<Adoptante> lista = new ArrayList<>();

    public ListaAdoptantes() {
    }

    public void add(Adoptante adoptante) {
        lista.add(adoptante);
    }

    public List<Adoptante> getListaAdoptantes() {
        return lista;
    }
}
