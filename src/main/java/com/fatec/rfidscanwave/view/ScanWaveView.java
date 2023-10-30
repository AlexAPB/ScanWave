package com.fatec.rfidscanwave.view;

import com.fatec.rfidscanwave.ScanWave;
import com.fatec.rfidscanwave.db.ScanWaveDB;
import com.fatec.rfidscanwave.model.EmployeeModel;
import com.fatec.rfidscanwave.ui.animation.Animation;
import javafx.scene.layout.*;

public class ScanWaveView {
    private final ScanWave parent;
    private final ScanWaveDB db;
    private AnchorPane waveScreen;
    private LoginView loginView;
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

        loginView = new LoginView(this, db);
        employeesView = new EmployeesView(this, db);
        boardView = new BoardView(this, db);

        parent.getRoot().getChildren().add(waveScreen);
    }

    public void loadEmployee(EmployeeModel employee){
        boardView.updateView(employee);
        Animation.slideScreen(employeesView.getScreen(), boardView.getScreen(), false);
    }

    public void loadEmployees(){
        Animation.slideScreen(loginView.getContainer(), employeesView.getScreen(), false);
        employeesView.load();
    }

    public void loadBackEmployees(){
        Animation.slideScreen(boardView.getScreen(), employeesView.getScreen(), true);
    }

    public void loadLogin(){
        Animation.slideScreen(employeesView.getScreen(), loginView.getContainer(), true);
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