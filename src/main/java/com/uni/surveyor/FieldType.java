package com.uni.surveyor;

public enum FieldType {

    NOT_PATHABLE_AND_NOT_PLACABLE(0),
    PATHABLE_AND_PLACABLE(1),
    ONLY_PATHABLE(2), // for example ramp descent
    OTHERS(3); // include placable but not pathable

    private final int value;

    FieldType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
