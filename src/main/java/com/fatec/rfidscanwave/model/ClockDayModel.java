package com.fatec.rfidscanwave.model;

import com.fatec.rfidscanwave.ui.input.LabelTitle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import jfxtras.styles.jmetro.MDL2IconFont;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static com.fatec.rfidscanwave.util.StringUtil.getTime;

public class ClockDayModel {

    private ShiftModel shift;
    private Clock clockIn;
    private Clock lunchOut;
    private Clock lunchReturn;
    private Clock clockOut;
    private int offDuty = 0;


    //Offduty (0 - presença, 1 - falta, 2 - folga)
    public ClockDayModel(ShiftModel shift, int offDuty, LocalDate date){
        this.shift = shift;
        this.offDuty = offDuty;
        this.clockIn = new Clock(date, LocalTime.now(), ClockState.OFF_DUTY);
    }

    public ClockDayModel(ShiftModel shift, Clock clockIn, Clock clockOut){
        this.shift = shift;
        this.clockIn = clockIn;
        this.clockOut = clockOut;
    }


    public ClockDayModel(ShiftModel shift, Clock clockIn, Clock lunchOut, Clock lunchReturn, Clock clockOut){
        this.shift = shift;
        this.clockIn = clockIn;
        this.lunchOut = lunchOut;
        this.lunchReturn = lunchReturn;
        this.clockOut = clockOut;
    }

    public ClockDayModel(){

    }

    public static List<ClockDayModel> getMonthlyClock(EmployeeModel employee, LocalDate date){
        List<ClockDayModel> clockList = new ArrayList<>();
        int lengthOfMonth = YearMonth.of(date.getYear(), date.getMonth()).lengthOfMonth();
        int min = 1;
        int max = lengthOfMonth;

        if(Period.between(LocalDate.now().withDayOfMonth(lengthOfMonth), date).getDays() < 0){
            max -= Period.between(date, LocalDate.now().withDayOfMonth(lengthOfMonth)).getDays();
        }

        if(Period.between(employee.getHireDate(), date.withDayOfMonth(1)).getDays() < 0)
            min += Math.abs(Period.between(employee.getHireDate(), date.withDayOfMonth(1)).getDays());

        for(int i = max; i >= min; i--){
            boolean next = false;
            for(ClockDayModel c : employee.getClocks()){
                if(c.clockIn == null)
                    continue;

                if(c.clockIn.getDate().getYear() == date.getYear() &&
                        c.clockIn.getDate().getMonthValue() == date.getMonthValue()
                && c.clockIn.getDate().getDayOfMonth() == i){
                    clockList.add(c);
                    next = true;
                    break;
                }
            }

            if(!next) {
                if (date.withDayOfMonth(i).getDayOfWeek() == DayOfWeek.SUNDAY)
                    clockList.add(new ClockDayModel(employee.getShift(), 2, date.withDayOfMonth(i)));
                else
                    clockList.add(new ClockDayModel(employee.getShift(), 1, date.withDayOfMonth(i)));
            }
        }

        return clockList;
    }


    public boolean isWorking(){
        if(offDuty == 2)
            return false;

        if(clockIn == null && clockOut == null)
            return false;

        if(clockIn != null && clockOut == null)
            return true;

        return false;
    }

    public void setShift(ShiftModel shift) {
        this.shift = shift;
    }

    public LocalDateTime getInterval(Clock start, boolean out){
        LocalDateTime time = start.getDateTime();

        Duration halfWork = Duration.between(shift.getClockInTime(), shift.getClockOutTime());

        halfWork = halfWork.minusSeconds(halfWork.toSeconds() / 2);
        if(out)
            halfWork = halfWork.minus(shift.getBreakDuration().toNanoOfDay() / 2, ChronoUnit.NANOS);
        else
            halfWork = halfWork.plus(shift.getBreakDuration().toNanoOfDay() / 2, ChronoUnit.NANOS);

        time = time.plus(halfWork);

        return time;
    }

