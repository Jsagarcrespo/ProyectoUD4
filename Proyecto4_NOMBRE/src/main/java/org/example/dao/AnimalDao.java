package org.example.dao;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.Idao.IAnimalDao;
import org.example.modelos.Animal;

import java.util.List;
import java.util.regex.Pattern;

public class AnimalDao extends dao<Animal> implements IAnimalDao {

    // CONSTRUCTOR
    public AnimalDao(MongoDatabase db) {
        super(db, "animales");
    }

    // CONVERTIR DOCUMENTO EN ANIMAL
    @Override
    protected Animal convertir(Document documento) {

        Animal animal = new Animal(documento.getInteger("_id"), documento.getString("nombre"), documento.getString("especie"), documento.getString("raza"), documento.getInteger("edad"), documento.getString("sexo"), documento.getString("descripcion"), documento.getDate("fechaIngreso"), documento.getString("estado"));

        if (documento.get("historialMedico") != null) {

            animal.setHistorialMedico(documento.getList("historialMedico", Document.class));
        }

        return animal;
    }

    // PREPARAR LOS DATOS DEL ANIMAL
    private Document prepararDatos(Animal animal) {

        return new Document("nombre", animal.getNombre()).append("especie", animal.getEspecie()).append("raza", animal.getRaza()).append("edad", animal.getEdad()).append("sexo", animal.getSexo()).append("descripcion", animal.getDescripcion()).append("fechaIngreso", animal.getFechaIngreso()).append("estado", animal.getEstado());
    }

    // COMPROBAR EL ESTADO DEL ANIMAL
    private void comprobarEstadoAnimal(Animal animal) {

        // Buscamos si el animal tiene una adopcion aprobada.
        Document filtro = new Document("idAnimal", animal.getId()).append("estado", "aprobada");

        Document adopcion = db.getCollection("adopciones").find(filtro).first();

        // Si tiene una adopcion aprobada, debe estar adoptado.
        if (adopcion != null && !"adoptado".equals(animal.getEstado())) {

            throw new IllegalArgumentException("El animal tiene una adopcion aprobada");
        }

        // No puede estar adoptado si no existe una adopcion aprobada.
        if (adopcion == null && "adoptado".equals(animal.getEstado())) {

            throw new IllegalArgumentException("El animal no tiene una adopcion aprobada");
        }
    }

    // COMPROBAR LA FECHA DE INGRESO ANTES DE MODIFICAR EL ANIMAL
    private void comprobarFechaIngreso(Animal animal) {

        // Buscamos alguna solicitud anterior a la nueva fecha de ingreso.
        // $lt significa menor que. Una solicitud del mismo dia es valida.
        Document filtro = new Document("idAnimal", animal.getId())
                .append("fechaSolicitud", new Document("$lt", animal.getFechaIngreso()));

        Document adopcion = db.getCollection("adopciones").find(filtro).first();

        if (adopcion != null) {

            throw new IllegalArgumentException("La fecha de ingreso no puede ser posterior a una solicitud de adopcion del animal");
        }
    }

    // AÑADIR ANIMAL
    @Override
    public Animal create(Animal animal) {

        animal.validar();

        comprobarEstadoAnimal(animal);

        Document documento = prepararDatos(animal).append("_id", animal.getId()).append("historialMedico", animal.getHistorialMedico());

        insertar(documento);

        return animal;
    }

    // BUSCAR POR ID
    @Override
    public Animal find(int id) {

        return buscarUno(id);
    }

    // MODIFICAR ANIMAL
    @Override
    public Animal update(Animal animal) {

        animal.validar();

        comprobarEstadoAnimal(animal);

        comprobarFechaIngreso(animal);

        Document cambios = new Document("$set", prepararDatos(animal));

        actualizar(animal.getId(), cambios);

        return find(animal.getId());
    }

    // ELIMINAR ANIMAL
    @Override
    public void delete(int id) {

        // Comprobamos si existen adopciones relacionadas.
        Document adopcion = db.getCollection("adopciones").find(new Document("idAnimal", id)).first();

        if (adopcion != null) {

            throw new IllegalArgumentException("El animal tiene adopciones asociadas");
        }

        eliminar(id);
    }

    // MOSTRAR TODOS LOS ANIMALES
    @Override
    public List<Animal> listar() {

        return buscar(new Document());
    }

    // AÑADIR UN REGISTRO MEDICO
    @Override
    public void anadirRegistroMedico(int id, Document registro) {

        if (registro == null || registro.isEmpty()) {

            throw new IllegalArgumentException("El registro medico no puede estar vacio");
        }

        // Push añade un documento al array del historial medico.
        Document cambios = new Document("$push", new Document("historialMedico", registro));

        actualizar(id, cambios);
    }

    // CONSULTAR HISTORIAL MEDICO
    @Override
    public List<Document> consultarHistorialMedico(int id) {

        Animal animal = find(id);

        if (animal == null) {

            throw new IllegalArgumentException("El animal no existe");
        }

        return animal.getHistorialMedico();
    }

    // BUSCAR POR NOMBRE
    @Override
    public List<Animal> buscarPorNombre(String nombre) {

        Document filtro = new Document("nombre", new Document("$regex", Pattern.quote(nombre)).append("$options", "i"));

        return buscar(filtro);
    }

    // BUSCAR POR ESPECIE
    @Override
    public List<Animal> buscarPorEspecie(String especie) {

        return buscar(new Document("especie", especie));
    }

    // BUSCAR POR RAZA
    @Override
    public List<Animal> buscarPorRaza(String raza) {

        return buscar(new Document("raza", raza));
    }

    // BUSCAR POR EDAD
    @Override
    public List<Animal> buscarPorEdad(int edad) {

        return buscar(new Document("edad", edad));
    }

    // BUSCAR POR ESTADO
    @Override
    public List<Animal> buscarPorEstado(String estado) {

        return buscar(new Document("estado", estado));
    }
}