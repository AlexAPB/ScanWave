package com.fatec.rfidscanwave.view;

import com.fatec.rfidscanwave.ScanWave;
import com.fatec.rfidscanwave.db.ScanWaveDB;
import com.fatec.rfidscanwave.util.ImageUtil;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.JMetroStyleClass;

public class BoardView {
    private final ScanWaveView parent;
    private final ScanWaveDB db;
    private final VBox screen;
    private static Image defaultImage;

    //Header
    private Button returnButton;
    private ImageView userImage;
    private Label nameLabel;
    private Label jobLabel;
    private Label idLabel;
    private Label departmentLabel;
    private Label workShiftLabel;
    private Label workingLabel;

    //Board
    private Label totalWorked;
    private Label overTime;


    public BoardView(ScanWaveView parent, ScanWaveDB db){
        this.parent = parent;
        this.db = db;

        this.screen = new VBox();
        AnchorPane.setBottomAnchor(screen, 20D);
        AnchorPane.setLeftAnchor(screen, 20D);
        AnchorPane.setTopAnchor(screen, 20D);
        AnchorPane.setRightAnchor(screen, 20D);
        this.screen.setFillWidth(true);
        this.screen.setVisible(false);
        this.screen.setDisable(true);
        this.screen.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        VBox.setVgrow(screen, Priority.ALWAYS);
        this.screen.getChildren().add(create());
        parent.getWaveScreen().getChildren().add(screen);
    }

    public VBox create(){
        VBox container = new VBox();
        container.setFillWidth(true);
        VBox.setVgrow(container, Priority.ALWAYS);

        createTop(container);


        return container;
    }

    private void createTop(VBox container){
        HBox main = new HBox();
        main.setId("board-user-container");

        returnButton = new Button();

        if(defaultImage == null){
             defaultImage = new Image(ScanWave.class.getResource("/images/user.png").toString());
        }

        userImage = new ImageView(defaultImage);
        userImage.setSmooth(true);
        userImage.setPreserveRatio(true);
        userImage.setFitHeight(150);
        userImage.setFitWidth(150);

        nameLabel = new Label("Name");
        departmentLabel = new Label("Department");
        jobLabel = new Label("Job");
        idLabel = new Label("Id: -");
        workShiftLabel = new Label("Turno Atual: -");
        workingLabel = new Label("está trabalhando!");

        VBox userInfoVBox = new VBox();
        userInfoVBox.getChildren().add(nameLabel);
        userInfoVBox.getChildren().add(departmentLabel);
        userInfoVBox.getChildren().add(jobLabel);
        userInfoVBox.getChildren().add(idLabel);

        HBox userInfoHBox = new HBox();
        HBox.setHgrow(userInfoHBox, Priority.ALWAYS);
        userInfoHBox.getChildren().add(userImage);
        userInfoHBox.getChildren().add(userInfoVBox);

        VBox workInfoVBox = new VBox();
        workInfoVBox.getChildren().add(workShiftLabel);
        workInfoVBox.getChildren().add(workingLabel);

        HBox workInfoHBox = new HBox();
        HBox.setHgrow(workInfoHBox, Priority.ALWAYS);
        workInfoHBox.getChildren().add(workInfoVBox);

        main.getChildren().add(userInfoHBox);
        main.getChildren().add(workInfoHBox);
        container.getChildren().add(main);
    }

    public ScanWaveView getParent() {
        return parent;
    }

    public ScanWaveDB getDb() {
        return db;
    }

    public VBox getScreen() {
        return screen;
    }
}
