package com.dtstack.model.ide;

import java.io.Serializable;
import java.sql.Timestamp;

public class BaseEntity implements Serializable {
    private  Long id=0L;

    private Timestamp gmtCreate;//创建时间
    private Timestamp gmtModified;//修改时间

    private Integer isDeleted = 0 ;//是否删除

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
