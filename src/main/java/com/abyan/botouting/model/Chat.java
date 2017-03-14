package com.abyan.botouting.model;

import java.util.Arrays;


public class Chat {

    private String[] jawab1 = {
            "Dalem?",
            "Iya?",
            "Yo?",
            "何？",
    };

    private String[] jawab2 = {
            "Opo?",
            "Apa?",
            "Naon?",
    };

    private String[] jawab3 = {
            "Apaan?",
            "Ha?",
            "*nengok*",
    };

    private String[] jawab4 = {
            "Paan si panggil panggil?",
            "Paan?! Lagi kerja, jangan ganggu",
            "Kzl bat gua lu nanya mulu"
    };

    private String[] level6 = {
            "Kerja rodi gua", "*gempor*", "Tepar gua"
    };
    private String[] level5 = {
            "Iya, ampun bang", "Ga kuat bang", "Banget, suruh bot laen kek"
    };
    private String[] level4 = {
            "Hayati lelah, bang", "Aku lelah", "Tolong aku..."
    };
    private String[] level3 = {
            "Mulai lelah", "Dikit lah", "Sedikit..."
    };
    private String[] level2 = {
            "Belum", "Belon bang", "*geleng-geleng*"
    };
    private String[] level1 = {
            "Masih seger", "Ngga", "Kaga bos"
    };



    private String[][] level = {
            {"300", Arrays.toString(level6)},
            {"150", Arrays.toString(level5)},
            {"100", Arrays.toString(level4)},
            {"50", Arrays.toString(level3)},
            {"20", Arrays.toString(level2)},
            {"0", Arrays.toString(level1)},
    };

    private String[][] jawab = {
            {"150", Arrays.toString(jawab4)},
            {"100", Arrays.toString(jawab3)},
            {"50", Arrays.toString(jawab2)},
            {"0", Arrays.toString(jawab1)}

    };

    public String[][] getLevel() {
        return level;
    }

    public String[][] getJawab() {
        return jawab;
    }
}
