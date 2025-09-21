package Controller;

import Models.Appointments;
import Models.DateTimeUtil;
import Models.Doctor;
import Models.Patients;
import Services.Appointments_Service;
import Services.Doctors_Service;
import Services.Patient_Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

public class CreateAppointment {

    @FXML
    private TextField insuranceNumberField;
    @FXML
    private TextArea appointmentNoteArea;
    @FXML
    private TableView<Patients> tblw_SelectedPatient;
    @FXML
    private TableColumn<Appointments, String> InsuranceNumber;
    @FXML
    private TableColumn<Appointments, String> Name;
    @FXML
    private TableColumn<Appointments, String> Family;
    @FXML
    private TableColumn<Appointments, String> Birthdate;
    @FXML
    private TableColumn<Appointments, String> Insurance;
    @FXML
    private ComboBox<Doctor> doctorComboBox;
    @FXML
    private ComboBox<String> timeComboBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button saveButton;

    // Referenz zum Hauptcontroller / Reference to main controller
    private Main_Controller mainController;

    public void setMainController(Main_Controller controller) {
        // Setzt den Hauptcontroller / Sets the main controller
        this.mainController = controller;
    }

    private final Appointments_Service appointmentsService = new Appointments_Service();
    private final Patient_Service patientService = new Patient_Service();

    @FXML
    public void getPatientById() {
        // Holt Patientendaten anhand der Versicherungsnummer und zeigt sie in der Tabelle an
        // Fetches patient data by insurance number and displays it in the table
        String insuranceNumber = insuranceNumberField.getText().trim();

        if (insuranceNumber.isEmpty()) {
            return; // Keine Eingabe, Methode abbrechen / No input, exit method
        }
        Patients patients = patientService.getPatientById(insuranceNumber);
        ObservableList<Patients> patientInfo = FXCollections.observableArrayList();
        if (patients != null) patientInfo.add(patients);

        // Spaltenzuweisung / Set table columns
        InsuranceNumber.setCellValueFactory(new PropertyValueFactory<>("insurance_number"));
        Name.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        Family.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        Birthdate.setCellValueFactory(new PropertyValueFactory<>("birthday"));
        Insurance.setCellValueFactory(new PropertyValueFactory<>("insurance"));

        tblw_SelectedPatient.setItems(patientInfo);
    }

    // Doctor ComboBox
    @FXML
    public void loadDoctorsIntoComboBox() {
        // Lädt alle Ärzte in die ComboBox und stellt Darstellung ein
        // Loads all doctors into the ComboBox and configures display
        List<Doctor> doctorList = new Doctors_Service().getAllDoctors();
        ObservableList<Doctor> observableDoctor = FXCollections.observableArrayList(doctorList);
        doctorComboBox.setItems(observableDoctor);

        // Darstellung in Dropdown / Display in dropdown
        doctorComboBox.setCellFactory(new javafx.util.Callback<>() {
            @Override
            public ListCell<Doctor> call(javafx.scene.control.ListView<Doctor> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Doctor doctor, boolean empty) {
                        super.updateItem(doctor, empty);
                        setText((empty || doctor == null) ? null : doctor.getName() + " " + doctor.getFamily());
                    }
                };
            }
        });

        // Darstellung im Button / Display in button
        doctorComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Doctor doctor, boolean empty) {
                super.updateItem(doctor, empty);
                setText((empty || doctor == null) ? null : doctor.getName() + " " + doctor.getFamily());
            }
        });
    }

    // Appointment ComboBox
    @FXML
    public void handleDateSelection() {
        // Wird aufgerufen, wenn ein Datum gewählt wird und aktualisiert die Uhrzeiten
        // Called when a date is selected and updates available times
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate != null) {
            updateTimeComboBox(selectedDate);
        }
    }

    @FXML
    private void updateTimeComboBox(LocalDate selectedDate) {
        // Aktualisiert die ComboBox mit Uhrzeiten für den gewählten Tag
        // Updates the time ComboBox for the selected date
        List<String> allTimes = DateTimeUtil.appointmentTimePicker();
        List<String> bookedTimes = appointmentsService.getBookedAppointments(selectedDate);
        ObservableList<String> observableTimes = FXCollections.observableArrayList(allTimes);
        timeComboBox.setItems(observableTimes);

        // Formatierung für die Zellen / Cell formatting for booked times
        timeComboBox.setCellFactory(new javafx.util.Callback<>() {
            @Override
            public ListCell<String> call(javafx.scene.control.ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                            setDisable(false);
                            setStyle("");
                        } else if (bookedTimes.contains(item)) {
                            Text text = new Text(item);
                            text.setStrikethrough(true);
                            text.setFill(Color.GREY);
                            setGraphic(text);
                            setText(null);
                            setDisable(true);
                        } else {
                            setText(item);
                            setGraphic(null);
                            setDisable(false);
                            setStyle("-fx-text-fill: black;");
                        }
                    }
                };
            }
        });

        // Darstellung des ausgewählten Werts / Display of selected value
        timeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle(bookedTimes.contains(item) ? "-fx-text-fill: grey; -fx-opacity: 0.6;" : "-fx-text-fill: black;");
                }
            }
        });
    }

    @FXML
    private void createAppointment() {
        // Erstellt einen neuen Termin mit den gewählten Daten
        // Creates a new appointment with the selected data
        String insuranceNumber = insuranceNumberField.getText().trim();
        LocalDate selectedDate = datePicker.getValue();
        String selectedTime = timeComboBox.getValue();
        Doctor selectedDoctor = doctorComboBox.getValue();
        String note = appointmentNoteArea.getText().trim();

        if (insuranceNumber.isEmpty() || selectedDate == null ||
                selectedTime == null || selectedDoctor == null || note.isEmpty()) {
            showAlert("Please fill out all fields");
            return;
        }

        boolean success = appointmentsService.createAppointment(
                insuranceNumber, selectedDoctor.getPersonal_Number(), selectedDate, selectedTime, note
        );

        if (success) {
            showAlert("Appointment successfully created!");
            if (mainController != null) {
                mainController.GetAppointments();
                Stage stage = (Stage) saveButton.getScene().getWindow();
                stage.close();
            }
        } else {
            showAlert("Appointment cannot be created!");
        }
    }

    private void showAlert(String message) {
        // Zeigt eine Informationsmeldung an / Shows an informational alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