    public ClockState getLastState() {
        if (clockOut != null)
            return ClockState.CLOCK_OUT;
        else if (lunchReturn != null)
            return ClockState.LUNCH_RETURN;
        else if (lunchOut != null)
            return ClockState.LUNCH_OUT;
        else if (clockIn != null)
            return ClockState.CLOCK_IN;

        return ClockState.UNDEFINED;
    }

    public void setOffDuty(int offDuty) {
        this.offDuty = offDuty;
    }

    public Clock getLunchReturn() {
        return lunchReturn;
    }

    public Clock getLunchOut() {
        return lunchOut;
    }

    public Clock getLastClock() {
        if (clockOut != null)
            return clockOut;
        else if (lunchReturn != null)
            return lunchReturn;
        else if (lunchOut != null)
            return lunchOut;
        else if (clockIn != null)
            return clockIn;

        return null;
    }

    public boolean canSetClock(int stateNumber){
        boolean canSet = true;
        ClockState state = ClockState.fromState(stateNumber);

        switch (state){
            case CLOCK_IN -> {
                canSet = clockIn == null;
            }
            case LUNCH_OUT -> {
                canSet = lunchOut == null && clockIn == null;
            }
            case LUNCH_RETURN -> {
                canSet = lunchReturn == null && lunchOut == null && clockIn == null;
            }
            case CLOCK_OUT -> {
                canSet = clockOut == null && lunchReturn == null && lunchOut == null && clockIn == null;
            }
        }

        return canSet;
    }

    public void setClock(Timestamp dateStamp, Timestamp timestamp, int state) {
        switch (state) {
            case 1:
                setClockIn(new Clock(dateStamp.toLocalDateTime().toLocalDate(), timestamp.toLocalDateTime().toLocalTime(), ClockState.fromState(state)));
                break;
            case 2:
                setLunchOut(new Clock(dateStamp.toLocalDateTime().toLocalDate(), timestamp == null ? null : timestamp.toLocalDateTime().toLocalTime(), ClockState.fromState(state)));
                break;
            case 3:
                setLunchReturn(new Clock(dateStamp.toLocalDateTime().toLocalDate(), timestamp == null ? null : timestamp.toLocalDateTime().toLocalTime(), ClockState.fromState(state)));
                break;
            case 4:
                setClockOut(new Clock(dateStamp.toLocalDateTime().toLocalDate(), timestamp == null ? null : timestamp.toLocalDateTime().toLocalTime(), ClockState.fromState(state)));
                break;
        }

    }
    public boolean havePresence(){
        return offDuty == 0;
    }

    public void setLunchOut(Clock lunchOut) {
        this.lunchOut = lunchOut;
    }

    public void setLunchReturn(Clock lunchReturn) {
        this.lunchReturn = lunchReturn;
    }

    public ShiftModel getShift() {
        return shift;
    }

    public void setClockIn(Clock clockIn) {
        this.clockIn = clockIn;
    }

    public void setClockOut(Clock clockOut) {
        this.clockOut = clockOut;
    }

    public Clock getClockOut() {
        return clockOut;
    }

    public Clock getClockIn() {
        return clockIn;
    }

    public int getOffDuty() {
        return offDuty;
    }

    public static class Clock {
        private LocalDate date;
        private LocalTime time;
        private ClockState state;

        public Clock(LocalDate date, LocalTime time, ClockState state){
            this.date = date;
            this.time = time;
            this.state = state;
        }

        public Clock(Timestamp datestamp, Timestamp timestamp, ClockState state){
            this.date = datestamp.toLocalDateTime().toLocalDate();
            this.time = timestamp.toLocalDateTime().toLocalTime();
            this.state = state;
        }

        public LocalDateTime getDateTime(){
            return LocalDateTime.of(date, time);
        }

        public ClockState getState() {
            return state;
        }

        public LocalDate getDate() {
            return date;
        }

        public LocalTime getTime() {
            return time;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public void setTime(LocalTime time) {
            this.time = time;
        }
    }

    public enum ClockState {
        UNDEFINED (0),
        CLOCK_IN (1),
        LUNCH_OUT (2),
        LUNCH_RETURN (3),
        CLOCK_OUT (4),
        OFF_DUTY (5);

        private final int state;

        ClockState(int state){
            this.state = state;
        }

