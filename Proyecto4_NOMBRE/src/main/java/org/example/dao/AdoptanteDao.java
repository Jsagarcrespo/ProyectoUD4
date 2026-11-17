package org.example.dao;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.Idao.IAdoptanteDao;
import org.example.modelos.Adoptante;

import java.util.List;
import java.util.regex.Pattern;

public class AdoptanteDao extends dao<Adoptante> implements IAdoptanteDao {

    // CONSTRUCTOR
    public AdoptanteDao(MongoDatabase db) {

        super(db, "adoptantes");
    }

    // CONVERTIR DOCUMENTO EN ADOPTANTE
    @Override
    protected Adoptante convertir(Document documento) {

        Adoptante adoptante = new Adoptante(documento.getString("_id"), documento.getString("nombre"), documento.getString("apellidos"), documento.getString("telefono"), documento.getString("direccion"), documento.getString("email"), documento.getDate("fechaRegistro"));

        if (documento.get("preferencias") != null) {

            adoptante.setPreferencias(documento.getList("preferencias", String.class));
        }

        return adoptante;
    }

    // PREPARAR LOS DATOS DEL ADOPTANTE
    private Document prepararDatos(Adoptante adoptante) {

        return new Document("nombre", adoptante.getNombre()).append("apellidos", adoptante.getApellidos()).append("telefono", adoptante.getTelefono()).append("direccion", adoptante.getDireccion()).append("email", adoptante.getEmail()).append("fechaRegistro", adoptante.getFechaRegistro());
    }

    // AÑADIR ADOPTANTE
    @Override
    public Adoptante create(Adoptante adoptante) {

        // Validamos los datos antes de guardarlos.
        adoptante.validar();

        Document documento = prepararDatos(adoptante).append("_id", adoptante.getDni()).append("preferencias", adoptante.getPreferencias());

        insertar(documento);

        return adoptante;
    }

    // BUSCAR POR DNI
    @Override
    public Adoptante find(String dni) {

        return buscarUno(dni);
    }

    // MODIFICAR ADOPTANTE
    @Override
    public Adoptante update(Adoptante adoptante) {

        // Validamos los datos antes de modificarlos.
        adoptante.validar();

        Document cambios = new Document("$set", prepararDatos(adoptante));

        actualizar(adoptante.getDni(), cambios);

        return find(adoptante.getDni());
    }

    // ELIMINAR ADOPTANTE
    @Override
    public void delete(String dni) {

        // Comprobamos si tiene adopciones asociadas.
        Document adopcion = db.getCollection("adopciones").find(new Document("dniAdoptante", dni)).first();

        if (adopcion != null) {

            throw new IllegalArgumentException("El adoptante tiene adopciones asociadas");
        }

        eliminar(dni);
    }

    // MOSTRAR TODOS LOS ADOPTANTES
    @Override
    public List<Adoptante> listar() {

        return buscar(new Document());
    }

    // AÑADIR PREFERENCIA
    @Override
    public void anadirPreferencia(String dni, String preferencia) {

        if (preferencia == null || preferencia.isBlank()) {

            throw new IllegalArgumentException("La preferencia no puede estar vacia");
        }

        Document cambios = new Document("$addToSet", new Document("preferencias", preferencia.trim()));

        actualizar(dni, cambios);
    }

    // ELIMINAR PREFERENCIA
    @Override
    public void eliminarPreferencia(String dni, String preferencia) {

        if (preferencia == null || preferencia.isBlank()) {

            throw new IllegalArgumentException("La preferencia no puede estar vacia");
        }

        Document cambios = new Document("$pull", new Document("preferencias", preferencia.trim()));

        actualizar(dni, cambios);
    }

    // BUSCAR POR NOMBRE
    @Override
    public List<Adoptante> buscarPorNombre(String nombre) {

        Document filtro = new Document("nombre", new Document("$regex", Pattern.quote(nombre)).append("$options", "i"));

        return buscar(filtro);
    }

    // BUSCAR POR APELLIDOS
    @Override
    public List<Adoptante> buscarPorApellidos(String apellidos) {

        Document filtro = new Document("apellidos", new Document("$regex", Pattern.quote(apellidos)).append("$options", "i"));

        return buscar(filtro);
    }

    // BUSCAR POR PREFERENCIA
    @Override
    public List<Adoptante> buscarPorPreferencia(String preferencia) {

        return buscar(new Document("preferencias", preferencia));
    }
}