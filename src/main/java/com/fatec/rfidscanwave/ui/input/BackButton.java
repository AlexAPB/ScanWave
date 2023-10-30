package com.fatec.rfidscanwave.ui.input;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import jfxtras.styles.jmetro.MDL2IconFont;

public class BackButton extends HBox {
    public BackButton() {
        LabelTitle title = new LabelTitle("Voltar");
        title.setStyle("-fx-font-size: 18px");

        MDL2IconFont icon = new MDL2IconFont("\uE7EA");
        icon.setStyle("-fx-font-size: 18px; -fx-padding: 0px 10px 0px 0px;");

        setFillHeight(false);
        getStyleClass().add("back-container");
        setAlignment(Pos.CENTER_LEFT);
        getChildren().add(icon);
        getChildren().add(title);
    }
}
