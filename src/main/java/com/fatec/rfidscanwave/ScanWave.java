package com.fatec.rfidscanwave;

import com.fatec.rfidscanwave.view.ScanWaveView;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;

public class ScanWave extends Application {
    private AnchorPane root;

    @Override
    public void start(Stage stage) {
        root = new AnchorPane();

        Scene scene = new Scene(
                root,
                650,
                650
        );

        ScanWaveView scanWaveView = new ScanWaveView(this);
        scanWaveView.create();

        JMetro jMetro = new JMetro(root, Style.LIGHT);
        root.getStylesheets().add(ScanWave.class.getResource("/css/reset.css").toString());
        root.getStylesheets().add(ScanWave.class.getResource("/css/style-light.css").toString());

        jMetro.getOverridingStylesheets().add(ScanWave.class.getResource("/css/style-light.css").toString());

        stage.setTitle("Scan Wave");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        launch();
    }

    public AnchorPane getRoot() {
        return root;
    }
}