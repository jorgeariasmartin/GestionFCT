package controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import modelos.*;
import enumerados.JourneyType;
import enumerados.Modality;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utilidades.DatabaseConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import static modelos.Student.getStudentsFromDatabase;

public class EmpresasController {
    @FXML
    private TableView<Company> companyTableView;
    @FXML
    private TableColumn<Company, String> cifColumn;
    @FXML
    private TableColumn<Company, String> nameColumn;
    @FXML
    private TableColumn<Company, String> addressColumn;
    @FXML
    private TableColumn<Company, String> postalCodeColumn;
    @FXML
    private TableColumn<Company, String> cityColumn;
    @FXML
    private TableColumn<Company, String> journeyTypeColumn;
    @FXML
    private TableColumn<Company, String> modalityColumn;
    @FXML
    private TableColumn<Company, String> emailColumn;

    // Formulario de alta de empresa
    @FXML
    private TextField codigoEmpresaTextField;
    @FXML
    private TextField cifTextField;
    @FXML
    private TextField nombreTextField;
    @FXML
    private TextField direccionTextField;
    @FXML
    private TextField cpTextField;
    @FXML
    private TextField localidadTextField;
    @FXML
    private ComboBox<JourneyType> jornadaComboBox;
    @FXML
    private ComboBox<Modality> modalidadComboBox;
    @FXML
    private TextField mailTextField;

    @FXML
    private TextField dniRepLegalTextField;
    @FXML
    private TextField nombreRepLegalTextField;
    @FXML
    private TextField apellidosRepLegalTextField;
    @FXML
    private TextField dniTutLaboralTextField;
    @FXML
    private TextField nombreTutLaboralTextField;
    @FXML
    private TextField apellidosTutLaboralTextField;
    @FXML
    private TextField tlfTutLaboralTextField;


    // Botones
    @FXML
    private Button eliminarButton;
    @FXML
    private Button modificarButton;
    @FXML
    private Button alumnosButton;
    @FXML
    private Button tutoresButton;
    @FXML
    private Button insertarButton;
    @FXML
    private Button cancelarModificacion;


    // Label de alumnos
    @FXML
    private Label mensajeLabel;

    //Label de tutores
    @FXML
    private Label mensajeTutorLabel;

    // Asignacion comboBox

    @FXML
    private ComboBox<Student> studentComboBox;
    @FXML
    private ComboBox<Company> empresaComboBox;
    @FXML
    private ComboBox<Tutor> tutorComboBox;

    // Confirmar asignacion
    @FXML
    private Button confirmacion;
    @FXML
    private Label mensajeAsignacion;

    private Company selectedCompany;

    public void initialize() {
        ObservableList<Company> companyList = FXCollections.observableArrayList(Company.getAllCompanies());

        cifColumn.setCellValueFactory(new PropertyValueFactory<>("cif"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        journeyTypeColumn.setCellValueFactory(new PropertyValueFactory<>("journeyType"));
        modalityColumn.setCellValueFactory(new PropertyValueFactory<>("modality"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        companyTableView.setItems(companyList);

        companyTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillForm(newValue);
            }
        });

        // Listener para validar los campos
        ChangeListener<String> changeListener = (observable, oldValue, newValue) -> validateForm();
        codigoEmpresaTextField.textProperty().addListener(changeListener);
        cifTextField.textProperty().addListener(changeListener);
        nombreTextField.textProperty().addListener(changeListener);
        direccionTextField.textProperty().addListener(changeListener);
        cpTextField.textProperty().addListener(changeListener);
        localidadTextField.textProperty().addListener(changeListener);
        mailTextField.textProperty().addListener(changeListener);
        dniRepLegalTextField.textProperty().addListener(changeListener);
        nombreRepLegalTextField.textProperty().addListener(changeListener);
        apellidosRepLegalTextField.textProperty().addListener(changeListener);
        dniTutLaboralTextField.textProperty().addListener(changeListener);
        nombreTutLaboralTextField.textProperty().addListener(changeListener);
        apellidosTutLaboralTextField.textProperty().addListener(changeListener);
        tlfTutLaboralTextField.textProperty().addListener(changeListener);

        jornadaComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateForm());
        modalidadComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateForm());

        // Configurar el botón "Eliminar"
        eliminarButton.setOnAction(event -> {
            Company selectedCompany = companyTableView.getSelectionModel().getSelectedItem();
            if (selectedCompany != null) {
                try {
                    selectedCompany.eliminarDeBaseDeDatos();
                    companyTableView.getItems().remove(selectedCompany);
                    mostrarAlerta("Empresa eliminada correctamente.");
                } catch (SQLException e) {
                    mostrarAlerta("Error al eliminar la empresa: " + e.getMessage());
                }
            } else {
                mostrarAlerta("Por favor, seleccione una empresa para eliminar.");
            }
        });

