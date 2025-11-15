package com.tupojecto.biblioteca.vista;

import com.tupojecto.biblioteca.dao.ConexionDB;
import com.tupojecto.biblioteca.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 * Pantalla para configurar parámetros de préstamos de la biblioteca.
 * Lee/guarda desde la tabla configuracion_prestamos cuando está disponible.
 */
public class ConfiguracionPrestamosVista {

    private JPanel panelPrincipal;
    private JSpinner spDiasPrestamo;
    private JSpinner spMoraDiaria;
    private JSpinner spMaxPrestamos;
    private JButton btnCargar;
    private JButton btnGuardar;

    private final Usuario usuarioActual; // por si se requiere control de permisos a futuro

    public ConfiguracionPrestamosVista(Usuario usuario) {
        this.usuarioActual = usuario;
        inicializarComponentes();
        btnCargar.addActionListener(e -> cargarConfiguracion());
        btnGuardar.addActionListener(e -> guardarConfiguracion());
        // Cargar al abrir
        cargarConfiguracion();
    }

    private void inicializarComponentes() {
        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        // Días de préstamo
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Días de préstamo por defecto:"), gbc);
        spDiasPrestamo = new JSpinner(new SpinnerNumberModel(7, 1, 90, 1));
        gbc.gridx = 1; form.add(spDiasPrestamo, gbc);
        row++;

        // Mora diaria
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Mora diaria (USD):"), gbc);
        spMoraDiaria = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 20.0, 0.1));
        gbc.gridx = 1; form.add(spMoraDiaria, gbc);
        row++;

        // Máximo de préstamos simultáneos
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Máximo préstamos activos por usuario:"), gbc);
        spMaxPrestamos = new JSpinner(new SpinnerNumberModel(3, 1, 20, 1));
        gbc.gridx = 1; form.add(spMaxPrestamos, gbc);

        panelPrincipal.add(form, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCargar = new JButton("Cargar");
        btnGuardar = new JButton("Guardar");
        botones.add(btnCargar);
        botones.add(btnGuardar);
        panelPrincipal.add(botones, BorderLayout.SOUTH);
    }

    private void cargarConfiguracion() {
        String sql = "SELECT dias_prestamo, mora_diaria, max_prestamos FROM configuracion_prestamos LIMIT 1";
        boolean reintentado = false;
        while (true) {
            try (Connection conn = ConexionDB.getConnection();
                 Statement st = conn.createStatement()) {
                // Asegurar que el esquema existe antes de consultar
                asegurarEsquema(conn);

                try (ResultSet rs = st.executeQuery(sql)) {
                    if (rs.next()) {
                        spDiasPrestamo.setValue(rs.getInt("dias_prestamo"));
                        spMoraDiaria.setValue(rs.getDouble("mora_diaria"));
                        spMaxPrestamos.setValue(rs.getInt("max_prestamos"));
                    } else {
                        // Si no hay fila, crear una por defecto
                        crearFilaPorDefecto(conn);
                        JOptionPane.showMessageDialog(panelPrincipal, "No había configuración, se creó una por defecto.");
                    }
                }
                break; // OK
            } catch (SQLException ex) {
                String msg = ex.getMessage() == null ? "" : ex.getMessage();
                if (!reintentado && msg.toLowerCase().contains("unknown column") && msg.toLowerCase().contains("max_prestamos")) {
                    // Intento de autocorrección del esquema y reintento una vez
                    reintentado = true;
                    try (Connection fixConn = ConexionDB.getConnection()) {
                        asegurarEsquema(fixConn);
                    } catch (SQLException ignore) { }
                    continue;
                }
                JOptionPane.showMessageDialog(panelPrincipal,
                        "No se pudo cargar la configuración.\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                break;
            }
        }
    }

    private void crearFilaPorDefecto(Connection conn) throws SQLException {
        String insert = "INSERT INTO configuracion_prestamos (dias_prestamo, mora_diaria, max_prestamos) VALUES (7, 0.5, 3)";
        try (Statement s = conn.createStatement()) {
            s.executeUpdate(insert);
        }
    }

    private void guardarConfiguracion() {
        String existeSql = "SELECT COUNT(*) FROM configuracion_prestamos";
        String update = "UPDATE configuracion_prestamos SET dias_prestamo=?, mora_diaria=?, max_prestamos=?";
        String insert = "INSERT INTO configuracion_prestamos (dias_prestamo, mora_diaria, max_prestamos) VALUES (?,?,?)";

        try (Connection conn = ConexionDB.getConnection()) {
            // Asegurar esquema antes de guardar
            asegurarEsquema(conn);
            int count;
            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(existeSql)) {
                rs.next();
                count = rs.getInt(1);
            }

            if (count > 0) {
                try (PreparedStatement ps = conn.prepareStatement(update)) {
                    ps.setInt(1, (Integer) spDiasPrestamo.getValue());
                    ps.setDouble(2, (Double) spMoraDiaria.getValue());
                    ps.setInt(3, (Integer) spMaxPrestamos.getValue());
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(insert)) {
                    ps.setInt(1, (Integer) spDiasPrestamo.getValue());
                    ps.setDouble(2, (Double) spMoraDiaria.getValue());
                    ps.setInt(3, (Integer) spMaxPrestamos.getValue());
                    ps.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(panelPrincipal, "Configuración guardada correctamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "No se pudo guardar la configuración.\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Garantiza que la tabla y columna necesarias existan y que haya una fila de configuración
    private void asegurarEsquema(Connection conn) throws SQLException {
        // 1) Crear tabla si no existe
        String crearTabla = "CREATE TABLE IF NOT EXISTS configuracion_prestamos (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "dias_prestamo INT NOT NULL DEFAULT 7, " +
                "mora_diaria DOUBLE NOT NULL DEFAULT 0.5, " +
                "max_prestamos INT NOT NULL DEFAULT 3" +
                ")";
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(crearTabla);
        }

        // 2) Verificar columna max_prestamos
        String existeCol = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'configuracion_prestamos' AND COLUMN_NAME = 'max_prestamos'";
        boolean tieneColumna;
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(existeCol)) {
            rs.next();
            tieneColumna = rs.getInt(1) > 0;
        }
        if (!tieneColumna) {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("ALTER TABLE configuracion_prestamos ADD COLUMN max_prestamos INT NOT NULL DEFAULT 3");
            }
        }

        // 3) Asegurar que exista al menos una fila
        int count;
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM configuracion_prestamos")) {
            rs.next();
            count = rs.getInt(1);
        }
        if (count == 0) {
            crearFilaPorDefecto(conn);
        }
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }
}
