package com.fatec.rfidscanwave.ui.graphics;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.fatec.rfidscanwave.util.FXUtil.getSpacer;

public class HalfCircleGraph extends HBox {
    public HalfCircleGraph(){
        super();
    }

    public void unload(){
        if(!getChildren().isEmpty())
            getChildren().clear();
    }

    public void load(Color backgroundColor, int radius, GraphicContent... val1){
        List<GraphicContent> content = Arrays.stream(val1).toList().stream().sorted(Comparator.comparingInt(GraphicContent::getVal1).reversed()).toList();

        createGraphics(backgroundColor, radius, content);
        createInformation(Arrays.stream(val1).toList());
    }

    private void createGraphics(Color backgroundColor, int radius, List<GraphicContent> contents){
        Group group = new Group();

        int sum = 0;

        for(int i = 0; i< contents.size(); i++){
            sum += contents.get(i).getVal1();

            for(int j = i; j < contents.size(); j++){
                contents.get(i).addVal4(contents.get(j).getVal1());
            }
        }

        for (GraphicContent c : contents) {
            Arc arc = new Arc();
            arc.setSmooth(true);
            arc.setType(ArcType.ROUND);
            arc.setStartAngle(0);
            arc.setStrokeWidth(0);
            arc.setFill(c.getVal3());
            arc.setRadiusX(radius);
            arc.setRadiusY(radius);
            arc.setLength(((double) c.getVal4() / sum * 180));
            group.getChildren().add(arc);
        }

        Arc background = new Arc();
        background.setSmooth(true);
        background.setStartAngle(0);
        background.setStrokeWidth(0);
        background.setRadiusX(radius * 0.45);
        background.setRadiusY(radius * 0.45);
        background.setLength(180);
        background.setFill(backgroundColor);

        group.getChildren().add(background);

        getChildren().add(group);
        getChildren().add(getSpacer(20, 1));
    }

    private void createInformation(List<GraphicContent> contents){
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER_LEFT);

        for(GraphicContent c : contents){
            GraphicInformation information = new GraphicInformation(c.getVal3(), Color.BLACK, c.getVal2());
            information.setPadding(new Insets(0, 0, 5, 0));
            vBox.getChildren().add(information);
        }

        getChildren().add(vBox);
    }
}