        public int getState() {
            return state;
        }

        public static ClockState nextState(ClockState state){
            return switch (state){
                case CLOCK_IN -> LUNCH_OUT;
                case LUNCH_OUT -> LUNCH_RETURN;
                case LUNCH_RETURN -> CLOCK_OUT;
                case CLOCK_OUT, UNDEFINED -> CLOCK_IN;
                default -> CLOCK_IN;
            };
        }

        public ClockState getNext(){
            return switch (this){
                case CLOCK_IN -> LUNCH_OUT;
                case LUNCH_OUT -> LUNCH_RETURN;
                case LUNCH_RETURN -> CLOCK_OUT;
                case OFF_DUTY, CLOCK_OUT, UNDEFINED -> CLOCK_IN;
            };
        }

        public static ClockState fromState(int state){
            switch (state){
                case 0:
                    return UNDEFINED;
                case 1:
                    return CLOCK_IN;
                case 2:
                    return LUNCH_OUT;
                case 3:
                    return LUNCH_RETURN;
                case 4:
                    return CLOCK_OUT;
                case 5:
                    return OFF_DUTY;
                default:
                    return null;
            }
        }
    }


    public static class ClockLabel extends HBox {
        private ShiftModel shift;
        private MDL2IconFont icon;
        private LabelTitle in;
        private LabelTitle out;

        public ClockLabel(ShiftModel shift, Clock date1, Clock date2, boolean clockIn, int offDuty){
            this.shift = shift;

            if(offDuty == 0) {
                in = new LabelTitle(date1 == null ? "--:--" : getTime(date1.getTime()));

                icon = new MDL2IconFont("\uEA62");
                icon.setAlignment(Pos.CENTER);
                icon.setPadding(new Insets(5, 0, 5, 0));
                out = new LabelTitle(date2 == null ? "--:--" : getTime(date2.getTime()));

                if(clockIn && date1 != null) {
                    LocalDateTime entering = LocalDateTime
                            .of(
                                    date1.getDate().getYear(), date1.getDate().getMonth(), date1.getDate().getDayOfMonth(),
                                    shift.getClockOutTime().getHour(), shift.getClockOutTime().getMinute(), shift.getClockOutTime().getSecond()
                            );

                    if (Duration.between(date1.getDateTime(), entering).getSeconds() <= -600) {
                        in.setFill(Color.rgb(127,0 , 0));
                        icon.setStyle("-fx-text-fill: rgb(127, 0, 0);");
                        out.setFill(Color.rgb(127,0 , 0));
                    } else if (Duration.between(date1.getDateTime(), entering).getSeconds() >= 3600) {
                        in.setFill(Color.rgb(0, 63, 0));
                        icon.setStyle("-fx-text-fill: rgb(0, 63, 0);");
                        out.setFill(Color.rgb(0, 63, 0));
                    }
                } else if(!clockIn && date2 != null){
                    LocalDateTime exit = LocalDateTime
                            .of(
                                    date2.getDate().getYear(), date2.getDate().getMonth(), date2.getDate().getDayOfMonth(),
                                    shift.getClockOutTime().getHour(), shift.getClockOutTime().getMinute(), shift.getClockOutTime().getSecond()
                            );

                    if (Duration.between(exit, date2.getDateTime()).getSeconds() <= -300) {
                        in.setFill(Color.rgb(127,0 , 0));
                        icon.setStyle("-fx-text-fill: rgb(127, 0, 0);");
                        out.setFill(Color.rgb(127,0 , 0));
                    } else if(Duration.between(exit, date2.getDateTime()).getSeconds() >= 3600){
                        in.setFill(Color.rgb(0, 63, 0));
                        icon.setStyle("-fx-text-fill: rgb(0, 63, 0);");
                        out.setFill(Color.rgb(0, 63, 0));
                    }
                }

                icon.setStyle(icon.getStyle() + "-fx-padding: 0px 10px 0px 10px;");

                getChildren().add(in);
                getChildren().add(icon);
                getChildren().add(out);
            } else if(offDuty == 2){
                in = new LabelTitle("Folga");
                getChildren().add(in);
            }

            setAlignment(Pos.CENTER);
        }

