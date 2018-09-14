package com.tijiantest.model.resource;

import java.io.Serializable;

/**
 * Created by wangzhongxing on 2017/4/7.
 */
public class ConflictReason implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 与被删除的单项有冲突的单项id
     */
    private Integer itemId;
    /**
     * 因冲突被删除的单项id
     */
    private Integer conflictItemId;
    /**
     * 冲突类型，参考 ItemSelectException.ConflictType
     */
    private Integer conflictType;
    /**
     * 与被删除的单项有冲突的单项的加项包id，没有加项包id该字段为null
     */
    private Integer packageId;
    /**
     * 因冲突被删除的单项的加项包id，没有加项包id该字段为null
     */
    private Integer conflictPackageId;

    public ConflictReason(){}

    public ConflictReason(Integer itemId, Integer conflictItemId, Integer packageId, Integer conflictPackageId,
                          Integer conflictType){
        this.setItemId(itemId);
        this.setConflictItemId(conflictItemId);
        this.setPackageId(packageId);
        this.setConflictPackageId(conflictPackageId);
        this.setConflictType(conflictType);

    }

    public Integer getConflictPackageId() {
        return conflictPackageId;
    }

    public void setConflictPackageId(Integer conflictPackageId) {
        this.conflictPackageId = conflictPackageId;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public Integer getConflictType() {
        return conflictType;
    }

    public void setConflictType(Integer conflictType) {
        this.conflictType = conflictType;
    }

    public Integer getConflictItemId() {
        return conflictItemId;
    }

    public void setConflictItemId(Integer conflictItemId) {
        this.conflictItemId = conflictItemId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
}
