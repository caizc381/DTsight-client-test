package com.dtstack.model.po.uic;

import com.dtstack.lang.data.jpa.String2YNConverter;
import com.dtstack.model.domain.ide.BaseEntity;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

public class UserPO extends BaseEntity {
    private String userName;//用户名
    private String password;//MD5加密后的密码
    private String fullName;//姓名
    private String phone;//用户手机号

    @Convert(converter = String2YNConverter.class)
    private boolean active;//是否已经激活

    private String email;//邮箱地址
    private String company;//用户所属公司
    private Long ownTenantId;//用户创建的租户组id
    private Date lastLoginDate;//最后一次登录时间

    private Long lastLoginTenantId;//最后一次登录的租户组id

    private Integer source;//来源：0-直接输入网址；1-官网；2-云日志；3-easydb；4-阿里云市场；5-已有用户在uic创建；6-微信H
    private String externalId;//与第三方系统对接时的外部用户id
    @Convert(converter = String2YNConverter.class)
    private boolean root;
    @Convert(converter = String2YNConverter.class)
    private boolean admin;

    @ManyToMany(fetch = FetchType.LAZY)
    @WhereJoinTable(clause="is_deleted='N'")
    @JoinTable(
            name="uic_user_tenant_rel",
            joinColumns = {
                    @JoinColumn(name="user_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name="tenant_id")
            }
    )
    private List<TenantPO> tenants;

    @ManyToMany(fetch = FetchType.LAZY)
    @WhereJoinTable(clause = "is_deleted='N'")
    @JoinTable(
            name="uic_user_product_rel",
            joinColumns = {
                    @JoinColumn(name="user_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name="product_code",referencedColumnName="product_code"),
            }
    )
    private List<ProductPO> products;

    @OneToMany(mappedBy = "userPO")
    private List<UserTenantRelPO> userRenantRels;
}
