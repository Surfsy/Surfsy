module com.github.louiepietroni.surfsy {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens com.github.louiepietroni.surfsy to javafx.fxml;
    exports com.github.louiepietroni.surfsy;
}