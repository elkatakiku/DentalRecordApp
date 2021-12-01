package com.bsit_three_c.dentalrecordapp.data.form_state;

public class FormState {

    private Integer msgError;
    private boolean isDataValid;

    private FormState() {
    }

    public FormState(Integer msgError) {
        this.msgError = msgError;
        this.isDataValid = false;
    }

    public FormState(boolean isValid) {
        this.msgError = null;
        this.isDataValid = isValid;
    }

    public Integer getMsgError() {
        return msgError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
