package com.abyan.botouting.model;

import java.util.List;


public class PJDanus {
    private String hari;
    private String listNamaDagang;
    private String listNamaPesen;

    public PJDanus(String hari, String listNamaDagang, String listNamaPesen) {
        this.hari = hari;
        this.listNamaDagang = listNamaDagang;
        this.listNamaPesen = listNamaPesen;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getListNamaDagang() {
        return listNamaDagang;
    }

    public void setListNamaDagang(String listNamaDagang) {
        this.listNamaDagang = listNamaDagang;
    }

    public String getListNamaPesen() {
        return listNamaPesen;
    }

    public void setListNamaPesen(String listNamaPesen) {
        this.listNamaPesen = listNamaPesen;
    }
}