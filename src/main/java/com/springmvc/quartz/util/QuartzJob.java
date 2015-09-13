package com.springmvc.quartz.util;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Custom annotation to schedule/reschedule/unschedule/delete Quartz job.
 * Currently only cron job supported since all of quartz jobs could be expressed as cron jo
 * There is no need to support simple job.
 *
 * Created by xiaoxiao7 on 2015/9/13.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Scope("prototype")
public @interface QuartzJob {
    /**
     * Job name, note that the trigger name will be defined as job_name_trigger
     * @return
     */
    String name();

    /**
     * Group name
     * @return
     */
    String group() default "DEFAULT_GROUP";

    /**
     * cron expression, for the trigger
     * @return
     */
    String cronExp();

    /**
     *  This will be used as job and trigger description
     * @return
     */
    String description() default "";

    /**
     * Cancel the trigger, but keep the job in database
     * @return
     */
    boolean unschedule() default false;

    /**
     * Delete the job, along with triggers attached on it.
     * Note that within cluster, sometimes the deletion could be fail since
     * other scheduler instance could recover the job after delete by current scheduler instance.
     * @return
     */
    boolean deleteJob() default  false;
}
