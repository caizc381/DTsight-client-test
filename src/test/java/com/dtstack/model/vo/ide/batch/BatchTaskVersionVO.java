package com.dtstack.model.vo.ide.batch;

import com.dtstack.model.domain.ide.batch.BatchTaskVersion;

import java.util.List;

public class BatchTaskVersionVO extends BatchTaskVersion {
    private String userName;

    private List<String> dependencyTaskNames;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getDependencyTaskNames() {
        return dependencyTaskNames;
    }

    public void setDependencyTaskNames(List<String> dependencyTaskNames) {
        this.dependencyTaskNames = dependencyTaskNames;
    }
}
