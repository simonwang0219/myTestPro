package com.springmvc.quartz.util;

import com.google.common.collect.Maps;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.JobDetailBean;

import java.text.ParseException;
import java.util.*;


/**
 * Created by xiaoxiao7 on 2015/9/13.
 */
public class QuartJobSchedulingListener implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(QuartJobSchedulingListener.class);

    private static final String JOBS_TO_DELETE = "jobs_to_delete";
    private static final String JOBS_TO_UNSCHEDULE = "jobs_to_unschedule";
    private static final String JOBS_TO_SCHEDULE = "jobs_to_schedule";

    @Autowired
    private Scheduler scheduler;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            ApplicationContext applicationContext = event.getApplicationContext();
            Map<String, List<CronTriggerBean>> cronTriggerBeans = this.loadCronTriggerBeans(applicationContext);
            this.scheduleJobs(cronTriggerBeans);
        }catch (Exception e){
            logger.error("Job schedule error: ", e);
        }
    }

    /**
     * load all of triggers/jobs defined by Java annotation.
     *
     * @param applicationContext Spring application context.
     * @return all of jobs/triggers been defined.
     */
    private Map<String, List<CronTriggerBean>> loadCronTriggerBeans(ApplicationContext applicationContext){
        Map<String, Object> quartzJobBeans = applicationContext.getBeansWithAnnotation(QuartzJob.class);
        Set<String> beanNames = quartzJobBeans.keySet();
        Map<String, List<CronTriggerBean>> cronTriggerBeans = Maps.newHashMap();
        //Initialize the empty map
        cronTriggerBeans.put(JOBS_TO_SCHEDULE, new ArrayList<CronTriggerBean>());
        cronTriggerBeans.put(JOBS_TO_DELETE, new ArrayList<CronTriggerBean>());
        cronTriggerBeans.put(JOBS_TO_UNSCHEDULE, new ArrayList<CronTriggerBean>());

        for (String beanName : beanNames){
            CronTriggerBean cronTriggerBean = null;
            Object object = quartzJobBeans.get(beanName);

            try {
                cronTriggerBean = this.buildCronTriggerBean(object);
            }catch (Exception e){
                logger.info("Error occured while building CronTriggerBean: " + e);
            }

            if (cronTriggerBeans != null){
                QuartzJob quartzJobAnnotation = AnnotationUtils.findAnnotation(object.getClass(), QuartzJob.class);
                //delete a job will unschedule it first, so check it in the first place.
                if (quartzJobAnnotation.deleteJob()){
                    cronTriggerBeans.get(JOBS_TO_DELETE).add(cronTriggerBean);
                }else if (quartzJobAnnotation.unschedule()){
                    cronTriggerBeans.get(JOBS_TO_UNSCHEDULE).add(cronTriggerBean);
                }else {
                    cronTriggerBeans.get(JOBS_TO_SCHEDULE).add(cronTriggerBean);
                }
            }
        }
        return cronTriggerBeans;
    }

    /**
     * Build trigger bean according to annotation definition
     * @param job   Quartz job represented by Spring bean
     * @return  CronTriggerBean instance
     * @throws ParseException   Indicate that cron expression is wrong.
     */
    private CronTriggerBean buildCronTriggerBean(Object job) throws ParseException {
        CronTriggerBean cronTriggerBean;
        QuartzJob quartzJobAnnotation = AnnotationUtils.findAnnotation(job.getClass(), QuartzJob.class);
        if (Job.class.isAssignableFrom(job.getClass())){
            cronTriggerBean = new CronTriggerBean();
            cronTriggerBean.setCronExpression(quartzJobAnnotation.cronExp());
            cronTriggerBean.setName(quartzJobAnnotation.name());
            cronTriggerBean.setGroup(quartzJobAnnotation.group());
            cronTriggerBean.setDescription(quartzJobAnnotation.description());
            /**
             * 产生misfire的前提是
             * 到了该触发执行时上一个执行还未完成，且线程池中没有空闲线程可以使用（或有空闲线程可以使用但job设置为@DisallowConcurrentExecution）
             * 且过期时间已经超过misfireThreshold
             *
             * MISFIRE_INSTRUCTION_SMART_POLICY = 0
             * MISFIRE_INSTRUCTION_FIRE_ONCE_NOW = 1   触发器超时后会被立即安排执行
             *  MISFIRE_INSTRUCTION_DO_NOTHING = 2  不触发立即执行,等待下次Cron触发频率到达时刻开始按照Cron频率依次执行
             */

            logger.info("设置" + quartzJobAnnotation.name() + "_trigger" + "的MisfireInstruction为MISFIRE_INSTRUCTION_DO_NOTHING");
            cronTriggerBean.setMisfireInstruction(2);
            JobDetailBean jobDetailBean = new JobDetailBean();
            jobDetailBean.setName(quartzJobAnnotation.name());
            jobDetailBean.setGroup(quartzJobAnnotation.group());    //DEFAULT_GROUP
            jobDetailBean.setDescription(quartzJobAnnotation.description());
            jobDetailBean.setJobClass(job.getClass());
            cronTriggerBean.setJobDetail(jobDetailBean);
        }else{
            throw new RuntimeException(job.getClass() + "doesn't implemented" + Job.class);
        }
        return cronTriggerBean;
    }

    /**
     * Try to schedule/reschedule/unschedule/delete jobs
     *
     * @param cronTriggerBeans  instance of triggers
     */
    public void scheduleJobs(Map<String, List<CronTriggerBean>> cronTriggerBeans){
        List<CronTriggerBean> jobsToSchedule = cronTriggerBeans.get(JOBS_TO_SCHEDULE);
        for (CronTriggerBean trigger : jobsToSchedule){
            boolean needReschedule = false;
            JobDetail jobDetail = trigger.getJobDetail();
            try {
                scheduler.scheduleJob(jobDetail, trigger);
            }catch (SchedulerException e){
                logger.warn("Job instance " + jobDetail.getFullName() + " schedule error, try to reschedule it.");
                needReschedule = true;
            }

            if (needReschedule){
                try {
                    Date rescheduleDate = scheduler.rescheduleJob(trigger.getName(), trigger.getGroup(), trigger);
                    if (rescheduleDate != null){
                        logger.info("trigger.getName():"+trigger.getName() + ",trigger.getGroup():"+trigger.getGroup()+",rescheduleDate:"+rescheduleDate);
                    }else {
                        logger.info("trigger.getName():"+trigger.getName() + ",trigger.getGroup():"+trigger.getGroup()+",rescheduleDate:null");
                    }
                }catch (SchedulerException e){
                    logger.error("Job reschedule error, with exception: ", e);
                    logger.error("ignore the job - " + jobDetail.getFullName() + " for now.");
                }
            }
        }

        List<CronTriggerBean> jobsToUnschedule = cronTriggerBeans.get(JOBS_TO_UNSCHEDULE);
        for (CronTriggerBean trigger : jobsToUnschedule) {
            try {
                boolean flag = scheduler.unscheduleJob(trigger.getName(), trigger.getGroup());
                logger.info("trigger.getName():"+trigger.getName()
                        +",trigger.getGroup():"+trigger.getGroup()+",flag:"+flag);
            } catch (SchedulerException e) {
                logger.error("Failed to unschedule the trigger: " + trigger.getName() + ", which" +
                        "belongs to the job: " + trigger.getJobName());
                logger.error("With below exception: ", e);
            }
        }

        List<CronTriggerBean> jobsToDelete = cronTriggerBeans.get(JOBS_TO_DELETE);
        for (CronTriggerBean trigger : jobsToDelete) {
            try {
                boolean flag = scheduler.deleteJob(trigger.getJobDetail().getName(), trigger.getGroup());
                logger.info("trigger.getJobName():"+trigger.getJobDetail().getName()
                        +",trigger.getJobGroup():"+trigger.getGroup()+",flag:"+flag);
            } catch (SchedulerException e) {
                logger.error("Failed to delete the job: " + trigger.getJobName() + ", with below error: ", e);
            }
        }
    }

}
