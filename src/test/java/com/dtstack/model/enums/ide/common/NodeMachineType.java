package com.dtstack.model.enums.ide.common;

public enum NodeMachineType {
    MASTER(0), SLAVE(1);

    private int type;

    NodeMachineType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
