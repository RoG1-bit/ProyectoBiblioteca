package com.tupojecto.biblioteca.vista;

import com.tupojecto.biblioteca.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipalVista {
    private JPanel panelPrincipal;
    private JLabel lblBienvenida;
    private JPanel panelMenu;
    private Usuario usuarioActual;

    public MenuPrincipalVista(Usuario usuario) {
        this.usuarioActual = usuario;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel superior con bienvenida
        JPanel panelSuperior = new JPanel(new BorderLayout());
        lblBienvenida = new JLabel("Bienvenido: " + usuarioActual.getNombre() +
                " (" + usuarioActual.getTipoUsuario() + ")");
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 16));
        panelSuperior.add(lblBienvenida, BorderLayout.CENTER);

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        panelSuperior.add(btnCerrarSesion, BorderLayout.EAST);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // Panel central con menú de opciones
        panelMenu = new JPanel(new GridLayout(0, 2, 10, 10));
        crearMenuSegunTipoUsuario();
        panelPrincipal.add(panelMenu, BorderLayout.CENTER);
    }

    private void crearMenuSegunTipoUsuario() {
        String tipoUsuario = usuarioActual.getTipoUsuario();

        // Opciones comunes para todos
        agregarBoton("Consultar Documentos", e -> abrirConsultaDocumentos());
        agregarBoton("Mis Préstamos", e -> abrirMisPrestamos());

        // Opciones específicas según tipo de usuario
        if (tipoUsuario.equals("Administrador")) {
            agregarBoton("Gestión de Usuarios", e -> abrirGestionUsuarios());
            agregarBoton("Gestión de Documentos", e -> abrirGestionDocumentos());
            agregarBoton("Gestión de Préstamos", e -> abrirGestionPrestamos());
            agregarBoton("Gestión de Devoluciones", e -> abrirGestionDevoluciones());
            agregarBoton("Configuración de Préstamos", e -> abrirConfiguracionPrestamos());
            agregarBoton("Calcular Moras", e -> abrirCalculoMoras());
            agregarBoton("Reportes", e -> abrirReportes());

        } else if (tipoUsuario.equals("Profesor") || tipoUsuario.equals("Alumno")) {
            // Los profesores y alumnos pueden ver el catálogo y sus préstamos
            agregarBoton("Solicitar Préstamo", e -> abrirSolicitudPrestamo());
        }
    }

    private void agregarBoton(String texto, java.awt.event.ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(200, 50));
        boton.setFont(new Font("Arial", Font.PLAIN, 14));
        boton.addActionListener(accion);
        panelMenu.add(boton);
    }

    // Métodos para abrir diferentes ventanas
    private void abrirConsultaDocumentos() {
        JFrame frame = new JFrame("Consulta de Documentos");
        frame.setContentPane(new ConsultaDocumentosVista(usuarioActual).getPanelPrincipal());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void abrirMisPrestamos() {
        JFrame frame = new JFrame("Mis Préstamos");
        frame.setContentPane(new MisPrestamosVista(usuarioActual).getPanelPrincipal());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void abrirGestionUsuarios() {
        JFrame frame = new JFrame("Gestión de Usuarios");
        frame.setContentPane(new GestionUsuariosVista(usuarioActual).getPanelPrincipal());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void abrirGestionDocumentos() {
        JFrame frame = new JFrame("Gestión de Documentos");
        frame.setContentPane(new GestionDocumentosVista(usuarioActual).getPanelPrincipal());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void abrirGestionPrestamos() {
        JFrame frame = new JFrame("Gestión de Préstamos");
        frame.setContentPane(new GestionPrestamosVista(usuarioActual).getPanelPrincipal());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void abrirGestionDevoluciones() {
        JFrame frame = new JFrame("Gestión de Devoluciones");
        frame.setContentPane(new GestionDevolucionesVista(usuarioActual).getPanelPrincipal());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void abrirConfiguracionPrestamos() {
        JFrame frame = new JFrame("Configuración de Préstamos");
        frame.setContentPane(new ConfiguracionPrestamosVista(usuarioActual).getPanelPrincipal());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void abrirCalculoMoras() {
        JFrame frame = new JFrame("Cálculo de Moras");
        frame.setContentPane(new CalculoMorasVista(usuarioActual).getPanelPrincipal());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void abrirReportes() {
        JFrame frame = new JFrame("Reportes del Sistema");
        frame.setContentPane(new ReportesVista(usuarioActual).getPanelPrincipal());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void abrirSolicitudPrestamo() {
        JFrame frame = new JFrame("Solicitar Préstamo");
        frame.setContentPane(new SolicitudPrestamoVista(usuarioActual).getPanelPrincipal());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(panelPrincipal,
                "¿Está seguro de cerrar sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            JFrame frameActual = (JFrame) SwingUtilities.getWindowAncestor(panelPrincipal);
            frameActual.dispose();

            // Volver a abrir ventana de login
            JFrame frame = new JFrame("Sistema de Biblioteca - Login");
            frame.setContentPane(new LoginVista().getPanelPrincipal());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }
}
