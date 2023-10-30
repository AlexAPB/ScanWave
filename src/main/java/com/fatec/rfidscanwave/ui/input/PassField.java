package com.fatec.rfidscanwave.ui.input;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;

public class PassField extends TextFieldSkin {
    public static final char BULLET = '\u2022';

    public PassField(TextField textField) {
        super(textField);
    }

    @Override
    protected String maskText(String txt) {
        if (getSkinnable() instanceof TextField2) {
            int n = txt.length();
            StringBuilder passwordBuilder = new StringBuilder(n);
            passwordBuilder.append(String.valueOf(BULLET).repeat(n));

            return passwordBuilder.toString();
        } else {
            return txt;
        }
    }
}
