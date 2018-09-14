package com.tijiantest.model.settlement;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by wangzhongxing on 2017/11/30.
 */
public class TradeConsumeQuotaDetail implements Serializable {

    private static final long serialVersionUID = -1L;

    private Integer id;

    /**
     * 消费明细sn
     */
    private String sn;

    /**
     * 体检机构id，-1表示平台
     */
    private Integer organizationId;

    /**
     * 体检单位id
     */
    private Integer companyId;

    /**
     * 关联的平台账单号
     */
    private String platformBillSn;

    /**
     * 金额
     */
    private Long amount;

    /**
     * 消费额度发生时间
     */
    private Date payTime;

    /**
     * 场景  1=医院开票 2=账务调整 3=结算盈余 4=结算支付
     */
    private Integer scene;

    /**
     * 凭证
     */
    private String certificate;

    /**
     * 审核状态  1=医院审核中 2=冻结中 3=医院已确认 4=平台已撤销
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 乐观锁版本号
     */
    private Integer version;

    /**
     * 0=数据有效  1=数据无效
     */
    private Integer isDeleted;

    /**
     * 数据插入时间
     */
    private Date gmtCreated;

    /**
     * 数据更新时间
     */
    private Date gmtModified;

    /**
     * 医院名称
     */
    private String hospitalName;

    /**
     * 单位名称
     */
    private String companyName;

    /**
     * 添加流转日志类
     */
    private TradeCommonLogAddDTO commonLogAddDTO;

    /**
     * 流转日志
     */
	private List<TradeCommonLogResultDTO> circulationLog;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getPlatformBillSn() {
        return platformBillSn;
    }

    public void setPlatformBillSn(String platformBillSn) {
        this.platformBillSn = platformBillSn;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Integer getScene() {
        return scene;
    }

    public void setScene(Integer scene) {
        this.scene = scene;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public TradeCommonLogAddDTO getCommonLogAddDTO() {
        return commonLogAddDTO;
    }

    public void setCommonLogAddDTO(TradeCommonLogAddDTO commonLogAddDTO) {
        this.commonLogAddDTO = commonLogAddDTO;
    }

    public List<TradeCommonLogResultDTO> getCirculationLog() {
        return circulationLog;
    }

    public void setCirculationLog(List<TradeCommonLogResultDTO> circulationLog) {
        this.circulationLog = circulationLog;
    }
}
