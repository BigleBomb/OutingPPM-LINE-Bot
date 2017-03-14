package com.abyan.botouting.model;

import java.util.List;

public class Order {
    private int id;
    private String nama;
    private String pesanan;
    private int total;
    private int hutang;
    private String tanggal;
    private String jam;

    public Order(int id, String nama, String pesanan, int total, int hutang, String tanggal, String jam) {
        this.id = id;
        this.nama = nama;
        this.pesanan = pesanan;
        this.total = total;
        this.hutang = hutang;
        this.tanggal = tanggal;
        this.jam = jam;
    }

    public Order(int id) {
        this.id = id;
    }

    public Order(){}

    public Order(Order order, Order order1) {


    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public int getHutang() {
        return hutang;
    }

    public void setHutang(int hutang) {
        this.hutang = hutang;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPesanan() {
        return pesanan;
    }

    public void setPesanan(String pesanan) {
        this.pesanan = pesanan;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }


    public String display(){
        return  "ID: "+id
                +"\nNama: "+nama
                +"\nPesanan: "+pesanan
                +"\nTotal: Rp."+total
                +"\nWaktu: "+jam+" ("+tanggal+") ";
    }
}
