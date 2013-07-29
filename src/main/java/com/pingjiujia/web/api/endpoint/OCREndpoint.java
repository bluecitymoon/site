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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.media.domain.Media;
import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.broadleafcommerce.core.rating.service.RatingService;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.broadleafcommerce.core.web.api.wrapper.ProductBundleWrapper;
import org.broadleafcommerce.core.web.api.wrapper.ProductWrapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pingjiujia.admin.domain.PJJRatingSummary;
import com.pingjiujia.admin.domain.WineImpl;
import com.pingjiujia.service.CommentsService;
import com.pingjiujia.service.OCRService;

//import com.sun.jersey.multipart.FormDataParam;

@Component("ocr")
@Scope("singleton")
@Path("/search")
@Consumes(value = { MediaType.MULTIPART_FORM_DATA })
public class OCREndpoint implements ApplicationContextAware  {

	private static Logger logger = Logger.getLogger(OCREndpoint.class);

	private ApplicationContext context;
	
	@Resource(name = "blSystemPropertiesService")
	SystemPropertiesService systemPropertiesService;

	@Resource(name = "ocrservice")
	OCRService ocrService;
	
	@Resource(name = "blCatalogService")
	CatalogService catalogService;	

	@Resource(name = "blRatingService")
	private RatingService ratingService;
	
	@Resource(name = "txjjCommentsService")
	private CommentsService commentsService;
	
