package org.example;

import org.example.modelos.Animal;
import java.util.ArrayList;
import java.util.List;

public class ListaAnimales {

    private List<Animal> lista = new ArrayList<>();

    public ListaAnimales() {
    }

    public void add(Animal animal) {
        lista.add(animal);
    }

    public List<Animal> getListaAnimales() {
        return lista;
    }
}
