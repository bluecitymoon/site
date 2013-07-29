package com.pingjiujia.web.api.endpoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.cms.file.domain.StaticAssetStorage;
import org.broadleafcommerce.common.config.domain.SystemProperty;
import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("client")
@Scope("singleton")
@Path("/client")
@Consumes(value = { MediaType.APPLICATION_JSON })
public class ClientDownloadEndpoint {

	@Resource(name = "blSystemPropertiesService")
	SystemPropertiesService systemPropertiesService;

	@GET
	@Path("/{os}")
	@Produces(value = { MediaType.APPLICATION_OCTET_STREAM })
	public Response readImageByMediaId(@PathParam("os") String os) {

		File file = null;

		String filePath = "";
		if (StringUtils.isNotBlank(os)) {

			SystemProperty systemProperty = null;
			if (os.equalsIgnoreCase("android")) {
				systemProperty = systemPropertiesService.findSystemPropertyByName("TXJJ_CLIENT_ANDROID");

			} else if (os.equalsIgnoreCase("ios")) {
				systemProperty = systemPropertiesService.findSystemPropertyByName("TXJJ_CLIENT_IOS");

			} else {
				throw new WebApplicationException(Response.Status.BAD_REQUEST);
			}

			filePath = systemProperty == null ? null : systemProperty.getValue();
			if (StringUtils.isEmpty(filePath)) {
				throw new WebApplicationException(new Exception("The URL of android file not defined in the DB."));
			} else {
				file = new File(filePath);
				if (!file.exists() ) {
					throw new WebApplicationException(new Exception("The file not exists."));
				} else if( file.isDirectory()) {
					throw new WebApplicationException(new Exception("It must a file but not directory."));
				}
			}
			ResponseBuilder responseBuilder = Response.ok(file);

			if (os.equalsIgnoreCase("android")) {
				responseBuilder.header("Content-Disposition", "attachment; filename=citycellar.apk");
			} else if (os.equalsIgnoreCase("ios")) {
				// responseBuilder.header("Content-Disposition",
				// "attachment; filename=citycellar.");
			}

			return responseBuilder.build();
			
		} else {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
	}
}