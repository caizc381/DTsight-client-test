package com.dtstack.model.dto.ide;

import com.dtstack.model.domain.ide.Project;

import java.sql.Timestamp;
import java.util.Map;

public class ProjectDTO extends Project {
    private Integer jobSum;
    private Integer tableCount;
    private String totalSize;
    private Map<String,Integer> taskCountMap;
    private Timestamp stick;
    private Integer stickStatus;

    public Integer getJobSum() {
        return jobSum;
    }

    public void setJobSum(Integer jobSum) {
        this.jobSum = jobSum;
    }

    public Integer getTableCount() {
        return tableCount;
    }

    public void setTableCount(Integer tableCount) {
        this.tableCount = tableCount;
    }

    public String getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }

    public Map<String, Integer> getTaskCountMap() {
        return taskCountMap;
    }

    public void setTaskCountMap(Map<String, Integer> taskCountMap) {
        this.taskCountMap = taskCountMap;
    }

    public Timestamp getStick() {
        return stick;
    }

    public void setStick(Timestamp stick) {
        this.stick = stick;
    }

    public Integer getStickStatus() {
        return stickStatus;
    }

    public void setStickStatus(Integer stickStatus) {
        this.stickStatus = stickStatus;
    }
}
