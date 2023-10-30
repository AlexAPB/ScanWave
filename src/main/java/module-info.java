module com.fatec.rfidscanwave {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires mysql.connector.j;

    requires org.jfxtras.styles.jmetro;

    requires org.controlsfx.controls;
    requires se.alipsa.ymp;

    opens com.fatec.rfidscanwave.model to javafx.base;
    opens com.fatec.rfidscanwave.ui.table to javafx.base;

    opens com.fatec.rfidscanwave to javafx.fxml;
    exports com.fatec.rfidscanwave;
    exports com.fatec.rfidscanwave.view;
    opens com.fatec.rfidscanwave.view to javafx.fxml;
}