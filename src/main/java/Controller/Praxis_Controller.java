package Controller;

import Models.EnumModels;
import Models.User;
import Services.Doctors_Service;
import Services.Personal_Service;
import Services.Praxis_Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

public class Praxis_Controller {
    // Logger für Debugging und Fehlermeldungen
    // Logger for debugging and error messages
    private static final Logger logger = LoggerFactory.getLogger(Praxis_Controller.class);

    // ==================== FXML-Felder ====================
    // Eingabefeld für Monat/Jahr (z. B. 09/2025)
    // Input field for month/year (e.g., 09/2025)
    @FXML
    private TextField monthYearField;

    // Eingabefelder für Personal- und Kontaktdaten
    // Input fields for personal and contact information
    @FXML private TextField nameField, emailField, familyField, passwordField,
            addressField, cityField, cityCodeField, personalNumberField,
            phoneNumberField, workingSinceField;

    // Auswahlfeld für Urlaubsart (EnumModels.VacationType)
    // Dropdown for selecting vacation type (EnumModels.VacationType)
    @FXML
    private ComboBox<EnumModels.VacationType> vacationTypeComboBox;

    // Service-Objekte für Datenzugriff und Business-Logik
    // Service objects for data access and business logic
    private final Personal_Service personalService = new Personal_Service();
    private final Doctors_Service doctorsService = new Doctors_Service();
    private final Praxis_Service praxisService = new Praxis_Service();

    // ==================== Tabellen ====================
    // Tabelle für Urlaubsanfragen
    // Table for vacation requests
    @FXML
    private TableView<ObservableList<String>> ReqVacationTable;

    // Tabelle für genehmigte Urlaube
    // Table for approved vacations
    @FXML
    private TableView<ObservableList<String>> vacationTable;

    // ScrollPane für die VacationTable
    // ScrollPane for the vacationTable
    private ScrollPane tableScrollPane;

    // DatePicker für Urlaubszeitraum
    // DatePickers for vacation period
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;

    // ==================== Initialisierung ====================
    // Initialisiert den Controller nach dem Laden der FXML
    // Initializes the controller after the FXML is loaded
    @FXML
    public void initialize() {
        loadCurrentUserData(); // Lade aktuelle Benutzerdaten

        vacationTypeComboBox.getItems().setAll(EnumModels.VacationType.values());

        // ScrollPane für die TableView konfigurieren
        tableScrollPane = new ScrollPane();
        tableScrollPane.setContent(vacationTable);
        tableScrollPane.setFitToHeight(true);
        tableScrollPane.setFitToWidth(false); // NICHT automatisch strecken
        tableScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        tableScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        vacationTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Aktueller Monat im Feld anzeigen
        YearMonth now = YearMonth.now();
        monthYearField.setText(String.format("%02d/%d", now.getMonthValue(), now.getYear()));
        loadVacationTable(now.getYear(), now.getMonthValue()); // Tabelle laden

        // Tabelle der Urlaubsanfragen laden
        loadReqVacationTable();
    }

    // ==================== Urlaubsanfrage ====================
    // Event-Handler zum Absenden einer Urlaubsanfrage
    // Event handler to submit a vacation request
    @FXML
    private void submitVacationBtn() {
        EnumModels.VacationType type = vacationTypeComboBox.getValue();
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        // Prüfen, ob alle Felder ausgewählt sind
        // Check if all fields are selected
        if (type == null || fromDate == null || toDate == null) {
            showAlert("Error", "Please select vacation type and dates.");
            return;
        }

        // 2️⃣ Datum validieren: Enddatum darf nicht vor Startdatum liegen
        // Validate dates: end date cannot be before start date
        if (toDate.isBefore(fromDate)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "End date cannot be before start date!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Urlaubsanfrage an Praxis_Service senden
        // Send vacation request to Praxis_Service
        boolean success = praxisService.requestVacationForCurrentUser(type, fromDate, toDate, "");

        if (success) {
            showAlert("Success", "Vacation request submitted!");
            // Eingabefelder zurücksetzen
            vacationTypeComboBox.getSelectionModel().clearSelection();
            fromDatePicker.setValue(null);
            toDatePicker.setValue(null);
            YearMonth ym = YearMonth.now();
            loadVacationTable(ym.getYear(), ym.getMonthValue()); // Tabelle aktualisieren
        } else {
            showAlert("Error", "Failed to submit vacation request.");
        }
    }

    // ==================== Hilfsmethoden ====================
    // Zeigt einen Alert an
    // Shows an alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ==================== Urlaub anzeigen ====================
    // Lädt die Urlaubstabelle für einen bestimmten Monat
    // Loads the vacation table for a specific month
    private void loadVacationTable(int year, int month) {
        vacationTable.getColumns().clear();
        setupVacationColumns(year, month);

        ObservableList<ObservableList<String>> tableRows = buildVacationRows(year, month);
        vacationTable.setItems(tableRows);

        // Gesamtbreite der Tabelle berechnen
        // Set total width of table
        double totalWidth = 120 + praxisService.getDaysInMonth(year, month) * 30;
        vacationTable.setPrefWidth(totalWidth);
        vacationTable.setMinWidth(totalWidth);
        vacationTable.setMaxWidth(totalWidth);
    }

    // Spalten für Urlaubstabelle erzeugen (Personal + Tage)
    // Set up columns for vacation table (personal + days)
    private void setupVacationColumns(int year, int month) {
        int daysInMonth = praxisService.getDaysInMonth(year, month);

        TableColumn<ObservableList<String>, String> personalCol = new TableColumn<>("Personal");
        personalCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().get(0))
        );
        personalCol.setPrefWidth(120);
        vacationTable.getColumns().add(personalCol);

