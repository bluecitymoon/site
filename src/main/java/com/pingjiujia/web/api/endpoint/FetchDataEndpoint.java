package com.pingjiujia.web.api.endpoint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.broadleafcommerce.common.config.domain.SystemProperty;
import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.broadleafcommerce.core.rating.service.RatingService;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.broadleafcommerce.core.web.api.wrapper.ProductWrapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pingjiujia.admin.domain.PJJRatingSummary;
import com.pingjiujia.admin.domain.WineImpl;
import com.pingjiujia.service.OCRService;

//import com.sun.jersey.multipart.FormDataParam;

@Component("catchData")
@Scope("singleton")
@Path("/fetch")
@Consumes(value = { MediaType.MULTIPART_FORM_DATA })
public class FetchDataEndpoint implements ApplicationContextAware  {

	private static Logger logger = Logger.getLogger(FetchDataEndpoint.class);

	private ApplicationContext context;
	

	@Resource(name = "blCatalogService")
	CatalogService catalogService;	

	@GET
	@Path("/website/{website}")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String readProductsByOCRImage( @Context HttpServletRequest request, @PathParam("fileName") String fileName)
	{
		return "";
	}



	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}
}
