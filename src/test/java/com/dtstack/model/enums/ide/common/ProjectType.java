package com.dtstack.model.enums.ide.common;


public enum ProjectType {

    GENERAL(0),//普通项目，没有绑定生产项目
    TEST(1),//测试项目
    PRODUCE(2);//生产项目

    private Integer type;

    ProjectType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
