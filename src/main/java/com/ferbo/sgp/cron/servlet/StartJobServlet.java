package com.ferbo.sgp.cron.servlet;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ferbo.sgp.cron.business.StartJobBL;
import com.ferbo.sgp.cron.model.request.StartJobRequest;
import com.ferbo.sgp.cron.model.response.StartJobResponse;
import com.ferbo.sgp.util.SGPException;
import com.google.gson.Gson;

/**
 * Servlet implementation class StartJobServlet
 */
public class StartJobServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger(StartJobServlet.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StartJobServlet() {
        super();
    }
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuilder jsonBuffer = new StringBuilder();
        String linea;
        BufferedReader reader = null;
        String jsonRecibido = null;
        
        StartJobRequest jobRequest = null;
        StartJobResponse jobResponse = null;
        Integer httpStatus = null;
        
        Gson gson = new Gson();
        
        try {
        	
        	reader = request.getReader();
			while ((linea = reader.readLine()) != null) {
                jsonBuffer.append(linea);
            }
			
			jsonRecibido = jsonBuffer.toString();
			log.info("JSON recibido: {}", jsonRecibido);
			
			jobRequest = gson.fromJson(jsonRecibido, StartJobRequest.class);
			jobResponse = new StartJobResponse(jobRequest.getJobName(), jobRequest.getJobGroup(), jobRequest.getCron());
        	
            if(jobRequest.getCron() == null) {
            	StartJobBL.getInstance().startJob(jobRequest.getJobName());
            	log.info("Job ejecutado manualmente desde el servlet.");
            	jobResponse.setMessage("Job ejecutado manualmente.");
            }
            else {
            	StartJobBL.getInstance().startJob(jobRequest.getJobName(), jobRequest.getJobGroup(), jobRequest.getCron());
            	log.info("Job reprogramado.");
            	jobResponse.setMessage("Job reprogramado.");
            }
            
            jobResponse.setStatus("ok");
            
            httpStatus = HttpServletResponse.SC_ACCEPTED;
        } catch(SGPException ex) {
        	response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        	jobResponse.setStatus("error");
        	jobResponse.setMessage(ex.getMessage());
        } catch(Exception ex) {
        	log.error("Problema con el job solicitado...", ex);
        	httpStatus = HttpServletResponse.SC_FORBIDDEN;
        	jobResponse.setStatus("error");
        	jobResponse.setMessage("Ocurrió un problema no identificado. Por favor contacte a su administrador de sistemas.");
        }finally {
        	response.setStatus(httpStatus);
        	response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(gson.toJson(jobResponse));
        }
	}

}
