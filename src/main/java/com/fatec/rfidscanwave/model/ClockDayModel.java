package com.fatec.rfidscanwave.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Random;

public class ClockDayModel {
    private Clock clockIn;
    private Clock clockOut;

    public ClockDayModel(Clock clockIn, Clock clockOut){
        this.clockIn = clockIn;
        this.clockOut = clockOut;
    }

    public ClockDayModel(Clock clockIn){
        this.clockIn = clockIn;
    }

    public ClockDayModel(){

    }

    public boolean isWorking(){
        if(clockIn == null && clockOut == null)
            return false;

        if(clockIn != null && clockOut == null)
            return true;

        return false;
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

    public static class Clock {
        private final LocalDateTime clock;
        private final ClockState state;

        public Clock(LocalDateTime clock, ClockState state){
            this.clock = clock;
            this.state = state;
        }

        public Clock(Timestamp timestamp, ClockState state){
            this.clock = timestamp.toLocalDateTime();
            this.state = state;
        }

        public ClockState getState() {
            return state;
        }

        public LocalDateTime getClock() {
            return clock;
        }
    }

    public enum ClockState {
        UNDEFINED (0),
        CLOCK_IN (1),
        CLOCK_OUT (2);

        private final int state;

        ClockState(int state){
            this.state = state;
        }

        public int getState() {
            return state;
        }

        public static ClockState nextState(ClockState state){
            return switch (state){
                case CLOCK_IN -> CLOCK_OUT;
                case CLOCK_OUT, UNDEFINED -> CLOCK_IN;
            };
        }

        public static ClockState fromState(int state){
            switch (state){
                case 0:
                    return ClockState.UNDEFINED;
                case 1:
                    return ClockState.CLOCK_IN;
                case 2:
                    return ClockState.CLOCK_OUT;
                default:
                    return null;
            }
        }
    }
}
