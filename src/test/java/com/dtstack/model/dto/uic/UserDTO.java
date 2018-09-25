package com.dtstack.model.dto.uic;

import java.util.Date;

public class UserDTO {
    private Long id;//用户id
    private Long creator;//创建者
    private Date gmtCreate;//用户创建时间
    private String username;//登陆用户名
    private String fullName;//姓名
    private String phone;//用户手机号
    private boolean active;//是否已经激活
    private String email;//邮箱地址
    private String company;//用户所属公司
    private Long ownTelantId;//用户创建的租户组id
    private String password;//密码
    private String externalId;//外部用户id
    private boolean root;//是否是root用户
    private boolean admin;//是否是admin用户
}
