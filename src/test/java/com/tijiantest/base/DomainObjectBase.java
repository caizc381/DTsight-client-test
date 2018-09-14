/**
 * 
 */
package com.tijiantest.base;

import java.util.Date;

/**
 * @author yuefengyang
 * 
 */
public class DomainObjectBase {

    private Date createTime;
    private Date updateTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }


}
