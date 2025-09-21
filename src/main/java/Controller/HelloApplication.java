package Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Pages/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 960, 560);
        stage.setTitle("Praxis Management");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }






}