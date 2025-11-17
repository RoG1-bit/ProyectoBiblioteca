package com.tupojecto.biblioteca.vista;

import com.tupojecto.biblioteca.dao.PrestamoDAO;
import com.tupojecto.biblioteca.modelo.Prestamo;
import com.tupojecto.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista de reportes básicos del sistema.
 * Incluye reportes de préstamos activos y préstamos atrasados.
 */
public class ReportesVista {

    private JPanel panelPrincipal;
    private JTabbedPane tabs;
    private JTable tablaActivos;
    private JTable tablaAtrasados;

    private final Usuario usuarioActual;
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    public ReportesVista(Usuario usuario) {
        this.usuarioActual = usuario;
        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tabs = new JTabbedPane();
        tablaActivos = new JTable();
        tablaAtrasados = new JTable();

        tabs.addTab("Préstamos Activos", new JScrollPane(tablaActivos));
        tabs.addTab("Préstamos Atrasados", new JScrollPane(tablaAtrasados));

        panelPrincipal.add(tabs, BorderLayout.CENTER);
    }

    private void cargarDatos() {
        // Activos
        List<Prestamo> activos = prestamoDAO.listarPrestamosActivos();
        tablaActivos.setModel(crearModeloDesdePrestamos(activos));

        // Atrasados
        List<Prestamo> atrasados = prestamoDAO.listarPrestamosAtrasados();
        tablaAtrasados.setModel(crearModeloDesdePrestamos(atrasados));
    }

    private DefaultTableModel crearModeloDesdePrestamos(List<Prestamo> lista) {
        String[] cols = {"ID", "Usuario", "Documento", "Fecha Préstamo", "Vence", "Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Prestamo p : lista) {
            model.addRow(new Object[]{
                    p.getIdPrestamo(),
                    p.getNombreUsuario(),
                    p.getTituloDocumento(),
                    p.getFechaPrestamo(),
                    p.getFechaDevolucionEsperada(),
                    p.getEstado()
            });
        }
        return model;
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }
}
