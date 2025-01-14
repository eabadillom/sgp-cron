package com.ferbo.sgp;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

@WebListener
public class SchedulerVacaciones implements ServletContextListener {

    private Scheduler scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            SchedulerFactory schedulerfactory = new StdSchedulerFactory();
            scheduler = schedulerfactory.getScheduler();
            scheduler.start();
            
            JobDetail jobdetail = new JobDetail("jobVacaciones", "group1", JobVacaciones.class);
            Trigger trigger = new CronTrigger("triggerVacaciones", "group1", "0 20 16 * * ?");
            
            scheduler.scheduleJob(jobdetail, trigger);
            
        } catch (JobExecutionException ex) {
            
        } catch (SchedulerException ex) {
            Logger.getLogger(SchedulerVacaciones.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(SchedulerVacaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            if (scheduler != null) {
                scheduler.shutdown();
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
