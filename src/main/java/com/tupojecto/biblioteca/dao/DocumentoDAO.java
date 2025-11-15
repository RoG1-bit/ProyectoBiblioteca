package com.tupojecto.biblioteca.dao;

import com.tupojecto.biblioteca.modelo.Documento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentoDAO {

    // Inserta los tipos de documento estándar si no existen
    private void asegurarTiposDocumento() {
        String[][] tipos = new String[][]{
                {"Libro", "Libros de texto, novelas, etc."},
                {"Revista", "Publicaciones periódicas"},
                {"Tesis", "Trabajos de investigación universitaria"},
                {"CD", "Discos compactos con contenido multimedia"},
                {"Documento", "Documentos varios de información"},
                {"Periódico", "Publicaciones diarias o semanales de noticias"},
                {"Mapa", "Representaciones geográficas de territorios"},
                {"DVD", "Discos de video digital para películas o datos"},
                {"Blu-ray", "Disco óptico de alta definición"},
                {"Audiolibro", "Grabaciones de libros leídos en voz alta"},
                {"Manuscrito", "Documentos históricos o textos escritos a mano"},
                {"Partitura", "Notación musical escrita"},
                {"Artículo Científico", "Publicación en una revista especializada o journal"},
                {"Enciclopedia", "Obra de referencia con conocimiento compendiado"},
                {"Software", "Programas de computadora o recursos digitales"}
        };

        // Usamos INSERT con ON DUPLICATE KEY para que sea idempotente
        String sql = "INSERT INTO tipos_documento (nombre_tipo, descripcion) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE nombre_tipo = nombre_tipo";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String[] t : tipos) {
                ps.setString(1, t[0]);
                ps.setString(2, t[1]);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException ignored) {
            // No interrumpir el flujo si falla el seed; las consultas podrán seguir
        }
    }

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
        asegurarTiposDocumento();
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
        asegurarTiposDocumento();
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

    // Buscar documentos por título que estén disponibles para préstamo
    public List<Documento> buscarPorTituloDisponibles(String titulo) {
        List<Documento> documentos = new ArrayList<>();
        asegurarTiposDocumento();
        // LEFT JOIN para no perder documentos si el tipo no existe o está inconsistente
        // COALESCE en cantidad_disponible para tolerar NULL (lo tratamos como cantidad_total)
        String sql = "SELECT d.*, t.nombre_tipo FROM documentos d " +
                     "LEFT JOIN tipos_documento t ON d.id_tipo = t.id_tipo " +
                     "WHERE d.titulo LIKE ? " +
                     "AND COALESCE(d.cantidad_disponible, d.cantidad_total, 0) > 0 " +
                     "AND COALESCE(d.es_prestable, 1) <> 0 " +
                     "ORDER BY d.titulo";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + titulo + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                documentos.add(crearDocumentoDesdeResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar documentos disponibles: " + e.getMessage());
            e.printStackTrace();
        }

        return documentos;
    }

    // Listar todos los documentos
    public List<Documento> listarTodos() {
        List<Documento> documentos = new ArrayList<>();
        asegurarTiposDocumento();
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
        asegurarTiposDocumento();
        // LEFT JOIN para no perder filas si el tipo quedó sin referenciar
        // COALESCE para tolerar cantidad_disponible NULL
        String sql = "SELECT d.*, t.nombre_tipo FROM documentos d " +
                     "LEFT JOIN tipos_documento t ON d.id_tipo = t.id_tipo " +
                     "WHERE COALESCE(d.cantidad_disponible, d.cantidad_total, 0) > 0 " +
                     "AND COALESCE(d.es_prestable, 1) <> 0 " +
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
