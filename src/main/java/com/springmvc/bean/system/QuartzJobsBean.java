package com.springmvc.bean.system;

import java.util.Date;

/**
 * Created by xiaoxiao7 on 2015/9/13.
 */
public class QuartzJobsBean {
    private String name;
    private String triggerName;
    private String jobGroupName;
    private String triggerGroupName;
    private boolean volatileJob;
    private String description;
    private Date nextFireTime;
    private Date prevFireTime;
    private String triggerState;
    private String triggerType;
    private String cronExpression;
    private Date startTime;
    private Date endTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getJobGroupName() {
        return jobGroupName;
    }

    public void setJobGroupName(String jobGroupName) {
        this.jobGroupName = jobGroupName;
    }

    public String getTriggerGroupName() {
        return triggerGroupName;
    }

    public void setTriggerGroupName(String triggerGroupName) {
        this.triggerGroupName = triggerGroupName;
    }

    public boolean isVolatileJob() {
        return volatileJob;
    }

    public void setVolatileJob(boolean volatileJob) {
        this.volatileJob = volatileJob;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public Date getPrevFireTime() {
        return prevFireTime;
    }

    public void setPrevFireTime(Date prevFireTime) {
        this.prevFireTime = prevFireTime;
    }

    public String getTriggerState() {
        return triggerState;
    }

    public void setTriggerState(String triggerState) {
        this.triggerState = triggerState;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
