package com.fatec.rfidscanwave.util;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class FXUtil {
    public static Pane getSpacer(int width, int height){
        Pane pane = new Pane();
        pane.setPrefHeight(height);
        pane.setPrefWidth(width);
        return pane;
    }

    public static Pane getDivider(boolean vertical, Color color, float opacity, Insets insets){
        Pane divider = new Pane();
        divider.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, insets)));
        divider.setOpacity(opacity);
        if(vertical)
            divider.setPrefWidth(1);
        else
            divider.setPrefHeight(1);

        return divider;
    }
}
