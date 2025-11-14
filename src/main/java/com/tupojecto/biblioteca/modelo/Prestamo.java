package com.tupojecto.biblioteca.modelo;

import java.sql.Date;

public class Prestamo {

    private int idPrestamo;
    private int idUsuario;
    private String nombreUsuario;
    private int idDocumento;
    private String tituloDocumento;
    private Date fechaPrestamo;
    private Date fechaDevolucionEsperada;
    private Date fechaDevolucionReal;
    private String estado; // "Activo", "Devuelto", "Atrasado"

    // Constructor vacío
    public Prestamo() { }

    // Constructor para crear nuevo préstamo
    public Prestamo(int idUsuario, int idDocumento, Date fechaPrestamo, Date fechaDevolucionEsperada) {
        this.idUsuario = idUsuario;
        this.idDocumento = idDocumento;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.estado = "Activo";
    }

    // Constructor completo
    public Prestamo(int idPrestamo, int idUsuario, String nombreUsuario, int idDocumento,
                    String tituloDocumento, Date fechaPrestamo, Date fechaDevolucionEsperada,
                    Date fechaDevolucionReal, String estado) {
        this.idPrestamo = idPrestamo;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.idDocumento = idDocumento;
        this.tituloDocumento = tituloDocumento;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.fechaDevolucionReal = fechaDevolucionReal;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(int idDocumento) {
        this.idDocumento = idDocumento;
    }

    public String getTituloDocumento() {
        return tituloDocumento;
    }

    public void setTituloDocumento(String tituloDocumento) {
        this.tituloDocumento = tituloDocumento;
    }

    public Date getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(Date fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public Date getFechaDevolucionEsperada() {
        return fechaDevolucionEsperada;
    }

    public void setFechaDevolucionEsperada(Date fechaDevolucionEsperada) {
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
    }

    public Date getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    public void setFechaDevolucionReal(Date fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Prestamo{" +
                "idPrestamo=" + idPrestamo +
                ", usuario='" + nombreUsuario + '\'' +
                ", documento='" + tituloDocumento + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
