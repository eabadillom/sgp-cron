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

public class JobAusencias implements Job {

	private static final Logger log = LogManager.getLogger(JobAusencias.class);

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
		
		List<Empleado> empleados = null;

		try {
			log.info("Inicia el proceso para generar los registros de ausencias");

			Date hoy = DateTool.now();
			Date ayerInicio = DateTool.addDay(hoy, -1);
			DateTool.setTime(ayerInicio, 0, 0, 0, 0);

			if (DiaNoLaboralBL.esDiaFestivo(ayerInicio, "MX")) {
				log.info("Dia NO laboral: {}", DateTool.getString(ayerInicio, DateTool.FORMATO_DD_MM_YYYY));
				return;
			}
			
			empleados = EmpleadoBL.obtenerEmpleadosActivos(hoy);

			for (Empleado empleado : empleados) {
				log.info("Evaluando ausencia para {} {} {}", empleado.getNombre(), empleado.getPrimerApellido(), empleado.getSegundoApellido());
				EmpleadoBL.generarAusencia(empleado);
			}

			log.info("Finaliza el proceso para generar los registros de ausencias");
		} catch (SGPException sgpEx) {
			log.warn("Problema al momento de generar las ausencias de los empleados:  " + sgpEx.getMessage());
			throw new JobExecutionException("Hubo algun problema al momento de generar las ausencias");
		} catch (Exception ex) {
			log.error("Sucedio un error inesperado...", ex);
			throw new JobExecutionException("Sucedio un error inesperado.");
		}
	}

}
