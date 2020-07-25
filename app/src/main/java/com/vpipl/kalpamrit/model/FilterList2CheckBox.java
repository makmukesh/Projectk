package com.vpipl.kalpamrit.model;

/**
 * Created by PC14 on 17-Aug-16.
 */
public class FilterList2CheckBox {
    private String name = "";
    private String id = "";
    private boolean selected = false;

    public FilterList2CheckBox(String name, String id, boolean selected) {
        super();
        this.name = name;
        this.id = id;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}