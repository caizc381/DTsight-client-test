package com.tijiantest.model.settlement;

import java.io.Serializable;
import java.util.List;

public class FinancePaymentDto implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 915916735237873227L;

	private List<String> snList;

    private Integer organizationId;

    private String imageUrl;

    private String remark;

    public List<String> getSnList() {
        return snList;
    }

    public void setSnList(List<String> snList) {
        this.snList = snList;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
