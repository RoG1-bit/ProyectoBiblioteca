package com.tupojecto.biblioteca.vista;

import com.tupojecto.biblioteca.dao.PrestamoDAO;
import com.tupojecto.biblioteca.modelo.Prestamo;
import com.tupojecto.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionDevolucionesVista {
    private JPanel panelPrincipal;
    private JTable tablaPrestamos;
    private DefaultTableModel modeloTabla;
    private PrestamoDAO prestamoDAO;
    private Usuario usuarioActual;

    public GestionDevolucionesVista(Usuario usuario) {
        this.usuarioActual = usuario;
        this.prestamoDAO = new PrestamoDAO();
        inicializarComponentes();
        cargarPrestamosActivos();
    }

    private void inicializarComponentes() {
        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        JLabel lblTitulo = new JLabel("Gestión de Devoluciones");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Tabla de préstamos activos
        String[] columnas = {"ID Préstamo", "Usuario", "Documento", "Fecha Préstamo", "Fecha Vence", "Estado"};
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

        JButton btnRegistrarDevolucion = new JButton("Registrar Devolución");
        btnRegistrarDevolucion.addActionListener(e -> registrarDevolucion());

        JButton btnActualizar = new JButton("Actualizar Lista");
        btnActualizar.addActionListener(e -> cargarPrestamosActivos());

        panelBotones.add(btnRegistrarDevolucion);
        panelBotones.add(btnActualizar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarPrestamosActivos() {
        modeloTabla.setRowCount(0);
        try {
            List<Prestamo> prestamos = prestamoDAO.listarPrestamosActivos();
            for (Prestamo p : prestamos) {
                // Determinar si está atrasado
                long hoy = System.currentTimeMillis();
                long fechaVence = p.getFechaDevolucionEsperada().getTime();
                String estado = fechaVence < hoy ? "ATRASADO" : "ACTIVO";

                Object[] fila = {
                        p.getIdPrestamo(),
                        p.getNombreUsuario(),
                        p.getTituloDocumento(),
                        p.getFechaPrestamo(),
                        p.getFechaDevolucionEsperada(),
                        estado
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

    private void registrarDevolucion() {
        int filaSeleccionada = tablaPrestamos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "Por favor seleccione un préstamo",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPrestamo = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String usuario = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
        String documento = (String) modeloTabla.getValueAt(filaSeleccionada, 2);
        String estado = (String) modeloTabla.getValueAt(filaSeleccionada, 5);

        String mensaje = "¿Confirma la devolución de:\n\n" +
                "Usuario: " + usuario + "\n" +
                "Documento: " + documento + "\n" +
                "Estado: " + estado;

        if (estado.equals("ATRASADO")) {
            mensaje += "\n\n¡ATENCIÓN! Este préstamo está atrasado.\n" +
                    "Se debe calcular la mora correspondiente.";
        }

        int confirmacion = JOptionPane.showConfirmDialog(panelPrincipal,
                mensaje,
                "Confirmar Devolución",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                if (prestamoDAO.registrarDevolucion(idPrestamo)) {
                    String mensajeExito = "Devolución registrada exitosamente";

                    if (estado.equals("ATRASADO")) {
                        mensajeExito += "\n\nRecuerde calcular y registrar la mora correspondiente.";
                    }

                    JOptionPane.showMessageDialog(panelPrincipal,
                            mensajeExito,
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    cargarPrestamosActivos();
                } else {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "Error al registrar devolución",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(panelPrincipal,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }
}
