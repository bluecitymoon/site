package com.pingjiujia.web.api.endpoint;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

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
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.file.service.StaticAssetStorageService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component("image")
@Scope("singleton")
@Path("/image")
@Consumes(value = { MediaType.APPLICATION_JSON })
public class PictureEndpoint {
	
	@Resource(name = "blStaticAssetService")
	StaticAssetService staticAssetService;
	
	@Resource(name = "blStaticAssetStorageService")
	StaticAssetStorageService staticAssetStorageService;
	
	@GET
	@Path("/media/{mediaId}")
	@Produces("image/jpg")
	public Response readImageByMediaId(@PathParam("mediaId") String mediaId) {
		File file = new File("C:\\jerry\\broadleaf_commerce\\DemoSite\\site\\src\\main\\webapp\\img\\merch\\habanero_mens_black.jpg");
		
		
		
		if (StringUtils.isNotBlank(mediaId)) {
			
			if (NumberUtils.isNumber(mediaId)) {
				StaticAssetStorage staticAsset = staticAssetStorageService.readStaticAssetStorageByStaticAssetId(Long.valueOf(mediaId));
				 ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                InputStream is = null;
//	                try {
//	                    is = staticAsset.getFileData().getBinaryStream();
//	                    boolean eof = false;
//	                    while (!eof) {
//	                        int temp = is.read();
//	                        if (temp < 0) {
//	                            eof = true;
//	                        } else {
//	                            baos.write(temp);
//	                        }
//	                    }
//	                    baos.flush();
//	                } catch (SQLException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}  finally {
//	                    if (is != null) {
//	                        try{
//	                            is.close();
//	                        } catch (Throwable e) {
//	                            //do nothing
//	                        }
//	                    }
//	                }
	                InputStream original = new ByteArrayInputStream(baos.toByteArray());
			} 
			
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
			
			
		}
		
//		
//		
//		Sku sku = catalogService.findSkuById(id);
//        if (sku != null) {
//            List<MediaWrapper> medias = new ArrayList<MediaWrapper>();
//            if (sku.getSkuMedia() != null && ! sku.getSkuMedia().isEmpty()) {
//                for (Media media : sku.getSkuMedia().values()) {
//                    MediaWrapper wrapper = (MediaWrapper)context.getBean(MediaWrapper.class.getName());
//                    wrapper.wrap(media, request);
//                    if (wrapper.isAllowOverrideUrl()){
//                        wrapper.setUrl(getStaticAssetService().convertAssetPath(media.getUrl(), request.getContextPath(), request.isSecure()));
//                    }
//                    medias.add(wrapper);
//                }
//            }
//            return medias;
//        }
//        throw new WebApplicationException(Response.Status.NOT_FOUND);
		ResponseBuilder responseBuilder = Response.ok(file);
		responseBuilder.header("Content-Disposition", "attachment; filename=image_from_server.jpg");
		
		return responseBuilder.build();
	}

}
