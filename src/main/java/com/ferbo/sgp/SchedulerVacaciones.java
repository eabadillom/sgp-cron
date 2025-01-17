package com.ferbo.sgp;

import java.text.ParseException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger log = LogManager.getLogger(SchedulerVacaciones.class);
    private Scheduler scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            log.info("Inicia la tarea programada para actualizar los periodos vacionales de los empleados.");
            SchedulerFactory schedulerfactory = new StdSchedulerFactory();
            scheduler = schedulerfactory.getScheduler();
            scheduler.start();
            
            JobDetail jobdetail = new JobDetail("jobVacaciones", "group1", JobVacaciones.class);
            Trigger trigger = new CronTrigger("triggerVacaciones", "group1", "30 0 0 * * ?");
            
            scheduler.scheduleJob(jobdetail, trigger);
            log.info("Termino la tarea programada para actualizar los periodos vacionales de los empleados. El sistema espera por la siguiente fecha.");
        } catch (JobExecutionException ex) {
            log.warn("No se pudo ejecutar la tarea de actualizar el periodo vacacional del empleado. " + ex.getMessage());
        } catch (SchedulerException ex) {
            log.warn("No se pudo iniciar o terminar la tarea de acutalizar el periodo vacacional del empleado. " + ex.getMessage());
        } catch (ParseException ex) {
            log.warn("Hubo algun problema en la ejucion de la tarea programada. " + ex.getMessage());
        } catch (Exception ex){
            log.error("Error: Problema desconocido. " + ex.getMessage(), ex);
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
