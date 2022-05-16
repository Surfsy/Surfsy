module com.github.louiepietroni.surfsy {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
//    This was added due to the async get for the api
    requires java.net.http;
    requires org.json;
    requires com.sothawo.mapjfx;
    requires com.jfoenix;

    opens com.github.louiepietroni.surfsy to javafx.fxml;
    exports com.github.louiepietroni.surfsy;
    exports com.github.louiepietroni.surfsy.views;
    opens com.github.louiepietroni.surfsy.views to javafx.fxml;
}