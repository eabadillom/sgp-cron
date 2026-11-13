package com.ferbo.sgp.cron.jobs;

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
import com.ferbo.sgp.tools.time.DateTool;

public class JobDiaDescanso implements Job {

    public static final Logger log = LogManager.getLogger(JobDiaDescanso.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<Empleado> empleados;

        try {

        log.info("Se establece el día de mañana");
        Date maniana = DateTool.getMananaDateMexico();

        log.info("Se busca si el día de mañana es no laboral");
        boolean esLaboral = DiaNoLaboralBL.esDiaFestivoAmpliado(maniana, "MX");

        if (esLaboral) {
            log.info("El día {} no es laborable. Omitiendo proceso de registro de día descanso.", maniana);
            return;
        }   

        log.info("Inicia proceso para obtener los empleados vigenes hasta el día de mañana");
        empleados = EmpleadoBL.obtenerEmpleadosActivos(maniana);
        log.info("Finaliza proceso para obtener los empleados vigenes hasta el día de mañana");
        log.info("Inicia proceso para asignar el día de descanso para cada uno de los empleado en el sistema");
        for (Empleado empleado : empleados) {
            EmpleadoBL.generarDiaDescanso(empleado);
        }
        log.info("Finaliza proceso para asignar el día de descanso para cada uno de los empleado en el sistema");

        } catch (SGPException ex) {
            throw new JobExecutionException(ex.getMessage());
        }
    }

}
