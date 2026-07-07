module com.example.gestionemployer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.desktop;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.slf4j;

    opens com.example.gestionemployer to javafx.graphics;
    opens com.example.gestionemployer.controller to javafx.fxml;
    opens com.example.gestionemployer.model to javafx.fxml;

    exports com.example.gestionemployer;
    exports com.example.gestionemployer.controller;
    exports com.example.gestionemployer.model;
}