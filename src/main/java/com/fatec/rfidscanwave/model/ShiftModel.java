package com.fatec.rfidscanwave.model;

import java.time.LocalTime;

public class ShiftModel {
    private int id;
    private LocalTime clockInTime;
    private LocalTime clockOutTime;
    private LocalTime breakDuration;

    public ShiftModel(){

    }

    public ShiftModel(int id, LocalTime clockInTime, LocalTime clockOutTime, LocalTime breakDuration){
        this.id = id;
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
        this.breakDuration = breakDuration;
    }

    public int getId() {
        return id;
    }

    public LocalTime getClockInTime() {
        return clockInTime;
    }

    public LocalTime getClockOutTime() {
        return clockOutTime;
    }

    public LocalTime getBreakDuration() {
        return breakDuration;
    }
}
