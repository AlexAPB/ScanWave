package com.fatec.rfidscanwave.ui.input;

import com.fatec.rfidscanwave.exception.ImpossibleCast;
import com.fatec.rfidscanwave.exception.InvalidDateFormat;
import com.fatec.rfidscanwave.model.ClockDayModel;
import com.fatec.rfidscanwave.util.FXUtil;
import javafx.scene.Group;
import javafx.scene.layout.HBox;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class EditableText extends Group {
    private TextField2 editField1;
    private TextField2 editField2;
    private HBox editContainer;
    private ClockDayModel.ClockLabel text;

    public EditableText(ClockDayModel.ClockLabel clockLabel){
        text = clockLabel;
        text.setVisible(true);

        editField1 = new TextField2();
        editField1.setMaxWidth(60);

        if(clockLabel.getIn() != null)
            editField1.setText(clockLabel.getIn().getText());

        editField2 = new TextField2();
        editField2.setMaxWidth(60);

        if(clockLabel.getOut() != null)
            editField2.setText(clockLabel.getOut().getText());

        editContainer = new HBox();
        editContainer.getChildren().add(editField1);
        editContainer.getChildren().add(FXUtil.getSpacer(10, 1));
        editContainer.getChildren().add(editField2);
        editContainer.setVisible(false);
        editContainer.setDisable(true);

        getChildren().add(text);
        getChildren().add(editContainer);
    }

    public void initEdit(){
        editContainer.setDisable(false);
        editContainer.setVisible(true);
        text.setVisible(false);
    }

    public boolean isValid(boolean isIn) throws InvalidDateFormat {
        try{
            String[] date1String = null;
            String[] date2String = null;

            if(!editField1.getText().trim().isBlank())
                date1String = editField1.getText().trim().split(":");

            if(!editField2.getText().trim().isBlank())
                date2String = editField2.getText().trim().split(":");

            if(date1String != null && date1String[0].equals("--") && date1String[1].equals("--"))
                date1String = null;

            if(date2String != null && date2String[0].equals("--") && date2String[1].equals("--"))
                date2String = null;


            if(isIn) {
                if (Integer.parseInt(date1String[0]) > 23 || Integer.parseInt(date1String[0]) < 0 ||
                        Integer.parseInt(date1String[1]) < 0 || Integer.parseInt(date1String[1]) > 59)
                    throw new Exception();

                if(date2String != null) {
                    if (Integer.parseInt(date2String[0]) > 23 || Integer.parseInt(date2String[0]) < 0 ||
                            Integer.parseInt(date2String[1]) < 0 || Integer.parseInt(date2String[1]) > 59)
                        throw new Exception();
                }
            } else {
                if (date1String != null) {
                    if (Integer.parseInt(date1String[0]) > 23 || Integer.parseInt(date1String[0]) < 0 ||
                            Integer.parseInt(date1String[1]) < 0 || Integer.parseInt(date1String[1]) > 59)
                        throw new Exception();
                }

                if (Integer.parseInt(date2String[0]) > 23 || Integer.parseInt(date2String[0]) < 0 ||
                        Integer.parseInt(date2String[1]) < 0 || Integer.parseInt(date2String[1]) > 59)
                    throw new Exception();
            }
        } catch(Exception e){
            throw new InvalidDateFormat("Formato da data inválido! Exemplo: 22:10");
        }

        return true;
    }

    public LocalTime getLocalTime(boolean first) throws ImpossibleCast {
        LocalTime dateTime = null;
        String[] dateString = null;

        if(first && !editField1.getText().trim().isBlank()){
            dateString = editField1.getText().trim().split(":");
        } else if(!first && !editField2.getText().trim().isBlank()){
            dateString = editField2.getText().trim().split(":");
        }

        if(dateString != null && dateString[0].equals("--") && dateString[1].equals("--"))
            dateString = null;

        if(dateString != null){
            dateTime = LocalTime.of(Integer.parseInt(dateString[0]), Integer.parseInt(dateString[1]));
        }

        return dateTime;
    }

    public void closeEdit(){
        editField1.setText(null);
        editField2.setText(null);
        editContainer.setDisable(true);
        editContainer.setVisible(false);
        text.setVisible(true);

        if(text.getIn() != null)
            editField1.setText(text.getIn().getText());

        if(text.getOut() != null)
            editField2.setText(text.getOut().getText());
    }

    public ClockDayModel.ClockLabel getText() {
        return text;
    }

    public TextField2 getEditField1() {
        return editField1;
    }

    public TextField2 getEditField2() {
        return editField2;
    }
}
