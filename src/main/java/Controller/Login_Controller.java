package Controller;

import DataBase.LoginDAO;
import Models.Login;
import Models.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Loading Animation
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class Login_Controller {

    // Logger zur Fehler- und Infoausgabe / Logger for error and info output
    private static final Logger logger = LoggerFactory.getLogger(Login_Controller.class);

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwortField;
    @FXML
    private Label message_Login;
    @FXML
    private ProgressIndicator loadingSpinner;

    @FXML
    private void onLoginButtonClick() {
        // Wird aufgerufen, wenn der Login-Button geklickt wird
        // Called when the login button is clicked
        String user = usernameField.getText();
        String pass = passwortField.getText();

        loadingSpinner.setVisible(true); // Spinner anzeigen / Show loading spinner

        Task<Void> loginTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Prüft Login-Daten in der Datenbank / Check login credentials in DB
                Optional<Login> success = LoginDAO.checkLogin(user, pass);

                if (success.isPresent()) {
                    if (success.get().isLoggedIn()) {
                        // Wenn Benutzer bereits eingeloggt ist / If user is already logged in
                        Platform.runLater(() -> {
                            message_Login.setStyle("-fx-text-fill: red");
                            message_Login.setText("The User " + success.get().getUser() + " is already logged in");
                        });
                    } else {
                        // Benutzer einloggen und Session setzen / Set session and log in user
                        Session.setCurrentUser(success.get());

                        Platform.runLater(() -> {
                            try {
                                // Lade
                                // Dashboard-FXML / Load dashboard FXML
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Pages/Dashboard.fxml"));
                                Scene mainScene = new Scene(loader.load());

                                // CSS hinzufügen / Add CSS
                                URL cssUrl = getClass().getResource("/css/style.css");
                                if (cssUrl != null) {
                                    mainScene.getStylesheets().add(cssUrl.toExternalForm());
                                } else {
                                    logger.error("Css not found");
                                }

                                // Neues Fenster öffnen / Open new stage
                                Stage newStage = new Stage();
                                newStage.setScene(mainScene);
                                newStage.setTitle("Dashboard");
                                newStage.setResizable(true); // UI flexibel / UI flexible
                                newStage.centerOnScreen();
                                newStage.show();

                                // Aktuelles Login-Fenster schließen / Close current login stage
                                Stage loginStage = (Stage) usernameField.getScene().getWindow();
                                loginStage.close();

                            } catch (IOException e) {
                                // Fehler beim Laden des Dashboards / Error loading dashboard
                                logger.error("Fehler beim Laden: {}", e.getMessage(), e);
                                message_Login.setText("Fehler beim Laden der Hauptseite");
                            }
                        });
                    }
                } else {
                    // Login fehlgeschlagen / Login failed
                    Platform.runLater(() -> {
                        loadingSpinner.setVisible(false);
                        message_Login.setStyle("-fx-text-fill: red");
                        message_Login.setText("Username oder Passwort ist ungültig");
                    });
                }

                return null;
            }
        };

        // Starte Login-Task in eigenem Thread / Start login task in separate thread
        Thread loginThread = new Thread(loginTask);
        loginThread.setDaemon(true);
        loginThread.start();
    }
}
