package com.bsit_three_c.dentalrecordapp.data.model;

import androidx.annotation.NonNull;

public class DentalServiceOption extends DentalService {

    private int servicePosition;
    private boolean isSelected;

    public DentalServiceOption(String title, int servicePosition, boolean isSelected) {
        super(title);
        this.servicePosition = servicePosition;
        this.isSelected = isSelected;
    }

    public int getServicePosition() {
        return servicePosition;
    }

    public void setServicePosition(int servicePosition) {
        this.servicePosition = servicePosition;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @NonNull
    @Override
    public String toString() {
        return "\nDentalServiceOption{" +
                "\n\t\tservicePosition=" + servicePosition +
                "\n\t\tisSelected=" + isSelected +
                "}\n";
    }
}
