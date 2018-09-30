package com.dtstack.model.domain.ide.common;

import com.dtstack.model.domain.ide.BaseEntity;

public class Dict extends BaseEntity {

    private Integer type;

    private String dictName;
    private Integer dictValue;

    private String dictNameZH;

    private String dictNameEN;

    private Integer dictSort;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public Integer getDictValue() {
        return dictValue;
    }

    public void setDictValue(Integer dictValue) {
        this.dictValue = dictValue;
    }

    public String getDictNameZH() {
        return dictNameZH;
    }

    public void setDictNameZH(String dictNameZH) {
        this.dictNameZH = dictNameZH;
    }

    public String getDictNameEN() {
        return dictNameEN;
    }

    public void setDictNameEN(String dictNameEN) {
        this.dictNameEN = dictNameEN;
    }

    public Integer getDictSort() {
        return dictSort;
    }

    public void setDictSort(Integer dictSort) {
        this.dictSort = dictSort;
    }
}
