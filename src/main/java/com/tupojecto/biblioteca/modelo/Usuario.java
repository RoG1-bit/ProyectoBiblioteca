package com.tupojecto.biblioteca.modelo;

import java.sql.Timestamp;

public class Usuario {

    // Atributos (igual que en la tabla SQL)
    private int idUsuario;
    private String nombre;
    private String username;
    private String password;
    private String tipoUsuario; // "Administrador", "Profesor", "Alumno"
    private boolean tieneMora;
    private Timestamp fechaRegistro;

    // Constructor vacío
    public Usuario() { }

    // Constructor con parámetros principales
    public Usuario(String nombre, String username, String password, String tipoUsuario) {
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.tipoUsuario = tipoUsuario;
        this.tieneMora = false;
    }

    // Constructor completo
    public Usuario(int idUsuario, String nombre, String username, String password,
                   String tipoUsuario, boolean tieneMora, Timestamp fechaRegistro) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.tipoUsuario = tipoUsuario;
        this.tieneMora = tieneMora;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public boolean isTieneMora() {
        return tieneMora;
    }

    public void setTieneMora(boolean tieneMora) {
        this.tieneMora = tieneMora;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", username='" + username + '\'' +
                ", tipoUsuario='" + tipoUsuario + '\'' +
                ", tieneMora=" + tieneMora +
                '}';
    }
}