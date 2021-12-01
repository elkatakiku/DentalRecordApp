package com.bsit_three_c.dentalrecordapp.data.patient;

import com.bsit_three_c.dentalrecordapp.data.model.DentalOperation;

import java.util.ArrayList;

public class DentalHistory {

    private String patientUID;
    private ArrayList<DentalOperation> dentalOperations;

    public DentalHistory(String patientUID, ArrayList<DentalOperation> dentalOperations) {
        this.patientUID = patientUID;
        this.dentalOperations = dentalOperations;
    }

    public String getPatientUID() {
        return patientUID;
    }

    public ArrayList<DentalOperation> getDentalOperations() {
        return dentalOperations;
    }

//    public void addDentalOperation(DentalOperation) {
//
//    }
}
