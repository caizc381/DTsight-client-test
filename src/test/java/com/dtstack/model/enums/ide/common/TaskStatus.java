package com.dtstack.model.enums.ide.common;

import com.google.common.collect.Lists;

import java.util.List;

public enum TaskStatus {
    //MANUALSUCCESS: 手动设置为成功
    UNSUBMIT(0), CREATED(1), SCHEDULED(2), DEPLOYING(3), RUNNING(4), FINISHED(5), CANCELING(6), CANCELED(7), FAILED(8), SUBMITFAILD(9), SUBMITTING(10), RESTARTING(11), MANUALSUCCESS(12), KILLED(13), SUBMITTED(14), WAITENGINE(16),
    WAITCOMPUTE(17), FROZEN(18), ENGINEACCEPTED(19), ENGINEDISTRIBUTE(20), PARENTFAILED(21);

    private int status;

    private static List<Integer> canStopStatus = Lists.newArrayList(
            UNSUBMIT.getStatus(),
            CREATED.getStatus(), SCHEDULED.getStatus(),
            DEPLOYING.getStatus(), RUNNING.getStatus(),
            SUBMITTING.getStatus(), RESTARTING.getStatus(),
            SUBMITTED.getStatus(), WAITENGINE.getStatus(),
            WAITCOMPUTE.getStatus(), ENGINEACCEPTED.getStatus(),
            ENGINEDISTRIBUTE.getStatus());

    TaskStatus(int status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }

    /**
     * 需要捕获无法转换异常
     *
     * @param taskStatus
     * @return
     */
    public static TaskStatus getTaskStatus(String taskStatus) {
        return TaskStatus.valueOf(taskStatus);
    }

    public static TaskStatus getTaskStatusByVal(int val) {
        for (TaskStatus taskStatus : TaskStatus.values()) {
            if (taskStatus.getStatus() == val) {
                return taskStatus;
            }
        }

        return null;
    }

    public static boolean needClean(Byte status) {
        int sta = status.intValue();
        if (sta == TaskStatus.FINISHED.status || sta == TaskStatus.CANCELED.status || sta == TaskStatus.FAILED.status) {
            return true;
        }
        return false;
    }

    public static List<Integer> getCanStopStatus() {
        return canStopStatus;
    }
}
