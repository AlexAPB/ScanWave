package com.fatec.rfidscanwave.ui.animation;

import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animation {
    public static void slideScreen(Node slideOut, Node slideIn, boolean returning){
        ParallelTransition parallelTransition = new ParallelTransition();

        TranslateTransition slideOutTransition = new TranslateTransition();
        slideOutTransition.setNode(slideOut);
        slideOutTransition.setFromX(0);
        slideOutTransition.setToX(-slideOut.getLayoutBounds().getWidth());
        slideOutTransition.setDuration(Duration.millis(500));

        TranslateTransition slideInTransition = new TranslateTransition();
        slideInTransition.setNode(slideIn);
        slideInTransition.setFromX(slideIn.getLayoutBounds().getWidth());
        slideInTransition.setToX(0);
        slideInTransition.setDuration(Duration.millis(500));

        if(returning){
            slideOutTransition.setToX(Math.abs(slideOutTransition.getToX()));
            slideInTransition.setFromX(-slideInTransition.getFromX());
        }

        slideIn.setVisible(true);
        slideOut.setVisible(true);
        slideOut.setDisable(true);

        parallelTransition.getChildren().add(slideOutTransition);
        parallelTransition.getChildren().add(slideInTransition);
        parallelTransition.play();

        parallelTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                slideOut.setVisible(false);
                slideOut.setVisible(true);
                slideIn.setDisable(false);
            }
        });
    }
}
