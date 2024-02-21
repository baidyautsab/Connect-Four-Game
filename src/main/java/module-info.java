module com.example.connectforegame {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.connectforegame to javafx.fxml;
    exports com.example.connectforegame;
}