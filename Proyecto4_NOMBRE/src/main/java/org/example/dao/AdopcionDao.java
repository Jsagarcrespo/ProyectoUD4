package org.example.dao;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.Idao.IAdopcionDao;
import org.example.modelos.Adopcion;
import org.example.modelos.Animal;

import java.util.Date;
import java.util.List;

public class AdopcionDao extends dao<Adopcion> implements IAdopcionDao {

    // CONSTRUCTOR
    public AdopcionDao(MongoDatabase db) {

        super(db, "adopciones");
    }

    // CONVERTIR DOCUMENTO EN ADOPCION
    @Override
    protected Adopcion convertir(Document documento) {

        Adopcion adopcion = new Adopcion(documento.getInteger("_id"), documento.getInteger("idAnimal"), documento.getString("dniAdoptante"), documento.getDate("fechaSolicitud"), documento.getDate("fechaResolucion"), documento.getString("estado"), documento.getString("observaciones"));

        if (documento.get("seguimiento") != null) {

            adopcion.setSeguimiento(documento.getList("seguimiento", Document.class));
        }

        return adopcion;
    }

    // PREPARAR LOS DATOS DE LA ADOPCION
    private Document prepararDatos(Adopcion adopcion) {

        return new Document("idAnimal", adopcion.getIdAnimal()).append("dniAdoptante", adopcion.getDniAdoptante()).append("fechaSolicitud", adopcion.getFechaSolicitud()).append("fechaResolucion", adopcion.getFechaResolucion()).append("estado", adopcion.getEstado()).append("observaciones", adopcion.getObservaciones());
    }

    // COMPROBAR LAS REFERENCIAS
    private void comprobarReferencias(Adopcion adopcion) {

        AnimalDao animalDao = new AnimalDao(db);
        AdoptanteDao adoptanteDao = new AdoptanteDao(db);

        Animal animal = animalDao.find(adopcion.getIdAnimal());

        if (animal == null) {

            throw new IllegalArgumentException("El animal no existe");
        }

        if (adoptanteDao.find(adopcion.getDniAdoptante()) == null) {

            throw new IllegalArgumentException("El adoptante no existe");
        }

        if (adopcion.getFechaSolicitud().before(animal.getFechaIngreso())) {

            throw new IllegalArgumentException("La solicitud no puede ser anterior al ingreso del animal");
        }
    }

    public List<Adopcion> buscarPorAdoptanteYFecha(String dni, Date desde, Date hasta) {

        Document filtro = new Document("dniAdoptante", dni).append("fechaSolicitud", new Document("$gte", desde).append("$lte", hasta));

        return buscar(filtro);
    }

    // COMPROBAR SI EXISTE OTRA ADOPCION APROBADA
    private void comprobarAprobacion(Adopcion adopcion) {

        if ("aprobada".equals(adopcion.getEstado())) {

            Document filtro = new Document("idAnimal", adopcion.getIdAnimal()).append("estado", "aprobada").append("_id", new Document("$ne", adopcion.getId()));

            // Un animal no puede tener dos adopciones aprobadas.
            if (coleccion.find(filtro).first() != null) {

                throw new IllegalArgumentException("El animal ya tiene una adopcion aprobada");
            }
        }
    }

    // ACTUALIZAR EL ESTADO DEL ANIMAL
    private void actualizarEstadoAnimal(int idAnimal) {

        Document filtro = new Document("idAnimal", idAnimal).append("estado", "aprobada");

        Document adopcion = coleccion.find(filtro).first();

        if (adopcion != null) {

            // Existe una adopcion aprobada.
            db.getCollection("animales").updateOne(new Document("_id", idAnimal), new Document("$set", new Document("estado", "adoptado")));

        } else {

            // Si ya no tiene una adopcion aprobada,
            // vuelve a estar disponible si estaba adoptado.
            db.getCollection("animales").updateOne(new Document("_id", idAnimal).append("estado", "adoptado"), new Document("$set", new Document("estado", "disponible")));
        }
    }

    // AÑADIR ADOPCION
    @Override
    public Adopcion create(Adopcion adopcion) {

        adopcion.validar();

        comprobarReferencias(adopcion);

        comprobarAprobacion(adopcion);

        // Solo se puede registrar seguimiento en adopciones aprobadas.
        if (!"aprobada".equals(adopcion.getEstado()) && !adopcion.getSeguimiento().isEmpty()) {

            throw new IllegalArgumentException("La adopcion no esta aprobada");
        }

        Document documento = prepararDatos(adopcion).append("_id", adopcion.getId()).append("seguimiento", adopcion.getSeguimiento());

        insertar(documento);

        // Actualizamos el estado del animal.
        if ("aprobada".equals(adopcion.getEstado())) {

            actualizarEstadoAnimal(adopcion.getIdAnimal());
        }

        return adopcion;
    }

