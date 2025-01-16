package com.ferbo.sgp;

import com.ferbo.sgp.business.EmpleadoBL;
import com.ferbo.sgp.dao.EmpleadoDAO;
import com.ferbo.sgp.model.Empleado;
import com.ferbo.sgp.util.SGPException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobVacaciones implements Job {

    private static final Logger log = LogManager.getLogger(JobVacaciones.class);
    
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {

        try {
            log.info("Inicia el proceso para generar el nuevo periodo vacacional de los empleados");
            List<Empleado> empleados = null;
            EmpleadoDAO empleadodao = new EmpleadoDAO();

            empleados = EmpleadoBL.obtenerEmpleados();

            for (Empleado empleado : empleados) {
                if (empleado.getDatoEmpresarial() != null) {
                    if (empleado.getDatoEmpresarial().getFechaBaja() == null) {
                        EmpleadoBL.generarAnioVacaciones(empleado);
                        empleadodao.actualizar(empleado);
                    }
                }
            }

            log.info("Finaliza el proceso para generar el nuevo periodo vacacional de los empleados");
        } catch (SGPException sgpex) {
            log.warn("Problema al momento de obtener la informacion de los empleados:  " + sgpex.getMessage());
            throw new JobExecutionException("Hubo algun problema al momento de obtener la informacion");
        } catch (Exception ex) {
            log.error("Sucedio un error inesperado: " + ex.getMessage());
            throw new JobExecutionException("Sucedio un error inesperado.");
        }

    }

}
