package com.tupojecto.biblioteca.vista;

import com.tupojecto.biblioteca.dao.MoraDAO;
import com.tupojecto.biblioteca.dao.PrestamoDAO;
import com.tupojecto.biblioteca.modelo.Prestamo;
import com.tupojecto.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * Pantalla para calcular moras de préstamos atrasados.
 */
public class CalculoMorasVista {

    private JPanel panelPrincipal;
    private JTable tablaMoras;
    private JButton btnRecalcular;
    private JButton btnActualizarEstados;
    private JLabel lblResumen;

    private final Usuario usuarioActual;
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final MoraDAO moraDAO = new MoraDAO();

    public CalculoMorasVista(Usuario usuario) {
        this.usuarioActual = usuario;
        inicializarComponentes();
        btnRecalcular.addActionListener(e -> cargarDatos());
        btnActualizarEstados.addActionListener(e -> actualizarEstados());
        cargarDatos();
    }

    private void inicializarComponentes() {
        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tablaMoras = new JTable();
        JScrollPane scroll = new JScrollPane(tablaMoras);
        panelPrincipal.add(scroll, BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRecalcular = new JButton("Recalcular");
        btnActualizarEstados = new JButton("Actualizar estados de mora");
        botones.add(btnRecalcular);
        botones.add(btnActualizarEstados);
        lblResumen = new JLabel(" ");
        sur.add(lblResumen, BorderLayout.WEST);
        sur.add(botones, BorderLayout.EAST);
        panelPrincipal.add(sur, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        List<Prestamo> atrasados = prestamoDAO.listarPrestamosAtrasados();
        Map<Integer, Double> moras = moraDAO.calcularTodasLasMoras();

        String[] cols = {"ID Préstamo", "Usuario", "Documento", "Vence", "Días atraso", "Mora (USD)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        double total = 0.0;
        for (Prestamo p : atrasados) {
            double mora = moras.getOrDefault(p.getIdPrestamo(), 0.0);
            int dias = calcularDiasAtraso(p.getFechaDevolucionEsperada());
            model.addRow(new Object[]{
                    p.getIdPrestamo(),
                    p.getNombreUsuario(),
                    p.getTituloDocumento(),
                    p.getFechaDevolucionEsperada(),
                    dias,
                    String.format("%.2f", mora)
            });
            total += mora;
        }
        tablaMoras.setModel(model);
        lblResumen.setText("Préstamos atrasados: " + atrasados.size() + " — Mora total estimada: $" + String.format("%.2f", total));
    }

    private int calcularDiasAtraso(Date fechaVence) {
        if (fechaVence == null) return 0;
        long hoy = System.currentTimeMillis();
        long fin = fechaVence.getTime();
        if (hoy <= fin) return 0;
        long diff = hoy - fin;
        return (int) (diff / (1000L * 60 * 60 * 24));
    }

    private void actualizarEstados() {
        boolean ok = moraDAO.actualizarEstadosMoraUsuarios();
        if (ok) {
            JOptionPane.showMessageDialog(panelPrincipal, "Estados de mora actualizados.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(panelPrincipal, "No fue posible actualizar estados.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }
}
