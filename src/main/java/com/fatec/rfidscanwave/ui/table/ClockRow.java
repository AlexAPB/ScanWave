package com.fatec.rfidscanwave.ui.table;

import com.fatec.rfidscanwave.model.ClockDayModel;
import com.fatec.rfidscanwave.ui.input.EditableCheckBox;
import com.fatec.rfidscanwave.ui.input.EditableText;
import com.fatec.rfidscanwave.ui.input.LabelTitle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import jfxtras.styles.jmetro.MDL2IconFont;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClockRow {
    private ClockDayModel clock;
    private EditableText clock1;
    private EditableText clock2;
    private EditableCheckBox icon;
    private LabelTitle overtime;
    private LabelTitle late;

    public ClockRow(ClockDayModel clock){
        this.clock = clock;

        clock1 = new EditableText(getClockIn());

        clock2 = new EditableText(getClockOut());

        overtime = new LabelTitle();

        late = new LabelTitle();

        icon = new EditableCheckBox(getStatus(), FXCollections.observableArrayList("Presença", "Falta"));
        icon.getChoiceBox().getSelectionModel().select(clock.getOffDuty());

        switch (clock.getOffDuty()){
            case 0:
                clock1.setDisable(false);
                clock2.setDisable(false);
                break;
            case 1:
            case 2:
                clock1.setDisable(true);
                clock2.setDisable(true);
                break;
        }
        icon.getChoiceBox().valueProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                int n;

                if(t1.equals("Presença")){
                    n = 0;
                } else if(t1.equals("Falta")){
                    n = 1;
                } else {
                    n = 2;
                }

                switch (n){
                    case 0:
                        clock1.setDisable(false);
                        clock2.setDisable(false);
                        break;
                    case 1:
                    case 2:
                        clock1.setDisable(true);
                        clock2.setDisable(true);
                        break;
                }
            }
        });
    }

    public void setClock(ClockDayModel clock) {
        this.clock = clock;
    }

    public ClockDayModel getClock() {
        return clock;
    }

    public String getDay(){
        return clock.getClockIn().getDate().format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));
    }


    public EditableCheckBox getIcon() {
        if(clock.getOffDuty() == 0){
            this.icon.setIcon("\uE73E");
            this.icon.getIcon().setStyle("-fx-text-fill: green; -fx-font-size: 1.75em;");
        } else if(clock.getOffDuty() == 1){
            this.icon.setIcon("\uE711");
            this.icon.getIcon().setStyle("-fx-text-fill: red; -fx-font-size: 1.75em;");
        } else {
            this.icon.setIcon("\uE708");
            this.icon.getIcon().setStyle("-fx-text-fill: rgb(109, 110, 190); -fx-font-size: 1.75em;");
        }

        return icon;
    }

    public MDL2IconFont getStatus(){
        MDL2IconFont icon = null;

        if(clock.getOffDuty() == 0){
            icon = new MDL2IconFont("\uE73E");
            icon.setStyle("-fx-text-fill: green; -fx-font-size: 1.75em;");
        } else if(clock.getOffDuty() == 1){
            icon = new MDL2IconFont("\uE711");
            icon.setStyle("-fx-text-fill: red; -fx-font-size: 1.75em;");
        } else {
            icon = new MDL2IconFont("\uE708");
            icon.setStyle("-fx-text-fill: rgb(109, 110, 190); -fx-font-size: 1.75em;");
        }

        return icon;
    }

    private ClockDayModel.ClockLabel getClockIn(){
        if(!clock.havePresence()) {
            if(clock.getOffDuty() == 2)
                return new ClockDayModel.ClockLabel(clock.getShift(), null, null, true, clock.getOffDuty());
            else
                return new ClockDayModel.ClockLabel(clock.getShift(), null, null, true, clock.getOffDuty());
        } else {
            return new ClockDayModel.ClockLabel(clock.getShift(), clock.getClockIn(), clock.getLunchOut(), true, clock.getOffDuty());
        }
    }

    private ClockDayModel.ClockLabel getClockOut(){
        if(!clock.havePresence()) {
            if(clock.getOffDuty() == 2)
                return new ClockDayModel.ClockLabel(clock.getShift(), null, null, false, clock.getOffDuty());
            else
                return new ClockDayModel.ClockLabel(clock.getShift(), null, null,false,  clock.getOffDuty());
        } else {
            return new ClockDayModel.ClockLabel(clock.getShift(), clock.getLunchReturn(), clock.getClockOut(),false,  clock.getOffDuty());
        }
    }

    public EditableText getClock1(){
        return clock1;
    }

    public EditableText getClock2(){
        return clock2;
    }

    public LabelTitle getOvertime(){
        if(clock.getOffDuty() != 0 || clock.getClockIn() == null || clock.getClockOut() == null) {
            overtime.setText("");
            return overtime;
        }

        int overtimeNumber = 0;

        LocalDateTime dateIn = LocalDateTime.of(clock.getClockIn().getDate(), clock.getShift().getClockInTime());
        Duration durationIn = Duration.between(clock.getClockIn().getDateTime(), dateIn);

        LocalDateTime dateOut = LocalDateTime.of(clock.getClockOut().getDate(), clock.getShift().getClockOutTime());
        Duration durationOut = Duration.between(dateOut, clock.getClockOut().getDateTime());

        overtimeNumber += durationIn.toHoursPart();
        overtimeNumber += durationOut.toHoursPart();

        if(overtimeNumber <= 0)
            overtime.setText("");
        else
            overtime.setText(overtimeNumber + "h");

        return overtime;
    }

    public LabelTitle getLate(){
        if(clock.getOffDuty() != 0) {
            late.setText("");
            return late;
        }

        LocalDateTime date = LocalDateTime.of(clock.getClockIn().getDate(), clock.getShift().getClockInTime());
        Duration duration = Duration.between(date, clock.getClockIn().getDateTime());

        if(duration.toSeconds() < 60) {
            late.setText("");
            return late;
        }

        if(duration.toHoursPart() > 0)
            late.setText(duration.toHoursPart() + "h");
        else
            late.setText(duration.toMinutesPart() + " min");

        return late;
    }
}
