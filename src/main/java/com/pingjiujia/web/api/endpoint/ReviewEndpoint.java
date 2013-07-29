package com.pingjiujia.web.api.endpoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.media.domain.Media;
import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.broadleafcommerce.core.rating.service.RatingService;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.broadleafcommerce.core.web.api.wrapper.ProductWrapper;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.pingjiujia.admin.domain.PJJRatingSummary;
import com.pingjiujia.admin.domain.UserCollection;
import com.pingjiujia.admin.domain.WineImpl;
import com.pingjiujia.domain.client.ReviewsImpl;
import com.pingjiujia.service.CommentsService;
import com.pingjiujia.web.api.wrapper.ProductTitlesWrapper;
import com.pingjiujia.web.api.wrapper.RatingSummaryWrapper;

@Component("review")
@Scope("singleton")
@Path("/colletions")
@Produces(value = { MediaType.APPLICATION_JSON })
@Consumes(value = { MediaType.APPLICATION_JSON })
public class ReviewEndpoint implements ApplicationContextAware {

	private ApplicationContext context;
	
	private Logger logger = Logger.getLogger(ReviewEndpoint.class);

	@Resource(name = "txjjCommentsService")
	private CommentsService commentsService;
	
	@Resource(name = "blRatingService")
	private RatingService ratingService;
	
	@Resource(name = "blCustomerService")
	private CustomerService customerService;
	
	@Resource(name = "blCatalogService")
	private CatalogService catalogService;
	
