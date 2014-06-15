package com.romraider.definition;

public enum TableType {
	TABLE_1D(1), TABLE_2D(2), TABLE_3D(3), TABLE_X_AXIS(4), TABLE_Y_AXIS(5), TABLE_BLOB(6), TABLE_STATIC_X_AXIS(7), TABLE_STATIC_Y_AXIS(8), UNKNOWN(9);
    public int value;

    private TableType(int value) {
            this.value = value;
    }

}
