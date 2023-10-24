package com.fatec.rfidscanwave;

import com.fatec.rfidscanwave.view.ScanWaveView;
import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Theme;
import io.github.palexdev.materialfx.css.themes.Themes;
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

import java.awt.*;
import java.io.IOException;

public class ScanWave extends Application {
    private AnchorPane root;

    @Override
    public void start(Stage stage) {
        root = new AnchorPane();

        Scene scene = new Scene(
                root,
                Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.9,
                Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.9
        );

        ScanWaveView scanWaveView = new ScanWaveView(this);
        scanWaveView.create();

        JMetro jMetro = new JMetro(root, Style.LIGHT);
        jMetro.getOverridingStylesheets().add(ScanWave.class.getResource("/css/reset.css").toString());

        if(jMetro.getStyle() == Style.LIGHT){
            jMetro.getOverridingStylesheets().add(ScanWave.class.getResource("/css/style-light.css").toString());
        } else {
            jMetro.getOverridingStylesheets().add(ScanWave.class.getResource("/css/style-dark.css").toString());
        }

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