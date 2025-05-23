package com.ferbo.sgp.cron.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ferbo.sgp.core.dao.SistemaDAO;
import com.ferbo.sgp.core.model.Sistema;
import com.ferbo.sgp.core.util.SGPException;
import com.ferbo.sgp.cron.business.StartJobBL;
import com.ferbo.sgp.cron.model.request.StartJobRequest;
import com.ferbo.sgp.cron.model.response.StartJobResponse;
import com.ferbo.sgp.tools.SecurityTool;
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
		StringBuilder    jsonBuffer   = null;
        String           linea        = null;
        BufferedReader   reader       = null;
        Gson             gson         = null;
        String           jsonRecibido = null;
        
        StartJobRequest  jobRequest   = null;
        StartJobResponse jobResponse  = null;
        Integer          httpStatus   = null;
        
        try {
        	jobResponse = new StartJobResponse();
        	this.validarSesion(request, response);
        	
        	jsonBuffer = new StringBuilder();
        	gson = new Gson();
        	
        	reader = request.getReader();
			while ((linea = reader.readLine()) != null) {
                jsonBuffer.append(linea);
            }
			
			jsonRecibido = jsonBuffer.toString();
			log.info("JSON recibido: {}", jsonRecibido);
			
			jobRequest = gson.fromJson(jsonRecibido, StartJobRequest.class);
			
			jobResponse.setJobName(jobRequest.getJobName());
			jobResponse.setJobGroup(jobRequest.getJobGroup());
			jobResponse.setCron(jobRequest.getCron());
        	
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
        	httpStatus = HttpServletResponse.SC_FORBIDDEN;
        	jobResponse.setStatus("error");
        	jobResponse.setMessage(ex.getMessage());
        } catch(Exception ex) {
        	log.error("Problema con el job solicitado...", ex);
        	httpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        	jobResponse.setStatus("error");
        	jobResponse.setMessage("Ocurrió un problema no identificado. Por favor contacte a su administrador de sistemas.");
        }finally {
        	response.setStatus(httpStatus);
        	response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(gson.toJson(jobResponse));
        }
	}
	
	protected void validarSesion(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException, SGPException {
    	String            authHeader        = null;
    	String            base64Credentials = null;
    	byte[]            credDecoded       = null;
    	String            credentials       = null;
    	String[]          values            = null;
    	String            username          = null;
    	String            password          = null;
    	SistemaDAO        sistemaDAO        = null;
    	Optional<Sistema> sistema           = null;
    	
    	authHeader = request.getHeader("Authorization");
    	
    	if(authHeader == null) {
    		log.warn("No se proporcionó encabezado de autenticación.");
    		throw new SGPException("Acceso restringido.");
    	}
    	
    	if(authHeader.startsWith("Basic ") == false) {
    		log.warn("El encabezado de autenticación es incorrecto.");
    		throw new SGPException("Acceso restringido.");
    	}
    	
    	base64Credentials = authHeader.substring("Basic ".length());
    	credDecoded = Base64.getDecoder().decode(base64Credentials);
    	credentials = new String(credDecoded);
    	
    	values = credentials.split(":", 2);
    	
    	username = values[0];
        password = values[1];
        
        if(username == null)
        	throw new SGPException("Información incorrecta.");
        
        if(password == null)
        	throw new SGPException("Información incorrecta.");
        
        sistemaDAO = new SistemaDAO();
        sistema = sistemaDAO.buscarPorNombre(username);
        if(sistema.isPresent() == false) {
        	throw new SGPException("Información incorrecta.");
        }
        
        if(SecurityTool.verifyPassword(password, sistema.get().getPassword()) == false)
        		throw new SGPException("Información incorrecta.");	
        
    	log.info("OK - username: {}, password: {}, nombreSistema: {}, passwordSistema: {}",
    			username, password, sistema.get().getNombre(), sistema.get().getPassword());
	}

}
