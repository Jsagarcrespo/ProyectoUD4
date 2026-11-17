package org.example.modelos;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Animal {

    // ATRIBUTOS
    private int id;
    private String nombre;
    private String especie;
    private String raza;
    private int edad;
    private String sexo;
    private String descripcion;
    private Date fechaIngreso;
    private String estado;

    private List<Document> historialMedico = new ArrayList<>();

    public Animal() {
    }

    public Animal(int id, String nombre, String especie, String raza, int edad, String sexo, String descripcion, Date fechaIngreso, String estado) {

        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.sexo = sexo;
        this.descripcion = descripcion;
        this.fechaIngreso = fechaIngreso;
        this.estado = estado;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Document> getHistorialMedico() {
        return historialMedico;
    }

    public void setHistorialMedico(List<Document> historialMedico) {
        this.historialMedico = historialMedico;
    }

    // VALIDAR LOS DATOS DEL ANIMAL
    public void validar() {

        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor que cero");
        }

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (especie == null || especie.isBlank()) {
            throw new IllegalArgumentException("La especie es obligatoria");
        }

        if (raza == null || raza.isBlank()) {
            throw new IllegalArgumentException("La raza es obligatoria");
        }

        if (edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }

        if (sexo == null || sexo.isBlank()) {
            throw new IllegalArgumentException("El sexo es obligatorio");
        }

        if (fechaIngreso == null) {
            throw new IllegalArgumentException("La fecha de ingreso es obligatoria");
        }

        // Comprobamos los estados que permite el enunciado.
        if (!"disponible".equals(estado) && !"reservado".equals(estado) && !"adoptado".equals(estado) && !"en tratamiento".equals(estado)) {

            throw new IllegalArgumentException("Estado del animal incorrecto");
        }

        if (historialMedico == null) {
            throw new IllegalArgumentException("El historial medico no puede ser null");
        }
    }

    @Override
    public String toString() {
        return "Animal{" + "id=" + id + ", nombre='" + nombre + '\'' + ", especie='" + especie + '\'' + ", raza='" + raza + '\'' + ", edad=" + edad + ", sexo='" + sexo + '\'' + ", descripcion='" + descripcion + '\'' + ", fechaIngreso=" + fechaIngreso + ", estado='" + estado + '\'' + ", historialMedico=" + historialMedico + '}';
    }
}