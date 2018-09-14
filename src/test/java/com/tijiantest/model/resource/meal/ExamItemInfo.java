package com.tijiantest.model.resource.meal;

import java.io.Serializable;

public class ExamItemInfo implements Serializable{

    private static final long serialVersionUID = 8014826815005750628L;
    /**
     * 体检项目ID
     */
    private Integer examItemId;
    /**
     * 套餐ID
     */
    private Integer mealId;
    /**
     * 该单项对于单项包的类型，0：正常项，1：重复项忽略计算
     */
    private Integer typeToMeal;
    /*
    * 新增等价分组字段*/
    private Integer multiChooseId;

    public Integer getMultiChooseId() {
        return multiChooseId;
    }

    public void setMultiChooseId(Integer multiChooseId) {
        this.multiChooseId = multiChooseId;
    }

    public Integer getExamItemId() {
        return examItemId;
    }

    public void setExamItemId(Integer examItemId) {
        this.examItemId = examItemId;
    }

    public Integer getMealId() {
        return mealId;
    }

    public void setMealId(Integer mealId) {
        this.mealId = mealId;
    }

    public Integer getTypeToMeal() {
        return typeToMeal;
    }

    public void setTypeToMeal(Integer typeToMeal) {
        this.typeToMeal = typeToMeal;
    }

}
