package com.tijiantest.model.resource.meal;


import java.io.Serializable;

public class MultiChoosenExamitem implements Serializable {
    private static final long serialVersionUID = -5790286419103132171L;
    private Integer itemId;
    private boolean selected;
    private String itemName;
    private Integer price;
    private String description;

    public MultiChoosenExamitem() {
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getItemId() {
        return this.itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getPrice() {
        return this.price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