    // BUSCAR POR ID
    @Override
    public Adopcion find(int id) {

        return buscarUno(id);
    }

    // MODIFICAR ADOPCION
    @Override
    public Adopcion update(Adopcion adopcion) {

        adopcion.validar();

        comprobarReferencias(adopcion);

        comprobarAprobacion(adopcion);

        // Recuperamos la adopcion antes de modificarla.
        Adopcion anterior = find(adopcion.getId());

        if (anterior == null) {

            throw new IllegalArgumentException("La adopcion no existe");
        }
        if (!anterior.getSeguimiento().isEmpty()) {

            if (!"aprobada".equals(adopcion.getEstado())) {

                throw new IllegalArgumentException("No puedes cambiar el estado de una adopcion con seguimiento");
            }

            if (anterior.getIdAnimal() != adopcion.getIdAnimal() || !anterior.getDniAdoptante().equals(adopcion.getDniAdoptante())) {

                throw new IllegalArgumentException("No puedes cambiar el animal o adoptante de una adopcion con seguimiento");
            }
        }

        Document cambios = new Document("$set", prepararDatos(adopcion));

        actualizar(adopcion.getId(), cambios);

        // Comprobamos si el animal anterior debe cambiar de estado.
        if ("aprobada".equals(anterior.getEstado())) {

            actualizarEstadoAnimal(anterior.getIdAnimal());
        }

        // Actualizamos el estado del animal de la adopcion.
        if ("aprobada".equals(adopcion.getEstado())) {

            actualizarEstadoAnimal(adopcion.getIdAnimal());
        }

        return find(adopcion.getId());
    }

    // ELIMINAR ADOPCION
    @Override
    public void delete(int id) {

        Adopcion adopcion = find(id);

        if (adopcion == null) {

            throw new IllegalArgumentException("La adopcion no existe");
        }

        eliminar(id);

        // Si estaba aprobada, revisamos el estado del animal.
        if ("aprobada".equals(adopcion.getEstado())) {

            actualizarEstadoAnimal(adopcion.getIdAnimal());
        }
    }

    // MOSTRAR TODAS LAS ADOPCIONES
    @Override
    public List<Adopcion> listar() {

        return buscar(new Document());
    }

    // AÑADIR SEGUIMIENTO
    @Override
    public void anadirSeguimiento(int id, Document registro) {

        if (registro == null || registro.isEmpty()) {

            throw new IllegalArgumentException("El seguimiento no puede estar vacio");
        }

        Adopcion adopcion = find(id);

        if (adopcion == null) {

            throw new IllegalArgumentException("La adopcion no existe");
        }

        // Solo las adopciones aprobadas tienen seguimiento.
        if (!"aprobada".equals(adopcion.getEstado())) {

            throw new IllegalArgumentException("La adopcion no esta aprobada");
        }

        Document cambios = new Document("$push", new Document("seguimiento", registro));

        actualizar(id, cambios);
    }

    // CONSULTAR SEGUIMIENTO
    @Override
    public List<Document> consultarSeguimiento(int id) {

        Adopcion adopcion = find(id);

        if (adopcion == null) {

            throw new IllegalArgumentException("La adopcion no existe");
        }

        return adopcion.getSeguimiento();
    }

    // BUSCAR POR ANIMAL
    @Override
    public List<Adopcion> buscarPorAnimal(int idAnimal) {

        return buscar(new Document("idAnimal", idAnimal));
    }

    // BUSCAR POR ADOPTANTE
    @Override
    public List<Adopcion> buscarPorAdoptante(String dni) {

        return buscar(new Document("dniAdoptante", dni));
    }

    // BUSCAR ENTRE DOS FECHAS
    @Override
    public List<Adopcion> buscarPorFecha(Date desde, Date hasta) {

        if (desde == null || hasta == null || desde.after(hasta)) {

            throw new IllegalArgumentException("Intervalo de fechas incorrecto");
        }

        Document filtro = new Document("fechaSolicitud", new Document("$gte", desde).append("$lte", hasta));

        return buscar(filtro);
    }

    // BUSCAR POR ESTADO
    @Override
    public List<Adopcion> buscarPorEstado(String estado) {

        return buscar(new Document("estado", estado));
    }
}