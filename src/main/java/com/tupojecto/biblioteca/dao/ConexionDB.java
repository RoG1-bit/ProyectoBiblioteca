package com.tupojecto.biblioteca.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    // Configuración de la base de datos
    // Nota: El esquema del proyecto utiliza la base `biblioteca` (ver database_schema.sql)
    // Se agregan parámetros recomendados para evitar errores comunes de conexión con MySQL 8+
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        try {
            // Carga el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Retorna la conexión
            return DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (ClassNotFoundException e) {
            // Manejo de Excepciones (Clase 9)
            throw new SQLException("Error: Driver de MySQL no encontrado. Asegúrate de tener mysql-connector-java en el classpath (pom.xml)", e);
        }
    }

    // Método para cerrar conexión de forma segura
    public static void cerrarConexion(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        // Prueba de conexión
        try (Connection conn = ConexionDB.getConnection()) {
            if (conn != null) {
                System.out.println("¡Conexión a la base de datos exitosa!");
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos:");
            e.printStackTrace();
        }
    }
}