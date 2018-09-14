package com.tijiantest.model.resource.meal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;

import com.tijiantest.model.examitempackage.ExamItemInPackageInfo;
import com.tijiantest.model.examitempackage.ExamItemPackage;
import com.tijiantest.model.examitempackage.TypeToPackageEnum;
import com.tijiantest.model.item.ExamItem;

/**
 * 用户选择的单项包信息
 *
 * @create 2016年11月10日 上午9:49:44
 * @author tangyi
 * @version
 */
public class UserPackagesInfo implements Serializable{
    private static final long serialVersionUID = -4833248868822898770L;
   /**
     * 用户选择的加项包
     */
    private List<ExamItemPackage>       packages;
    /**
     * 单项包内部的单项信息
     */
    private List<ExamItemInPackageInfo> examItemInfos;
    
    private List<ExamItem> normalPackageItems;
    private List<ExamItem> duplicatePackageItems;
    private List<ExamItemPackage> normalPackages;

    public List<ExamItemPackage> getPackages() {
        return packages;
    }

    public void setPackages(List<ExamItemPackage> packages) {
        this.packages = packages;
    }

    public List<ExamItemInPackageInfo> getExamItemInfos() {
        return examItemInfos;
    }

    public void setExamItemInfos(List<ExamItemInPackageInfo> examItemInfos) {
        this.examItemInfos = examItemInfos;
    }
    
    public void setNormalPackageItems(List<ExamItem> normalPackageItems) {
		this.normalPackageItems = normalPackageItems;
	}

	public void setDuplicatePackageItems(List<ExamItem> duplicatePackageItems) {
		this.duplicatePackageItems = duplicatePackageItems;
	}

	public void setNormalPackage(List<ExamItemPackage> normalPackages) {
		this.normalPackages = normalPackages;
	}

	/**
     * 获取用户所选的加项包中有效的单项
     * @return
     */
    public List<ExamItem> getNormalPackageItems() {
        if (CollectionUtils.isNotEmpty(packages) && CollectionUtils.isNotEmpty(examItemInfos)) {
        	normalPackageItems = examItemInfos
                    .stream().filter(itemInfo -> itemInfo
                            .getTypeToPackage() == TypeToPackageEnum.NORMAL.getCode())
                    .map(itemInfo -> {
                        for (ExamItemPackage pack : packages) {
                            for (ExamItem item : pack.getItemList()) {
                                if (itemInfo.getExamItemId().equals(item.getId())) {
                                    return item;
                                }
                            }
                        }
                        return null;
                    }).filter(item -> item != null).collect(Collectors.toList());
            return normalPackageItems;
        }
        return Collections.emptyList();
    }

    public List<ExamItem> getDuplicatePackageItems() {
        if (CollectionUtils.isNotEmpty(packages) && CollectionUtils.isNotEmpty(examItemInfos)) {
        	duplicatePackageItems =examItemInfos
                    .stream().filter(itemInfo -> itemInfo
                            .getTypeToPackage() == TypeToPackageEnum.DUPLICATE.getCode())
                    .map(itemInfo -> {
                        for (ExamItemPackage pack : packages) {
                            for (ExamItem item : pack.getItemList()) {
                                if (itemInfo.getExamItemId().equals(item.getId())) {
                                    return item;
                                }
                            }
                        }
                        return null;
                    }).filter(item -> item != null).collect(Collectors.toList());
        	return duplicatePackageItems;
        }
        return Collections.emptyList();
    }

    /**
     * 过滤包中项目全部为冲突项或重复项的加项包，防止空包计算调整金额
     * @return
     */
    public List<ExamItemPackage> getNormalPackage(){
        if (CollectionUtils.isEmpty(packages) || CollectionUtils.isEmpty(examItemInfos)){
            return new ArrayList<>(1);
        }
        //List<ExamItemPackage> normalPackages = new ArrayList<>();
        for (ExamItemPackage examItemPackage : packages){
            for (ExamItemInPackageInfo examItemInPackageInfo : examItemInfos){
                if (examItemInPackageInfo.getPackageId().equals(examItemPackage.getId()) &&
                        examItemInPackageInfo.getTypeToPackage().equals(TypeToPackageEnum.NORMAL.getCode())){
                    normalPackages.add(examItemPackage);
                    break;
                }
            }
        }

        return normalPackages;
    }

    /**
     * 获取加项包中正常的项目
     * @param isIncludeDuplicatedItem 是否包含重复项，true包含，false不包含
     * @return
     */
    public Map<Integer, List<Integer>> getNormalPackageIdItemIdMap(boolean isIncludeDuplicatedItem){
        @SuppressWarnings("unchecked")
		Map<Integer, List<Integer>> resultMap = new HashedMap();
        if (CollectionUtils.isEmpty(packages) || CollectionUtils.isEmpty(examItemInfos)){
            return resultMap;
        }

        for (ExamItemInPackageInfo examItemInPackageInfo : examItemInfos){
            if (!isIncludeDuplicatedItem && (examItemInPackageInfo.getTypeToPackage().equals(TypeToPackageEnum.CONFLICT.getCode())
                    || examItemInPackageInfo.getTypeToPackage().equals(TypeToPackageEnum.DUPLICATE.getCode()))){
                continue;
            }
            if (isIncludeDuplicatedItem && examItemInPackageInfo.getTypeToPackage().equals(TypeToPackageEnum.CONFLICT.getCode())){
                continue;
            }

            List<Integer> itemIds = resultMap.get(examItemInPackageInfo.getPackageId());
            if (null == itemIds){
                itemIds = new ArrayList<>();
                resultMap.put(examItemInPackageInfo.getPackageId(), itemIds);
            }
            itemIds.add(examItemInPackageInfo.getExamItemId());
        }
        // 因冲突导致包中的项目全部被删除要返回空的list，前端划中划线要用
        for (ExamItemPackage examItemPackage : packages){
            if (null == resultMap.get(examItemPackage.getId())){
                resultMap.put(examItemPackage.getId(), new ArrayList<>(1));
            }
        }

        return resultMap;
    }

}
