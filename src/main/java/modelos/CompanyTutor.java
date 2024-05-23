package modelos;

import utilidades.DatabaseConnection;

import java.sql.*;

public class CompanyTutor {
    private int id;
    private String dni;
    private String name;
    private String surname;
    private String phone;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public CompanyTutor(int id, String dni, String name, String surname, String phone) {
        this.id = id;
        this.dni = dni;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
    }

    public CompanyTutor() {
    }

    public int guardarEnBaseDeDatos() throws SQLException {
        int generatedId = -1;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO CompanyTutor (dni, name, surname, phone) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, dni);
            pstmt.setString(2, name);
            pstmt.setString(3, surname);
            pstmt.setString(4, phone);
            pstmt.executeUpdate();

            // Obtener el ID generado
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        }
        return generatedId;
    }

    public static CompanyTutor getCompanyTutorById(int id) {
        CompanyTutor companyTutor = null;
        String query = "SELECT id, dni, name, surname, phone FROM CompanyTutor WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                companyTutor = new CompanyTutor();
                companyTutor.setId(rs.getInt("id"));
                companyTutor.setDni(rs.getString("dni"));
                companyTutor.setName(rs.getString("name"));
                companyTutor.setSurname(rs.getString("surname"));
                companyTutor.setPhone(rs.getString("phone"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return companyTutor;
    }

    public void actualizarEnBaseDeDatosCompanyTutor() throws SQLException {
        // Construir la consulta SQL para actualizar el tutor laboral
        String query = "UPDATE CompanyTutor SET dni = ?, name = ?, surname = ?, phone = ? WHERE id = ?";

        // Establecer la conexión a la base de datos
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Establecer los parámetros de la consulta para el tutor laboral
            statement.setString(1, this.dni);
            statement.setString(2, this.name);
            statement.setString(3, this.surname);
            statement.setString(4, this.phone);
            statement.setInt(5, this.id); // Condicional para identificar el tutor laboral

            // Ejecutar la consulta de actualización para el tutor laboral
            statement.executeUpdate();
        }
    }
}

