package modelos;

import utilidades.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Student {
    private int id;
    private String dni;
    private String name;
    private String surname;
    private LocalDate birthdate;

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

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public Student(int id, String dni, String name, String surname, LocalDate birthdate) {
        this.id = id;
        this.dni = dni;
        this.name = name;
        this.surname = surname;
        this.birthdate = birthdate;
    }

    @Override
    public String toString() {
        return name + " " + surname;
    }

    public static List<Student> getStudentsFromDatabase() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT id, dni, name, surname, birthdate FROM Student";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String dni = resultSet.getString("dni");
                String name = resultSet.getString("name");
                String surname = resultSet.getString("surname");
                LocalDate birthdate = resultSet.getDate("birthdate").toLocalDate();

                Student student = new Student(id, dni, name, surname, birthdate);
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
}

