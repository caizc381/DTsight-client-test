package com.dtstack.model.vo.uic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.dtstack.lang.support.web.Webs;

import java.util.Date;
import java.util.List;

public class UserFullTenantVo {
    private Long tenantId;
    private String tenantName;
    private String tenantDesc;
    private boolean current;
    private boolean lastLogin;
    private boolean admin;
    private String creator;
    @JsonFormat(pattern = Webs.DATE_TIME_FORMAT, locale = "zh", timezone = "GMT+8")
    private Date createTime;
    private Integer otherUserCount;
    private List<UserFullTenantAdminVo> adminList;

}
