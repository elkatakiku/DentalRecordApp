package com.bsit_three_c.dentalrecordapp.ui.add_patient;

class AddPatientFormState {

    private Integer msgError;
    private boolean isDataValid;

    private AddPatientFormState() {
    }

    public AddPatientFormState(Integer msgError) {
        this.msgError = msgError;
    }

    public AddPatientFormState(boolean isValid) {
        this.isDataValid = isValid;
    }

    public Integer getMsgError() {
        return msgError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
