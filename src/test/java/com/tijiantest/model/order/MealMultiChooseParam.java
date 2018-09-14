package com.tijiantest.model.order;

import java.io.Serializable;

/**
 * 套餐多选一
 */
public class MealMultiChooseParam implements Serializable {

    private static final long serialVersionUID = 2139889770662914963L;
    /**
     * 套餐多选一组id
     */
    private String multiChooseId;
    /**
     * 套餐多选一选中选中单项id
     */
    private Integer selectExamItemId;
    /**
     * 选中的单项是否因为冲突原因不用体检  true 被移除， false 没有
     */
    private Boolean selectExamItemRemove;
    private String multiChooseName;

    public String getMultiChooseName() {
        return multiChooseName;
    }

    public void setMultiChooseName(String multiChooseName) {
        this.multiChooseName = multiChooseName;
    }

    public Boolean getSelectExamItemRemove() {
        return selectExamItemRemove;
    }

    public void setSelectExamItemRemove(Boolean selectExamItemRemove) {
        this.selectExamItemRemove = selectExamItemRemove;
    }

    public String getMultiChooseId() {
        return multiChooseId;
    }

    public void setMultiChooseId(String multiChooseId) {
        this.multiChooseId = multiChooseId;
    }

    public Integer getSelectExamItemId() {
        return selectExamItemId;
    }

    public void setSelectExamItemId(Integer selectExamItemId) {
        this.selectExamItemId = selectExamItemId;
    }
}
