package com.sagittariusk2.ui.DataModel;

import java.time.LocalDate;

public class SimpleTask {
    protected String taskID, taskName, taskDesc, taskLink, taskCategory;
    protected boolean taskCompleted;
    protected LocalDate taskScheduleDate;
    protected String taskScheduleTime, taskFinishTime;
    protected int taskNotifyBefore;

    public SimpleTask(String taskID) {
        this.taskID = taskID;
    }

    public SimpleTask(String taskID, String taskName, String taskDesc, String taskLink, boolean taskCompleted, LocalDate taskScheduleDate, String taskScheduleTime, int taskNotifyBefore, String taskFinishTime) {
        this.taskID = taskID;
        this.taskName = taskName;
        this.taskDesc = taskDesc;
        this.taskLink = taskLink;
        this.taskCompleted = taskCompleted;
        this.taskScheduleDate = taskScheduleDate;
        this.taskScheduleTime = taskScheduleTime;
        this.taskNotifyBefore = taskNotifyBefore;
        this.taskFinishTime = taskFinishTime;
    }

    public String getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(String taskCategory) {
        this.taskCategory = taskCategory;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public String getTaskLink() {
        return taskLink;
    }

    public void setTaskLink(String taskLink) {
        this.taskLink = taskLink;
    }

    public boolean isTaskCompleted() {
        return taskCompleted;
    }

    public void setTaskCompleted(boolean taskCompleted) {
        this.taskCompleted = taskCompleted;
    }

    public LocalDate getTaskScheduleDate() {
        return taskScheduleDate;
    }

    public void setTaskScheduleDate(LocalDate taskScheduleDate) {
        this.taskScheduleDate = taskScheduleDate;
    }

    public String getTaskScheduleTime() {
        return taskScheduleTime;
    }

    public void setTaskScheduleTime(String taskScheduleTime) {
        this.taskScheduleTime = taskScheduleTime;
    }

    public int getTaskNotifyBefore() {
        return taskNotifyBefore;
    }

    public void setTaskNotifyBefore(int taskNotifyBefore) {
        this.taskNotifyBefore = taskNotifyBefore;
    }

    public String getTaskFinishTime() {
        return taskFinishTime;
    }

    public void setTaskFinishTime(String taskFinishTime) {
        this.taskFinishTime = taskFinishTime;
    }
}
