package com.fatec.rfidscanwave.ui.input;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import jfxtras.styles.jmetro.MDL2IconFont;

public class EditableCheckBox extends Group {
    private final MDL2IconFont icon;
    private final ChoiceBox<String> choiceBox;

    public EditableCheckBox(MDL2IconFont icon, ObservableList<String> options){
        this.icon = icon;
        this.choiceBox = new ChoiceBox<>(options);

        choiceBox.setVisible(false);
        icon.setVisible(true);

        getChildren().add(choiceBox);
        getChildren().add(icon);
    }

    public void setIcon(String str){
        icon.setText(str);
    }

    public void update(){
        if(!getChildren().isEmpty())
            getChildren().clear();
    }

    public void initEdit(){
        choiceBox.setVisible(true);
        icon.setVisible(false);
    }

    public void closeEdit(){
        choiceBox.setVisible(false);
        icon.setVisible(true);
    }

    public ChoiceBox getChoiceBox() {
        return choiceBox;
    }

    public MDL2IconFont getIcon() {
        return icon;
    }
}
