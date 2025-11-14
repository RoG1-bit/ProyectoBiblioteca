package com.tupojecto.biblioteca.vista;

import com.tupojecto.biblioteca.dao.UsuarioDAO;
import com.tupojecto.biblioteca.modelo.Usuario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginVista {
    private JPanel panelPrincipal;
    private JLabel Usuario;
    private JLabel Contraseña;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;

    private UsuarioDAO usuarioDAO;

    // Constructor de la clase LoginVista
    public LoginVista() {
        usuarioDAO = new UsuarioDAO();

        // Lógica del botón Ingresar
        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticarUsuario();
            }
        });

        // Permitir login con Enter
        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticarUsuario();
            }
        });
    }

    // Método para autenticar usuario
    private void autenticarUsuario() {
        String username = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Validación básica
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "Por favor ingrese usuario y contraseña",
                    "Campos vacíos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Autenticar en la base de datos
            Usuario usuario = usuarioDAO.autenticar(username, password);

            if (usuario != null) {
                JOptionPane.showMessageDialog(panelPrincipal,
                        "Bienvenido, " + usuario.getNombre() + "\nTipo: " + usuario.getTipoUsuario(),
                        "Login exitoso",
                        JOptionPane.INFORMATION_MESSAGE);

                // Cerrar ventana de login
                JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(panelPrincipal);
                loginFrame.dispose();

                // Abrir menú principal según tipo de usuario
                abrirMenuPrincipal(usuario);

            } else {
                JOptionPane.showMessageDialog(panelPrincipal,
                        "Usuario o contraseña incorrectos",
                        "Error de autenticación",
                        JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "Error al conectar con la base de datos:\n" + ex.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Abrir menú principal según tipo de usuario
    private void abrirMenuPrincipal(Usuario usuario) {
        JFrame frame = new JFrame("Sistema de Biblioteca - " + usuario.getTipoUsuario());
        frame.setContentPane(new MenuPrincipalVista(usuario).getPanelPrincipal());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    // Método main para INICIAR esta ventana
    public static void main(String[] args) {
        JFrame frame = new JFrame("Sistema de Biblioteca - Login");
        frame.setContentPane(new LoginVista().panelPrincipal);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}