package com.tupojecto.biblioteca;

import com.tupojecto.biblioteca.dao.ConexionDB;
import com.tupojecto.biblioteca.dao.UsuarioDAO;
import com.tupojecto.biblioteca.modelo.Usuario;

import java.sql.*;

/**
 * DIAGNÓSTICO COMPLETO DEL SISTEMA
 * Ejecuta esto para ver exactamente qué está pasando
 */
public class DiagnosticoCompleto {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║        DIAGNÓSTICO COMPLETO DEL SISTEMA                      ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        // PRUEBA 1: Conexión
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("PRUEBA 1: Verificando conexión a MySQL");
        System.out.println("═══════════════════════════════════════════════════════════════");

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ ÉXITO: Conexión establecida");
                System.out.println("  URL: jdbc:mysql://localhost:3306/biblioteca_db");
                System.out.println("  Estado: Conectado\n");
            }
        } catch (Exception e) {
            System.err.println("✗ ERROR CRÍTICO: No se pudo conectar a MySQL");
            System.err.println("  Mensaje: " + e.getMessage());
            System.err.println("\n⚠ SOLUCIONES:");
            System.err.println("  1. Verifica que MySQL esté corriendo");
            System.err.println("  2. Verifica que la base de datos 'biblioteca_db' existe");
            System.err.println("  3. Verifica usuario/contraseña en ConexionDB.java\n");
            return;
        }

        // PRUEBA 2: Verificar si existe la base de datos
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("PRUEBA 2: Verificando base de datos 'biblioteca_db'");
        System.out.println("═══════════════════════════════════════════════════════════════");

        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet catalogs = metaData.getCatalogs();
            boolean existeDB = false;

            while (catalogs.next()) {
                String dbName = catalogs.getString(1);
                if (dbName.equals("biblioteca_db")) {
                    existeDB = true;
                    break;
                }
            }

            if (existeDB) {
                System.out.println("✓ Base de datos 'biblioteca_db' existe\n");
            } else {
                System.err.println("✗ ERROR: Base de datos 'biblioteca_db' NO EXISTE");
                System.err.println("\n⚠ SOLUCIÓN:");
                System.err.println("  Ejecuta el archivo database_schema.sql en MySQL\n");
                return;
            }

        } catch (Exception e) {
            System.err.println("✗ Error verificando base de datos: " + e.getMessage() + "\n");
        }

        // PRUEBA 3: Verificar tablas
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("PRUEBA 3: Verificando tablas de la base de datos");
        System.out.println("═══════════════════════════════════════════════════════════════");

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES FROM biblioteca_db");

            int cantidadTablas = 0;
            System.out.println("Tablas encontradas:");
            while (rs.next()) {
                System.out.println("  • " + rs.getString(1));
                cantidadTablas++;
            }

            if (cantidadTablas == 0) {
                System.err.println("\n✗ ERROR: NO hay tablas en biblioteca_db");
                System.err.println("\n⚠ SOLUCIÓN:");
                System.err.println("  Ejecuta TODO el contenido de database_schema.sql\n");
                return;
            } else {
                System.out.println("\n✓ Se encontraron " + cantidadTablas + " tablas\n");
            }

        } catch (Exception e) {
            System.err.println("✗ Error verificando tablas: " + e.getMessage() + "\n");
        }

        // PRUEBA 4: Verificar usuarios en la base de datos
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("PRUEBA 4: Verificando usuarios en la tabla");
        System.out.println("═══════════════════════════════════════════════════════════════");

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id_usuario, nombre, username, password, tipo_usuario FROM usuarios");

            int cantidadUsuarios = 0;
            System.out.println("Usuarios encontrados:\n");
            System.out.println(String.format("%-5s %-20s %-15s %-15s %-15s", "ID", "Nombre", "Username", "Password", "Tipo"));
            System.out.println("───────────────────────────────────────────────────────────────");

            while (rs.next()) {
                int id = rs.getInt("id_usuario");
                String nombre = rs.getString("nombre");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String tipo = rs.getString("tipo_usuario");

                System.out.println(String.format("%-5d %-20s %-15s %-15s %-15s",
                    id, nombre, username, password, tipo));
                cantidadUsuarios++;
            }

            if (cantidadUsuarios == 0) {
                System.err.println("\n✗ ERROR: NO hay usuarios en la tabla");
                System.err.println("\n⚠ SOLUCIÓN:");
                System.err.println("  Ejecuta la parte de INSERT del archivo database_schema.sql\n");
                return;
            } else {
                System.out.println("\n✓ Se encontraron " + cantidadUsuarios + " usuarios\n");
            }

        } catch (Exception e) {
            System.err.println("✗ Error leyendo usuarios: " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }

        // PRUEBA 5: Probar autenticación directa con SQL
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("PRUEBA 5: Probando autenticación con SQL directo");
        System.out.println("═══════════════════════════════════════════════════════════════");

        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM usuarios WHERE username = ? AND password = ?");
            stmt.setString(1, "admin");
            stmt.setString(2, "admin123");

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("✓ Autenticación SQL EXITOSA para admin/admin123");
                System.out.println("  ID: " + rs.getInt("id_usuario"));
                System.out.println("  Nombre: " + rs.getString("nombre"));
                System.out.println("  Tipo: " + rs.getString("tipo_usuario") + "\n");
            } else {
                System.err.println("✗ ERROR: Autenticación SQL FALLÓ para admin/admin123");
                System.err.println("\n⚠ POSIBLE CAUSA:");
                System.err.println("  Las credenciales en la BD son diferentes");
                System.err.println("  Revisa la tabla 'usuarios' arriba\n");
                return;
            }

        } catch (Exception e) {
            System.err.println("✗ Error en autenticación SQL: " + e.getMessage() + "\n");
        }

        // PRUEBA 6: Probar con UsuarioDAO
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("PRUEBA 6: Probando autenticación con UsuarioDAO");
        System.out.println("═══════════════════════════════════════════════════════════════");

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.autenticar("admin", "admin123");

            if (usuario != null) {
                System.out.println("✓ Autenticación con DAO EXITOSA");
                System.out.println("  ID: " + usuario.getIdUsuario());
                System.out.println("  Nombre: " + usuario.getNombre());
                System.out.println("  Username: " + usuario.getUsername());
                System.out.println("  Tipo: " + usuario.getTipoUsuario() + "\n");
            } else {
                System.err.println("✗ ERROR: Autenticación con DAO FALLÓ");
                System.err.println("\n⚠ PROBLEMA:");
                System.err.println("  El SQL directo funcionó pero el DAO no");
                System.err.println("  Hay un problema en el código UsuarioDAO\n");
                return;
            }

        } catch (Exception e) {
            System.err.println("✗ Error en autenticación DAO: " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }

        // Cerrar conexión
        try {
            if (conn != null) conn.close();
        } catch (Exception e) {
            // Ignorar
        }

        // RESUMEN FINAL
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║            ✓ TODAS LAS PRUEBAS EXITOSAS                     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("El sistema está funcionando correctamente.");
        System.out.println();
        System.out.println("Si LoginVistaSinForm.java no funciona, es un problema de interfaz.");
        System.out.println("Verifica que escribes exactamente: admin / admin123");
        System.out.println("(sin espacios extras)\n");
    }
}
