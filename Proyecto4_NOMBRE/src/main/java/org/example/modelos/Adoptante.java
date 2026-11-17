package org.example.modelos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Adoptante {

    private String dni;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String email;
    private Date fechaRegistro;

    private List<String> preferencias = new ArrayList<>();

    public Adoptante() {
    }

    public Adoptante(String dni, String nombre, String apellidos, String telefono, String direccion, String email, Date fechaRegistro) {

        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.direccion = direccion;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
    }


    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<String> getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(List<String> preferencias) {
        this.preferencias = preferencias;
    }

    // VALIDAR LOS DATOS DEL ADOPTANTE
    public void validar() {

        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI/NIE es obligatorio");
        }

        // Comprobamos el formato del DNI o NIE.
        if (!dni.matches("[0-9]{8}[A-Z]|[XYZ][0-9]{7}[A-Z]")) {
            throw new IllegalArgumentException("Formato de DNI/NIE incorrecto");
        }

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (apellidos == null || apellidos.isBlank()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios");
        }

        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("El telefono es obligatorio");
        }

        if (direccion == null || direccion.isBlank()) {
            throw new IllegalArgumentException("La direccion es obligatoria");
        }

        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email incorrecto");
        }

        if (fechaRegistro == null) {
            throw new IllegalArgumentException("La fecha de registro es obligatoria");
        }

        if (preferencias == null) {
            throw new IllegalArgumentException("Las preferencias no pueden ser null");
        }
    }

    @Override
    public String toString() {
        return "Adoptante{" + "dni='" + dni + '\'' + ", nombre='" + nombre + '\'' + ", apellidos='" + apellidos + '\'' + ", telefono='" + telefono + '\'' + ", direccion='" + direccion + '\'' + ", email='" + email + '\'' + ", fechaRegistro=" + fechaRegistro + ", preferencias=" + preferencias + '}';
    }
}