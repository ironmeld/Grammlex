package org.grammlex.v1;

class Action {
    public static final int SHIFT = 0;
    public static final int REDUCE = 1;
    public static final int ACCEPT = 2;
    private final int type;
    private final int operand;

    public Action(int type, int operand) {
        this.type = type;
        this.operand = operand;
    }

    @Override
    public String toString() {
        switch (type) {
            case REDUCE:
                return "REDUCE(R" + operand + ")";
            case SHIFT:
                return  "SHIFT(" +  operand + ")";
            case ACCEPT:
                return "ACCEPT";
            default:
                return "";
        }
    }

    public String getTypeAsString() {
        switch (type) {
            case REDUCE:
                return "REDUCE";
            case SHIFT:
                return  "SHIFT";
            case ACCEPT:
                return "ACCEPT";
            default:
                return "";
        }
    }
}

