package com.fatec.rfidscanwave.ui.graphics;

import javafx.scene.paint.Color;

public class GraphicContent {
    private final int val1;
    private final String val2;
    private final Color val3;
    private int val4;

    public GraphicContent(int val1, String val2, Color val3){
        this.val1 = val1;
        this.val2 = val2;
        this.val3 = val3;
        this.val4 = 0;
    }

    public int getVal1() {
        return val1;
    }

    public String getVal2() {
        return val2;
    }

    public Color getVal3() {
        return val3;
    }

    public int getVal4() {
        return val4;
    }

    public void addVal4(int value){
        val4 += value;
    }
}
