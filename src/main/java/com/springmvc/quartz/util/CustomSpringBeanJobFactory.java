package com.springmvc.quartz.util;

import org.quartz.SchedulerContext;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * The purpose of this class is mainly two fold:
 * <ol>
 *      <li>Enable autowire spring beans into Quartz job classes</li>
 *      <li>Enable serialization of JobDetail instances. By default, JobDetail instance serialization
 *      is not supported by Spring framework</li>
 * Created by xiaoxiao7 on 2015/9/13.
 */
public class CustomSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {
    private transient AutowireCapableBeanFactory beanFactory;
    private String[] ignoredUnknownProperties;
    private SchedulerContext schedulerContext;

    @Override
    public void setApplicationContext(final ApplicationContext context) {
        this.beanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    public void setIgnoredUnknownProperties(String[] ignoredUnknownProperties) {
        super.setIgnoredUnknownProperties(ignoredUnknownProperties);
        this.ignoredUnknownProperties = ignoredUnknownProperties;
    }

    @Override
    public void setSchedulerContext(SchedulerContext schedulerContext) {
        super.setSchedulerContext(schedulerContext);
        this.schedulerContext = schedulerContext;
    }

    /**
     * An implementation of SpringBeanJobFactory that retrieves the bean from
     * the Spring context so that autowiring and transactions work
     * <p/>
     * This method is overriden.
     * @see org.springframework.scheduling.quartz.SpringBeanJobFactory#createJobInstance(org.quartz.spi.TriggerFiredBundle)
     *
     * @param bundle
     * @return
     * @throws Exception
     */
    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        XmlWebApplicationContext ctx = (XmlWebApplicationContext)schedulerContext.get("applicationContext");
        Object job = ctx.getBean(bundle.getJobDetail().getName());
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(job);
        if (isEligibleForPropertyPopulation(bw.getWrappedInstance())){
            MutablePropertyValues pvs = new MutablePropertyValues();
            if (this.schedulerContext != null){
                pvs.addPropertyValues(this.schedulerContext);
            }
            pvs.addPropertyValues(bundle.getJobDetail().getJobDataMap());
            pvs.addPropertyValues(bundle.getTrigger().getJobDataMap());
            if (this.ignoredUnknownProperties != null){
                for (String propName : this.ignoredUnknownProperties){
                    if (pvs.contains(propName) && !bw.isWritableProperty(propName)){
                        pvs.removePropertyValue(propName);
                    }
                }
                bw.setPropertyValues(pvs);
            }else {
                bw.setPropertyValues(pvs, true);
            }
        }
        //autowire Support
        beanFactory.autowireBean(job);
        return job;
    }
}
