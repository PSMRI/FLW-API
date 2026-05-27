package com.iemr.flw.masterEnum;

public enum StateCode {
    AM(5),
    CG(8);

    private final int stateCode;

    StateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public int getStateCode() {
        return stateCode;
    }



    public static StateCode fromId(int id) {
        for (StateCode stateCode : values()) {
            if (stateCode.stateCode == id) {
                return stateCode;
            }
        }
        throw new IllegalArgumentException("Invalid State ID: " + id);
    }

}
