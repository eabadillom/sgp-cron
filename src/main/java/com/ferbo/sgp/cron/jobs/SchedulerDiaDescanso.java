package com.ferbo.sgp.cron.jobs;

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
public class SchedulerDiaDescanso implements ServletContextListener {

    private static final Logger log = LogManager.getLogger(SchedulerDiaDescanso.class);
    private static Scheduler scheduler;

    public SchedulerDiaDescanso() {
    }

    public static Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {

            log.info("Inicia la tarea programada para registrar el día de descanso de los empleados.");
			SchedulerFactory schedulerfactory = new StdSchedulerFactory();
			scheduler = schedulerfactory.getScheduler();
                        scheduler.start();

			JobDetail jobdetail = new JobDetail("jobDiaDescanso", "sgpGroup", JobDiaDescanso.class);
			Trigger trigger = new CronTrigger("triggerDiaDescanso", "sgpGroup", "0 50 23 * * ?");

			scheduler.scheduleJob(jobdetail, trigger);
			log.info("Termino la tarea programada para registrar el día de descanso de los empleados.");

        } catch (JobExecutionException ex) {
			log.error("No se pudo ejecutar la tarea de registrar el día de descanso para los empleados: {}", ex.getMessage());
		} catch (SchedulerException ex) {
			log.error("No se pudo iniciar o terminar la tarea de registrar el día de descanso para los empleados: {}", ex.getMessage());
		} catch (Exception ex) {
            log.error("Error desconocido al ejecutar la tarea de registrar el día de descanso para los empleados: {}", ex.getMessage(), ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
			if (scheduler != null) {
				scheduler.shutdown();
			}
		} catch (SchedulerException e) {
			log.error("Problema al destruir el listener del job...", e);
		}
    }

}
