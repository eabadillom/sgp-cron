
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

/**
 *
 * @author julio
 */
@WebListener
public class SchedulerNotificacionMovil implements ServletContextListener {

    private static final Logger log = LogManager.getLogger(SchedulerNotificacionMovil.class);
    private static Scheduler scheduler;

    public static Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            log.info("Inicia la tarea programada para notificar via movil de las ausencias presentadas el dia de ayer");
            SchedulerFactory schedulerfactory = new StdSchedulerFactory();
            scheduler = schedulerfactory.getScheduler();
            scheduler.start();

            JobDetail jobdetail = new JobDetail("jobNotificacionMovil", "sgpGroup", JobNotificacionMovil.class);
            Trigger trigger = new CronTrigger("triggerNotificacionMovil", "sgpGroup", "0 0 7 * * ?");

            scheduler.scheduleJob(jobdetail, trigger);
            log.info("Finaliza la tarea programada para notificar via movil de las ausencias presentadas el dia de ayer");
        } catch (JobExecutionException jEE) {
            log.error("No se pudo ejecutar la tarea de notificar via movil las ausencias de los empleados...", jEE.getMessage());
        } catch (SchedulerException sE) {
            log.error("No se pudo iniciar o terminar la tarea de notificar via movil las ausencias de los empleados...", sE.getMessage());
        } catch (ParseException pE) {
            log.error("Hubo algun problema en la ejucion de la tarea programada...", pE.getMessage());
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
        } catch (SchedulerException sE) {
            log.error("Problema al destruir el listener del job...", sE);
        }
    }
}
