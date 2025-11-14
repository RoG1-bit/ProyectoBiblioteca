package com.tupojecto.biblioteca.dao;

import com.tupojecto.biblioteca.modelo.Documento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentoDAO {

    // Insertar nuevo documento
    public boolean insertarDocumento(Documento documento) {
        String sql = "INSERT INTO documentos (titulo, autor, id_tipo, anio_publicacion, editorial, isbn, " +
                     "cantidad_total, cantidad_disponible, ubicacion_fisica, es_prestable) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, documento.getTitulo());
            stmt.setString(2, documento.getAutor());
            stmt.setInt(3, documento.getIdTipo());
            stmt.setInt(4, documento.getAnioPublicacion());
            stmt.setString(5, documento.getEditorial());
            stmt.setString(6, documento.getIsbn());
            stmt.setInt(7, documento.getCantidadTotal());
            stmt.setInt(8, documento.getCantidadDisponible());
            stmt.setString(9, documento.getUbicacionFisica());
            stmt.setBoolean(10, documento.isEsPrestable());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar documento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Buscar documento por ID
    public Documento buscarPorId(int idDocumento) {
        String sql = "SELECT d.*, t.nombre_tipo FROM documentos d " +
                     "JOIN tipos_documento t ON d.id_tipo = t.id_tipo " +
                     "WHERE d.id_documento = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDocumento);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return crearDocumentoDesdeResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar documento: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Buscar documentos por título (búsqueda parcial)
    public List<Documento> buscarPorTitulo(String titulo) {
        List<Documento> documentos = new ArrayList<>();
        String sql = "SELECT d.*, t.nombre_tipo FROM documentos d " +
                     "JOIN tipos_documento t ON d.id_tipo = t.id_tipo " +
                     "WHERE d.titulo LIKE ? ORDER BY d.titulo";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + titulo + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                documentos.add(crearDocumentoDesdeResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar documentos: " + e.getMessage());
            e.printStackTrace();
        }

        return documentos;
    }

    // Listar todos los documentos
    public List<Documento> listarTodos() {
        List<Documento> documentos = new ArrayList<>();
        String sql = "SELECT d.*, t.nombre_tipo FROM documentos d " +
                     "JOIN tipos_documento t ON d.id_tipo = t.id_tipo " +
                     "ORDER BY d.titulo";

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                documentos.add(crearDocumentoDesdeResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar documentos: " + e.getMessage());
            e.printStackTrace();
        }

        return documentos;
    }

    // Listar documentos disponibles para préstamo
    public List<Documento> listarDisponibles() {
        List<Documento> documentos = new ArrayList<>();
        String sql = "SELECT d.*, t.nombre_tipo FROM documentos d " +
                     "JOIN tipos_documento t ON d.id_tipo = t.id_tipo " +
                     "WHERE d.cantidad_disponible > 0 AND d.es_prestable = TRUE " +
                     "ORDER BY d.titulo";

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                documentos.add(crearDocumentoDesdeResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar documentos disponibles: " + e.getMessage());
            e.printStackTrace();
        }

        return documentos;
    }

    // Actualizar cantidad disponible (para préstamos y devoluciones)
    public boolean actualizarCantidadDisponible(int idDocumento, int nuevaCantidad) {
        String sql = "UPDATE documentos SET cantidad_disponible = ? WHERE id_documento = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nuevaCantidad);
            stmt.setInt(2, idDocumento);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar cantidad: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Actualizar si es prestable
    public boolean actualizarPrestable(int idDocumento, boolean esPrestable) {
        String sql = "UPDATE documentos SET es_prestable = ? WHERE id_documento = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, esPrestable);
            stmt.setInt(2, idDocumento);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar prestable: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Actualizar documento completo
    public boolean actualizarDocumento(Documento documento) {
        String sql = "UPDATE documentos SET titulo = ?, autor = ?, id_tipo = ?, anio_publicacion = ?, " +
                     "editorial = ?, isbn = ?, cantidad_total = ?, ubicacion_fisica = ?, es_prestable = ? " +
                     "WHERE id_documento = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, documento.getTitulo());
            stmt.setString(2, documento.getAutor());
            stmt.setInt(3, documento.getIdTipo());
            stmt.setInt(4, documento.getAnioPublicacion());
            stmt.setString(5, documento.getEditorial());
            stmt.setString(6, documento.getIsbn());
            stmt.setInt(7, documento.getCantidadTotal());
            stmt.setString(8, documento.getUbicacionFisica());
            stmt.setBoolean(9, documento.isEsPrestable());
            stmt.setInt(10, documento.getIdDocumento());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar documento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método auxiliar para crear objeto Documento desde ResultSet
    private Documento crearDocumentoDesdeResultSet(ResultSet rs) throws SQLException {
        Documento doc = new Documento();
        doc.setIdDocumento(rs.getInt("id_documento"));
        doc.setTitulo(rs.getString("titulo"));
        doc.setAutor(rs.getString("autor"));
        doc.setIdTipo(rs.getInt("id_tipo"));
        doc.setNombreTipo(rs.getString("nombre_tipo"));
        doc.setAnioPublicacion(rs.getInt("anio_publicacion"));
        doc.setEditorial(rs.getString("editorial"));
        doc.setIsbn(rs.getString("isbn"));
        doc.setCantidadTotal(rs.getInt("cantidad_total"));
        doc.setCantidadDisponible(rs.getInt("cantidad_disponible"));
        doc.setUbicacionFisica(rs.getString("ubicacion_fisica"));
        doc.setEsPrestable(rs.getBoolean("es_prestable"));
        doc.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        return doc;
    }
}
