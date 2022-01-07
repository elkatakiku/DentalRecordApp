package com.bsit_three_c.dentalrecordapp.data.model;

public class DentalServiceOption extends DentalService {

    private boolean isSelected;

    public DentalServiceOption(final String serviceIud, String title, boolean isSelected) {
        super(serviceIud, title);
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    @Override
    public String toString() {
        return "\nDentalServiceOption{" +
                "\ntitle='" + title + '\'' +
                "\ndescription='" + description + '\'' +
                "\nisSelected=" + isSelected +
                "\n}";
    }
}
