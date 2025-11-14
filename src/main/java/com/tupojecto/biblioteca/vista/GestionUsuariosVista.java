package com.tupojecto.biblioteca.vista;

import com.tupojecto.biblioteca.dao.UsuarioDAO;
import com.tupojecto.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionUsuariosVista {
    private JPanel panelPrincipal;
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioActual;

    public GestionUsuariosVista(Usuario usuario) {
        this.usuarioActual = usuario;
        this.usuarioDAO = new UsuarioDAO();
        inicializarComponentes();
        cargarUsuarios();
    }

    private void inicializarComponentes() {
        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        JLabel lblTitulo = new JLabel("Gestión de Usuarios");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Tabla de usuarios
        String[] columnas = {"ID", "Nombre", "Username", "Tipo", "Tiene Mora"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaUsuarios = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnNuevo = new JButton("Nuevo Usuario");
        btnNuevo.addActionListener(e -> nuevoUsuario());

        JButton btnResetearPassword = new JButton("Resetear Contraseña");
        btnResetearPassword.addActionListener(e -> resetearPassword());

        JButton btnActualizar = new JButton("Actualizar Lista");
        btnActualizar.addActionListener(e -> cargarUsuarios());

        panelBotones.add(btnNuevo);
        panelBotones.add(btnResetearPassword);
        panelBotones.add(btnActualizar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarUsuarios() {
        modeloTabla.setRowCount(0);
        try {
            List<Usuario> usuarios = usuarioDAO.listarTodos();
            for (Usuario u : usuarios) {
                Object[] fila = {
                        u.getIdUsuario(),
                        u.getNombre(),
                        u.getUsername(),
                        u.getTipoUsuario(),
                        u.isTieneMora() ? "Sí" : "No"
                };
                modeloTabla.addRow(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "Error al cargar usuarios: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void nuevoUsuario() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));

        JTextField txtNombre = new JTextField();
        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        String[] tipos = {"Administrador", "Profesor", "Alumno"};
        JComboBox<String> cmbTipo = new JComboBox<>(tipos);

        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Username:"));
        panel.add(txtUsername);
        panel.add(new JLabel("Contraseña:"));
        panel.add(txtPassword);
        panel.add(new JLabel("Tipo Usuario:"));
        panel.add(cmbTipo);

        int resultado = JOptionPane.showConfirmDialog(panelPrincipal, panel,
                "Nuevo Usuario", JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            String nombre = txtNombre.getText().trim();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());
            String tipo = (String) cmbTipo.getSelectedItem();

            if (nombre.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(panelPrincipal,
                        "Todos los campos son obligatorios",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Usuario nuevoUsuario = new Usuario(nombre, username, password, tipo);
                if (usuarioDAO.insertarUsuario(nuevoUsuario)) {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "Usuario creado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    cargarUsuarios();
                } else {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "Error al crear usuario",
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

    private void resetearPassword() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "Por favor seleccione un usuario",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUsuario = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombreUsuario = (String) modeloTabla.getValueAt(filaSeleccionada, 1);

        JPasswordField txtNuevaPassword = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(new JLabel("Nueva contraseña para " + nombreUsuario + ":"));
        panel.add(txtNuevaPassword);

        int resultado = JOptionPane.showConfirmDialog(panelPrincipal, panel,
                "Resetear Contraseña", JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            String nuevaPassword = new String(txtNuevaPassword.getPassword());

            if (nuevaPassword.isEmpty()) {
                JOptionPane.showMessageDialog(panelPrincipal,
                        "La contraseña no puede estar vacía",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                if (usuarioDAO.actualizarPassword(idUsuario, nuevaPassword)) {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "Contraseña actualizada exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "Error al actualizar contraseña",
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
