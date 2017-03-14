package com.abyan.botouting.model;

/**
 * Created by pc on 3/9/2017.
 */
public class PJWarkop {
    private String hari;
    private String nama;
    private String sesi1;
    private String sesi2;
    private String sesi3;

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String namap) {
        this.nama = nama;
    }

    public String getSesi1() {
        return sesi1;
    }

    public void setSesi1(String sesi1) {
        this.sesi1 = sesi1;
    }

    public String getSesi2() {
        return sesi2;
    }

    public void setSesi2(String sesi2) {
        this.sesi2 = sesi2;
    }

    public String getSesi3() {
        return sesi3;
    }

    public void setSesi3(String sesi3) {
        this.sesi3 = sesi3;
    }

    public PJWarkop(String hari, String nama, String sesi1, String sesi2, String sesi3) {
        this.hari = hari;
        this.nama = nama;
        this.sesi1 = sesi1;
        this.sesi2 = sesi2;
        this.sesi3 = sesi3;
    }
}
