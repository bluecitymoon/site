package com.pingjiujia.web.api.endpoint;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pingjiujia.admin.domain.ClientVersion;
import com.pingjiujia.service.VersionControllService;
import com.pingjiujia.web.api.wrapper.ClientVersionWrapper;


@Component("versionControll")
@Scope("singleton")
@Path("/version")
@Produces(value = { MediaType.APPLICATION_JSON })
@Consumes(value = { MediaType.APPLICATION_JSON })
public class VersionControllEndpoint implements ApplicationContextAware{
	
	private Logger logger = Logger.getLogger(VersionControllEndpoint.class);
	
    private ApplicationContext context;

	private VersionControllService versionControllService;
	
	/**
     * Search for {@code Product} by product id
     * FOr detail page.
     *
     * @param id the product id
     * @return the product instance with the given product id
     */
    @GET
    @Path("/getLatestVersion")
    public ClientVersionWrapper findLastestVersion(@Context HttpServletRequest request) {
        ClientVersion clientVersion = versionControllService.findLastestVersion();
        
        if (clientVersion != null) {
        	
        	ClientVersionWrapper wrapper = (ClientVersionWrapper) context.getBean(ClientVersionWrapper.class.getName());
        	
        	wrapper.wrap(clientVersion, request);
        	
            return wrapper;
        }
        
        return null;
       }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
    }


}
