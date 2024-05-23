package modelos;

import enumerados.JourneyType;
import enumerados.Modality;
import utilidades.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Company {
    private int id;
    private String cif;
    private String name;
    private String address;
    private String postalCode;
    private String city;
    private JourneyType journeyType;
    private Modality modality;
    private String email;
    private CompanyManager companyManager;
    private CompanyTutor companyTutor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public JourneyType getJourneyType() {
        return journeyType;
    }

    public void setJourneyType(JourneyType journeyType) {
        this.journeyType = journeyType;
    }

    public Modality getModality() {
        return modality;
    }

    public void setModality(Modality modality) {
        this.modality = modality;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CompanyManager getCompanyManager() {
        return companyManager;
    }

    public void setCompanyManager(CompanyManager companyManager) {
        this.companyManager = companyManager;
    }

    public CompanyTutor getCompanyTutor() {
        return companyTutor;
    }

    public void setCompanyTutor(CompanyTutor companyTutor) {
        this.companyTutor = companyTutor;
    }

    public Company(int id, String cif, String name, String address, String postalCode, String city, JourneyType journeyType, Modality modality, String email, CompanyManager companyManager, CompanyTutor companyTutor) {
        this.id = id;
        this.cif = cif;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.city = city;
        this.journeyType = journeyType;
        this.modality = modality;
        this.email = email;
        this.companyManager = companyManager;
        this.companyTutor = companyTutor;
    }

    public Company() {
    }

    @Override
    public String toString() {
        return name;
    }

    public static List<Company> getAllCompanies() {
        List<Company> companies = new ArrayList<>();
        String query = "SELECT id, cif, name, address, postalCode, city, journeyType, modality, email, companyManagerId, companyTutorId FROM Company";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                Company company = new Company();
                company.setId(rs.getInt("id"));
                company.setCif(rs.getString("cif"));
                company.setName(rs.getString("name"));
                company.setAddress(rs.getString("address"));
                company.setPostalCode(rs.getString("postalCode"));
                company.setCity(rs.getString("city"));
                company.setJourneyType(JourneyType.valueOf(rs.getString("journeyType")));
                company.setModality(Modality.valueOf(rs.getString("modality")));
                company.setEmail(rs.getString("email"));

                // Obtener los IDs de companyManager y companyTutor
                int companyManagerId = rs.getInt("companyManagerId");
                int companyTutorId = rs.getInt("companyTutorId");

                // Obtener los objetos CompanyManager y CompanyTutor usando los IDs
                CompanyManager companyManager = CompanyManager.getCompanyManagerById(companyManagerId);
                CompanyTutor companyTutor = CompanyTutor.getCompanyTutorById(companyTutorId);

                // Establecer los objetos en la instancia de Company
                company.setCompanyManager(companyManager);
                company.setCompanyTutor(companyTutor);

                companies.add(company);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return companies;
    }

    public void guardarEnBaseDeDatos(int companyManagerId, int companyTutorId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO Company (cif, name, address, postalCode, city, journeyType, modality, email, companyManagerId, companyTutorId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, cif);
            pstmt.setString(2, name);
            pstmt.setString(3, address);
            pstmt.setString(4, postalCode);
            pstmt.setString(5, city);
            pstmt.setString(6, journeyType.toString());
            pstmt.setString(7, modality.toString());
            pstmt.setString(8, email);
            pstmt.setInt(9, companyManagerId);
            pstmt.setInt(10, companyTutorId);
            pstmt.executeUpdate();
        }
    }

    public void eliminarDeBaseDeDatos() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM Company WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, this.id);
            pstmt.executeUpdate();
        }
    }

    public void actualizarEnBaseDeDatos() throws SQLException {
        // Construir la consulta SQL para actualizar la empresa
        String query = "UPDATE Company SET cif = ?, name = ?, address = ?, postalCode = ?, city = ?, journeyType = ?, modality = ?, email = ?, companyManagerId = ?, companyTutorId = ? WHERE id = ?";

        // Establecer la conexión a la base de datos
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Establecer los parámetros de la consulta para la empresa
            statement.setString(1, this.cif);
            statement.setString(2, this.name);
            statement.setString(3, this.address);
            statement.setString(4, this.postalCode);
            statement.setString(5, this.city);
            statement.setString(6, this.journeyType.toString());
            statement.setString(7, this.modality.toString());
            statement.setString(8, this.email);
            statement.setInt(9, this.companyManager.getId()); // Actualiza el ID del representante legal
            statement.setInt(10, this.companyTutor.getId()); // Actualiza el ID del tutor laboral
            statement.setInt(11, this.id); // Condicional para identificar la empresa

            // Ejecutar la consulta de actualización para la empresa
            statement.executeUpdate();

            // Luego, actualiza los registros asociados en CompanyManager y CompanyTutor
            this.companyManager.actualizarEnBaseDeDatosCompanyManager();
            this.companyTutor.actualizarEnBaseDeDatosCompanyTutor();
        }
    }
}
