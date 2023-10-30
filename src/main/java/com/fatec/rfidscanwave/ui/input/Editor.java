package com.fatec.rfidscanwave.ui.input;

import com.fatec.rfidscanwave.db.ScanWaveDB;
import com.fatec.rfidscanwave.exception.ImpossibleCast;
import com.fatec.rfidscanwave.exception.InvalidDateFormat;
import com.fatec.rfidscanwave.model.ClockDayModel;
import com.fatec.rfidscanwave.model.EmployeeModel;
import com.fatec.rfidscanwave.model.ManagerModel;
import com.fatec.rfidscanwave.ui.table.ClockRow;
import com.fatec.rfidscanwave.util.FXUtil;
import com.fatec.rfidscanwave.view.BoardView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import jfxtras.styles.jmetro.MDL2IconFont;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Editor extends Group {
    private BoardView boardView;
    private final TableView<ClockRow> table;
    private ClockRow selected;
    private EmployeeModel employee;
    private final LabelTitle warnings;
    private final MDL2IconFont icon;
    private final Button cancel;
    private final Button apply;
    private final HBox buttonsContainer;
    public Editor(BoardView boardView, final ScanWaveDB db, TableView<ClockRow> table, LabelTitle warnings){
        this.boardView = boardView;
        this.table = table;
        this.warnings = warnings;
        warnings.setStyle("-fx-font-size: 1.5em;");
        warnings.setFill(Color.rgb(180, 0, 0));

        icon = new MDL2IconFont("\uE70F");
        icon.setStyle("-fx-font-size: 1.75em; -fx-opacity: 0.5;");
        icon.setPadding(new Insets(10, 10, 0, 0));
        icon.setVisible(true);
        icon.pressedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if(t1){
                    warnings.setText("");

                    if(selected == null){
                        warnings.setText("Selecione algum ponto!");
                        return;
                    }

                    icon.setDisable(true);
                    icon.setVisible(false);
                    buttonsContainer.setVisible(true);
                    buttonsContainer.setDisable(false);

                    for(TableColumn<ClockRow, ?> col : table.getColumns()){
                        if(!col.getText().equals("Data"))
                            continue;

                        selected.getClock1().initEdit();
                        selected.getClock2().initEdit();
                        selected.getIcon().initEdit();
                    }

                }
            }
        });

        buttonsContainer = new HBox();
        buttonsContainer.setVisible(false);
        buttonsContainer.setDisable(true);

        cancel = new Button("Cancelar");
        cancel.pressedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if(!t1)
                    return;

                setSelected(null);
            }
        });

        apply = new Button("Salvar");
        apply.pressedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if(!t1)
                    return;

                try {
                    if(selected.getIcon().getChoiceBox().getSelectionModel().getSelectedIndex() == 0){
                        if(selected.getClock1().isValid(true) && selected.getClock2().isValid(false)){
                            LocalDateTime oldIn;

                            if(selected.getClock().getOffDuty() == 0){
                                oldIn = selected.getClock().getClockIn().getDateTime();
                            } else {
                                oldIn = LocalDateTime.of(selected.getClock().getClockIn().getDate(), LocalTime.of(0, 0, 0));
                            }

                            LocalTime newTimeIn = selected.getClock1().getLocalTime(true);
                            LocalTime newTimeLunchOut = selected.getClock1().getLocalTime(false);
                            LocalTime newTimeLunchReturn = selected.getClock2().getLocalTime(true);
                            LocalTime newTimeOut = selected.getClock2().getLocalTime(false);

                            db.updateClock(
                                    employee.getId(),
                                    employee,
                                    new ClockDayModel(
                                            selected.getClock().getShift(),
                                            new ClockDayModel.Clock(oldIn.toLocalDate(), newTimeIn, ClockDayModel.ClockState.CLOCK_IN),
                                            newTimeLunchOut == null ? null : new ClockDayModel.Clock(oldIn.toLocalDate(), newTimeLunchOut, ClockDayModel.ClockState.LUNCH_OUT),
                                            newTimeLunchReturn == null ? null : new ClockDayModel.Clock(oldIn.toLocalDate(), newTimeLunchReturn, ClockDayModel.ClockState.LUNCH_RETURN),
                                            newTimeOut == null ? null : new ClockDayModel.Clock(oldIn.toLocalDate(), newTimeOut, ClockDayModel.ClockState.CLOCK_OUT)
                                    )
                            );

                            db.generateLog(
                                    employee.getId(),
                                    selected.getClock(),
                                    ManagerModel.getInstance().getId(),
                                    new ClockDayModel(
                                            selected.getClock().getShift(),
                                            new ClockDayModel.Clock(oldIn.toLocalDate(), newTimeIn, ClockDayModel.ClockState.CLOCK_IN),
                                            newTimeLunchOut == null ? null : new ClockDayModel.Clock(oldIn.toLocalDate(), newTimeLunchOut, ClockDayModel.ClockState.LUNCH_OUT),
                                            newTimeLunchReturn == null ? null : new ClockDayModel.Clock(oldIn.toLocalDate(), newTimeLunchReturn, ClockDayModel.ClockState.LUNCH_RETURN),
                                            newTimeOut == null ? null : new ClockDayModel.Clock(oldIn.toLocalDate(), newTimeOut, ClockDayModel.ClockState.CLOCK_OUT)                                    )
                            );

                            selected.getClock().setOffDuty(0);
                            selected.getClock().setClockIn(new ClockDayModel.Clock(oldIn.toLocalDate(), newTimeIn, ClockDayModel.ClockState.CLOCK_IN));
                            selected.getClock().setLunchOut(newTimeLunchOut == null ? null : new ClockDayModel.Clock(oldIn.toLocalDate(), newTimeLunchOut, ClockDayModel.ClockState.LUNCH_OUT));
                            selected.getClock().setLunchReturn(newTimeLunchReturn == null ? null : new ClockDayModel.Clock(oldIn.toLocalDate(), newTimeLunchReturn, ClockDayModel.ClockState.LUNCH_RETURN));
                            selected.getClock().setClockOut(newTimeOut == null ? null : new ClockDayModel.Clock(oldIn.toLocalDate(), newTimeOut, ClockDayModel.ClockState.CLOCK_OUT));
                        }
                    } else if(selected.getIcon().getChoiceBox().getSelectionModel().getSelectedIndex() == 1){
                        db.delete(
                                employee.getId(),
                                selected.getClock()
                        );

                        db.generateLog(
                                employee.getId(),
                                selected.getClock(),
                                ManagerModel.getInstance().getId(),
                                new ClockDayModel(
                                        selected.getClock().getShift(),
                                        new ClockDayModel.Clock(selected.getClock().getClockIn().getDate(), null, ClockDayModel.ClockState.CLOCK_IN),
                                        new ClockDayModel.Clock(selected.getClock().getClockIn().getDate(), null, ClockDayModel.ClockState.LUNCH_OUT),
                                        new ClockDayModel.Clock(selected.getClock().getClockIn().getDate(), null, ClockDayModel.ClockState.LUNCH_RETURN),
                                        new ClockDayModel.Clock(selected.getClock().getClockIn().getDate(), null, ClockDayModel.ClockState.CLOCK_OUT)
                                )
                        );

                        if(selected.getClock().getOffDuty() != 2)
                            selected.getClock().setOffDuty(1);

                        selected.getClock().setClockIn(new ClockDayModel.Clock(selected.getClock().getClockIn().getDate(), null, ClockDayModel.ClockState.CLOCK_IN));
                        selected.getClock().setLunchOut(null);
                        selected.getClock().setLunchReturn(null);
                        selected.getClock().setClockOut(null);
                    }

                    selected.getClock1().getText().update(selected.getClock().getClockIn(), selected.getClock().getLunchOut(), true, selected.getClock().getOffDuty());
                    selected.getClock2().getText().update(selected.getClock().getLunchReturn(), selected.getClock().getClockOut(), false, selected.getClock().getOffDuty());
                    selected.getLate();
                    selected.getOvertime();
                    selected.getIcon();
                    boardView.initLateGraphic();
                    boardView.initLateGraphic();
                    boardView.loadTimeWorkedAndOvertime();

                    setSelected(null);
                } catch(InvalidDateFormat e){
                    warnings.setText(e.getMessage());
                } catch (ImpossibleCast e) {
                    warnings.setText("Conversão impossível!");
                }
            }
        });

        buttonsContainer.getChildren().add(cancel);
        buttonsContainer.getChildren().add(FXUtil.getSpacer(10, 1));
        buttonsContainer.getChildren().add(apply);

        getChildren().add(icon);
        getChildren().add(buttonsContainer);
    }

    public void setEmployee(EmployeeModel employee){
        this.employee = employee;
    }

    public void setSelected(ClockRow selected) {
        if(this.selected == selected)
            return;

        if(this.selected != null){
            this.selected.getClock1().closeEdit();
            this.selected.getClock2().closeEdit();
            this.selected.getIcon().closeEdit();

            if(buttonsContainer.isVisible()) {
                icon.setDisable(false);
                icon.setVisible(true);
                buttonsContainer.setVisible(false);
                buttonsContainer.setDisable(true);
            }
        }

        this.selected = selected;
    }

    public MDL2IconFont getIcon() {
        return icon;
    }

    public LabelTitle getWarnings() {
        return warnings;
    }
}
