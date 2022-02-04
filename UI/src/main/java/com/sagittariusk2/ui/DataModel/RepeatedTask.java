package com.sagittariusk2.ui.DataModel;

import java.time.LocalDate;

public class RepeatedTask extends SimpleTask {

    private LocalDate taskLastScheduleDate;
    private String repeatTime;
    private String weekDays;

    public RepeatedTask(String taskID, LocalDate taskLastScheduleDate, String repeatTime, String weekDays) {
        super(taskID);
        this.taskLastScheduleDate = taskLastScheduleDate;
        this.repeatTime = repeatTime;
        this.weekDays = weekDays;
    }

    public RepeatedTask(String taskID, String taskName, String taskDesc, String taskLink, boolean taskCompleted, LocalDate taskScheduleDate, String taskScheduleTime, int taskNotifyBefore, String taskFinishTime, LocalDate taskLastScheduleDate, String repeatTime, String weekDays) {
        super(taskID, taskName, taskDesc, taskLink, taskCompleted, taskScheduleDate, taskScheduleTime, taskNotifyBefore, taskFinishTime);
        this.taskLastScheduleDate = taskLastScheduleDate;
        this.repeatTime = repeatTime;
        this.weekDays = weekDays;
    }

    public LocalDate taskNextScheduleDate() {
        if(repeatTime.equalsIgnoreCase("daily")) {
            return taskScheduleDate.plusDays(1);
        } else if(repeatTime.equalsIgnoreCase("weekly")) {
            String[] x = weekDays.split(" ");
            for(int i=1; i<=7; i++) {
                for(String j:x) {
                    if(taskLastScheduleDate.plusDays(i).getDayOfWeek().toString().equalsIgnoreCase(j)) {
                        return taskLastScheduleDate.plusDays(i);
                    }
                }
            }
            return taskLastScheduleDate;
        } else if(repeatTime.equalsIgnoreCase("monthly")) {
            return taskScheduleDate.plusMonths(1);
        } else {
            return taskScheduleDate.plusYears(1);
        }
    }

    public String getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(String weekDays) {
        this.weekDays = weekDays;
    }

    public LocalDate getTaskLastScheduleDate() {
        return taskLastScheduleDate;
    }

    public void setTaskLastScheduleDate(LocalDate taskLastScheduleDate) {
        this.taskLastScheduleDate = taskLastScheduleDate;
    }

    public String getRepeatTime() {
        return repeatTime;
    }

    public void setRepeatTime(String repeatTime) {
        this.repeatTime = repeatTime;
    }
}
