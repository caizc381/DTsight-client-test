package com.tijiantest.model.card;

import java.io.Serializable;
import java.util.Date;

/**
 * 卡操作日志
 *
 * @author king
 */
public class CardOperateLog implements Serializable {

    private Integer id;

    /**
     * 操作类型
     *
     * @see com.mytijian.card.enums.CardOperateTypeEnum
     */
    private Integer operateType;

    /**
     * 卡号
     */
    private Integer cardId;


    private Integer cardStatus;


    /**
     * 操作人
     */
    private Integer operatorId;


    /**
     * 操作内容
     */
    private String content;


    /**
     * 创建时间
     */
    private Date gmtCreated;

    public CardOperateLog() {
    }

    public CardOperateLog(Integer operateType, Integer cardId, Integer operatorId, String content, Integer cardStatus) {
        this.operateType = operateType;
        this.cardId = cardId;
        this.operatorId = operatorId;
        this.content = content;
        this.cardStatus = cardStatus;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public Integer getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(Integer cardStatus) {
        this.cardStatus = cardStatus;
    }
}