	@PUT
	@Path("/byocr/{fileName}")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public List<ProductWrapper> readProductsByOCRImage( @Context HttpServletRequest request, @PathParam("fileName") String fileName)
	{
		InputStream inputStream = null;
		List<ProductWrapper> products = new ArrayList<ProductWrapper>();
		
		try {
		    inputStream = request.getInputStream();

			// temp dir to save the image.
			SystemProperty tempDir = systemPropertiesService.findSystemPropertyByName("TXJJ_OCR_IMAGE_TEMP_DIR");

			if (logger.isDebugEnabled()) {
				logger.debug(fileName + " will be saved in " + tempDir);
			}

			String uploadedFileLocation = "";
			if (null == tempDir || StringUtils.isEmpty(tempDir.getValue())) {
				logger.error("temp dir to save the uploaded image not be configured.");
			} else {
				uploadedFileLocation = tempDir.getValue();
			}
			String currentMillis = String.valueOf(System.currentTimeMillis());
			
			// save the image to the temp dir
			String savedFile = writeToFile(inputStream, uploadedFileLocation,
					fileName, currentMillis);

			List<String> contents = ocrService.readContentFromImage(savedFile, fileName, currentMillis);
			
			if (null == contents) {
				ProductWrapper pw = (ProductWrapper) context.getBean(ProductWrapper.class.getName());
				
				products.add(pw);
				return products;
				
			}
			
			List<Product> result = null;
			for (String keyword : contents) {
				if (keyword.length() < 3) {
					continue;
				}
				
				if (null == result) {
					
					result = new ArrayList<Product>();
				}
				
				if (keyword.matches(".*\\s+.*")) {
					
					//if keywords contains backspace, we need to split it and query the product by each element.
					String[] keywords = keyword.split("\\s");
					for (int i = 0; i < keywords.length; i++) {
						
						String keyWord = keywords[i];
						if (StringUtils.isEmpty(keyWord) || keyWord.length() < 2) {
							
							continue;
						}
						
						List<Product> queryResult = catalogService.findProductsByName(keyword, 20, 0);
						
						if (null != queryResult && !queryResult.isEmpty()) {
							result.addAll(queryResult);
						}
					}
					
				} else {
					result = catalogService.findProductsByName(keyword, 20, 0);
					
				}
			}
			
			ProductWrapper wrapper = (ProductWrapper)context.getBean(ProductWrapper.class.getName());;;
			for (Product resultProduct : result) {
	        	
	        	RatingSummary ratingSummary = ratingService.readRatingSummary(String.valueOf(resultProduct.getId()), RatingType.PRODUCT);
	        	
	        	
	        	if (resultProduct instanceof WineImpl) {
	        		if (ratingSummary instanceof PJJRatingSummary) {
	        			((WineImpl) resultProduct).setPjjRatingSummary((PJJRatingSummary)ratingSummary);
					}
	        	
	            }
	        	wrapper.wrap(resultProduct, request);
	        	products.add(wrapper);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		//GenericEntity<List<ProductWrapper>> entity = new GenericEntity<List<ProductWrapper>>(products) {}; 
		//return Response.ok(entity).build();
		
		return products;
	}

	
	@PUT
	@Path("/byocrimage/{fileName}")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response readPictureContent( @Context HttpServletRequest request, @PathParam("fileName") String fileName)
	{
		InputStream inputStream = null;
		
		try {
		    inputStream = request.getInputStream();

			// temp dir to save the image.
			SystemProperty tempDir = systemPropertiesService.findSystemPropertyByName("TXJJ_OCR_IMAGE_TEMP_DIR");

			if (logger.isDebugEnabled()) {
				logger.debug(fileName + " will be saved in " + tempDir);
			}

			String uploadedFileLocation = "";
			if (null == tempDir || StringUtils.isEmpty(tempDir.getValue())) {
				logger.error("temp dir to save the uploaded image not be configured.");
			} else {
				uploadedFileLocation = tempDir.getValue();
			}
			String currentMillis = String.valueOf(System.currentTimeMillis());
			
			// save the image to the temp dir
			String savedFile = writeToFile(inputStream, uploadedFileLocation,
					fileName, currentMillis);

			List<String> contents = ocrService.readContentFromImage(savedFile, fileName, currentMillis);
			
			StringBuilder stringBuilder = new StringBuilder();
			
			if (contents == null || contents.size() == 0) {
				return Response.ok("").build();
			}
			for (int i = 0; i < contents.size(); i ++) {
				
				stringBuilder.append(contents.get(i));
				if (i < (contents.size() - 1)) {
					stringBuilder.append(",");
				}
			}
			
			return Response.ok(stringBuilder.toString()).build();
			
		}catch (Exception e) {
			logger.error("catch exception message = " + e.getMessage());
		}
		
		return Response.status(Status.SERVICE_UNAVAILABLE).build();
	}
	
	//TODO
	@GET
	@Path("byimagecontent/{imagecontent}")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public List<ProductWrapper> findProductsByImageContent(@Context HttpServletRequest request, @PathParam("imagecontent") final String imagecontent,
													   @QueryParam("limit") @DefaultValue("20") int limit,
													   @QueryParam("offset") @DefaultValue("0") int offset) {
		
		if (StringUtils.isEmpty(imagecontent)) {
			return null;
		}
		String[] keywords = imagecontent.trim().split(",");
		List<Product> result = null;
		for (String keyword : keywords) {

			if (StringUtils.isEmpty(keyword) || keyword.length() == 1) {
				continue;
			}
			
			if (null == result) {
				
				result = new ArrayList<Product>();
			}
			
			List<Product> queryResult = commentsService.readProductsByName(keyword);
			
			if (null != queryResult && !queryResult.isEmpty()) {
				result.addAll(queryResult);
			}
//			if (keyword.matches(".*\\s+.*")) {
//				
//				//if keywords contains backspace, we need to split it and query the product by each element.
//				String[] keywords = keyword.split("\\s");
//				for (int i = 0; i < keywords.length; i++) {
//					
//					String keyWord = keywords[i];
//					if (StringUtils.isEmpty(keyWord) || keyWord.length() < 2) {
//						
//						continue;
//					}
//					
//					List<Product> queryResult = catalogService.findProductsByName(keyword, 20, 0);
//					
//					if (null != queryResult && !queryResult.isEmpty()) {
//						result.addAll(queryResult);
//					}
//				}
//				
//			} else {
//				result = catalogService.findProductsByName(keyword, 20, 0);
//				
//			}
		
		}
		
		//if no result found, try to find the product again with more detailed condition
		if (result.size() == 0) {
			for (String keyword : keywords) {
				if (keyword.length() < 4) {
					continue;
				}
				
				int count = 0;
				while ((count + 2) < keyword.length()) {
					String childKeyWord = keyword.substring(count, count + 3);
					List<Product> queryResult = commentsService.readProductsByName(childKeyWord);
					
					if (null != queryResult && !queryResult.isEmpty()) {
						result.addAll(queryResult);
					}
					count ++;
				}
				
			}
		}
		
		
		//if no result found, try to find the product again with more detailed condition again!
				if (result.size() == 0) {
					for (String keyword : keywords) {
						if (keyword.length() < 3) {
							continue;
						}
						
						int count = 0;
						while ((count + 1) < keyword.length()) {
							String childKeyWord = keyword.substring(count, count + 2);
							List<Product> queryResult = commentsService.readProductsByName(childKeyWord);
							
							if (null != queryResult && !queryResult.isEmpty()) {
								result.addAll(queryResult);
							}
							count ++;
						}
					}
				}
		//nothing found.		
		if (null == result || result.size() == 0) {
			return null;
		}
		
		List<ProductWrapper> out = new ArrayList<ProductWrapper>();
		//remove the duplicate records
		List<Product> tempProducts = new ArrayList<Product>();
		for (Product product : result) {
			if (!tempProducts.contains(product)) {
				tempProducts.add(product);
			}
		}
		
		//page navigation
		int fromIndex = limit * offset;
		int toIndex = (limit * (offset + 1) ) < tempProducts.size() ? (limit * (offset + 1)  ) : tempProducts.size() ;
				
		tempProducts = tempProducts.subList(fromIndex, toIndex);
		if (result != null) {
			for (Product product : tempProducts) {
				ProductWrapper wrapper;
				if (product instanceof ProductBundle) {
					wrapper = (ProductWrapper) context.getBean(ProductBundleWrapper.class.getName());
				} else {
					wrapper = (ProductWrapper) context.getBean(ProductWrapper.class.getName());
				}
                RatingSummary ratingSummary = ratingService.readRatingSummary(String.valueOf(product.getId()), RatingType.PRODUCT);
	        	
	        	if (product instanceof WineImpl) {
	        		if (ratingSummary instanceof PJJRatingSummary) {
	        			((WineImpl) product).setPjjRatingSummary((PJJRatingSummary)ratingSummary);
					}
	        	
	            }
	        	if (null != product.getDefaultSku()) {
        			List<String> mediaIds = readMediaIdsByProductId(product.getDefaultSku().getId());
        			((WineImpl) product).setMediaIds(mediaIds);
				}
				wrapper.wrap(product, request);
				out.add(wrapper);
			}
		}

		return out;
	}
	
	
	/**
	 * this can be opertimized with java nio.
	 * 
	 * @param uploadedInputStream
	 * @param uploadedFileLocation
	 */
	private String writeToFile(InputStream uploadedInputStream,
			String uploadedFileLocation, String recieptFileName, String currentMillis) {
		
		String fileName =  uploadedFileLocation + File.separator + currentMillis + "_" + recieptFileName;
		try {

			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			file.setReadable(true);
			file.setExecutable(true);
			file.setWritable(true);
			OutputStream out = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			throw new WebApplicationException(e);
		}
		return fileName;
	}
	
	/**
	 * read media urls by skuId
	 * @param skuId
	 * @return
	 */
	private List<String> readMediaIdsByProductId(Long skuId) {
		List<String> mediaIds = null;
		
		Sku sku = catalogService.findSkuById(skuId);
		if (sku.getSkuMedia() != null && ! sku.getSkuMedia().isEmpty()) {
			if (null == mediaIds) mediaIds = new ArrayList<String>();
			
            for (Media media : sku.getSkuMedia().values()) {
            	mediaIds.add(media.getUrl());
            }
        }
		
		return mediaIds;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}
}
