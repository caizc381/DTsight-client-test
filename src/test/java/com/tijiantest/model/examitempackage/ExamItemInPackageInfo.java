package com.tijiantest.model.examitempackage;

import java.io.Serializable;

import com.tijiantest.model.resource.ConflictReason;

public class ExamItemInPackageInfo implements Serializable{

    private static final long serialVersionUID = 8665346066112264969L;
    /**
     * 体检项目ID
     */
    private Integer examItemId;
    /**
     * 包ID
     */
    private Integer packageId;
    /**
     * 该单项对于单项包的类型，0：正常项，1：重复项忽略计算，2：冲突项忽略计算，参考TypeToPackageEnum
     */
    private Integer typeToPackage;
    
    private ConflictReason conflictReason;

    public ConflictReason getConflictReason() {
		return conflictReason;
	}

	public void setConflictReason(ConflictReason conflictReason) {
		this.conflictReason = conflictReason;
	}

	public Integer getExamItemId() {
        return examItemId;
    }

    public void setExamItemId(Integer examItemId) {
        this.examItemId = examItemId;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public Integer getTypeToPackage() {
        return typeToPackage;
    }

    public void setTypeToPackage(Integer typeToPackage) {
        this.typeToPackage = typeToPackage;
    }

}
