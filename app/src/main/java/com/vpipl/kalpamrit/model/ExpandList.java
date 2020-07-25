package com.vpipl.kalpamrit.model;

import java.util.List;

/**
 * Created by PC14 on 21-May-16.
 */
public class ExpandList {
    private String name = "";
    private String icon = "";
    private String image = "";
    private String id = "";
    private String type = "";
    private String IsComboPack = "";
    private List<ExpandList> expandList;

    public ExpandList(String name, String id, String type, List<ExpandList> expandList) {
        this.name = name;
        this.icon = "";
        this.image = "";
        this.id = id;
        this.type = type;
        this.IsComboPack = "False";
        this.expandList = expandList;
    }

    public String getName() {
        return this.name;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getImage() {
        return this.image;
    }

    public String getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public List<ExpandList> getExpandList() {
        return this.expandList;
    }
}
