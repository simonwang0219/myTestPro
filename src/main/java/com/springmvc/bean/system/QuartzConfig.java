package com.springmvc.bean.system;

/**
 * Created by xiaoxiao7 on 2015/9/13.
 */
public class QuartzConfig {
    private Long qcId;  //主键
    private String jobClass;    //具体job类
    private String type;    //定时任务类型：定时执行，间隔循环执行
    private String cronExpression;  //类Unix的Cron表达式
    private String jobName; //任务名
    private String groupName;   //任务所属组
    private String triggerId;   //触发器的ID(触发job执行)
    private String startDelay;  //启动后，延迟多少秒执行
    private String repeatInterval;  //间隔多长时间执行
    private String status;  //状态，是否启用

    public Long getQcId() {
        return qcId;
    }

    public void setQcId(Long qcId) {
        this.qcId = qcId;
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(String triggerId) {
        this.triggerId = triggerId;
    }

    public String getStartDelay() {
        return startDelay;
    }

    public void setStartDelay(String startDelay) {
        this.startDelay = startDelay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(String repeatInterval) {
        this.repeatInterval = repeatInterval;
    }
}
