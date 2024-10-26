module org.example.matchinggameclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires jdk.compiler;


    opens org.example.matchinggameclient.controller to javafx.fxml;
    exports org.example.matchinggameclient;

}