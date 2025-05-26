package com.ferbo.sgp.cron.business;

import java.text.ParseException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import com.ferbo.sgp.cron.jobs.SchedulerAusencias;
import com.ferbo.sgp.cron.jobs.SchedulerVacaciones;
import com.ferbo.sgp.tools.exceptions.SGPException;
import com.ferbo.sgp.tools.time.DateTool;

public class StartJobBL {
	private static Logger log = LogManager.getLogger(StartJobBL.class);
	
	private static StartJobBL instance = null;
	
	private StartJobBL() {
		
	}
	
	public static synchronized StartJobBL getInstance() {
		if(instance == null)
			instance = new StartJobBL();
		
		return instance;
	}
	
	private Scheduler getScheduler(String jobName) {
		if("jobAusencias".equals(jobName))
			return SchedulerAusencias.getScheduler();
		
		if("jobVacaciones".equals(jobName))
			return SchedulerVacaciones.getScheduler();
		
		throw new UnsupportedOperationException();
	}
	
	public synchronized void startJob(String jobName)
	throws SGPException {
		this.startJob(jobName, "sgpGroup");
	}
	
	public synchronized void startJob(String jobName, String jobGroup)
	throws SGPException {
		Scheduler scheduler = null;
		
		try {
			scheduler = getScheduler(jobName);
			if (scheduler == null)
				throw new SGPException("El scheduler no está disponible");
			
			scheduler.triggerJob(jobName, jobGroup);
			
		} catch(SchedulerException ex) {
			log.error("Problema para invocar al Job...", ex);
			throw new SGPException("Problema para invocar al job.");
		}
	}
	
	public synchronized void startJob(String jobName, String jobGroup, String cronExpression)
	throws SGPException {
		Scheduler scheduler = null;
		JobDetail jobDetail = null;
		String[] triggerGroupNames = null;
		String[] triggerNames = null;
		CronTrigger nuevoTrigger = null;
		Trigger[] triggersOfJob = null;
		
		Date scheduleJob = null;
		
		try {
			scheduler = getScheduler(jobName);
			if (scheduler == null)
				throw new SGPException("El scheduler no está disponible");
			
			jobDetail = scheduler.getJobDetail(jobName, jobGroup);
			
			if(jobDetail == null)
				throw new SGPException("Job no encontrado.");
			
			triggerGroupNames = scheduler.getTriggerGroupNames();
			
			for(String triggerGroup : triggerGroupNames) {
				log.debug("{}", triggerGroup);
				triggerNames = scheduler.getTriggerNames(jobGroup);
				
				for(String triggerName : triggerNames) {
					log.debug("{}", triggerName);
					triggersOfJob = scheduler.getTriggersOfJob(jobDetail.getName(), jobDetail.getGroup());
					
					for(Trigger trigger : triggersOfJob) {
						log.info("JobName: {} - JobGroup: {} - TriggerName: {} - TriggerGroup: {}",
								jobDetail.getName(), jobDetail.getGroup(), trigger.getName(), trigger.getGroup());
						
						nuevoTrigger = new CronTrigger(trigger.getName(), trigger.getGroup(), trigger.getJobName(), trigger.getJobGroup(), cronExpression);
						
						scheduleJob = scheduler.rescheduleJob(trigger.getName(), trigger.getGroup(), nuevoTrigger);
						
						log.info("Job reprogramado: {}", DateTool.getString( scheduleJob, DateTool.FORMATO_ISO_Z));
					}
				}
			}
			
		} catch(SchedulerException | ParseException ex) {
			log.error("Problema para invocar al Job...", ex);
			throw new SGPException("Problema para invocar al job.");
		}
	}
}
