package com.tupojecto.biblioteca.vista;

import com.tupojecto.biblioteca.dao.DocumentoDAO;
import com.tupojecto.biblioteca.modelo.Documento;
import com.tupojecto.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ConsultaDocumentosVista {
    private JPanel panelPrincipal;
    private JTable tablaDocumentos;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private DocumentoDAO documentoDAO;
    private Usuario usuarioActual;

    public ConsultaDocumentosVista(Usuario usuario) {
        this.usuarioActual = usuario;
        this.documentoDAO = new DocumentoDAO();
        inicializarComponentes();
        cargarDocumentos();
    }

    private void inicializarComponentes() {
        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout(5, 5));
        JLabel lblTitulo = new JLabel("Consulta de Documentos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelSuperior.add(lblTitulo, BorderLayout.NORTH);

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("Buscar por título:"));
        txtBuscar = new JTextField(20);
        panelBusqueda.add(txtBuscar);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarDocumentos());
        panelBusqueda.add(btnBuscar);

        JButton btnMostrarTodos = new JButton("Mostrar Todos");
        btnMostrarTodos.addActionListener(e -> cargarDocumentos());
        panelBusqueda.add(btnMostrarTodos);

        panelSuperior.add(panelBusqueda, BorderLayout.CENTER);
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // Tabla de documentos
        String[] columnas = {"ID", "Título", "Autor", "Tipo", "Año", "Editorial", "Disponibles/Total", "Prestable"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaDocumentos = new JTable(modeloTabla);
        tablaDocumentos.getColumnModel().getColumn(1).setPreferredWidth(200);
        JScrollPane scrollPane = new JScrollPane(tablaDocumentos);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Panel de información
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblInfo = new JLabel("Haga clic en un documento para ver más detalles");
        panelInfo.add(lblInfo);
        panelPrincipal.add(panelInfo, BorderLayout.SOUTH);
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
                        d.getEditorial(),
                        d.getCantidadDisponible() + "/" + d.getCantidadTotal(),
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

    private void buscarDocumentos() {
        String termino = txtBuscar.getText().trim();

        if (termino.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "Ingrese un término de búsqueda",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        modeloTabla.setRowCount(0);
        try {
            List<Documento> documentos = documentoDAO.buscarPorTitulo(termino);

            if (documentos.isEmpty()) {
                JOptionPane.showMessageDialog(panelPrincipal,
                        "No se encontraron documentos con ese título",
                        "Sin resultados",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (Documento d : documentos) {
                Object[] fila = {
                        d.getIdDocumento(),
                        d.getTitulo(),
                        d.getAutor(),
                        d.getNombreTipo(),
                        d.getAnioPublicacion(),
                        d.getEditorial(),
                        d.getCantidadDisponible() + "/" + d.getCantidadTotal(),
                        d.isEsPrestable() ? "Sí" : "No"
                };
                modeloTabla.addRow(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "Error al buscar documentos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }
}