        public void update(Clock date1, Clock date2, boolean clockIn, int offDuty){
            if(offDuty == 0) {
                if(in == null) {
                    in = new LabelTitle();
                    getChildren().add(in);
                }

                if(icon == null) {
                    icon = new MDL2IconFont("\uEA62");
                    icon.setAlignment(Pos.CENTER);
                    icon.setPadding(new Insets(5, 0, 5, 0));
                    getChildren().add(icon);
                }

                if(out == null) {
                    out = new LabelTitle();
                    getChildren().add(out);
                }

                in.setText(date1 == null ? "--:--" : getTime(date1.getTime()));
                out.setText(date2 == null ? "--:--" : getTime(date2.getTime()));

                if(clockIn && date1 != null) {
                    LocalDateTime entering = LocalDateTime
                            .of(
                                    date1.getDate().getYear(), date1.getDate().getMonth(), date1.getDate().getDayOfMonth(),
                                    shift.getClockOutTime().getHour(), shift.getClockOutTime().getMinute(), shift.getClockOutTime().getSecond()
                            );

                    if (Duration.between(date1.getDateTime(), entering).getSeconds() <= -600) {
                        in.setFill(Color.rgb(127,0 , 0));
                        icon.setStyle("-fx-text-fill: rgb(127, 0, 0);");
                        out.setFill(Color.rgb(127,0 , 0));
                    } else if (Duration.between(date1.getDateTime(), entering).getSeconds() >= 3600) {
                        in.setFill(Color.rgb(0, 63, 0));
                        icon.setStyle("-fx-text-fill: rgb(0, 63, 0);");
                        out.setFill(Color.rgb(0, 63, 0));
                    } else {
                        in.setFill(Color.rgb(0, 0, 0));
                        icon.setStyle("-fx-text-fill: rgb(0, 0, 0);");
                        out.setFill(Color.rgb(0, 0, 0));
                    }
                } else if(!clockIn && date2 != null) {
                    LocalDateTime exit = LocalDateTime
                            .of(
                                    date2.getDate().getYear(), date2.getDate().getMonth(), date2.getDate().getDayOfMonth(),
                                    shift.getClockOutTime().getHour(), shift.getClockOutTime().getMinute(), shift.getClockOutTime().getSecond()
                            );

                    if (Duration.between(exit, date2.getDateTime()).getSeconds() <= -300) {
                        in.setFill(Color.rgb(127, 0, 0));
                        icon.setStyle("-fx-text-fill: rgb(127, 0, 0);");
                        out.setFill(Color.rgb(127, 0, 0));
                    } else if (Duration.between(exit, date2.getDateTime()).getSeconds() >= 3600) {
                        in.setFill(Color.rgb(0, 63, 0));
                        icon.setStyle("-fx-text-fill: rgb(0, 63, 0);");
                        out.setFill(Color.rgb(0, 63, 0));
                    } else {
                        in.setFill(Color.rgb(0, 0, 0));
                        icon.setStyle("-fx-text-fill: rgb(0, 0, 0);");
                        out.setFill(Color.rgb(0, 0, 0));
                    }
                }

                icon.setStyle(icon.getStyle() + "-fx-padding: 0px 10px 0px 10px;");
            } else if(offDuty == 2){
                if(in == null) {
                    in = new LabelTitle();
                    getChildren().add(in);
                }

                in.setText("Folga");

                if(icon != null) {
                    icon.setText("");
                    getChildren().remove(icon);
                    icon = null;
                }

                if(out != null) {
                    out.setText("");
                    getChildren().remove(out);
                    out = null;
                }
            } else {
                if(in == null) {
                    in = new LabelTitle();
                    getChildren().add(in);
                }

                in.setText("");

                if(icon != null) {
                    icon.setText("");
                    getChildren().remove(icon);
                    icon = null;
                }

                if(out != null) {
                    out.setText("");
                    getChildren().remove(out);
                    out = null;
                }
            }
        }

        public LabelTitle getIn() {
            return in;
        }

        public LabelTitle getOut() {
            return out;
        }
    }
}
