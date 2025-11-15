package com.tupojecto.biblioteca.vista;

import com.tupojecto.biblioteca.dao.UsuarioDAO;
import com.tupojecto.biblioteca.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginVista {
    private JPanel panelPrincipal;
    private JLabel lblUsuario;
    private JLabel lblContrasena;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;

    private final UsuarioDAO usuarioDAO;

    // Constructor de la clase LoginVista
    public LoginVista() {
        usuarioDAO = new UsuarioDAO();
        inicializarComponentes();

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

    // Inicializa y organiza los componentes de la interfaz
    private void inicializarComponentes() {
        panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lblTitulo = new JLabel("Sistema de Biblioteca UDB");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelPrincipal.add(lblTitulo, gbc);

        // Etiqueta Usuario
        lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panelPrincipal.add(lblUsuario, gbc);

        // Campo Usuario
        txtUsuario = new JTextField(20);
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panelPrincipal.add(txtUsuario, gbc);

        // Etiqueta Contraseña
        lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelPrincipal.add(lblContrasena, gbc);

        // Campo Contraseña
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        panelPrincipal.add(txtPassword, gbc);

        // Botón Ingresar
        btnIngresar = new JButton("Ingresar");
        btnIngresar.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panelPrincipal.add(btnIngresar, gbc);
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