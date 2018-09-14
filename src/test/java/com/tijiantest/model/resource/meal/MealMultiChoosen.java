package com.tijiantest.model.resource.meal;


import java.io.Serializable;
import java.util.List;

public class MealMultiChoosen implements Serializable {
    private static final long serialVersionUID = -5104281762476975260L;
    private Integer mealId;
    private String multiChoosenId;
    private String multiChoosenName;
    private String ext;
    private List<MultiChoosenExamitem> multiChoosenExamitemList;

    public MealMultiChoosen() {
    }

    public Integer getMealId() {
        return this.mealId;
    }

    public void setMealId(Integer mealId) {
        this.mealId = mealId;
    }

    public String getMultiChoosenId() {
        return this.multiChoosenId;
    }

    public void setMultiChoosenId(String multiChoosenId) {
        this.multiChoosenId = multiChoosenId;
    }

    public String getMultiChoosenName() {
        return this.multiChoosenName;
    }

    public void setMultiChoosenName(String multiChoosenName) {
        this.multiChoosenName = multiChoosenName;
    }

    public String getExt() {
        return this.ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public List<MultiChoosenExamitem> getMultiChoosenExamitemList() {
        return this.multiChoosenExamitemList;
    }

    public void setMultiChoosenExamitemList(List<MultiChoosenExamitem> multiChoosenExamitemList) {
        this.multiChoosenExamitemList = multiChoosenExamitemList;
    }
}
