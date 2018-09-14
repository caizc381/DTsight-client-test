package com.tijiantest.model.settlement;


import java.io.Serializable;
import java.util.List;

import com.tijiantest.util.pagination.Page;

/**
 * Created by wangzhongxing on 2017/12/4.
 */
public class TradeConsumeQuotaDetailQueryDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 机构id，若该参数不为null，优先取该机构下面的消费明细信息
     */
    private Integer organizationId;

    /**
     * 机构id集合
     */
    private List<Integer> organizationIds;

    /**
     * 记录开始时间
     */
    private String startTime;

    /**
     * 记录结束时间
     */
    private String endTime;

    /**
     * 场景
     * @See ConsumeQuotaDetailSceneEnum
     */
    private List<Integer> scene;

    /**
     * 状态
     * @See ConsumeQuotaDetailStatusEnum
     */
    private List<Integer> status;

    /**
     * 省份id
     */
    private Integer provinceId;

    /**
     * 城市id
     */
    private Integer cityId;

    /**
     * 地区id
     */
    private Integer districtId;

    private Page page;

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<Integer> getScene() {
        return scene;
    }

    public void setScene(List<Integer> scene) {
        this.scene = scene;
    }

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public List<Integer> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(List<Integer> organizationIds) {
        this.organizationIds = organizationIds;
    }
}
