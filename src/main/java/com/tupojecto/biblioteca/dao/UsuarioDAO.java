package com.tupojecto.biblioteca.dao;

import com.tupojecto.biblioteca.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// DAO - Data Access Object (Patrón de diseño para acceso a datos)
public class UsuarioDAO {

    // Crear nuevo usuario
    public boolean insertarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, username, password, tipo_usuario, tiene_mora) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getUsername());
            stmt.setString(3, usuario.getPassword());
            stmt.setString(4, usuario.getTipoUsuario());
            stmt.setBoolean(5, usuario.isTieneMora());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Autenticar usuario (para login)
    public Usuario autenticar(String username, String password) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return crearUsuarioDesdeResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Buscar usuario por ID
    public Usuario buscarPorId(int idUsuario) {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return crearUsuarioDesdeResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Buscar usuario por username
    public Usuario buscarPorUsername(String username) {
        String sql = "SELECT * FROM usuarios WHERE username = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return crearUsuarioDesdeResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Listar todos los usuarios
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre";

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(crearUsuarioDesdeResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
            e.printStackTrace();
        }

        return usuarios;
    }

    // Actualizar contraseña
    public boolean actualizarPassword(int idUsuario, String nuevaPassword) {
        String sql = "UPDATE usuarios SET password = ? WHERE id_usuario = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevaPassword);
            stmt.setInt(2, idUsuario);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar contraseña: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Actualizar estado de mora
    public boolean actualizarMora(int idUsuario, boolean tieneMora) {
        String sql = "UPDATE usuarios SET tiene_mora = ? WHERE id_usuario = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, tieneMora);
            stmt.setInt(2, idUsuario);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar mora: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Actualizar usuario completo
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, username = ?, tipo_usuario = ?, tiene_mora = ? WHERE id_usuario = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getUsername());
            stmt.setString(3, usuario.getTipoUsuario());
            stmt.setBoolean(4, usuario.isTieneMora());
            stmt.setInt(5, usuario.getIdUsuario());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método auxiliar para crear objeto Usuario desde ResultSet
    private Usuario crearUsuarioDesdeResultSet(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPassword(rs.getString("password"));
        usuario.setTipoUsuario(rs.getString("tipo_usuario"));
        usuario.setTieneMora(rs.getBoolean("tiene_mora"));
        usuario.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        return usuario;
    }
}
