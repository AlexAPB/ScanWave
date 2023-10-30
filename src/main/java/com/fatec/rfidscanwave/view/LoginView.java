package com.fatec.rfidscanwave.view;

import com.fatec.rfidscanwave.ScanWave;
import com.fatec.rfidscanwave.db.ScanWaveDB;
import com.fatec.rfidscanwave.ui.input.Button2;
import com.fatec.rfidscanwave.ui.input.LabelTitle;
import com.fatec.rfidscanwave.ui.input.PassField;
import com.fatec.rfidscanwave.ui.input.TextField2;
import com.fatec.rfidscanwave.util.FXUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.controlsfx.control.textfield.CustomPasswordField;

public class LoginView {
    private final VBox container;
    private final TextField2 loginField;
    private final TextField2 passwordField;
    private final Button2 login;

    public LoginView(final ScanWaveView parent, final ScanWaveDB db){
        container = new VBox();
        container.setBackground(
                new Background(
                        new BackgroundImage(
                                new Image(ScanWave.class.getResource("/images/background.jpg").toString()),
                                BackgroundRepeat.REPEAT,
                                BackgroundRepeat.REPEAT,
                                BackgroundPosition.CENTER,
                                BackgroundSize.DEFAULT
                        )
                )
        );
        container.setAlignment(Pos.CENTER);

        LabelTitle warning = new LabelTitle();
        warning.getStyleClass().add("warning-text");
        warning.setFill(Color.rgb(255, 50, 50));
        warning.setVisible(false);

        LabelTitle loginText = new LabelTitle("Usuário");
        loginText.getStyleClass().add("login-title");
        loginText.setFill(Color.WHITE);

        loginField = new TextField2();
        loginField.setId("login-field");

        VBox loginBox = new VBox();
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setFillWidth(false);
        loginBox.getChildren().add(loginText);
        loginBox.getChildren().add(loginField);

        LabelTitle passwordText = new LabelTitle("Senha");
        passwordText.getStyleClass().add("login-title");
        passwordText.setFill(Color.WHITE);

        passwordField = new TextField2();
        passwordField.setId("login-field");
        passwordField.setSkin(new PassField(passwordField));

        VBox passwordBox = new VBox();
        passwordBox.setAlignment(Pos.CENTER);
        passwordBox.setFillWidth(false);
        passwordBox.getChildren().add(passwordText);
        passwordBox.getChildren().add(passwordField);

        login = new Button2("Entrar");
        login.setId("login-button");
        login.pressedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if(!t1)
                    return;

                if(db.login(loginField.getText().trim(), passwordField.getText().trim())){
                    warning.setVisible(false);
                    loginField.setText("");
                    passwordField.setText("");
                    parent.loadEmployees();
                } else {
                    warning.setVisible(true);
                    warning.setText("Usuário ou senha inválidos!");
                }
            }
        });


        container.getChildren().add(loginBox);
        container.getChildren().add(FXUtil.getSpacer(1, 10));
        container.getChildren().add(passwordBox);
        container.getChildren().add(FXUtil.getSpacer(1, 15));
        container.getChildren().add(login);
        container.getChildren().add(FXUtil.getSpacer(1, 10));
        container.getChildren().add(warning);

        AnchorPane.setTopAnchor(container, 0D);
        AnchorPane.setLeftAnchor(container, 0D);
        AnchorPane.setBottomAnchor(container, 0D);
        AnchorPane.setRightAnchor(container, 0D);

        parent.getWaveScreen().getChildren().add(container);
    }

    public VBox getContainer() {
        return container;
    }
}
