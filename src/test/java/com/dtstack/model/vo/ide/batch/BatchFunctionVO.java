package com.dtstack.model.vo.ide.batch;

import com.dtstack.model.domain.ide.User;
import com.dtstack.model.domain.ide.batch.BatchFunction;
import org.springframework.beans.BeanUtils;

public class BatchFunctionVO extends BatchFunction {

    public static BatchFunctionVO toVO(BatchFunction origin) {
        BatchFunctionVO vo = new BatchFunctionVO();
        try {
            BeanUtils.copyProperties(origin, vo);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return vo;
    }

    private User createUser;
    private User modifyUser;

    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public User getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(User modifyUser) {
        this.modifyUser = modifyUser;
    }

    @Override
    public String toString() {
        return "BatchFunctionVO{" +
                "functionName=" + getName() +
                ",createUser=" + createUser.getUserName() +
                ", modifyUser=" + modifyUser.getUserName() +
                ", time=" + getGmtModified() +
                '}';
    }
}
