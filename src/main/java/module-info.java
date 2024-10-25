module org.example.matchinggameclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.example.matchinggameclient.controller to javafx.fxml;
    exports org.example.matchinggameclient;

}