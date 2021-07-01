package com.fernando.proyectofinal;

public enum ActionType {
    CREATE(0),
    READ(1),
    EDIT(2),
    DELETE(3),
    NO_ACTION(4);

    private final int value;
    private ActionType(int value) {
        this.value = value;
    }

    public int getValue() { return value; }

    public static ActionType getActionFromInt(int value) {
        switch (value) {
            case 0: return ActionType.CREATE;
            case 1: return ActionType.READ;
            case 2: return ActionType.EDIT;
            case 3: return ActionType.DELETE;
            default: return ActionType.NO_ACTION;
        }
    }
}
