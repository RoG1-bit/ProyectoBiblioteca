package com.tupojecto.biblioteca.modelo;

import java.sql.Timestamp;

// Clase base Documento (Abstracción y Polimorfismo - POO)
public class Documento {

    protected int idDocumento;
    protected String titulo;
    protected String autor;
    protected int idTipo;
    protected String nombreTipo;
    protected int anioPublicacion;
    protected String editorial;
    protected String isbn;
    protected int cantidadTotal;
    protected int cantidadDisponible;
    protected String ubicacionFisica;
    protected boolean esPrestable;
    protected Timestamp fechaRegistro;

    // Constructor vacío
    public Documento() { }

    // Constructor principal
    public Documento(String titulo, String autor, int idTipo, int anioPublicacion,
                     String editorial, String isbn, int cantidadTotal, String ubicacionFisica) {
        this.titulo = titulo;
        this.autor = autor;
        this.idTipo = idTipo;
        this.anioPublicacion = anioPublicacion;
        this.editorial = editorial;
        this.isbn = isbn;
        this.cantidadTotal = cantidadTotal;
        this.cantidadDisponible = cantidadTotal;
        this.ubicacionFisica = ubicacionFisica;
        this.esPrestable = true;
    }

    // Constructor completo
    public Documento(int idDocumento, String titulo, String autor, int idTipo, String nombreTipo,
                     int anioPublicacion, String editorial, String isbn, int cantidadTotal,
                     int cantidadDisponible, String ubicacionFisica, boolean esPrestable,
                     Timestamp fechaRegistro) {
        this.idDocumento = idDocumento;
        this.titulo = titulo;
        this.autor = autor;
        this.idTipo = idTipo;
        this.nombreTipo = nombreTipo;
        this.anioPublicacion = anioPublicacion;
        this.editorial = editorial;
        this.isbn = isbn;
        this.cantidadTotal = cantidadTotal;
        this.cantidadDisponible = cantidadDisponible;
        this.ubicacionFisica = ubicacionFisica;
        this.esPrestable = esPrestable;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y Setters
    public int getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(int idDocumento) {
        this.idDocumento = idDocumento;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(int cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public String getUbicacionFisica() {
        return ubicacionFisica;
    }

    public void setUbicacionFisica(String ubicacionFisica) {
        this.ubicacionFisica = ubicacionFisica;
    }

    public boolean isEsPrestable() {
        return esPrestable;
    }

    public void setEsPrestable(boolean esPrestable) {
        this.esPrestable = esPrestable;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return "Documento{" +
                "idDocumento=" + idDocumento +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", tipo='" + nombreTipo + '\'' +
                ", disponibles=" + cantidadDisponible + "/" + cantidadTotal +
                '}';
    }
}