        alumnosButton.setOnAction(event -> handleAlumnosButtonClick(event));
        tutoresButton.setOnAction(event -> handleTutoresButtonClick(event));

        // Llenar los ComboBox con los valores de los enumerados
        jornadaComboBox.setItems(FXCollections.observableArrayList(JourneyType.values()));
        modalidadComboBox.setItems(FXCollections.observableArrayList(Modality.values()));

        // Llenar los Combobox de estudiantes
        loadStudentsIntoComboBox();
        loadTutorsIntoComboBox();
        loadCompaniesIntoComboBox();

        //Confirmar asignacion
        confirmacion.setOnAction(event -> createAssignment());

        insertarButton.setDisable(false);
        cancelarModificacion.setVisible(false);
    }

    // Método que valida los campos requeridos
    private boolean validarCampos() {
        return !cifTextField.getText().isEmpty() && !nombreTextField.getText().isEmpty() &&
                !direccionTextField.getText().isEmpty() && !cpTextField.getText().isEmpty() &&
                !localidadTextField.getText().isEmpty() && jornadaComboBox.getValue() != null &&
                modalidadComboBox.getValue() != null && !mailTextField.getText().isEmpty() &&
                !dniRepLegalTextField.getText().isEmpty() && !nombreRepLegalTextField.getText().isEmpty() &&
                !apellidosRepLegalTextField.getText().isEmpty() && !dniTutLaboralTextField.getText().isEmpty() &&
                !nombreTutLaboralTextField.getText().isEmpty() && !apellidosTutLaboralTextField.getText().isEmpty() &&
                !tlfTutLaboralTextField.getText().isEmpty();
    }

    // Método que muestra una alerta
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Validación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Metodo que muestra una alerta satisfactoria
    private void mostrarAlertaSatisfactoria(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operación Exitosa");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void guardarEmpresa(ActionEvent event) {
        if (!validarCampos()) {
            mostrarAlerta("Por favor, rellene todos los campos.");
            return;
        }

        try {
            // Crear y guardar el representante legal
            CompanyManager companyManager = new CompanyManager();
            companyManager.setDni(dniRepLegalTextField.getText());
            companyManager.setName(nombreRepLegalTextField.getText());
            companyManager.setSurname(apellidosRepLegalTextField.getText());
            int companyManagerId = companyManager.guardarEnBaseDeDatos();

            // Crear y guardar el tutor laboral
            CompanyTutor companyTutor = new CompanyTutor();
            companyTutor.setDni(dniTutLaboralTextField.getText());
            companyTutor.setName(nombreTutLaboralTextField.getText());
            companyTutor.setSurname(apellidosTutLaboralTextField.getText());
            companyTutor.setPhone(tlfTutLaboralTextField.getText());
            int companyTutorId = companyTutor.guardarEnBaseDeDatos();

            // Crear y guardar la empresa
            Company company = new Company();
            company.setCif(cifTextField.getText());
            company.setName(nombreTextField.getText());
            company.setAddress(direccionTextField.getText());
            company.setPostalCode(cpTextField.getText());
            company.setCity(localidadTextField.getText());
            company.setJourneyType(jornadaComboBox.getValue());
            company.setModality(modalidadComboBox.getValue());
            company.setEmail(mailTextField.getText());
            company.guardarEnBaseDeDatos(companyManagerId, companyTutorId);

            // Actualizar la tabla
            ObservableList<Company> companyList = FXCollections.observableArrayList(Company.getAllCompanies());
            companyTableView.setItems(companyList);

            // Mostrar mensaje de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Empresa guardada correctamente.", ButtonType.OK);
            alert.showAndWait();

            // Limpiar los campos y habilitar el botón de insertar
            limpiarCampos();
            insertarButton.setDisable(false);
            cancelarModificacion.setVisible(false);

        } catch (SQLException e) {
            mostrarAlerta("Error al guardar la empresa: " + e.getMessage());
        }
    }

    @FXML
    private void cancelarModificacion(ActionEvent event) {
        limpiarCampos();
        insertarButton.setDisable(false);
        cancelarModificacion.setVisible(false);
    }

    private void limpiarCampos() {
        cifTextField.clear();
        nombreTextField.clear();
        direccionTextField.clear();
        cpTextField.clear();
        localidadTextField.clear();
        jornadaComboBox.setValue(null);
        modalidadComboBox.setValue(null);
        mailTextField.clear();
        dniRepLegalTextField.clear();
        nombreRepLegalTextField.clear();
        apellidosRepLegalTextField.clear();
        dniTutLaboralTextField.clear();
        nombreTutLaboralTextField.clear();
        apellidosTutLaboralTextField.clear();
        tlfTutLaboralTextField.clear();
    }

    @FXML
    private void handleAlumnosButtonClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de alumnos");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de datos", "*.dat"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            // Aquí puedes implementar la lógica para leer el archivo .dat y agregar los estudiantes a la base de datos
            try {
                List<Student> students = readStudentsFromFile(selectedFile);
                saveStudentsToDatabase(students);
                mensajeLabel.setText("Estudiantes importados correctamente.");
            } catch (IOException | SQLException e) {
                mensajeLabel.setText("Error al importar estudiantes: " + e.getMessage());
            }
        } else {
            mensajeLabel.setText("No se ha seleccionado ningún archivo.");
        }
    }

    @FXML
    private void handleTutoresButtonClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de profesores");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos XML", "*.xml"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            // Aquí puedes implementar la lógica para leer el archivo XML y agregar los profesores a la base de datos
            try {
                List<Tutor> tutores = readTutoresFromXML(selectedFile);
                saveTutoresToDatabase(tutores);
                mensajeTutorLabel.setText("Profesores importados correctamente.");
            } catch (IOException | SQLException e) {
                mensajeTutorLabel.setText("Error al importar profesores: " + e.getMessage());
            }
        } else {
            mensajeTutorLabel.setText("No se ha seleccionado ningún archivo.");
        }
    }

    private List<Student> readStudentsFromFile(File file) throws IOException {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    int id = Integer.parseInt(parts[0]);
                    String dni = parts[1];
                    String name = parts[2];
                    String surname = parts[3];
                    LocalDate birthdate = LocalDate.parse(parts[4]);
                    students.add(new Student(id, dni, name, surname, birthdate));
                } else {
                    System.out.println("Formato inválido para la línea: " + line);
                }
            }
        }
        return students;
    }

    private List<Tutor> readTutoresFromXML(File file) throws IOException {
        List<Tutor> tutores = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("tutordoc");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                String[] nombreParts = element.getElementsByTagName("nomap").item(0).getTextContent().split(" ");
                String name = nombreParts[0];
                String surname = nombreParts[1];
                String email = element.getElementsByTagName("correo").item(0).getTextContent();
                String phone = element.getElementsByTagName("telefono").item(0).getTextContent();

                Tutor tutor = new Tutor(0, name, surname, email, phone);
                tutores.add(tutor);
            }
        } catch (Exception e) {
            throw new IOException("Error al leer el archivo XML: " + e.getMessage());
        }
        return tutores;
    }

    public static void saveStudentsToDatabase(List<Student> students) throws SQLException {
        String query = "INSERT INTO Student (dni, name, surname, birthdate) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {

            for (Student student : students) {
                statement.setString(1, student.getDni());
                statement.setString(2, student.getName());
                statement.setString(3, student.getSurname());
                statement.setDate(4, java.sql.Date.valueOf(student.getBirthdate()));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void saveTutoresToDatabase(List<Tutor> tutores) throws SQLException {
        String query = "INSERT INTO Tutor (name, surname, email, phone) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            for (Tutor tutor : tutores) {
                statement.setString(1, tutor.getName());
                statement.setString(2, tutor.getSurname());
                statement.setString(3, tutor.getEmail());
                statement.setString(4, tutor.getPhone());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void loadStudentsIntoComboBox() {
        List<Student> students = getStudentsFromDatabase();
        studentComboBox.getItems().addAll(students);
    }

    private void loadTutorsIntoComboBox() {
        List<Tutor> tutors = Tutor.getTutorsFromDatabase();
        tutorComboBox.getItems().addAll(tutors);
    }

    private void loadCompaniesIntoComboBox() {
        List<Company> companies = Company.getAllCompanies();
        empresaComboBox.getItems().addAll(companies);
    }

    private void createAssignment() {
        Student selectedStudent = studentComboBox.getSelectionModel().getSelectedItem();
        Company selectedCompany = empresaComboBox.getSelectionModel().getSelectedItem();
        Tutor selectedTutor = tutorComboBox.getSelectionModel().getSelectedItem();

        if (selectedStudent != null && selectedCompany != null && selectedTutor != null) {
            Assigment assignment = new Assigment(selectedStudent, selectedCompany, selectedTutor);
            Assigment.saveAssignment(assignment);

            String companyTutorName = selectedCompany.getCompanyTutor() != null
                    ? selectedCompany.getCompanyTutor().getName() + " " + selectedCompany.getCompanyTutor().getSurname()
                    : "No asignado";

            String successMessage = String.format(
                    "El alumno %s queda asignado a la empresa %s supervisado por el tutor docente %s y por el tutor laboral %s",
                    selectedStudent.getName() + " " + selectedStudent.getSurname(),
                    selectedCompany.getName(),
                    selectedTutor.getName() + " " + selectedTutor.getSurname(),
                    companyTutorName
            );

            mensajeAsignacion.setText(successMessage);
        } else {
            mensajeAsignacion.setText("Por favor seleccione un estudiante, una empresa y un tutor.");
        }
    }

    private void fillForm(Company company) {
        codigoEmpresaTextField.setText(String.valueOf(company.getId()));
        cifTextField.setText(company.getCif());
        nombreTextField.setText(company.getName());
        direccionTextField.setText(company.getAddress());
        cpTextField.setText(company.getPostalCode());
        localidadTextField.setText(company.getCity());
        jornadaComboBox.setValue(company.getJourneyType());
        modalidadComboBox.setValue(company.getModality());
        mailTextField.setText(company.getEmail());

        CompanyManager companyManager = company.getCompanyManager();
        if (companyManager != null) {
            dniRepLegalTextField.setText(companyManager.getDni());
            nombreRepLegalTextField.setText(companyManager.getName());
            apellidosRepLegalTextField.setText(companyManager.getSurname());
        } else {
            dniRepLegalTextField.clear();
            nombreRepLegalTextField.clear();
            apellidosRepLegalTextField.clear();
        }

        CompanyTutor companyTutor = company.getCompanyTutor();
        if (companyTutor != null) {
            dniTutLaboralTextField.setText(companyTutor.getDni());
            nombreTutLaboralTextField.setText(companyTutor.getName());
            apellidosTutLaboralTextField.setText(companyTutor.getSurname());
            tlfTutLaboralTextField.setText(companyTutor.getPhone());
        } else {
            dniTutLaboralTextField.clear();
            nombreTutLaboralTextField.clear();
            apellidosTutLaboralTextField.clear();
            tlfTutLaboralTextField.clear();
        }

        insertarButton.setDisable(true);
        validateForm(); // Validate form after filling it
    }

    private void validateForm() {
        cancelarModificacion.setVisible(validarCampos());
    }

    @FXML
    private void modificarEmpresa(ActionEvent event) {
        Company empresaSeleccionada = companyTableView.getSelectionModel().getSelectedItem();
        if (empresaSeleccionada != null) {
            // Actualizar los datos de la empresa seleccionada
            empresaSeleccionada.setCif(cifTextField.getText());
            empresaSeleccionada.setName(nombreTextField.getText());
            empresaSeleccionada.setAddress(direccionTextField.getText());
            empresaSeleccionada.setPostalCode(cpTextField.getText());
            empresaSeleccionada.setCity(localidadTextField.getText());
            empresaSeleccionada.setJourneyType(jornadaComboBox.getValue());
            empresaSeleccionada.setModality(modalidadComboBox.getValue());
            empresaSeleccionada.setEmail(mailTextField.getText());

            // Obtener y actualizar los datos del representante legal
            CompanyManager representanteLegal = empresaSeleccionada.getCompanyManager();
            if (representanteLegal != null) {
                representanteLegal.setDni(dniRepLegalTextField.getText());
                representanteLegal.setName(nombreRepLegalTextField.getText());
                representanteLegal.setSurname(apellidosRepLegalTextField.getText());
                // Puedes hacer lo mismo para otros campos del representante legal si es necesario
            }

            // Obtener y actualizar los datos del tutor laboral
            CompanyTutor tutorLaboral = empresaSeleccionada.getCompanyTutor();
            if (tutorLaboral != null) {
                tutorLaboral.setDni(dniTutLaboralTextField.getText());
                tutorLaboral.setName(nombreTutLaboralTextField.getText());
                tutorLaboral.setSurname(apellidosTutLaboralTextField.getText());
                tutorLaboral.setPhone(tlfTutLaboralTextField.getText());
                // Puedes hacer lo mismo para otros campos del tutor laboral si es necesario
            }

            try {
                // Actualizar los datos en la base de datos
                empresaSeleccionada.actualizarEnBaseDeDatos();
                // Mensaje de Exito
                mostrarAlertaSatisfactoria("La Empresa se ha modificado correctamente.");
            } catch (SQLException e) {
                // Manejar cualquier error que pueda ocurrir al actualizar la empresa en la base de datos
                e.printStackTrace();
                mostrarAlerta("Error al actualizar la empresa en la base de datos: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Por favor, seleccione una empresa para modificar.");
        }
    }
}
