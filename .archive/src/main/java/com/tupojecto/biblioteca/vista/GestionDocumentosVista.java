package com.tupojecto.biblioteca.vista;

import com.tupojecto.biblioteca.dao.DocumentoDAO;
import com.tupojecto.biblioteca.modelo.Documento;
import com.tupojecto.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionDocumentosVista {
    private JPanel panelPrincipal;
    private JTable tablaDocumentos;
    private DefaultTableModel modeloTabla;
    private DocumentoDAO documentoDAO;
    private Usuario usuarioActual;

    public GestionDocumentosVista(Usuario usuario) {
        this.usuarioActual = usuario;
        this.documentoDAO = new DocumentoDAO();
        inicializarComponentes();
        cargarDocumentos();
    }

    private void inicializarComponentes() {
        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        JLabel lblTitulo = new JLabel("Gestión de Documentos (Ejemplares)");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Tabla de documentos
        String[] columnas = {"ID", "Título", "Autor", "Tipo", "Año", "Total", "Disponibles", "Prestable"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaDocumentos = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaDocumentos);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnNuevo = new JButton("Nuevo Documento");
        btnNuevo.addActionListener(e -> nuevoDocumento());

        JButton btnConfigPrestable = new JButton("Configurar Prestable");
        btnConfigPrestable.addActionListener(e -> configurarPrestable());

        JButton btnActualizar = new JButton("Actualizar Lista");
        btnActualizar.addActionListener(e -> cargarDocumentos());

        panelBotones.add(btnNuevo);
        panelBotones.add(btnConfigPrestable);
        panelBotones.add(btnActualizar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDocumentos() {
        modeloTabla.setRowCount(0);
        try {
            List<Documento> documentos = documentoDAO.listarTodos();
            for (Documento d : documentos) {
                Object[] fila = {
                        d.getIdDocumento(),
                        d.getTitulo(),
                        d.getAutor(),
                        d.getNombreTipo(),
                        d.getAnioPublicacion(),
                        d.getCantidadTotal(),
                        d.getCantidadDisponible(),
                        d.isEsPrestable() ? "Sí" : "No"
                };
                modeloTabla.addRow(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "Error al cargar documentos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void nuevoDocumento() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));

        JTextField txtTitulo = new JTextField();
        JTextField txtAutor = new JTextField();
        String[] tipos = {"1 - Libro", "2 - Revista", "3 - Tesis", "4 - CD", "5 - Documento"};
        JComboBox<String> cmbTipo = new JComboBox<>(tipos);
        JTextField txtAnio = new JTextField();
        JTextField txtEditorial = new JTextField();
        JTextField txtIsbn = new JTextField();
        JTextField txtCantidad = new JTextField();
        JTextField txtUbicacion = new JTextField();

        panel.add(new JLabel("Título:*"));
        panel.add(txtTitulo);
        panel.add(new JLabel("Autor:*"));
        panel.add(txtAutor);
        panel.add(new JLabel("Tipo:*"));
        panel.add(cmbTipo);
        panel.add(new JLabel("Año:"));
        panel.add(txtAnio);
        panel.add(new JLabel("Editorial:"));
        panel.add(txtEditorial);
        panel.add(new JLabel("ISBN:"));
        panel.add(txtIsbn);
        panel.add(new JLabel("Cantidad:*"));
        panel.add(txtCantidad);
        panel.add(new JLabel("Ubicación:*"));
        panel.add(txtUbicacion);

        int resultado = JOptionPane.showConfirmDialog(panelPrincipal, panel,
                "Nuevo Documento", JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                String titulo = txtTitulo.getText().trim();
                String autor = txtAutor.getText().trim();
                String tipoSeleccionado = (String) cmbTipo.getSelectedItem();
                int idTipo = Integer.parseInt(tipoSeleccionado.substring(0, 1));
                int anio = txtAnio.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtAnio.getText().trim());
                String editorial = txtEditorial.getText().trim();
                String isbn = txtIsbn.getText().trim();
                int cantidad = Integer.parseInt(txtCantidad.getText().trim());
                String ubicacion = txtUbicacion.getText().trim();

                if (titulo.isEmpty() || autor.isEmpty() || cantidad <= 0 || ubicacion.isEmpty()) {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "Complete los campos obligatorios (*)",
                            "Advertencia",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Documento nuevoDoc = new Documento(titulo, autor, idTipo, anio, editorial,
                        isbn, cantidad, ubicacion);

                if (documentoDAO.insertarDocumento(nuevoDoc)) {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "Documento creado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    cargarDocumentos();
                } else {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "Error al crear documento",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(panelPrincipal,
                        "Año y Cantidad deben ser números válidos",
                        "Error de formato",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(panelPrincipal,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void configurarPrestable() {
        int filaSeleccionada = tablaDocumentos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "Por favor seleccione un documento",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idDocumento = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String titulo = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
        String prestableActual = (String) modeloTabla.getValueAt(filaSeleccionada, 7);

        String[] opciones = {"Prestable", "No Prestable"};
        int seleccion = JOptionPane.showOptionDialog(panelPrincipal,
                "Configurar préstamo para: " + titulo,
                "Configurar Prestable",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[prestableActual.equals("Sí") ? 0 : 1]);

        if (seleccion != -1) {
            boolean esPrestable = seleccion == 0;
            try {
                if (documentoDAO.actualizarPrestable(idDocumento, esPrestable)) {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "Configuración actualizada",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    cargarDocumentos();
                } else {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "Error al actualizar configuración",
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
