package com.iemr.flw.masterEnum;


public enum DiseaseType {
    MALARIA(1),
    KALA_AZAR(2),
    AES_JE(3),
    FILARIA(4),
    LEPROSY(5);

    private final int id;

    DiseaseType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }



    public static DiseaseType fromId(int id) {
        for (DiseaseType disease : values()) {
            if (disease.id == id) {
                return disease;
            }
        }
        throw new IllegalArgumentException("Invalid Disease ID: " + id);
    }
}
