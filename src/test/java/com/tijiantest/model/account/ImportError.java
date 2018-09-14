package com.tijiantest.model.account;


public class ImportError {
    private Integer key;
    private String description;

    public ImportError(Integer key, String description) {
        this.key = key;
        this.description = description;
    }

    public Integer getKey() {
        return this.key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
