package com.tupojecto.biblioteca.dao;

import com.tupojecto.biblioteca.modelo.Prestamo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    // Crear nuevo préstamo
    public boolean insertarPrestamo(Prestamo prestamo) {
        String sql = "INSERT INTO prestamos (id_usuario, id_documento, fecha_prestamo, " +
                     "fecha_devolucion_esperada, estado) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // Insertar préstamo
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, prestamo.getIdUsuario());
            stmt.setInt(2, prestamo.getIdDocumento());
            stmt.setDate(3, prestamo.getFechaPrestamo());
            stmt.setDate(4, prestamo.getFechaDevolucionEsperada());
            stmt.setString(5, prestamo.getEstado());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                // Actualizar cantidad disponible del documento
                String sqlUpdate = "UPDATE documentos SET cantidad_disponible = cantidad_disponible - 1 " +
                                   "WHERE id_documento = ? AND cantidad_disponible > 0";
                PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate);
                stmtUpdate.setInt(1, prestamo.getIdDocumento());
                int actualizados = stmtUpdate.executeUpdate();

                if (actualizados > 0) {
                    conn.commit(); // Confirmar transacción
                    return true;
                } else {
                    conn.rollback(); // Revertir si no hay ejemplares disponibles
                    System.err.println("No hay ejemplares disponibles");
                    return false;
                }
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Error al insertar préstamo: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Registrar devolución
    public boolean registrarDevolucion(int idPrestamo) {
        String sql = "UPDATE prestamos SET fecha_devolucion_real = ?, estado = 'Devuelto' " +
                     "WHERE id_prestamo = ?";

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            // Obtener id del documento antes de actualizar
            String sqlGetDoc = "SELECT id_documento FROM prestamos WHERE id_prestamo = ?";
            PreparedStatement stmtGet = conn.prepareStatement(sqlGetDoc);
            stmtGet.setInt(1, idPrestamo);
            ResultSet rs = stmtGet.executeQuery();

            if (rs.next()) {
                int idDocumento = rs.getInt("id_documento");

                // Actualizar préstamo
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setDate(1, new Date(System.currentTimeMillis()));
                stmt.setInt(2, idPrestamo);
                int filasAfectadas = stmt.executeUpdate();

                if (filasAfectadas > 0) {
                    // Incrementar cantidad disponible
                    String sqlUpdate = "UPDATE documentos SET cantidad_disponible = cantidad_disponible + 1 " +
                                       "WHERE id_documento = ?";
                    PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate);
                    stmtUpdate.setInt(1, idDocumento);
                    stmtUpdate.executeUpdate();

                    conn.commit();
                    return true;
                }
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Error al registrar devolución: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Listar préstamos activos de un usuario
    public List<Prestamo> listarPrestamosPorUsuario(int idUsuario) {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre as nombre_usuario, d.titulo as titulo_documento " +
                     "FROM prestamos p " +
                     "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                     "JOIN documentos d ON p.id_documento = d.id_documento " +
                     "WHERE p.id_usuario = ? ORDER BY p.fecha_prestamo DESC";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                prestamos.add(crearPrestamoDesdeResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar préstamos: " + e.getMessage());
            e.printStackTrace();
        }

        return prestamos;
    }

    // Listar todos los préstamos activos
    public List<Prestamo> listarPrestamosActivos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre as nombre_usuario, d.titulo as titulo_documento " +
                     "FROM prestamos p " +
                     "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                     "JOIN documentos d ON p.id_documento = d.id_documento " +
                     "WHERE p.estado = 'Activo' ORDER BY p.fecha_devolucion_esperada";

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                prestamos.add(crearPrestamoDesdeResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar préstamos activos: " + e.getMessage());
            e.printStackTrace();
        }

        return prestamos;
    }

    // Listar préstamos atrasados
    public List<Prestamo> listarPrestamosAtrasados() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre as nombre_usuario, d.titulo as titulo_documento " +
                     "FROM prestamos p " +
                     "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                     "JOIN documentos d ON p.id_documento = d.id_documento " +
                     "WHERE p.estado = 'Activo' AND p.fecha_devolucion_esperada < CURDATE() " +
                     "ORDER BY p.fecha_devolucion_esperada";

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                prestamos.add(crearPrestamoDesdeResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar préstamos atrasados: " + e.getMessage());
            e.printStackTrace();
        }

        return prestamos;
    }

    // Actualizar estado de préstamo
    public boolean actualizarEstado(int idPrestamo, String nuevoEstado) {
        String sql = "UPDATE prestamos SET estado = ? WHERE id_prestamo = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idPrestamo);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método auxiliar para crear objeto Prestamo desde ResultSet
    private Prestamo crearPrestamoDesdeResultSet(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo();
        prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
        prestamo.setIdUsuario(rs.getInt("id_usuario"));
        prestamo.setNombreUsuario(rs.getString("nombre_usuario"));
        prestamo.setIdDocumento(rs.getInt("id_documento"));
        prestamo.setTituloDocumento(rs.getString("titulo_documento"));
        prestamo.setFechaPrestamo(rs.getDate("fecha_prestamo"));
        prestamo.setFechaDevolucionEsperada(rs.getDate("fecha_devolucion_esperada"));
        prestamo.setFechaDevolucionReal(rs.getDate("fecha_devolucion_real"));
        prestamo.setEstado(rs.getString("estado"));
        return prestamo;
    }
}
