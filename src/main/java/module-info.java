module com.example.managment_demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires mysql.connector.j;
    requires org.slf4j;
    requires jollyday;
    requires java.desktop;
    requires java.net.http;
    requires javafx.media;
    requires jdk.compiler;

    opens com.example.managment_demo to javafx.fxml;
    opens Controller to javafx.fxml;


    opens Models to javafx.base;

    exports com.example.managment_demo;
    exports Controller;
}
