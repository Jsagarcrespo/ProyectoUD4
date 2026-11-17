package org.example.modelos;

import java.util.Date;

public class Refugio {

    // ATRIBUTOS
    private String nombre;
    private String razonSocial;
    private String cif;
    private String direccion;
    private String telefono;
    private String email;
    private Date fechaApertura;

    public Refugio() {
    }

    public Refugio(String nombre, String razonSocial, String cif, String direccion, String telefono, String email, Date fechaApertura) {

        this.nombre = nombre;
        this.razonSocial = razonSocial;
        this.cif = cif;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.fechaApertura = fechaApertura;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(Date fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    // VALIDAR LOS DATOS DEL REFUGIO
    public void validar() {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (razonSocial == null || razonSocial.isBlank()) {
            throw new IllegalArgumentException("La razon social es obligatoria");
        }

        if (cif == null || cif.isBlank()) {
            throw new IllegalArgumentException("El CIF es obligatorio");
        }

        if (direccion == null || direccion.isBlank()) {
            throw new IllegalArgumentException("La direccion es obligatoria");
        }

        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("El telefono es obligatorio");
        }

        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email incorrecto");
        }

        if (fechaApertura == null) {
            throw new IllegalArgumentException("La fecha de apertura es obligatoria");
        }
    }

    @Override
    public String toString() {
        return "Refugio{" + "nombre='" + nombre + '\'' + ", razonSocial='" + razonSocial + '\'' + ", cif='" + cif + '\'' + ", direccion='" + direccion + '\'' + ", telefono='" + telefono + '\'' + ", email='" + email + '\'' + ", fechaApertura=" + fechaApertura + '}';
    }
}