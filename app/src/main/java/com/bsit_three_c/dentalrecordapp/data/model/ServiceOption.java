package com.bsit_three_c.dentalrecordapp.data.model;

public class ServiceOption extends Service {

    private int servicePosition;
    private boolean isSelected;

    public ServiceOption(String title, int servicePosition, boolean isSelected) {
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
        return "ServiceOption{" +
                "\nservicePosition=" + servicePosition +
                "\nisSelected=" + isSelected +
                '}';
    }
}
