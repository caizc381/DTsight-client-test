package com.tijiantest.model.order;

import java.io.Serializable;
import java.util.List;

public class MealMutiChooseVO implements Serializable {
    private static final long serialVersionUID = -5206919397063029013L;
    private String multiChooseId;
    private String multiChooseName;
    private List<MealMutiChhoseItemVO> groupItemList;

    public String getMultiChooseId() {
        return multiChooseId;
    }

    public void setMultiChooseId(String multiChooseId) {
        this.multiChooseId = multiChooseId;
    }

    public String getMultiChooseName() {
        return multiChooseName;
    }

    public void setMultiChooseName(String multiChooseName) {
        this.multiChooseName = multiChooseName;
    }

    public List<MealMutiChhoseItemVO> getGroupItemList() {
        return groupItemList;
    }

    public void setGroupItemList(List<MealMutiChhoseItemVO> groupItemList) {
        this.groupItemList = groupItemList;
    }
}
