package Controller;

import DataBase.LoginDAO;
import DataBase.ToDosDAO;
import Models.*;
import Services.Appointments_Service;
import Services.Dashboard_Service;
import Services.Personal_Service;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main_Controller.java
 * Hauptcontroller für Dashboard, Termine, ToDos, Charts und Navigation
 * Main controller for dashboard, appointments, to-dos, charts, and navigation
 */
public class Main_Controller {

    // -------------------- FXML UI Elemente --------------------

    // Dashboard Labels
    @FXML
    private Label lbl_Time, lbl_Date, lbl_next_holiday, lbl_Personal_Number, lbl_Personal;
    // Dashboard labels: time, date, next holiday, personal number and name

    // Appointments TableView und Spalten
    @FXML
    private TableView<Appointments> tblw_appointments;
    @FXML
    private TableColumn<Appointments, Void> number; // Nummer der Zeile
    @FXML
    private TableColumn<Appointments, String> patient; // Patientenname
    @FXML
    private TableColumn<Appointments, String> date_O_B; // Geburtsdatum
    @FXML
    private TableColumn<Appointments, String> doctor; // Arzt
    @FXML
    private TableColumn<Appointments, String> treatment; // Behandlung
    @FXML
    private TableColumn<Appointments, String> appointment_time; // Terminzeit

    // Outages Table (heutige Ausfälle)
    @FXML
    private TableView<Appointments> tblw_Todays_Outages;
    @FXML
    private TableColumn<Appointments, Void> numberOutages;
    @FXML
    private TableColumn<Appointments, String> patientOutages;
    @FXML
    private TableColumn<Appointments, String> date_O_B_Outages;
    @FXML
    private TableColumn<Appointments, String> doctorOutages;
    @FXML
    private TableColumn<Appointments, String> treatmentOutages;
    @FXML
    private TableColumn<Appointments, String> appointment_timeOutages;
    @FXML
    private TableColumn<Appointments, String> statusOutages;

    // Suchfeld für Patienten
    @FXML
    private TextArea searchPatient;

    // Logout Button
    @FXML
    private Button bt_Logout;

    // ToDo TableView und Spalten
    @FXML
    private TableView<ToDos> todoTable;
    @FXML
    private TableColumn<ToDos, String> descriptionColumn;
    @FXML
    private TableColumn<ToDos, String> dueDateColumn;
    @FXML
    private TableColumn<ToDos, String> priorityColumn;

    // Root BorderPane für dynamisches Laden von Views
    @FXML
    private BorderPane rootPane;

    // -------------------- Interne Attribute --------------------

    private final ObservableList<Appointments> appoimentsData = FXCollections.observableArrayList();
    // ObservableList für Termine

    private static final Logger logger = Logger.getLogger(Main_Controller.class.getName());
    // Logger für Fehlerausgabe

    // Service-Objekte
    private final Personal_Service personalService = new Personal_Service(); // Personalservice
    private final Dashboard_Service dashboardService = new Dashboard_Service(); // Dashboardservice
    private final Appointments_Service appointmentsService = new Appointments_Service(); // Terminservice

    // -------------------- Initialisierung --------------------

    /**
     * Initialisierung nach Laden der FXML
     * Called after FXML is loaded
     */
    public void initialize() {
        DashboardHeader(); // Dashboard Header laden
        GetAppointments(); // Termine laden
    }

    /**
     * Dashboard Header initialisieren
     * Load all dashboard header info
     */
    public void DashboardHeader() {
        DateTime(); // Uhrzeit und Datum setzen
        Holidays(); // Nächsten Feiertag laden
        GetPersonalInformation(); // Personalinformationen laden
        LoadToDosInListView(); // ToDos laden
        loadWeeklyAppointmentsToChart(); // Wochenchart laden
        loadBubbleChartData(); // LineChart laden
    }

