package com.abyan.botouting.model;


import com.abyan.botouting.database.Database;

import java.util.List;

public class Menu {
    private String nama;
    private int harga;
    private String kategori;
//    private String[][] menuList = {
//            {"miegoreng", "5000", "makanan"},
//            {"mierebus", "5000", "makanan"},
//            {"omlete", "7500", "makanan"},
//            {"telurgoreng", "3000", "makanan"},
//            {"nasi", "3000", "makanan"},
//            {"nasi1/2", "2000", "makanan"},
//            {"bulatcoklat", "3500", "makanan"},
//            {"bulatpisang", "3500", "makanan"},
//            {"bulatkeju", "4500", "makanan"},
//            {"bulatcoklatkeju", "5500", "makanan"},
//            {"bulatcoklatpisang", "4500", "makanan"},
//            {"bulattelurkeju", "6500", "makanan"},
//            {"bulatsosiskeju", "6000", "makanan"},
//            {"bulatsosistelur", "7500", "makanan"},
//            {"persegicoklat", "2500", "makanan"},
//            {"persegibulatpisang", "2500", "makanan"},
//            {"persegikeju", "3500", "makanan"},
//            {"persegicoklatkeju", "4500", "makanan"},
//            {"persegicoklatpisang", "3500", "makanan"},
//            {"persegitelurkeju", "5500", "makanan"},
//            {"persegisosiskeju", "5000", "makanan"},
//            {"persegisosistelur", "6500", "makanan"},
//            {"nutrisari", "2500", "minuman"},
//            {"susu", "2500", "minuman"},
//            {"goodday", "2500", "minuman"},
//            {"teh", "2500", "minuman"},
//            {"es", "500", "minuman"},
//            {"buahnaga", "9000", "minuman"},
//            {"alpukat", "7000", "minuman"},
//            {"mangga", "7000", "minuman"},
//            {"jambu", "5000", "minuman"},
//            {"melon", "5000", "minuman"},
//            {"jeruk", "5000", "minuman"},
//            {"sosisbakar", "6500", "makanan"},
//            {"telur", "2000", "makanan"},
//            {"sosis", "1500", "makanan"},
//            {"bakso", "1000", "makanan"},
//            {"sayur", "500", "makanan"},
//            {"boncabe", "1500", "makanan"},
//    };

    public Menu(String nama, int harga, String kategori) {
        this.nama = nama;
        this.harga = harga;
        this.kategori = kategori;
    }

    public Menu() {
    }

    public String getNama() {
        return nama;
    }

    public int getHarga() {
        return harga;
    }

    public String getKategori() {
        return kategori;
    }

//    public String[][] getMenu() {
//        return menuList;
//    }
//    public int getLength(){
//        return menuList.length;
//    }

}
