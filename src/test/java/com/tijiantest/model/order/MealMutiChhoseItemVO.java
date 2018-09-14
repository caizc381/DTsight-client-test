package com.tijiantest.model.order;

import java.io.Serializable;

public class MealMutiChhoseItemVO implements Serializable {
    private static final long serialVersionUID = 5097770836893343923L;
    private Integer id;
    private String itemName;
    private String description;
    private Integer price;
    /**
     * 选中
     */
    private Boolean selected;

    private Boolean remove;
    /**
     * 推荐选中
     */
    private Boolean recommend;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getRecommend() {
        return recommend;
    }

    public void setRecommend(Boolean recommend) {
        this.recommend = recommend;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Boolean getRemove() {
        return remove;
    }

    public void setRemove(Boolean remove) {
        this.remove = remove;
    }
}
