package modelos;

import utilidades.DatabaseConnection;

import java.sql.*;

public class CompanyManager {
    private int id;
    private String dni;
    private String name;
    private String surname;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public CompanyManager(int id, String dni, String name, String surname) {
        this.id = id;
        this.dni = dni;
        this.name = name;
        this.surname = surname;
    }

    public CompanyManager() {
    }

    public int guardarEnBaseDeDatos() throws SQLException {
        int generatedId = -1;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO CompanyManager (dni, name, surname) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, dni);
            pstmt.setString(2, name);
            pstmt.setString(3, surname);
            pstmt.executeUpdate();

            // Obtener el ID generado
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        }
        return generatedId;
    }

    public static CompanyManager getCompanyManagerById(int id) {
        CompanyManager companyManager = null;
        String query = "SELECT id, dni, name, surname FROM CompanyManager WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                companyManager = new CompanyManager();
                companyManager.setId(rs.getInt("id"));
                companyManager.setDni(rs.getString("dni"));
                companyManager.setName(rs.getString("name"));
                companyManager.setSurname(rs.getString("surname"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return companyManager;
    }

    public void actualizarEnBaseDeDatosCompanyManager() throws SQLException {
        // Construir la consulta SQL para actualizar el representante legal
        String query = "UPDATE CompanyManager SET dni = ?, name = ?, surname = ? WHERE id = ?";

        // Establecer la conexión a la base de datos
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Establecer los parámetros de la consulta para el representante legal
            statement.setString(1, this.dni);
            statement.setString(2, this.name);
            statement.setString(3, this.surname);
            statement.setInt(4, this.id); // Condicional para identificar el representante legal

            // Ejecutar la consulta de actualización para el representante legal
            statement.executeUpdate();
        }
    }
}
