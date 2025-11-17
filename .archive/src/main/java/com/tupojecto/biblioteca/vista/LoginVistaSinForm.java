package com.tupojecto.biblioteca.vista;

import com.tupojecto.biblioteca.dao.UsuarioDAO;
import com.tupojecto.biblioteca.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * VERSION SIN ARCHIVO .FORM
 * Esta clase funciona en cualquier IDE (Eclipse, NetBeans, VS Code, etc.)
 * Crea la interfaz gráfica programáticamente sin depender de IntelliJ Form Designer
 */
public class LoginVistaSinForm extends JFrame {

    private JPanel panelPrincipal;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private UsuarioDAO usuarioDAO;

    public LoginVistaSinForm() {
        usuarioDAO = new UsuarioDAO();
        inicializarComponentes();
        configurarVentana();
    }

    private void inicializarComponentes() {
        // Panel principal
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridBagLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        panelPrincipal.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lblTitulo = new JLabel("Sistema de Biblioteca UDB");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelPrincipal.add(lblTitulo, gbc);

        // Espacio
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panelPrincipal.add(Box.createVerticalStrut(20), gbc);

        // Label Usuario
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panelPrincipal.add(lblUsuario, gbc);

        // Campo Usuario
        txtUsuario = new JTextField(20);
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsuario.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 2;
        panelPrincipal.add(txtUsuario, gbc);

        // Label Contraseña
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        panelPrincipal.add(lblPassword, gbc);

        // Campo Contraseña
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 3;
        panelPrincipal.add(txtPassword, gbc);

        // Espacio
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        panelPrincipal.add(Box.createVerticalStrut(20), gbc);

        // Botón Ingresar
        btnIngresar = new JButton("Ingresar");
        btnIngresar.setFont(new Font("Arial", Font.BOLD, 14));
        btnIngresar.setPreferredSize(new Dimension(300, 40));
        btnIngresar.setBackground(new Color(0, 123, 255));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panelPrincipal.add(btnIngresar, gbc);

        // Panel de información
        JPanel panelInfo = new JPanel();
        panelInfo.setBackground(new Color(240, 240, 240));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));

        JLabel lblInfo1 = new JLabel("Credenciales de prueba:");
        lblInfo1.setFont(new Font("Arial", Font.BOLD, 12));
        JLabel lblInfo2 = new JLabel("Admin: admin / admin123");
        lblInfo2.setFont(new Font("Arial", Font.PLAIN, 11));
        JLabel lblInfo3 = new JLabel("Profesor: jperez / profesor123");
        lblInfo3.setFont(new Font("Arial", Font.PLAIN, 11));
        JLabel lblInfo4 = new JLabel("Alumno: mgarcia / alumno123");
        lblInfo4.setFont(new Font("Arial", Font.PLAIN, 11));

        panelInfo.add(lblInfo1);
        panelInfo.add(lblInfo2);
        panelInfo.add(lblInfo3);
        panelInfo.add(lblInfo4);

        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panelPrincipal.add(panelInfo, gbc);

        // Eventos
        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticarUsuario();
            }
        });

        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticarUsuario();
            }
        });
    }

    private void configurarVentana() {
        setTitle("Sistema de Biblioteca - Login");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void autenticarUsuario() {
        String username = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Validación básica
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese usuario y contraseña",
                    "Campos vacíos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Autenticar en la base de datos
            Usuario usuario = usuarioDAO.autenticar(username, password);

            if (usuario != null) {
                JOptionPane.showMessageDialog(this,
                        "Bienvenido, " + usuario.getNombre() + "\nTipo: " + usuario.getTipoUsuario(),
                        "Login exitoso",
                        JOptionPane.INFORMATION_MESSAGE);

                // Cerrar ventana de login
                dispose();

                // Abrir menú principal
                abrirMenuPrincipal(usuario);

            } else {
                JOptionPane.showMessageDialog(this,
                        "Usuario o contraseña incorrectos",
                        "Error de autenticación",
                        JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al conectar con la base de datos:\n" + ex.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void abrirMenuPrincipal(Usuario usuario) {
        JFrame frame = new JFrame("Sistema de Biblioteca - " + usuario.getTipoUsuario());
        frame.setContentPane(new MenuPrincipalVista(usuario).getPanelPrincipal());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Método main para ejecutar
    public static void main(String[] args) {
        // Configurar look and feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ejecutar en el Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginVistaSinForm().setVisible(true);
            }
        });
    }
}
