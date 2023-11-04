package com.fatec.rfidscanwave.ui.date;

import com.fatec.rfidscanwave.ui.input.LabelTitle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import jfxtras.styles.jmetro.MDL2IconFont;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import static com.fatec.rfidscanwave.util.FXUtil.getSpacer;

public class MonthYearSelector extends HBox {
    private Button previous;
    private LabelTitle date;
    private Button next;
    private LocalDate min;
    private LocalDate current;
    private LocalDate now;

    public MonthYearSelector(){
        create();

        getChildren().add(previous);
        getChildren().add(getSpacer(15, 1));
        getChildren().add(date);
        getChildren().add(getSpacer(15, 1));
        getChildren().add(next);
        setAlignment(Pos.CENTER);
    }

    public void setMin(LocalDate min) {
        this.min = min;
    }

    private void create(){
        MDL2IconFont previousIcon = new MDL2IconFont("\uE76B");
        previousIcon.setSize(30);

        MDL2IconFont nextIcon = new MDL2IconFont("\uE76C");
        nextIcon.setSize(30);

        previous = new Button();
        previous.setGraphic(previousIcon);

        now = LocalDate.now();

        current = LocalDate.now();

        date = new LabelTitle(current.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " " + current.getYear());
        date.getStyleClass().add("date-text");

        next = new Button();
        next.setGraphic(nextIcon);
    }

    public boolean previous(){
        if(previous.isArmed())
            return false;

        if(min.getYear() == current.getYear() && min.getMonth() == current.getMonth())
            return false;

        current = current.minusMonths(1);
        date.setText(current.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " " + current.getYear());
        return true;
    }

    public boolean next(){
        if(next.isArmed())
            return false;

        if(current.getMonthValue() == now.getMonthValue() && current.getYear() == now.getYear())
            return false;

        current = current.plusMonths(1);
        date.setText(current.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " " + current.getYear());
        return true;
    }

    public Text getDate() {
        return date;
    }

    public Button getNext() {
        return next;
    }

    public Button getPrevious() {
        return previous;
    }

    public LocalDate getCurrent() {
        return current;
    }
}