    /**
     * Search for {@code Product} by product id
     * FOr detail page.
     *
     * @param id the product id
     * @return the product instance with the given product id
     */
    @GET
    @Path("product/{id}")
    public ProductWrapper findProductById(@Context HttpServletRequest request, @PathParam("id") Long id) {
        Product product = catalogService.findProductById(id);
        
        if (product != null) {
        	
        	RatingSummary ratingSummary = ratingService.readRatingSummary(String.valueOf(product.getId()), RatingType.PRODUCT);
        	
        	ProductWrapper wrapper;
        	if (product instanceof WineImpl) {
        		if (ratingSummary instanceof PJJRatingSummary) {
        			((WineImpl) product).setPjjRatingSummary((PJJRatingSummary)ratingSummary);
				}
        		if (null != product.getDefaultSku()) {
        			List<String> mediaIds = readMediaIdsByProductId(product.getDefaultSku().getId());
        			((WineImpl) product).setMediaIds(mediaIds);
				}
        		
        		wrapper = (ProductWrapper)context.getBean(ProductWrapper.class.getName());
        	
        	wrapper.wrap(product, request);
        	
            return wrapper;
        }
       }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

	/**
	 * username : user name
	 * limit : page size
	 * @param request
	 * @param userName
	 * @param limit
	 * @return
	 */
	@GET
	@Path("collector/{userName}")
	public List<ProductTitlesWrapper> findProductsByUserName(@Context HttpServletRequest request, @PathParam("userName") final String userName,
													   @QueryParam("limit") @DefaultValue("20") int limit, @QueryParam("type") @DefaultValue("username") String type,
													   @QueryParam("offset") @DefaultValue("0") int offset) {
		
		Set<Product> result = commentsService.readProductsUserReviewed(userName, limit, offset);
		List<ProductTitlesWrapper> out = new ArrayList<ProductTitlesWrapper>();
		if (null == result || result.size() == 0) {
			
			return out;
		}
		Iterator<Product> wineIterator = result.iterator();
		while(wineIterator.hasNext()) {
			Product product = wineIterator.next();
            RatingSummary ratingSummary = ratingService.readRatingSummary(String.valueOf(product.getId()), RatingType.PRODUCT);
            ProductTitlesWrapper wrapper;
        	if (product instanceof WineImpl) {
        		if (ratingSummary instanceof PJJRatingSummary) {
        			((WineImpl) product).setPjjRatingSummary((PJJRatingSummary)ratingSummary);
				}
        		if (null != product.getDefaultSku()) {
        			List<String> mediaIds = readMediaIdsByProductId(product.getDefaultSku().getId());
        			((WineImpl) product).setMediaIds(mediaIds);
				}
        		//transfer user name to the model
        		((WineImpl) product).setSpecifiComment(userName);
        		
        		wrapper = (ProductTitlesWrapper) context.getBean(ProductTitlesWrapper.class.getName());
        		wrapper.wrap(product, request);
        		out.add(wrapper);
        	}
		}

		return out;
	}
	
	
	/**
	 * 
	 * find product by barcode 1d/2d
	 * @param request
	 * @param userName
	 * @param limit
	 * @return
	 */
	@GET
	@Path("barcode/{barcode}/type/{type}")
	public ProductWrapper findProductsByBarCode(@Context HttpServletRequest request, @PathParam("barcode") final String barcode, @PathParam("type") @DefaultValue("") final String type  ) {
		
		if (StringUtils.isEmpty(barcode)) {
			return null;
		}
		
		Product product = null;
		if (StringUtils.isEmpty(type)) {
			product = commentsService.readTxjjProductByBarcode(barcode);
		} else {
			product = commentsService.readTxjjProductByBarcode(barcode, type);
		}

		 if (product != null) {
	        	
	        	RatingSummary ratingSummary = ratingService.readRatingSummary(String.valueOf(product.getId()), RatingType.PRODUCT);
	        	
	        	ProductWrapper wrapper;
	        	if (product instanceof WineImpl) {
	        		if (ratingSummary instanceof PJJRatingSummary) {
	        			((WineImpl) product).setPjjRatingSummary((PJJRatingSummary)ratingSummary);
					}
	        		wrapper = (ProductWrapper)context.getBean(ProductWrapper.class.getName());
	        		if (null != product.getDefaultSku()) {
	        			List<String> mediaIds = readMediaIdsByProductId(product.getDefaultSku().getId());
	        			((WineImpl) product).setMediaIds(mediaIds);
					}
	        	wrapper.wrap(product, request);
	        	
	            return wrapper;
	        }
	     }

		return null;
	}
	
	/**
	 * 
	 * find product by barcode 1d/2d
	 * @param request
	 * @param userName
	 * @param limit
	 * @return
	 */
	@GET
	@Path("barcode/{barcode}")
	public List<ProductWrapper> findProductsByBarCode(@Context HttpServletRequest request, @PathParam("barcode") final String barcode) {
		
		if (StringUtils.isEmpty(barcode)) {
			return null;
		}
		
		Product product = commentsService.readTxjjProductByBarcode(barcode);
		List<ProductWrapper> resultList = new ArrayList<ProductWrapper>();
		if (product != null) {
	        	
	        	RatingSummary ratingSummary = ratingService.readRatingSummary(String.valueOf(product.getId()), RatingType.PRODUCT);
	        	
	        	ProductWrapper wrapper;
	        	if (product instanceof WineImpl) {
	        		if (ratingSummary instanceof PJJRatingSummary) {
	        			((WineImpl) product).setPjjRatingSummary((PJJRatingSummary)ratingSummary);
					}
	        		wrapper = (ProductWrapper)context.getBean(ProductWrapper.class.getName());
	        		if (null != product.getDefaultSku()) {
	        			List<String> mediaIds = readMediaIdsByProductId(product.getDefaultSku().getId());
	        			((WineImpl) product).setMediaIds(mediaIds);
					}
	        	wrapper.wrap(product, request);
	        	resultList.add(wrapper);
	            return resultList;
	        }
	       }
		

		return null;
	}
	
	
	/**
	 * 
	 * find wines by user input
	 * manufactor, name of the wine,
	 * @param request
	 * @param userName
	 * @param limit
	 * @return
	 */
	@GET
	@Path("query/{input}")
	public List<ProductWrapper> quickSearch(@Context HttpServletRequest request, @PathParam("input") final String input, @QueryParam("limit") @DefaultValue("20") int limit,
            @QueryParam("offset") @DefaultValue("0") int offset) {
		
		if (StringUtils.isEmpty(input)) {
			return null;
		}
		
		List<Product> result = commentsService.readProductsByUserinput(input);
		if (null == result) {
			return null;
		}
		        //page navigation
				int fromIndex = limit * offset;
				int toIndex = (limit * (offset + 1) ) < result.size() ? (limit * (offset + 1)  ) : result.size() ;
						
				result = result.subList(fromIndex, toIndex);
		List<ProductWrapper> out = new ArrayList<ProductWrapper>();
		for ( Product product : result) {
			ProductWrapper wrapper = (ProductWrapper)context.getBean(ProductWrapper.class.getName());;
            if (product != null) {
	        	
	        	RatingSummary ratingSummary = ratingService.readRatingSummary(String.valueOf(product.getId()), RatingType.PRODUCT);
	        	
	        	
	        	if (product instanceof WineImpl) {
	        		if (ratingSummary instanceof PJJRatingSummary) {
	        			((WineImpl) product).setPjjRatingSummary((PJJRatingSummary)ratingSummary);
					}
	        		if (null != product.getDefaultSku()) {
	        			List<String> mediaIds = readMediaIdsByProductId(product.getDefaultSku().getId());
	        			((WineImpl) product).setMediaIds(mediaIds);
					}
	            }
	       }
            wrapper.wrap(product, request);
            out.add(wrapper);
		}
		
//		if (result != null) {
//			for (Product product : result) {
//				ProductWrapper wrapper;
//				if (product instanceof ProductBundle) {
//					wrapper = (ProductWrapper) context.getBean(ProductBundleWrapper.class.getName());
//				} else {
//					wrapper = (ProductWrapper) context.getBean(ProductWrapper.class.getName());
//				}
//				wrapper.wrap(product, request);
//				out.add(wrapper);
//			}
//		}

		return out;
	}
	
	/**
	 * find the comments for the wine which product is is {productId}
	 * 
	 * @param request
	 * @param productId
	 * @return
	 */
	@GET
	@Path("comments/{productId}")
	public RatingSummaryWrapper findCommentsByProductId(@Context HttpServletRequest request, @PathParam("productId") final String productId ) {
		
		RatingSummary ratingSummary = ratingService.readRatingSummary(productId, RatingType.PRODUCT);
		RatingSummaryWrapper wrapper = (RatingSummaryWrapper) context.getBean(RatingSummaryWrapper.class.getName());
		
		if (null == ratingSummary) return wrapper;

		wrapper.wrap(ratingSummary, request);

		return wrapper;
	}
	
//	/**
//	 * find the comments for the wine which product is is {productId}
//	 * 
//	 * @param request
//	 * @param productId
//	 * @return
//	 */
//	@GET
//	@Path("comments/{productId}")
//	public RatingSummaryWrapper checkIfTheCustomerRegisted(@Context HttpServletRequest request, @PathParam("productId") final String productId ) {
//		
//		RatingSummary ratingSummary = ratingService.readRatingSummary(productId, RatingType.PRODUCT);
//		RatingSummaryWrapper wrapper = (RatingSummaryWrapper) context.getBean(RatingSummaryWrapper.class.getName());
//		
//		if (null == ratingSummary) return wrapper;
//
//		wrapper.wrap(ratingSummary, request);
//
//		return wrapper;
//	}
//	
	
	
	/**
	 * post the review comments and rating point to the server.
	 * @param request
	 * @param reviewText
	 * @param rating
	 * @param customerId
	 * @param productId
	 * @return
	 */
	@POST
	@Path("/postcomments/{comments}/customer/{username}/product/{productId}")
	public Response submitComments(@Context HttpServletRequest request, @PathParam("comments") String reviewText, @PathParam("username") String userName, @PathParam("productId") String productId,
			@QueryParam("xiangqi") Double xiangqi, @QueryParam("suandu") Double suandu, @QueryParam("danning") Double danning, @QueryParam("jiuti") Double jiuti, @QueryParam("huiwei") Double huiwei, @QueryParam("seze") Double seze) {
		
		Customer customer = customerService.readCustomerByUsername(userName);
		
		if (null == customer) {
			//create a new customer
			customer = customerService.createNewCustomer();
			customer.setUsername(userName);
			customer = customerService.saveCustomer(customer, false);
		}
		
		Long customerId = customer.getId();
		
		if (null != reviewText && null != customerId && null != productId ) {
			ReviewsImpl reviewsImpl = new ReviewsImpl();
			reviewsImpl.setXiangqi(xiangqi);
			reviewsImpl.setSuandu(suandu);
			reviewsImpl.setDanning(danning);
			reviewsImpl.setJiuti(jiuti);
			reviewsImpl.setHuiwei(huiwei);
			reviewsImpl.setSeze(seze);
			reviewsImpl.setProductId(productId);
			reviewsImpl.setComments(reviewText);
			reviewsImpl.setCustomerId(customerId);
			
			RatingSummary ratingSummary = commentsService.reviewItem(customer, reviewsImpl);
			
			RatingSummaryWrapper ratingSummaryWrapper = context.getBean(RatingSummaryWrapper.class.getName(), RatingSummaryWrapper.class);
			ratingSummaryWrapper.wrap(ratingSummary, request);
			
			return Response.ok().entity(ratingSummaryWrapper.toString()).build();
		} 
		
		throw new WebApplicationException(Response.Status.BAD_REQUEST);

	}
	
	/**
	 * post the review comments and rating point to the server.
	 * @param request
	 * @param reviewText
	 * @param rating
	 * @param customerId
	 * @param productId
	 * @return
	 */
	@POST
	@Path("/remove/{productId}/username/{username}")
	public Response removeCollection(@Context HttpServletRequest request, @PathParam("username") String userName, @PathParam("productId") String productId) {
		
		Customer customer = customerService.readCustomerByUsername(userName);
		
		if (null == customer) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
		if (StringUtils.isEmpty(productId)) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
		Long customerId = customer.getId();
		UserCollection userCollection = commentsService.findUserCollectionByUserNameAndProductId(customerId, Long.valueOf(productId));
		commentsService.removeUserCollection(userCollection);
		
		return Response.ok().entity("sucessfully removed").build();

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
