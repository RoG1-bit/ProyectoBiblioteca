package com.tupojecto.biblioteca.vista;

import com.tupojecto.biblioteca.dao.DocumentoDAO;
import com.tupojecto.biblioteca.dao.PrestamoDAO;
import com.tupojecto.biblioteca.modelo.Documento;
import com.tupojecto.biblioteca.modelo.Prestamo;
import com.tupojecto.biblioteca.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

public class SolicitudPrestamoVista {
    private JPanel panelPrincipal;
    private JComboBox<String> cmbDocumentos;
    private JTextField txtDiasPrestamo;
    private DocumentoDAO documentoDAO;
    private PrestamoDAO prestamoDAO;
    private Usuario usuarioActual;

    public SolicitudPrestamoVista(Usuario usuario) {
        this.usuarioActual = usuario;
        this.documentoDAO = new DocumentoDAO();
        this.prestamoDAO = new PrestamoDAO();
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel lblTitulo = new JLabel("Solicitar Préstamo de Documento");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Panel central con formulario
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelFormulario.add(new JLabel("Usuario:"));
        panelFormulario.add(new JLabel(usuarioActual.getNombre()));

        panelFormulario.add(new JLabel("Seleccione documento:"));
        cmbDocumentos = new JComboBox<>();
        cargarDocumentosDisponibles();
        panelFormulario.add(cmbDocumentos);

        panelFormulario.add(new JLabel("Días de préstamo:"));
        txtDiasPrestamo = new JTextField("7");
        txtDiasPrestamo.setEditable(false);
        panelFormulario.add(txtDiasPrestamo);

        panelFormulario.add(new JLabel(""));
        panelFormulario.add(new JLabel(""));

        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnSolicitar = new JButton("Solicitar Préstamo");
        btnSolicitar.addActionListener(e -> solicitarPrestamo());

        JButton btnActualizar = new JButton("Actualizar Lista");
        btnActualizar.addActionListener(e -> cargarDocumentosDisponibles());

        panelBotones.add(btnSolicitar);
        panelBotones.add(btnActualizar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        // Advertencia si tiene mora
        if (usuarioActual.isTieneMora()) {
            JPanel panelAdvertencia = new JPanel();
            JLabel lblAdvertencia = new JLabel("⚠ ADVERTENCIA: Usted tiene mora pendiente");
            lblAdvertencia.setForeground(Color.RED);
            lblAdvertencia.setFont(new Font("Arial", Font.BOLD, 14));
            panelAdvertencia.add(lblAdvertencia);
            panelPrincipal.add(panelAdvertencia, BorderLayout.PAGE_START);
        }
    }

    private void cargarDocumentosDisponibles() {
        cmbDocumentos.removeAllItems();
        try {
            List<Documento> documentos = documentoDAO.listarDisponibles();

            if (documentos.isEmpty()) {
                cmbDocumentos.addItem("No hay documentos disponibles");
            } else {
                for (Documento d : documentos) {
                    cmbDocumentos.addItem(d.getIdDocumento() + " - " + d.getTitulo() +
                            " (" + d.getCantidadDisponible() + " disponibles)");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "Error al cargar documentos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void solicitarPrestamo() {
        String documentoSeleccionado = (String) cmbDocumentos.getSelectedItem();

        if (documentoSeleccionado == null || documentoSeleccionado.equals("No hay documentos disponibles")) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "No hay documentos disponibles para préstamo",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Advertir si tiene mora
        if (usuarioActual.isTieneMora()) {
            int confirmacion = JOptionPane.showConfirmDialog(panelPrincipal,
                    "Usted tiene mora pendiente.\n¿Desea solicitar el préstamo de todos modos?",
                    "Usuario con mora",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            int idDocumento = Integer.parseInt(documentoSeleccionado.split(" - ")[0]);
            int diasPrestamo = Integer.parseInt(txtDiasPrestamo.getText());

            // Crear fechas
            Date fechaPrestamo = new Date(System.currentTimeMillis());
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaPrestamo);
            cal.add(Calendar.DAY_OF_MONTH, diasPrestamo);
            Date fechaDevolucion = new Date(cal.getTimeInMillis());

            // Crear préstamo
            Prestamo nuevoPrestamo = new Prestamo(usuarioActual.getIdUsuario(), idDocumento,
                    fechaPrestamo, fechaDevolucion);

            if (prestamoDAO.insertarPrestamo(nuevoPrestamo)) {
                JOptionPane.showMessageDialog(panelPrincipal,
                        "Préstamo solicitado exitosamente\n\n" +
                                "Fecha de devolución: " + fechaDevolucion + "\n" +
                                "Por favor devuelva el documento a tiempo para evitar moras.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                // Cerrar ventana
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(panelPrincipal);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(panelPrincipal,
                        "Error al solicitar préstamo\n" +
                                "Verifique que haya ejemplares disponibles.",
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

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }
}
