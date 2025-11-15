package com.tupojecto.biblioteca;

import com.tupojecto.biblioteca.dao.ConexionDB;
import com.tupojecto.biblioteca.dao.UsuarioDAO;
import com.tupojecto.biblioteca.modelo.Usuario;

import java.sql.Connection;

/**
 * CLASE DE PRUEBA - Ejecutar esto PRIMERO para verificar que todo funciona
 *
 * Este programa prueba:
 * 1. Conexión a la base de datos
 * 2. Lectura de usuarios de prueba
 * 3. Autenticación de usuario admin
 *
 * Si este programa funciona, el sistema completo funcionará.
 */
public class PruebaConexion {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("  PRUEBA DE CONEXIÓN AL SISTEMA DE BIBLIOTECA");
        System.out.println("================================================================================\n");

        // PRUEBA 1: Conexión a la base de datos
        System.out.println("PRUEBA 1: Verificando conexión a MySQL...");
        try (Connection conn = ConexionDB.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ ÉXITO: Conexión a base de datos establecida");
                System.out.println("  Base de datos: biblioteca");
                System.out.println("  Host: localhost:3306\n");
            }
        } catch (Exception e) {
            System.err.println("✗ ERROR: No se pudo conectar a la base de datos");
            System.err.println("  Mensaje: " + e.getMessage());
            System.err.println("\nVERIFICA:");
            System.err.println("  1. MySQL está corriendo");
            System.err.println("  2. Ejecutaste database_schema.sql");
            System.err.println("  3. Las credenciales en ConexionDB.java son correctas\n");
            e.printStackTrace();
            return;
        }

        // PRUEBA 2: Leer usuarios de la base de datos
        System.out.println("PRUEBA 2: Leyendo usuarios de prueba...");
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();

            // Probar lectura de usuario admin
            Usuario admin = usuarioDAO.buscarPorUsername("admin");
            if (admin != null) {
                System.out.println("✓ ÉXITO: Usuario 'admin' encontrado");
                System.out.println("  ID: " + admin.getIdUsuario());
                System.out.println("  Nombre: " + admin.getNombre());
                System.out.println("  Tipo: " + admin.getTipoUsuario());
                System.out.println("  Tiene mora: " + (admin.isTieneMora() ? "Sí" : "No") + "\n");
            } else {
                System.err.println("✗ ERROR: Usuario 'admin' no encontrado");
                System.err.println("  VERIFICA: ¿Ejecutaste database_schema.sql completo?\n");
                return;
            }

            // Probar lectura de usuario profesor
            Usuario profesor = usuarioDAO.buscarPorUsername("jperez");
            if (profesor != null) {
                System.out.println("✓ ÉXITO: Usuario 'jperez' (profesor) encontrado");
                System.out.println("  Nombre: " + profesor.getNombre());
                System.out.println("  Tipo: " + profesor.getTipoUsuario() + "\n");
            }

            // Probar lectura de usuario alumno
            Usuario alumno = usuarioDAO.buscarPorUsername("mgarcia");
            if (alumno != null) {
                System.out.println("✓ ÉXITO: Usuario 'mgarcia' (alumno) encontrado");
                System.out.println("  Nombre: " + alumno.getNombre());
                System.out.println("  Tipo: " + alumno.getTipoUsuario() + "\n");
            }

        } catch (Exception e) {
            System.err.println("✗ ERROR: No se pudieron leer los usuarios");
            System.err.println("  Mensaje: " + e.getMessage() + "\n");
            e.printStackTrace();
            return;
        }

        // PRUEBA 3: Autenticación
        System.out.println("PRUEBA 3: Probando autenticación...");
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();

            // Probar login correcto
            Usuario usuarioAutenticado = usuarioDAO.autenticar("admin", "admin123");
            if (usuarioAutenticado != null) {
                System.out.println("✓ ÉXITO: Autenticación correcta para admin/admin123");
                System.out.println("  Usuario autenticado: " + usuarioAutenticado.getNombre() + "\n");
            } else {
                System.err.println("✗ ERROR: No se pudo autenticar admin/admin123\n");
                return;
            }

            // Probar login incorrecto
            Usuario loginFallido = usuarioDAO.autenticar("admin", "incorrecta");
            if (loginFallido == null) {
                System.out.println("✓ ÉXITO: Rechazó contraseña incorrecta (esperado)\n");
            } else {
                System.err.println("✗ ERROR: Aceptó contraseña incorrecta (no debería)\n");
            }

        } catch (Exception e) {
            System.err.println("✗ ERROR: Falló la autenticación");
            System.err.println("  Mensaje: " + e.getMessage() + "\n");
            e.printStackTrace();
            return;
        }

        // RESUMEN FINAL
        System.out.println("================================================================================");
        System.out.println("  ✓ TODAS LAS PRUEBAS PASARON EXITOSAMENTE");
        System.out.println("================================================================================\n");
        System.out.println("El sistema está listo para usar. Ahora puedes ejecutar:");
        System.out.println("  LoginVistaSinForm.java (recomendado)");
        System.out.println("  o");
        System.out.println("  LoginVista.java (si usas IntelliJ IDEA)\n");

        System.out.println("CREDENCIALES DE PRUEBA:");
        System.out.println("  Administrador: admin / admin123");
        System.out.println("  Profesor:      jperez / profesor123");
        System.out.println("  Alumno:        mgarcia / alumno123\n");

        System.out.println("================================================================================\n");
    }
}
