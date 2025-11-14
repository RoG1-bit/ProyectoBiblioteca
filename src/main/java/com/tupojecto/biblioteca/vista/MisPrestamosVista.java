package com.tupojecto.biblioteca.vista;

import com.tupojecto.biblioteca.dao.PrestamoDAO;
import com.tupojecto.biblioteca.modelo.Prestamo;
import com.tupojecto.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MisPrestamosVista {
    private JPanel panelPrincipal;
    private JTable tablaPrestamos;
    private DefaultTableModel modeloTabla;
    private PrestamoDAO prestamoDAO;
    private Usuario usuarioActual;

    public MisPrestamosVista(Usuario usuario) {
        this.usuarioActual = usuario;
        this.prestamoDAO = new PrestamoDAO();
        inicializarComponentes();
        cargarMisPrestamos();
    }

    private void inicializarComponentes() {
        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        JLabel lblTitulo = new JLabel("Mis Préstamos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Tabla de préstamos
        String[] columnas = {"ID", "Documento", "Fecha Préstamo", "Fecha Vence", "Fecha Devolución", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaPrestamos = new JTable(modeloTabla);
        tablaPrestamos.getColumnModel().getColumn(1).setPreferredWidth(250);
        JScrollPane scrollPane = new JScrollPane(tablaPrestamos);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Panel de información
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información"));

        if (usuarioActual.isTieneMora()) {
            JLabel lblMora = new JLabel("⚠ Usted tiene mora pendiente de pago");
            lblMora.setForeground(Color.RED);
            lblMora.setFont(new Font("Arial", Font.BOLD, 14));
            panelInfo.add(lblMora);
        }

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarMisPrestamos());
        panelInfo.add(btnActualizar);

        panelPrincipal.add(panelInfo, BorderLayout.SOUTH);
    }

    private void cargarMisPrestamos() {
        modeloTabla.setRowCount(0);
        try {
            List<Prestamo> prestamos = prestamoDAO.listarPrestamosPorUsuario(usuarioActual.getIdUsuario());

            if (prestamos.isEmpty()) {
                JOptionPane.showMessageDialog(panelPrincipal,
                        "No tiene préstamos registrados",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (Prestamo p : prestamos) {
                // Determinar estado real
                String estado = p.getEstado();
                if (estado.equals("Activo")) {
                    long hoy = System.currentTimeMillis();
                    long fechaVence = p.getFechaDevolucionEsperada().getTime();
                    if (fechaVence < hoy) {
                        estado = "ATRASADO";
                    }
                }

                Object[] fila = {
                        p.getIdPrestamo(),
                        p.getTituloDocumento(),
                        p.getFechaPrestamo(),
                        p.getFechaDevolucionEsperada(),
                        p.getFechaDevolucionReal() != null ? p.getFechaDevolucionReal() : "N/A",
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

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }
}
