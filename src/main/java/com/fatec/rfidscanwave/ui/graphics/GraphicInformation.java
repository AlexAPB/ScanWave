package com.fatec.rfidscanwave.ui.graphics;

import com.fatec.rfidscanwave.ui.input.LabelTitle;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import static com.fatec.rfidscanwave.util.FXUtil.getSpacer;

public class GraphicInformation extends HBox {
    private Rectangle filledRectangle;
    private LabelTitle description;

    public GraphicInformation(Color rectColor, Color stroke, String description){
        super();

        this.filledRectangle = new Rectangle();
        this.filledRectangle.setWidth(13);
        this.filledRectangle.setHeight(13);
        this.filledRectangle.setArcWidth(4);
        this.filledRectangle.setArcHeight(4);
        this.filledRectangle.setFill(rectColor);
        if(stroke != null) {
            this.filledRectangle.setStroke(stroke);
            this.filledRectangle.setStrokeWidth(0);
            this.filledRectangle.setStrokeType(StrokeType.OUTSIDE);
        }

        this.description = new LabelTitle(description);
        this.description.setStyle("-fx-font-size: 14px;");
        this.description.setOpacity(0.75);

        getChildren().add(this.filledRectangle);
        getChildren().add(getSpacer(5, 1));
        getChildren().add(this.description);
        setAlignment(Pos.CENTER_LEFT);
    }

    public LabelTitle getDescription() {
        return description;
    }

    public Rectangle getFilledRectangle() {
        return filledRectangle;
    }
}
