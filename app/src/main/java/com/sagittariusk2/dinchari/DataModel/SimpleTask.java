package com.sagittariusk2.dinchari.DataModel;

public class SimpleTask {
    private String taskID, taskName, taskDesc, taskLink, taskCategory;
    private boolean taskCompleted;
    private String taskScheduleDate;
    private String taskScheduleTime, taskFinishTime;
    private int taskNotifyBefore;
    private String repeatTime;
    private long createTime;

    public SimpleTask() {}

    public SimpleTask(String taskID) {
        this.taskID = taskID;
    }

    public SimpleTask(String id, String taskName, String taskDesc, String taskLink,
                      String taskCategory, boolean taskCompleted, String taskScheduleDate,
                      String taskScheduleTime, String taskFinishTime, int taskNotifyBefore,
                      String repeatTime, long createTime) {
        taskID = id;
        this.taskName = taskName;
        this.taskDesc = taskDesc;
        this.taskLink = taskLink;
        this.taskCategory = taskCategory;
        this.taskCompleted = taskCompleted;
        this.taskScheduleDate = taskScheduleDate;
        this.taskScheduleTime = taskScheduleTime;
        this.taskFinishTime = taskFinishTime;
        this.taskNotifyBefore = taskNotifyBefore;
        this.repeatTime = repeatTime;
        this.createTime = createTime;
    }

    public long getNotificationID() {
        return createTime;
    }

    public String getRepeatTime() {
        return repeatTime;
    }

    public void setRepeatTime(String repeatTime) {
        this.repeatTime = repeatTime;
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

    public String getTaskScheduleDate() {
        return taskScheduleDate;
    }

    public void setTaskScheduleDate(String taskScheduleDate) {
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
