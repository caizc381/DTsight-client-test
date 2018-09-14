package com.tijiantest.model.hospital;

import org.apache.commons.lang.StringUtils;

public enum CalculatorServiceEnum {

	YUAN_ROUND_CALCULATOR(1,"defaultCalculator"),
	JIAO_ROUND_CALCULATOR(2,"jiaoRoundCalculator"),
	FEN_ROUND_CALCULATOR(3,"fenRoundCalculator");
	private int code;
    private String name;

    CalculatorServiceEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public static int getCodeByName(String name){
    	if (StringUtils.isEmpty(name)) {
			return 1;
		}
    	
    	for (CalculatorServiceEnum e : CalculatorServiceEnum.values()) {
    		if (name.equals(e.getName()))
                return e.getCode();
        }
        return 1;
    }
    
	public static String getNameByCode(int code) {
		for (CalculatorServiceEnum e : CalculatorServiceEnum.values()) {
			if (code == e.getCode())
				return e.getName();
		}
		return null;
	}
}

