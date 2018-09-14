package com.tijiantest.model.order;

import java.util.Date;

/**
 * Created by king on 2017/3/30.
 */
public class ExamOrderOperateLogDO {

    private Integer id;

    /**
     * 订单号码
     */
    private String orderNum;

    /**
     * 操作类型，1：下单 2：支付 3：完成支付 4：撤销 5：删除 6：关闭  7：回单 8：导出 9：改项10：改期 11：收款
     * @see com.mytijian.order.enums.OrderOperateTypeEnum
     */
    private Integer type;

    /**
     * 备注
     */
    private String remark;
    
    /**
     * 修改内容
     */
    private String content;

    /**
     * 订单状态
     */
    private Integer orderStatus;
    
    /**
     * 操作人
     */
    private Integer operator;

    /**
     * 创建时间
     */
    private Date gmt_created;

    /**
     * 系统 ： 1： c端手机，2：C端pc，3：crm系统，4：渠道商系统，5：定时任务，6：manage系统
     * @see com.mytijian.order.enums.OperateAppEnum
     */
    private Integer system;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getOperator() {
        return operator;
    }

    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    public Date getGmt_created() {
        return gmt_created;
    }

    public void setGmt_created(Date gmt_created) {
        this.gmt_created = gmt_created;
    }

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getSystem() {
		return system;
	}

	public void setSystem(Integer system) {
		this.system = system;
	}
	
}

