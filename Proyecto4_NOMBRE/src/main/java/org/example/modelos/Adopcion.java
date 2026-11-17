package org.example.modelos;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Adopcion {

    private int id;

    private int idAnimal;

    private String dniAdoptante;

    private Date fechaSolicitud;
    private Date fechaResolucion;
    private String estado;
    private String observaciones;

    private List<Document> seguimiento = new ArrayList<>();

    public Adopcion() {
    }

    public Adopcion(int id, int idAnimal, String dniAdoptante, Date fechaSolicitud, Date fechaResolucion, String estado, String observaciones) {

        this.id = id;
        this.idAnimal = idAnimal;
        this.dniAdoptante = dniAdoptante;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaResolucion = fechaResolucion;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    // GETTERS Y SETTERS

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(int idAnimal) {
        this.idAnimal = idAnimal;
    }

    public String getDniAdoptante() {
        return dniAdoptante;
    }

    public void setDniAdoptante(String dniAdoptante) {
        this.dniAdoptante = dniAdoptante;
    }

    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public Date getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(Date fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<Document> getSeguimiento() {
        return seguimiento;
    }

    public void setSeguimiento(List<Document> seguimiento) {
        this.seguimiento = seguimiento;
    }

    // VALIDAR LOS DATOS DE LA ADOPCION
    public void validar() {

        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor que cero");
        }

        if (idAnimal <= 0) {
            throw new IllegalArgumentException("El ID del animal es incorrecto");
        }

        if (dniAdoptante == null || dniAdoptante.isBlank()) {
            throw new IllegalArgumentException("El adoptante es obligatorio");
        }

        if (fechaSolicitud == null) {
            throw new IllegalArgumentException("La fecha de solicitud es obligatoria");
        }

        // Comprobamos los estados del enunciado.
        if (!"pendiente".equals(estado) && !"aprobada".equals(estado) && !"rechazada".equals(estado)) {

            throw new IllegalArgumentException("Estado de adopcion incorrecto");
        }

        // Una solicitud pendiente todavia no esta resuelta.
        if ("pendiente".equals(estado) && fechaResolucion != null) {

            throw new IllegalArgumentException("Una solicitud pendiente no tiene fecha de resolucion");
        }

        // Una solicitud aprobada o rechazada ya esta resuelta.
        if (!"pendiente".equals(estado) && fechaResolucion == null) {

            throw new IllegalArgumentException("Introduce la fecha de resolucion");
        }

        // La resolucion no puede ser anterior a la solicitud.
        if (fechaResolucion != null && fechaResolucion.before(fechaSolicitud)) {

            throw new IllegalArgumentException("La fecha de resolucion no puede ser anterior a la solicitud");
        }

        if (seguimiento == null) {
            throw new IllegalArgumentException("El seguimiento no puede ser null");
        }
    }

    @Override
    public String toString() {
        return "Adopcion{" + "id=" + id + ", idAnimal=" + idAnimal + ", dniAdoptante='" + dniAdoptante + '\'' + ", fechaSolicitud=" + fechaSolicitud + ", fechaResolucion=" + fechaResolucion + ", estado='" + estado + '\'' + ", observaciones='" + observaciones + '\'' + ", seguimiento=" + seguimiento + '}';
    }
}