        double dayColWidth = 30;
        for (int i = 1; i <= daysInMonth; i++) {
            final int colIndex = i;
            TableColumn<ObservableList<String>, String> dayCol = new TableColumn<>(String.valueOf(i));
            dayCol.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().get(colIndex))
            );
            dayCol.setPrefWidth(dayColWidth);
            dayCol.setCellFactory(col -> createDayCellFactory());
            vacationTable.getColumns().add(dayCol);
        }
    }

    // Baut die Datenzeilen für die Urlaubstabelle auf
    // Builds data rows for vacation table
    private ObservableList<ObservableList<String>> buildVacationRows(int year, int month) {
        var dbData = praxisService.getVacationData(year, month);
        int daysInMonth = praxisService.getDaysInMonth(year, month);

        Map<String, ObservableList<String>> rowsByPerson = new LinkedHashMap<>();

        for (var person : dbData) {
            String personalName = (String) person.get("Personal");
            java.time.LocalDate start = (java.time.LocalDate) person.get("Start_Date");
            java.time.LocalDate end = (java.time.LocalDate) person.get("End_Date");

            String typeStr = (String) person.get("VacationType");
            EnumModels.VacationType vacationType = null;
            if (typeStr != null) {
                try {
                    vacationType = EnumModels.VacationType.valueOf(typeStr);
                } catch (IllegalArgumentException e) {
                    logger.warn("Unbekannter VacationType: {}", typeStr);
                }
            }

            rowsByPerson.putIfAbsent(personalName, initRow(personalName, daysInMonth));
            var row = rowsByPerson.get(personalName);

            // Urlaubstage markieren
            // Mark vacation days
            if (start != null && end != null && vacationType != null) {
                for (int day = 1; day <= daysInMonth; day++) {
                    java.time.LocalDate current = java.time.LocalDate.of(year, month, day);
                    if (!current.isBefore(start) && !current.isAfter(end)) {
                        int idx = day;
                        String symbol = mapVacationTypeToSymbol(vacationType);
                        String existing = row.get(idx);
                        if (existing == null || existing.isEmpty()) {
                            row.set(idx, symbol);
                        } else {
                            row.set(idx, resolveConflict(existing, symbol));
                        }
                    }
                }
            }
        }

        return FXCollections.observableArrayList(rowsByPerson.values());
    }

    // Initialisiert eine neue Zeile für eine Person
    // Initializes a new row for a person
    private ObservableList<String> initRow(String personalName, int daysInMonth) {
        ObservableList<String> row = FXCollections.observableArrayList();
        row.add(personalName);
        for (int i = 1; i <= daysInMonth; i++) row.add("");
        return row;
    }

    // Mappt VacationType auf Symbol (V, T, S, H)
    // Maps VacationType to symbol (V, T, S, H)
    private String mapVacationTypeToSymbol(EnumModels.VacationType type) {
        return switch (type) {
            case VACATION -> "V";
            case TIME_OFF -> "T";
            case SICK_LEAVE -> "S";
            case HALF_DAY -> "H";
            default -> "";
        };
    }

    // Konflikte zwischen Urlaubstypen auflösen
    // Resolve conflicts between vacation types
    private String resolveConflict(String existing, String symbol) {
        if (existing.equals("S") || symbol.equals("S")) return "S";
        if (existing.equals("H") || symbol.equals("H")) return "H";
        return existing;
    }

    // Erzeugt TableCell mit Hintergrundfarbe je nach Urlaubsart
    // Creates TableCell with background color depending on vacation type
    private TableCell<ObservableList<String>, String> createDayCellFactory() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "V", "T" -> setStyle("-fx-background-color: #90ee90");
                        case "S" -> setStyle("-fx-background-color: #ffff99");
                        case "H" -> setStyle("-fx-background-color: #d3d3d3");
                        default -> setStyle("");
                    }
                }
            }
        };
    }

    // ==================== Monat/Jahr Picker ====================
    // Zeigt Popup zur Auswahl von Monat und Jahr
    // Shows popup to select month and year
    @FXML
    private void showMonthYearPicker() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Select Month/Year");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        HBox yearBox = new HBox(10);
        Label yearLabel = new Label(String.valueOf(YearMonth.now().getYear()));
        Button prevYear = new Button("<");
        Button nextYear = new Button(">");
        prevYear.setOnAction(e -> yearLabel.setText(String.valueOf(Integer.parseInt(yearLabel.getText()) - 1)));
        nextYear.setOnAction(e -> yearLabel.setText(String.valueOf(Integer.parseInt(yearLabel.getText()) + 1)));
        yearBox.getChildren().addAll(prevYear, yearLabel, nextYear);

        GridPane monthsGrid = new GridPane();
        monthsGrid.setHgap(10);
        monthsGrid.setVgap(10);
        String[] monthNames = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        for (int i = 0; i < 12; i++) {
            Button monthBtn = new Button(monthNames[i]);
            monthBtn.setPrefWidth(60);
            int monthIndex = i + 1;
            monthBtn.setOnAction(ev -> {
                int selectedMonth = monthIndex;
                int selectedYear = Integer.parseInt(yearLabel.getText());
                monthYearField.setText(String.format("%02d/%d", selectedMonth, selectedYear));
                loadVacationTable(selectedYear, selectedMonth);
                popup.close();
            });
            monthsGrid.add(monthBtn, i % 3, i / 3);
        }

        layout.getChildren().addAll(yearBox, monthsGrid);
        Scene scene = new Scene(layout);
        popup.setScene(scene);
        popup.showAndWait();
    }

    // ==================== Aktueller User ====================
    // Lädt aktuelle Benutzerdaten und füllt die Eingabefelder
    // Loads current user data and fills input fields
    private void loadCurrentUserData() {
        personalService.getCurrentPersonal().ifPresentOrElse(
                this::fillFields,
                () -> doctorsService.getCurrentDoctor().ifPresentOrElse(
                        this::fillFields,
                        () -> logger.error("No user info found for current session")
                )
        );
    }

    // Füllt Eingabefelder mit User-Daten
    // Fills input fields with user data
    private void fillFields(User user) {
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        familyField.setText(user.getFamily());
        passwordField.setText(user.getPassword());
        addressField.setText(user.getAddress());
        cityField.setText(user.getCity());
        cityCodeField.setText(user.getCity_Code());
        personalNumberField.setText(user.getPersonal_Number());
        phoneNumberField.setText(user.getPhone_Number());
        if (user.getWorking_Since() != null) workingSinceField.setText(user.getWorking_Since().toString());
    }

    // ==================== Urlaubsanfragen Tabelle ====================
    // Lädt Tabelle der eigenen Urlaubsanfragen
    // Loads table of own vacation requests
    private void loadReqVacationTable() {
        ReqVacationTable.getColumns().clear();

        // Spalten definieren
        TableColumn<ObservableList<String>, String> fromCol = new TableColumn<>("From");
        fromCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));

        TableColumn<ObservableList<String>, String> toCol = new TableColumn<>("To");
        toCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));

        TableColumn<ObservableList<String>, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(2)));

        ReqVacationTable.getColumns().addAll(fromCol, toCol, statusCol);

        // Daten aus DB laden
        var dbData = praxisService.getVacationRequestsForCurrentUser();
        ObservableList<ObservableList<String>> rows = FXCollections.observableArrayList();

        for (var row : dbData) {
            ObservableList<String> observableRow = FXCollections.observableArrayList(
                    row.get("From_Date").toString(),
                    row.get("To_Date").toString(),
                    row.get("Status").toString()
            );
            rows.add(observableRow);
        }

        ReqVacationTable.setItems(rows);
    }
}
