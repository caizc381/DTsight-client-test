package com.tijiantest.model.resource.meal;

import java.io.Serializable;
import java.util.Date;

/**
 * 套餐多选一组表
 */
public class MealExamitemGroup implements Serializable {

    private Integer id;

    private Integer mealId ;

    private  String groupId;

    private String groupName;

    private Integer itemId;

    private boolean selected;

    private String ext;

    private Date gmtCreated;

    private Date gmtModifed;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMealId() {
        return mealId;
    }

    public void setMealId(Integer mealId) {
        this.mealId = mealId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModifed() {
        return gmtModifed;
    }

    public void setGmtModifed(Date gmtModifed) {
        this.gmtModifed = gmtModifed;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
}
