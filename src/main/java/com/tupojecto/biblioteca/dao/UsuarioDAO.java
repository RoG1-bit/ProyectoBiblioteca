package com.tupojecto.biblioteca.dao;

import com.tupojecto.biblioteca.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// DAO - Data Access Object (Patrón de diseño para acceso a datos)
public class UsuarioDAO {

    // Cache del nombre real de la columna de usuario en la tabla `usuarios`.
    // En algunos entornos puede llamarse "username", "usuario", "nombre_usuario", etc.
    private static volatile String USERNAME_COLUMN_CACHE = null;

    // Candidatos comunes para nombre de columna de usuario
    private static final String[] USERNAME_CANDIDATES = new String[]{
            "username", "usuario", "nombre_usuario", "user_name", "login"
    };

    // Resuelve y cachea el nombre de la columna de usuario consultando el metadata
    private String resolveUsernameColumn(Connection conn) throws SQLException {
        if (USERNAME_COLUMN_CACHE != null) return USERNAME_COLUMN_CACHE;

        // Intentar detectar el nombre real de la columna a partir del metadata
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM usuarios LIMIT 1")) {
            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();
            for (String candidate : USERNAME_CANDIDATES) {
                for (int i = 1; i <= count; i++) {
                    String label = meta.getColumnLabel(i);
                    if (label != null && label.equalsIgnoreCase(candidate)) {
                        USERNAME_COLUMN_CACHE = label; // usar el label tal cual aparece
                        return USERNAME_COLUMN_CACHE;
                    }
                }
            }
        } catch (SQLException e) {
            // Si falla (por ejemplo, tabla vacía o permisos), intentar fallback con metadata a nivel de columnas
            DatabaseMetaData dbm = conn.getMetaData();
            try (ResultSet cols = dbm.getColumns(conn.getCatalog(), null, "usuarios", null)) {
                while (cols.next()) {
                    String colName = cols.getString("COLUMN_NAME");
                    for (String candidate : USERNAME_CANDIDATES) {
                        if (colName != null && colName.equalsIgnoreCase(candidate)) {
                            USERNAME_COLUMN_CACHE = colName;
                            return USERNAME_COLUMN_CACHE;
                        }
                    }
                }
            }
        }

        // Si no se detecta, asumir "username" para mantener compatibilidad con el esquema del proyecto
        USERNAME_COLUMN_CACHE = "username";
        return USERNAME_COLUMN_CACHE;
    }

    // Obtiene un valor de texto tratando varios posibles nombres de columna
    private String getStringByPossibleColumns(ResultSet rs, String... names) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int count = meta.getColumnCount();
        for (String name : names) {
            for (int i = 1; i <= count; i++) {
                String label = meta.getColumnLabel(i);
                if (label != null && label.equalsIgnoreCase(name)) {
                    return rs.getString(i);
                }
            }
        }
        return null;
    }

    // Crear nuevo usuario
    public boolean insertarUsuario(Usuario usuario) {
        try (Connection conn = ConexionDB.getConnection()) {
            String userCol = resolveUsernameColumn(conn);
            String sql = "INSERT INTO usuarios (nombre, " + userCol + ", password, tipo_usuario, tiene_mora) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, usuario.getNombre());
                stmt.setString(2, usuario.getUsername());
                stmt.setString(3, usuario.getPassword());
                stmt.setString(4, usuario.getTipoUsuario());
                stmt.setBoolean(5, usuario.isTieneMora());

                int filasAfectadas = stmt.executeUpdate();
                return filasAfectadas > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Autenticar usuario (para login)
    public Usuario autenticar(String username, String password) {
        try (Connection conn = ConexionDB.getConnection()) {
            String userCol = resolveUsernameColumn(conn);
            String sql = "SELECT * FROM usuarios WHERE " + userCol + " = ? AND password = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return crearUsuarioDesdeResultSet(rs);
                }
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
        try (Connection conn = ConexionDB.getConnection()) {
            String userCol = resolveUsernameColumn(conn);
            String sql = "SELECT * FROM usuarios WHERE " + userCol + " = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return crearUsuarioDesdeResultSet(rs);
                }
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
        try (Connection conn = ConexionDB.getConnection()) {
            String userCol = resolveUsernameColumn(conn);
            String sql = "UPDATE usuarios SET nombre = ?, " + userCol + " = ?, tipo_usuario = ?, tiene_mora = ? WHERE id_usuario = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, usuario.getNombre());
                stmt.setString(2, usuario.getUsername());
                stmt.setString(3, usuario.getTipoUsuario());
                stmt.setBoolean(4, usuario.isTieneMora());
                stmt.setInt(5, usuario.getIdUsuario());

                int filasAfectadas = stmt.executeUpdate();
                return filasAfectadas > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método auxiliar para crear objeto Usuario desde ResultSet
    private Usuario crearUsuarioDesdeResultSet(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        // Resolver ID de usuario de forma tolerante a variantes
        Integer idDetectado = null;
        SQLException ultimoErrorId = null;
        String[] idCandidates = new String[]{"id_usuario", "id", "idusuario", "idUsuario"};
        for (String idCol : idCandidates) {
            try {
                // getInt devuelve 0 si la columna existe y es NULL; diferenciamos usando wasNull
                int valor = rs.getInt(idCol);
                if (!rs.wasNull() || valor != 0) {
                    idDetectado = valor;
                    break;
                } else {
                    idDetectado = valor; // aceptar 0 si realmente fuera 0
                    break;
                }
            } catch (SQLException e) {
                ultimoErrorId = e;
                // probar siguiente candidato
            }
        }
        if (idDetectado == null) {
            // Si no se encontró ninguna columna válida, volver a lanzar el último error para visibilidad
            if (ultimoErrorId != null) throw ultimoErrorId;
            // fallback improbable
            idDetectado = 0;
        }
        usuario.setIdUsuario(idDetectado);
        usuario.setNombre(rs.getString("nombre"));
        // Intentar obtener el nombre de usuario desde cualquier posible variante
        String username = getStringByPossibleColumns(rs, USERNAME_CANDIDATES);
        if (username == null) {
            // Fallback al nombre original si no se detectó
            try {
                username = rs.getString("username");
            } catch (SQLException ignored) {
                username = null;
            }
        }
        usuario.setUsername(username);
        usuario.setPassword(rs.getString("password"));
        usuario.setTipoUsuario(rs.getString("tipo_usuario"));
        usuario.setTieneMora(rs.getBoolean("tiene_mora"));
        usuario.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        return usuario;
    }
}
