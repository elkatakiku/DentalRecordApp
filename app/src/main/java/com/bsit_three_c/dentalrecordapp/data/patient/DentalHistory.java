package com.bsit_three_c.dentalrecordapp.data.patient;

import com.bsit_three_c.dentalrecordapp.data.model.DentalProcedure;

import java.util.ArrayList;

public class DentalHistory {

    private String patientUID;
    private ArrayList<DentalProcedure> dentalProcedures;

    public DentalHistory(String patientUID, ArrayList<DentalProcedure> dentalProcedures) {
        this.patientUID = patientUID;
        this.dentalProcedures = dentalProcedures;
    }

    public String getPatientUID() {
        return patientUID;
    }

    public ArrayList<DentalProcedure> getDentalOperations() {
        return dentalProcedures;
    }

//    public void addDentalOperation(DentalProcedure) {
//
//    }
}
