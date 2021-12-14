package com.bsit_three_c.dentalrecordapp.data.patient;

import com.bsit_three_c.dentalrecordapp.data.model.Procedure;

import java.util.ArrayList;

public class DentalHistory {

    private String patientUID;
    private ArrayList<Procedure> procedures;

    public DentalHistory(String patientUID, ArrayList<Procedure> procedures) {
        this.patientUID = patientUID;
        this.procedures = procedures;
    }

    public String getPatientUID() {
        return patientUID;
    }

    public ArrayList<Procedure> getDentalOperations() {
        return procedures;
    }
}