    /**
     * Datum und Uhrzeit mit Timeline aktualisieren
     * Update date and time with timeline
     */
    private void DateTime() {
        lbl_Time.setText(DateTimeUtil.getCurrentTime()); // Zeit initial setzen

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), _ -> lbl_Time.setText(DateTimeUtil.getCurrentTime())),
                new KeyFrame(Duration.seconds(1))
        );

        timeline.setCycleCount(Timeline.INDEFINITE); // unendlicher Zyklus
        timeline.play();

        lbl_Date.setText(DateTimeUtil.getCurrentDate()); // Datum setzen
    }

    /**
     * Feiertag laden
     * Load next holiday
     */
    private void Holidays() {
        lbl_next_holiday.setText(DateTimeUtil.getHoliday());
    }

    /**
     * Personalinformationen laden
     * Load personal information
     */
    private void GetPersonalInformation() {
        try {
            var personal = dashboardService.getCurrentPersonal()
                    .orElseThrow(() -> new RuntimeException("Not finding a Person"));

            lbl_Personal_Number.setText(personal.getPersonal_Number());
            lbl_Personal.setText(personal.getName() + " " + personal.getFamily());

        } catch (Exception e) {
            logger.severe("Error occurred during load Personal information's: " + e.getMessage());
        }
    }

    /**
     * Alle Termine für heute laden
     * Load all appointments for today
     */
    public void GetAppointments() {
        List<Appointments> openAppointments = appointmentsService.getAppointmentsForToday(
                EnumModels.Appointment_Status.Open, false
        );

        List<Appointments> closedAppointments = appointmentsService.getAppointmentsForToday(
                EnumModels.Appointment_Status.Open, true
        );

        // Callback für Zeilennummern in TableView
        Callback<TableColumn<Appointments, Void>, TableCell<Appointments, Void>> indexCellFactory =
                _ -> new TableCell<>() {
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(String.valueOf(getIndex() + 1));
                        }
                    }
                };

        number.setCellFactory(indexCellFactory);
        numberOutages.setCellFactory(indexCellFactory);

        // CellValueFactory setzen
        patient.setCellValueFactory(new PropertyValueFactory<>("patient"));
        date_O_B.setCellValueFactory(new PropertyValueFactory<>("birthdate"));
        doctor.setCellValueFactory(new PropertyValueFactory<>("doctor"));
        treatment.setCellValueFactory(new PropertyValueFactory<>("treatment"));
        appointment_time.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));

        // Outages Table
        patientOutages.setCellValueFactory(new PropertyValueFactory<>("patient"));
        appointment_timeOutages.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        date_O_B_Outages.setCellValueFactory(new PropertyValueFactory<>("birthdate"));
        doctorOutages.setCellValueFactory(new PropertyValueFactory<>("doctor"));
        treatmentOutages.setCellValueFactory(new PropertyValueFactory<>("treatment"));
        statusOutages.setCellValueFactory(new PropertyValueFactory<>("status"));

        appoimentsData.setAll(openAppointments);
        tblw_appointments.setItems(appoimentsData);

        tblw_Todays_Outages.setItems(FXCollections.observableArrayList(closedAppointments));

        SetupSearchFilter(); // Suchfilter initialisieren
    }

    /**
     * Suchfilter für TableView initialisieren
     * Initialize search filter
     */
    private void SetupSearchFilter() {
        FilteredList<Appointments> filteredData = new FilteredList<>(appoimentsData, _ -> true);

        searchPatient.textProperty().addListener((_, _, newVal) ->
                filteredData.setPredicate(appoiment -> MatchesFilter(appoiment, newVal))
        );

        SortedList<Appointments> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblw_appointments.comparatorProperty());

        tblw_appointments.setItems(sortedData);
    }

    /**
     * Prüft, ob ein Termin den Filtertext enthält
     * Check if appointment matches filter text
     */
    private boolean MatchesFilter(Appointments appoiment, String filterText) {
        if (filterText == null || filterText.isEmpty()) return true;

        String lowerCaseFilter = filterText.toLowerCase();

        return appoiment.getPatient().toLowerCase().contains(lowerCaseFilter)
                || appoiment.getBirthdate().toLowerCase().contains(lowerCaseFilter)
                || appoiment.getDoctor().toLowerCase().contains(lowerCaseFilter)
                || appoiment.getTreatment().toLowerCase().contains(lowerCaseFilter);
    }

    /**
     * ToDos laden
     * Load ToDos into TableView
     */
    private void LoadToDosInListView() {
        List<ToDos> todos = ToDosDAO.getToDosList();
        ObservableList<ToDos> todoObservableList = FXCollections.observableArrayList(todos);

        // Spaltenwerte zuweisen
        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTitle()));

        dueDateColumn.setCellValueFactory(cellData -> {
            Date dueDate = cellData.getValue().getDueDate();
            String dateStr = (dueDate != null) ? dueDate.toString() : "Kein Datum";
            return new SimpleStringProperty(dateStr);
        });

        priorityColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPriority()));

        // Emojis für Priorität über CellFactory
        priorityColumn.setCellFactory(_ -> new TableCell<>() {
            private final Circle circle = new Circle(6);

            @Override
            protected void updateItem(String priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setText(null);
                } else {
                    switch (priority.toUpperCase()) {
                        case "HIGH":
                            circle.setFill(Color.RED);
                            break;
                        case "MEDIUM":
                            circle.setFill(Color.ORANGE);
                            break;
                        case "LOW":
                            circle.setFill(Color.GREEN);
                            break;
                        default:
                            circle.setFill(Color.GRAY);
                    }
                    setText(priority);
                    setGraphic(circle);
                }
            }
        });

        todoTable.setItems(todoObservableList);
    }

    /**
     * Logout Button Action
     * Handles logout process
     */
    @FXML
    private void OnButtonLogout() {
        int loginId = Session.getCurrentUser().getId();
        try {
            LoginDAO.setLogOutState(loginId);
            bt_Logout.getScene().getWindow().hide();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Pages/Login.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.severe("Error occurred during logout process: " + e.getMessage());
        }
    }

    /**
     * Dashboard View laden
     * Load dashboard view
     */
    @FXML
    private void HandleDashboardView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Pages/Dashboard.fxml"));
            Parent newRoot = loader.load();

            // Controller holen und DashboardHeader aufrufen
            Main_Controller controller = loader.getController();
            controller.DashboardHeader();

            BorderPane newRootPane = (BorderPane) newRoot;
            Node centerContent = newRootPane.getCenter();
            rootPane.setCenter(centerContent);
        } catch (IOException e) {
            logger.severe("Error occurred during load Dashboard view process: " + e.getMessage());
        }
    }

    /**
     * Praxis View laden
     * Load Praxis view
     */
    @FXML
    private void HandlePraxisView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Pages/Praxis.fxml"));
            Parent centerContent = loader.load();
            rootPane.setCenter(centerContent);
        } catch (IOException e) {
            logger.severe("Error occurred during load Praxis view process: " + e.getMessage());
        }
    }

    /**
     * Labour View laden
     * Load Labour view
     */
    @FXML
    private void HandleLaboarView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Pages/Labour.fxml"));
            Parent centerContent = loader.load();
            rootPane.setCenter(centerContent);
        } catch (IOException e) {
            logger.severe("Error occurred during load Labour view process: " + e.getMessage());
        }
    }

    /**
     * Nächster Termin bearbeiten
     * Update next appointment status
     */
    @FXML
    private void NextAppointment() {
        if (tblw_appointments.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Appointment for today");
            alert.setHeaderText(null);
            alert.setContentText("There are no appointments available.");
            alert.showAndWait();
            return;
        }

        Appointments selectedAppointment = tblw_appointments.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select an appointment first.");
            alert.showAndWait();
            return;
        }

        List<EnumModels.Appointment_Status> statusOptions = Arrays.asList(EnumModels.Appointment_Status.values());
        ChoiceDialog<EnumModels.Appointment_Status> dialog = new ChoiceDialog<>(EnumModels.Appointment_Status.Completed, statusOptions);
        dialog.setTitle("Appointment Status");
        dialog.setHeaderText("Please select the status for the appointment");
        dialog.setContentText("Status:");

        Optional<EnumModels.Appointment_Status> result = dialog.showAndWait();

        result.ifPresent(selectedStatus -> {
            boolean success = appointmentsService.nextAppointment(selectedAppointment.getId(), selectedStatus);
            if (success) {
                appoimentsData.remove(selectedAppointment);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("The appointment status was successfully updated to: " + selectedStatus);
                alert.showAndWait();

                if (!tblw_appointments.getItems().isEmpty()) {
                    tblw_appointments.getSelectionModel().selectFirst();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to update the appointment status.");
                alert.showAndWait();
            }
        });
    }

    /**
     * Neuen Termin erstellen
     * Open Create Appointment window
     */
    @FXML
    private void CreateAppointment() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Pages/CreateAppointment.fxml"));
            Parent root = fxmlLoader.load();

            CreateAppointment controller = fxmlLoader.getController();
            controller.setMainController(this); // MainController referenz setzen

            Stage stage = new Stage();
            stage.setTitle("Create Appointment");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            logger.severe("Error occurred during create appointment process: " + e.getMessage());
        }
    }

    /**
     * Termin löschen
     * Delete selected appointment
     */
    @FXML
    private void DeleteAppointment() {
        Appointments selectedAppointment = tblw_appointments.getSelectionModel().getSelectedItem();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this appointment?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            if (selectedAppointment == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No Row Selected ");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }

            boolean deleted = appointmentsService.deleteAppointment(selectedAppointment.getId());

            if (deleted) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Appointment Deleted");
                alert.setHeaderText(null);
                alert.setContentText("The appointment was successfully deleted.");
                alert.showAndWait();
                appoimentsData.remove(selectedAppointment);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Deletion Failed");
                alert.setHeaderText(null);
                alert.setContentText("The appointment could not be deleted.");
                alert.showAndWait();
            }
        }
    }

    // -------------------- Charts --------------------

    @FXML
    private BarChart<String, Number> barChartWeek;
    @FXML
    private CategoryAxis xAxisWeek;
    @FXML
    private NumberAxis yAxisWeek;

    /**
     * Wochenchart laden
     * Load weekly appointments chart
     */
    private void loadWeeklyAppointmentsToChart() {
        Task<Map<String, Integer>> task = new Task<>() {
            @Override
            protected Map<String, Integer> call() {
                return appointmentsService.getWeeklyAppointmentsCount();
            }
        };
        task.setOnSucceeded(_ -> {
            Map<String, Integer> result = task.getValue();
            Platform.runLater(() -> populateBarChart(result));
        });

        task.setOnFailed(_ -> {
            Throwable ex = task.getException();
            logger.log(Level.SEVERE, "Task failed", ex);
        });

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    private void populateBarChart(Map<String, Integer> data) {
        barChartWeek.getData().clear();
        ObservableList<String> categories = FXCollections.observableArrayList(
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        );
        xAxisWeek.setCategories(categories);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Appointments (this week)");
        data.forEach((day, count) -> series.getData().add(new XYChart.Data<>(day, count)));

        barChartWeek.getData().add(series);

        int max = data.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        yAxisWeek.setAutoRanging(false);
        yAxisWeek.setLowerBound(0);
        yAxisWeek.setUpperBound(Math.max(1, max + 1));
        yAxisWeek.setTickUnit(1);
    }

    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private CategoryAxis xAxis;

    /**
     * LineChart laden
     * Load monthly appointments chart
     */
    private void loadBubbleChartData() {
        Map<String, Integer> monthlyData = appointmentsService.getAppointmentCountByMonthCurrentYear();

        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        ObservableList<String> categories = FXCollections.observableArrayList(months);
        xAxis.setCategories(categories);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Appointments (this year)");

        for (String month : months) {
            int count = monthlyData.getOrDefault(month, 0);
            series.getData().add(new XYChart.Data<>(month, count));
        }

        lineChart.getData().clear();
        lineChart.getData().add(series);
    }
}
