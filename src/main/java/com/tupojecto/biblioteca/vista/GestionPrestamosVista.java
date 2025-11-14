package com.tupojecto.biblioteca.vista;

import com.tupojecto.biblioteca.dao.DocumentoDAO;
import com.tupojecto.biblioteca.dao.PrestamoDAO;
import com.tupojecto.biblioteca.dao.UsuarioDAO;
import com.tupojecto.biblioteca.modelo.Documento;
import com.tupojecto.biblioteca.modelo.Prestamo;
import com.tupojecto.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

public class GestionPrestamosVista {
    private JPanel panelPrincipal;
    private JTable tablaPrestamos;
    private DefaultTableModel modeloTabla;
    private PrestamoDAO prestamoDAO;
    private UsuarioDAO usuarioDAO;
    private DocumentoDAO documentoDAO;
    private Usuario usuarioActual;

    public GestionPrestamosVista(Usuario usuario) {
        this.usuarioActual = usuario;
        this.prestamoDAO = new PrestamoDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.documentoDAO = new DocumentoDAO();
        inicializarComponentes();
        cargarPrestamos();
    }

    private void inicializarComponentes() {
        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        JLabel lblTitulo = new JLabel("Gestión de Préstamos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Tabla de préstamos
        String[] columnas = {"ID", "Usuario", "Documento", "Fecha Préstamo", "Fecha Vence", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaPrestamos = new JTable(modeloTabla);
        tablaPrestamos.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaPrestamos.getColumnModel().getColumn(2).setPreferredWidth(200);
        JScrollPane scrollPane = new JScrollPane(tablaPrestamos);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnNuevoPrestamo = new JButton("Nuevo Préstamo");
        btnNuevoPrestamo.addActionListener(e -> nuevoPrestamo());

        JButton btnVerAtrasados = new JButton("Ver Atrasados");
        btnVerAtrasados.addActionListener(e -> verPrestamosAtrasados());

        JButton btnActualizar = new JButton("Actualizar Lista");
        btnActualizar.addActionListener(e -> cargarPrestamos());

        panelBotones.add(btnNuevoPrestamo);
        panelBotones.add(btnVerAtrasados);
        panelBotones.add(btnActualizar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarPrestamos() {
        modeloTabla.setRowCount(0);
        try {
            List<Prestamo> prestamos = prestamoDAO.listarPrestamosActivos();
            for (Prestamo p : prestamos) {
                Object[] fila = {
                        p.getIdPrestamo(),
                        p.getNombreUsuario(),
                        p.getTituloDocumento(),
                        p.getFechaPrestamo(),
                        p.getFechaDevolucionEsperada(),
                        p.getEstado()
                };
                modeloTabla.addRow(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "Error al cargar préstamos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void nuevoPrestamo() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));

        // Cargar usuarios
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        JComboBox<String> cmbUsuarios = new JComboBox<>();
        for (Usuario u : usuarios) {
            cmbUsuarios.addItem(u.getIdUsuario() + " - " + u.getNombre() +
                    (u.isTieneMora() ? " (TIENE MORA)" : ""));
        }

        // Cargar documentos disponibles
        List<Documento> documentos = documentoDAO.listarDisponibles();
        JComboBox<String> cmbDocumentos = new JComboBox<>();
        for (Documento d : documentos) {
            cmbDocumentos.addItem(d.getIdDocumento() + " - " + d.getTitulo() +
                    " (" + d.getCantidadDisponible() + " disponibles)");
        }

        JTextField txtDiasPrestamo = new JTextField("7");

        panel.add(new JLabel("Usuario:"));
        panel.add(cmbUsuarios);
        panel.add(new JLabel("Documento:"));
        panel.add(cmbDocumentos);
        panel.add(new JLabel("Días de préstamo:"));
        panel.add(txtDiasPrestamo);
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));

        int resultado = JOptionPane.showConfirmDialog(panelPrincipal, panel,
                "Nuevo Préstamo", JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                // Obtener IDs seleccionados
                String usuarioSeleccionado = (String) cmbUsuarios.getSelectedItem();
                String documentoSeleccionado = (String) cmbDocumentos.getSelectedItem();

                if (usuarioSeleccionado == null || documentoSeleccionado == null) {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "Seleccione usuario y documento",
                            "Advertencia",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int idUsuario = Integer.parseInt(usuarioSeleccionado.split(" - ")[0]);
                int idDocumento = Integer.parseInt(documentoSeleccionado.split(" - ")[0]);
                int diasPrestamo = Integer.parseInt(txtDiasPrestamo.getText());

                // Verificar si el usuario tiene mora
                Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
                if (usuario.isTieneMora()) {
                    int confirmacion = JOptionPane.showConfirmDialog(panelPrincipal,
                            "El usuario tiene mora. ¿Desea continuar de todos modos?",
                            "Usuario con mora",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmacion != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                // Crear fechas
                Date fechaPrestamo = new Date(System.currentTimeMillis());
                Calendar cal = Calendar.getInstance();
                cal.setTime(fechaPrestamo);
                cal.add(Calendar.DAY_OF_MONTH, diasPrestamo);
                Date fechaDevolucion = new Date(cal.getTimeInMillis());

                // Crear préstamo
                Prestamo nuevoPrestamo = new Prestamo(idUsuario, idDocumento, fechaPrestamo, fechaDevolucion);

                if (prestamoDAO.insertarPrestamo(nuevoPrestamo)) {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "Préstamo registrado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    cargarPrestamos();
                } else {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "Error al registrar préstamo (puede que no haya ejemplares disponibles)",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(panelPrincipal,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void verPrestamosAtrasados() {
        modeloTabla.setRowCount(0);
        try {
            List<Prestamo> prestamos = prestamoDAO.listarPrestamosAtrasados();

            if (prestamos.isEmpty()) {
                JOptionPane.showMessageDialog(panelPrincipal,
                        "No hay préstamos atrasados",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
                cargarPrestamos();
                return;
            }

            for (Prestamo p : prestamos) {
                Object[] fila = {
                        p.getIdPrestamo(),
                        p.getNombreUsuario(),
                        p.getTituloDocumento(),
                        p.getFechaPrestamo(),
                        p.getFechaDevolucionEsperada(),
                        "ATRASADO"
                };
                modeloTabla.addRow(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "Error al cargar préstamos atrasados: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }
}
