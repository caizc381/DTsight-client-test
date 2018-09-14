package com.tijiantest.model.order.snapshot;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.google.common.collect.Lists;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.resource.meal.ExamItemInfo;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.model.resource.meal.MealSnap;

/**
 * 订单的套餐快照，包括套餐内项目，套餐的加项，套餐的减项。注意：不包括单项包内的项目
 *
 * @create 2016年11月9日 上午10:43:36
 * @author tangyi
 * @version
 */
public class MealSnapshot extends MealSnap {

    private static final long serialVersionUID = 222033733097594798L;

    private Meal               originMeal;
    /**
     * 套餐项目，包括套餐内项目+加项
     */
    private List<ExamItem>     examItems;
    /**
     * 套餐项目信息，加项减项信息
     */
    private List<ExamItemInfo> examItemInfos;

	@SuppressWarnings("unused")
	private List<ExamItem> normalExamItems;
    
    @SuppressWarnings("unused")
	private List<ExamItem> addExamItems;
    
    @SuppressWarnings("unused")
	private List<ExamItem> removeExamItems;

    @SuppressWarnings("unused")
	private List<ExamItem> inMealExamItems;
    	
    public List<ExamItem> getExamItems() {
        return examItems;
    }

    public void setExamItems(List<ExamItem> examItems) {
        this.examItems = examItems;
    }

    public List<ExamItemInfo> getExamItemInfos() {
        return examItemInfos;
    }

    public void setExamItemInfos(List<ExamItemInfo> examItemInfos) {
        this.examItemInfos = examItemInfos;
    }

    public Meal getOriginMeal() {
        return originMeal;
    }

    public void setOriginMeal(Meal originMeal) {
        this.originMeal = originMeal;
    }

    public List<ExamItem> getNormalExamItems() {
        List<ExamItem> examItemList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(examItems) && CollectionUtils.isNotEmpty(examItemInfos)) {
            for (ExamItem examItem : examItems) {
                for (ExamItemInfo examItemInfo : examItemInfos) {
                    if (examItemInfo.getExamItemId().equals(examItem.getId())) {
                        if (examItemInfo.getTypeToMeal() == ExamItemToMealEnum.inMeal.getCode()
                                || examItemInfo.getTypeToMeal() == ExamItemToMealEnum.addToMeal
                                        .getCode()) {
                            examItemList.add(examItem);
                        }
                    }
                }
            }
        }
        return examItemList;
    }

    public List<ExamItem> getAddExamItems() {
        List<ExamItem> examItemList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(examItems) && CollectionUtils.isNotEmpty(examItemInfos)) {
            for (ExamItem examItem : examItems) {
                for (ExamItemInfo examItemInfo : examItemInfos) {
                    if (examItemInfo.getExamItemId().equals(examItem.getId())) {
                        if (examItemInfo.getTypeToMeal() == ExamItemToMealEnum.addToMeal
                                .getCode()) {
                            examItemList.add(examItem);
                        }
                    }
                }
            }
        }
        return examItemList;
    }

    public List<ExamItem> getRemoveExamItems() {
        List<ExamItem> examItemList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(examItems) && CollectionUtils.isNotEmpty(examItemInfos)) {
            for (ExamItem examItem : examItems) {
                for (ExamItemInfo examItemInfo : examItemInfos) {
                    if (examItemInfo.getExamItemId().equals(examItem.getId())) {
                        if (examItemInfo.getTypeToMeal() == ExamItemToMealEnum.outMeal.getCode()) {
                            examItemList.add(examItem);
                        }
                    }
                }
            }
        }
        return examItemList;
    }

    public List<ExamItem> getInMealExamItems() {
        List<ExamItem> examItemList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(examItems) && CollectionUtils.isNotEmpty(examItemInfos)) {
            for (ExamItem examItem : examItems) {
                for (ExamItemInfo examItemInfo : examItemInfos) {
                    if (examItemInfo.getExamItemId().equals(examItem.getId())) {
                        if (examItemInfo.getTypeToMeal() == ExamItemToMealEnum.inMeal.getCode()) {
                            examItemList.add(examItem);
                        }
                    }
                }
            }
        }
        return examItemList;
    }

    public String buildSnapshotJSONString() {
        PropertyFilter filter = new PropertyFilter() {
            public boolean apply(Object source, String name, Object value) {
                if (source instanceof MealSnapshot) {
                    List<String> includeProperties = Arrays.asList("id", "name", "price",
                            "originalPrice", "adjustPrice", "discount", "externalDiscount",
                            "gender", "otherMoneys", "originMeal", "examItems", "examItemInfos");
                    if (includeProperties.contains(name)) {
                        return true;
                    } else {
                        return false;
                    }
                }
                if (source instanceof ExamItem) {
                    List<String> includeProperties = Arrays.asList("id", "name", "gender",
                            "hospitalId", "mealId", "price", "groupId", "discount", "hisItemId",
                            "itemType", "departmentId", "syncPrice", "tagName");
                    if (includeProperties.contains(name)) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return true;
            }
        };
        return JSON.toJSONString(this, filter);
    }

	public void setNormalExamItems(List<ExamItem> normalExamItems) {
		this.normalExamItems = getNormalExamItems();
	}

	public void setAddExamItems(List<ExamItem> addExamItems) {
		this.addExamItems = getAddExamItems();
	}

	public void setRemoveExamItems(List<ExamItem> removeExamItems) {
		this.removeExamItems = getRemoveExamItems();
	}

	public void setInMealExamItems(List<ExamItem> inMealExamItems) {
		this.inMealExamItems = getInMealExamItems();
	}
}
