package com.tupojecto.biblioteca.dao;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MoraDAO {

    // Calcular mora de un préstamo específico
    public double calcularMoraPrestamo(int idPrestamo) {
        String sql = "SELECT p.fecha_devolucion_esperada, c.mora_diaria " +
                     "FROM prestamos p, configuracion_prestamos c " +
                     "WHERE p.id_prestamo = ? AND p.estado = 'Activo'";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPrestamo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Date fechaVence = rs.getDate("fecha_devolucion_esperada");
                double moraDiaria = rs.getDouble("mora_diaria");

                // Calcular días de atraso
                long hoy = System.currentTimeMillis();
                long fechaVenceMillis = fechaVence.getTime();

                if (hoy > fechaVenceMillis) {
                    long diferencia = hoy - fechaVenceMillis;
                    int diasMora = (int) (diferencia / (1000 * 60 * 60 * 24));
                    return diasMora * moraDiaria;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al calcular mora: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }

    // Calcular todas las moras pendientes del sistema
    public Map<Integer, Double> calcularTodasLasMoras() {
        Map<Integer, Double> moras = new HashMap<>();
        String sql = "SELECT p.id_prestamo, p.id_usuario, p.fecha_devolucion_esperada, c.mora_diaria " +
                     "FROM prestamos p, configuracion_prestamos c " +
                     "WHERE p.estado = 'Activo' AND p.fecha_devolucion_esperada < CURDATE()";

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int idPrestamo = rs.getInt("id_prestamo");
                Date fechaVence = rs.getDate("fecha_devolucion_esperada");
                double moraDiaria = rs.getDouble("mora_diaria");

                // Calcular días de atraso
                long hoy = System.currentTimeMillis();
                long fechaVenceMillis = fechaVence.getTime();
                long diferencia = hoy - fechaVenceMillis;
                int diasMora = (int) (diferencia / (1000 * 60 * 60 * 24));

                double montoMora = diasMora * moraDiaria;
                moras.put(idPrestamo, montoMora);
            }

        } catch (SQLException e) {
            System.err.println("Error al calcular moras: " + e.getMessage());
            e.printStackTrace();
        }

        return moras;
    }

    // Registrar mora en la base de datos
    public boolean registrarMora(int idPrestamo, int diasMora, double montoMora) {
        String sql = "INSERT INTO moras (id_prestamo, dias_mora, monto_mora) VALUES (?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPrestamo);
            stmt.setInt(2, diasMora);
            stmt.setDouble(3, montoMora);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar mora: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Actualizar estado de usuarios con mora
    public boolean actualizarEstadosMoraUsuarios() {
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            // Primero, limpiar todas las moras (poner en false)
            String sqlLimpiar = "UPDATE usuarios SET tiene_mora = FALSE";
            PreparedStatement stmtLimpiar = conn.prepareStatement(sqlLimpiar);
            stmtLimpiar.executeUpdate();

            // Luego, marcar usuarios con préstamos atrasados
            String sqlMarcar = "UPDATE usuarios u " +
                               "SET u.tiene_mora = TRUE " +
                               "WHERE u.id_usuario IN (" +
                               "    SELECT DISTINCT p.id_usuario " +
                               "    FROM prestamos p " +
                               "    WHERE p.estado = 'Activo' " +
                               "    AND p.fecha_devolucion_esperada < CURDATE()" +
                               ")";
            PreparedStatement stmtMarcar = conn.prepareStatement(sqlMarcar);
            stmtMarcar.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Error al actualizar estados de mora: " + e.getMessage());
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

    // Obtener configuración de mora
    public double obtenerMoraDiaria() {
        String sql = "SELECT mora_diaria FROM configuracion_prestamos LIMIT 1";

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("mora_diaria");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener mora diaria: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.50; // Valor por defecto
    }

    // Obtener total de mora de un usuario
    public double obtenerMoraTotalUsuario(int idUsuario) {
        String sql = "SELECT SUM(m.monto_mora) as total " +
                     "FROM moras m " +
                     "JOIN prestamos p ON m.id_prestamo = p.id_prestamo " +
                     "WHERE p.id_usuario = ? AND m.pagado = FALSE";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener mora total: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }
}
