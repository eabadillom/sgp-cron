package com.ferbo.sgp;

import com.ferbo.sgp.business.DiaNoLaboralBL;
import com.ferbo.sgp.business.EmpleadoBL;
import com.ferbo.sgp.model.Empleado;
import com.ferbo.sgp.util.DateUtil;
import com.ferbo.sgp.util.SGPException;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobAusencias implements Job {

    private static final Logger log = LogManager.getLogger(JobAusencias.class);

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {

        try {
            log.info("Inicia el proceso para generar los registros de ausencias");

            Date hoy = DateUtil.now();
            Date ayerInicio = DateUtil.addDay(hoy, -1);
            DateUtil.setTime(ayerInicio, 0, 0, 0, 0);

            if (DiaNoLaboralBL.esFestivoEsteDia(ayerInicio, "MX")) {
                log.info("El dia de ayer no fue laborable");
                return;
            }

            List<Empleado> empleados = EmpleadoBL.obtenerEmpleados();

            for(Empleado empleadoaux : empleados){
                if(empleadoaux.getDatoEmpresarial() != null){
                    if(empleadoaux.getDatoEmpresarial().getFechaBaja() == null){
                        EmpleadoBL.generarInasistencia(empleadoaux);
                    }
                }
            }

            log.info("Finaiza el proceso para generar los registros de ausencias");
        } catch (SGPException sgpEx) {
            log.warn("Problema al momento de generar las ausencias de los empleados:  " + sgpEx.getMessage());
            throw new JobExecutionException("Hubo algun problema al momento de generar las ausencias");
        } catch (Exception ex) {
            log.error("Sucedio un error inesperado: " + ex.getMessage());
            throw new JobExecutionException("Sucedio un error inesperado.");
        }
    }

}
