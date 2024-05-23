package modelos;

import utilidades.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class Assigment {
    private int id;
    private Student student;
    private Company company;
    private Tutor tutor;
    private LocalDate creationDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Assigment(Student student, Company company, Tutor tutor) {
        this.student = student;
        this.company = company;
        this.tutor = tutor;
        this.creationDate = LocalDate.now(); // Fecha actual
    }

    public static void saveAssignment(Assigment assigment) {
        String query = "INSERT INTO Assigment (studentId, companyId, tutorId, creationDate) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, assigment.getStudent().getId());
            statement.setInt(2, assigment.getCompany().getId());
            statement.setInt(3, assigment.getTutor().getId());
            statement.setDate(4, java.sql.Date.valueOf(assigment.getCreationDate()));

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
