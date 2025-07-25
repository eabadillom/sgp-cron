
package com.ferbo.sgp.cron.jobs;

import com.ferbo.sgp.core.business.notifmovil.NotifMovilBL;
import com.ferbo.sgp.core.business.sgpapiclient.SGPApiClientBL;
import com.ferbo.sgp.core.dao.AsistenciaDAO;
import com.ferbo.sgp.core.dto.NotificacionMovilDTO;
import com.ferbo.sgp.core.model.Asistencia;
import com.ferbo.sgp.tools.exceptions.SGPException;
import com.ferbo.sgp.tools.time.DateTool;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author julio
 */
public class JobNotificacionMovil implements Job {

    private static final Logger log = LogManager.getLogger(JobNotificacionMovil.class);
    
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        try {
            log.info("Inicia el proceso de notificar las ausencias de los empleados, via movil");
            Date ayer = DateTool.now();
            Date inicio = DateTool.addDay(ayer, -1);
            Date fin = DateTool.addDay(ayer, -1);
            DateTool.resetTime(inicio);
            DateTool.resetTime(fin);
            AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
            List<Asistencia> ausencias = asistenciaDAO.buscarPorFaltasDeAyer(inicio, fin, "F");
            if(ausencias.isEmpty()){
                throw new SGPException("Finaliza el proceso de notificacion porque no se presentaron ausencias el día de ayer");
            }
            NotificacionMovilDTO nmDTO = NotifMovilBL.obtenerMensaje(ausencias.size());
            SGPApiClientBL sgpApiClient = new SGPApiClientBL();
            sgpApiClient.enviarNotificacion(nmDTO);
            log.info("Finaliza el proceso de notificar las ausencias de los empleados, via movil");
        } catch (SGPException sgpEx) {
            log.warn("Problema al momento de notificar de las ausencias via movil:  " + sgpEx.getMessage());
            throw new JobExecutionException("Hubo algun problema al momento de notificar via movil de las aucencias");
        } catch (Exception ex) {
            log.error("Sucedio un error inesperado...", ex);
			throw new JobExecutionException("Sucedio un error inesperado.");
        }
    }
    
}
