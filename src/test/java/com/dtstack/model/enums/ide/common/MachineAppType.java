package com.dtstack.model.enums.ide.common;

public enum MachineAppType {
    ENGINE("engine"), WEB("web"), FLINK("flink"), SPARK("spark"), DATAX("datax");

    private String type;

    MachineAppType(String type) {
        this.type = type;
    }

    private String getType() {
        return type;
    }

    public static MachineAppType getMachineAppType(String type) {
        MachineAppType[] machineAppTypes = MachineAppType.values();
        for (MachineAppType machineAppType : machineAppTypes
        ) {
            if (machineAppType.getType().equals(type)) {
                return machineAppType;
            }
        }
        return null;
    }
}
