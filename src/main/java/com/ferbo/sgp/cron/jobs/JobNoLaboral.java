package com.ferbo.sgp.cron.jobs;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ferbo.sgp.core.business.DiaNoLaboralBL;
import com.ferbo.sgp.core.business.EmpleadoBL;
import com.ferbo.sgp.core.model.Empleado;
import com.ferbo.sgp.tools.exceptions.SGPException;

public class JobNoLaboral implements Job {

    private static final Logger log = LogManager.getLogger(JobNoLaboral.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        ZoneId zone = ZoneId.of("America/Mexico_City");

        log.info("Se establece el día de mañana");
        Date maniana = Date.from(
            LocalDate.now(zone)
                .plusDays(1)
                .atStartOfDay(zone)
                .toInstant()
        );

        log.info("Se busca si el día de mañana es no laboral");
        boolean esLaboral = DiaNoLaboralBL.esDiaFestivo(maniana, "MX");

        if (!esLaboral) {
            log.info("El día {} es laborable. Omitiendo proceso de registro de día no laboral.", maniana);
            return;
        }
        
        
        List<Empleado> empleados;
        try {
            log.info("Inicia proceso para obtener los empleados vigenes hasta el día de mañana");
            empleados = EmpleadoBL.obtenerEmpleadosActivos(maniana);
            log.info("Finaliza proceso para obtener los empleados vigenes hasta el día de mañana");
             log.info("Inicia proceso para asignar el día no laboral a cada uno de los empleado en el sistema");
        for (Empleado empleado : empleados) {
            EmpleadoBL.generarDiaNoLaboral(empleado);
            log.info("Día no laboral, generado exitosamente para el empleado: {} {} {}", empleado.getNombre(), empleado.getPrimerApellido(), empleado.getSegundoApellido());
        }
        log.info("Finaliza proceso para asignar el día no laboral a cada uno de los empleado en el sistema");
        } catch (SGPException ex) {
            throw new JobExecutionException(ex.getMessage());
        }

    }

    

}
