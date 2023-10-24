package com.fatec.rfidscanwave.view;

import com.fatec.rfidscanwave.ScanWave;
import com.fatec.rfidscanwave.controller.ClockController;
import com.fatec.rfidscanwave.controller.ScanWaveController;
import com.fatec.rfidscanwave.db.ScanWaveDB;
import com.fatec.rfidscanwave.model.EmployeeModel;
import com.fatec.rfidscanwave.ui.Animation;
import io.github.palexdev.materialfx.builders.control.ButtonBuilder;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.css.themes.Themes;
import io.github.palexdev.materialfx.enums.ButtonType;
import io.github.palexdev.materialfx.skins.MFXButtonSkin;
import io.github.palexdev.mfxeffects.ripple.base.RippleGenerator;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;

public class ScanWaveView {
    private final ScanWave parent;
    private final ScanWaveDB db;
    private AnchorPane waveScreen;
    private EmployeesView employeesView;
    private BoardView boardView;

    public ScanWaveView(ScanWave parent){
        this.parent = parent;
        this.db = new ScanWaveDB();
    }

    public void create(){
        waveScreen = new AnchorPane();
        AnchorPane.setRightAnchor(waveScreen, 0D);
        AnchorPane.setTopAnchor(waveScreen, 0D);
        AnchorPane.setLeftAnchor(waveScreen, 0D);
        AnchorPane.setBottomAnchor(waveScreen, 0D);

        employeesView = new EmployeesView(this, db);
        boardView = new BoardView(this, db);

        parent.getRoot().getChildren().add(waveScreen);
    }

    public void loadEmployee(EmployeeModel employee){
        Animation.slideScreen(employeesView.getScreen(), boardView.getScreen(), false);
    }

    public AnchorPane getWaveScreen() {
        return waveScreen;
    }

    public ScanWave getParent() {
        return parent;
    }

    public EmployeesView getEmployeesView() {
        return employeesView;
    }
}