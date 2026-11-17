package org.example.dao;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.Idao.IRefugioDao;
import org.example.modelos.Refugio;

public class RefugioDao extends dao<Refugio> implements IRefugioDao {

    // CONSTRUCTOR
    public RefugioDao(MongoDatabase db) {

        super(db, "refugio");
    }

    // CONVERTIR DOCUMENTO EN REFUGIO
    @Override
    protected Refugio convertir(Document documento) {

        return new Refugio(documento.getString("nombre"), documento.getString("razonSocial"), documento.getString("cif"), documento.getString("direccion"), documento.getString("telefono"), documento.getString("email"), documento.getDate("fechaApertura"));
    }

    // PREPARAR LOS DATOS DEL REFUGIO
    private Document prepararDatos(Refugio refugio) {

        return new Document("nombre", refugio.getNombre()).append("razonSocial", refugio.getRazonSocial()).append("cif", refugio.getCif()).append("direccion", refugio.getDireccion()).append("telefono", refugio.getTelefono()).append("email", refugio.getEmail()).append("fechaApertura", refugio.getFechaApertura());
    }

    // CREAR REFUGIO
    @Override
    public Refugio create(Refugio refugio) {

        // Validamos los datos antes de guardar.
        refugio.validar();

        Document documento = prepararDatos(refugio).append("_id", "refugio");

        insertar(documento);

        return refugio;
    }

    // CONSULTAR REFUGIO
    @Override
    public Refugio find() {

        return buscarUno("refugio");
    }

    // MODIFICAR REFUGIO
    @Override
    public Refugio update(Refugio refugio) {

        // Validamos los datos antes de modificar.
        refugio.validar();

        Document cambios = new Document("$set", prepararDatos(refugio));

        actualizar("refugio", cambios);

        return find();
    }
}