package com.bsit_three_c.dentalrecordapp.interfaces;

public interface TextChange {
    void beforeDataChange(String input, int after, String s);
    void dataChanged(String input, String label);
}
