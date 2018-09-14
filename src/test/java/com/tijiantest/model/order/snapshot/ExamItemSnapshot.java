package com.tijiantest.model.order.snapshot;


import java.util.List;

import com.tijiantest.model.resource.meal.ExamItemSnap;

public class ExamItemSnapshot extends ExamItemSnap {

    private static final long serialVersionUID = 439864332931483153L;

    /**
     * 套餐多选一组ID
     */
    private String multiChooseId;
    /**
     * 套餐多选一组名称
     */
    private String multiChooseName;

    /**
     * 所属单项包ID
     */
    private Integer packageId;
    /**
     * 对于单项包的类型
     */
    private Integer typeToPackage;
    /**
     * 回单项目类型(1、拒检 2、已检)
     *
     * @return
     */
    private String refuseStatus;

    /**
     * 回单项目是否是现场加项或现场减项(1、现场加项，2、现场减项，0、没加没减)
     *
     * @return
     */
    private Integer addOrDel;

    //start以下几个字段是为展示订单详情扩展的几个属性字段
    /**
     * 体检后的检查状态：1：已检；2：拒检；3：未检
     */
    private Integer checkState;
    /**
     * 是否是加项：true：加项，false：非加项（原订单拒检和未检）
     */
    private boolean addExam;

    /**
     * 项目退款加项状态：1：退款；2：加项；3：无退款也无加项
     */
    private Integer refundState;
    //end以上几个字段是为展示订单详情扩展的几个属性字段

    /**
     * 如果此项目为合并项，此值为合并项的子项集合
     */
    private List<ExamItemSnapshot> mergeItemChildren;
	

    public List<ExamItemSnapshot> getMergeItemChildren() {
        return mergeItemChildren;
    }

    public void setMergeItemChildren(List<ExamItemSnapshot> mergeItemChildren) {
        this.mergeItemChildren = mergeItemChildren;
    }

    public Integer getCheckState() {
        return checkState;
    }

    public void setCheckState(Integer checkState) {
        this.checkState = checkState;
    }

    public boolean isAddExam() {
        return addExam;
    }

    public void setAddExam(boolean addExam) {
        this.addExam = addExam;
    }

    public Integer getRefundState() {
        return refundState;
    }

    public void setRefundState(Integer refundState) {
        this.refundState = refundState;
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

    public String getRefuseStatus() {
        return refuseStatus;
    }

    public void setRefuseStatus(String refuseStatus) {
        this.refuseStatus = refuseStatus;
    }

    public Integer getAddOrDel() {
        return addOrDel;
    }

    public void setAddOrDel(Integer addOrDel) {
        this.addOrDel = addOrDel;
    }

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
}
