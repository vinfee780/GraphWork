module com.example.graphwork {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;

    opens com.example.graphwork to javafx.fxml, org.junit.platform.commons;
    exports com.example.graphwork;
}