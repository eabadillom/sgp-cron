package com.ferbo.sgp.cron.jobs;

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
	private static Scheduler scheduler;
	
	public SchedulerVacaciones() {
	
	}

	public static Scheduler getScheduler() {
		return scheduler;
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			log.info("Inicia la tarea programada para actualizar los periodos vacionales de los empleados.");
			SchedulerFactory schedulerfactory = new StdSchedulerFactory();
			scheduler = schedulerfactory.getScheduler();
			scheduler.start();

			JobDetail jobdetail = new JobDetail("jobVacaciones", "sgpGroup", JobVacaciones.class);
			Trigger trigger = new CronTrigger("triggerVacaciones", "sgpGroup", "0 30 0 * * ?");

			scheduler.scheduleJob(jobdetail, trigger);
			log.info("Termino la tarea programada para actualizar los periodos vacionales de los empleados. El sistema espera por la siguiente fecha.");
		} catch (JobExecutionException ex) {
			log.error("No se pudo ejecutar la tarea de actualizar el periodo vacacional del empleado...", ex.getMessage());
		} catch (SchedulerException ex) {
			log.error("No se pudo iniciar o terminar la tarea de acutalizar el periodo vacacional del empleado...", ex.getMessage());
		} catch (ParseException ex) {
			log.error("Hubo algun problema en la ejucion de la tarea programada...", ex.getMessage());
		} catch (Exception ex) {
			log.error("Error: Problema desconocido...", ex);
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
