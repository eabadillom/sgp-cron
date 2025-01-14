package com.ferbo.sgp;

import com.ferbo.sgp.business.EmpleadoBL;
import com.ferbo.sgp.dao.EmpleadoDAO;
import com.ferbo.sgp.model.Empleado;
import com.ferbo.sgp.util.SGPException;
import java.util.List;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobVacaciones implements Job {

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {

        try {
            System.out.println("Inicia el proceso para generar el nuevo periodo vacacional de los empleados");
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

            System.out.println("Finaliza el proceso para generar el nuevo periodo vacacional de los empleados");
        } catch (SGPException sgpex) {
            System.out.println("Problema al momento de obtener la informacion de los empleados:  " + sgpex.getMessage());
            throw new JobExecutionException("Hubo algun problema al momento de obtener la informacion");
        } catch (Exception ex) {
            System.out.println("Sucedio un error inesperado: " + ex.getMessage());
            throw new JobExecutionException("Sucedio un error inesperado.");
        }

    }

}
