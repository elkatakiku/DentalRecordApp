package com.bsit_three_c.dentalrecordapp.data.model;

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

    @Override
    public String toString() {
        return "DentalServiceOption{" +
                "\nservicePosition=" + servicePosition +
                "\nisSelected=" + isSelected +
                '}';
    }
}
