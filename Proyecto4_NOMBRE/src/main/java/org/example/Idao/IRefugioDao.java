package org.example.Idao;

import org.example.modelos.Refugio;

public interface IRefugioDao {

    // CREAR LOS DATOS INICIALES DEL REFUGIO
    Refugio create(Refugio refugio);

    // CONSULTAR LOS DATOS
    Refugio find();

    // MODIFICAR LOS DATOS
    Refugio update(Refugio refugio);
}