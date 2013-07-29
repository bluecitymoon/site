package com.pingjiujia.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Column;

import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.config.domain.SystemProperty;
import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.catalog.dao.ProductDao;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.media.domain.Media;
import org.broadleafcommerce.core.rating.dao.RatingSummaryDao;
import org.broadleafcommerce.core.rating.dao.ReviewDetailDao;
import org.broadleafcommerce.core.rating.domain.RatingDetail;
import org.broadleafcommerce.core.rating.domain.RatingDetailImpl;
import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.broadleafcommerce.core.rating.domain.RatingSummaryImpl;
import org.broadleafcommerce.core.rating.domain.ReviewDetail;
import org.broadleafcommerce.core.rating.domain.ReviewDetailImpl;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.broadleafcommerce.core.web.api.wrapper.MediaWrapper;
import org.broadleafcommerce.core.web.api.wrapper.ProductWrapper;
import org.broadleafcommerce.profile.core.dao.CustomerDao;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.email.EmailClickTrackingFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pingjiujia.admin.domain.PJJRatingSummary;
import com.pingjiujia.admin.domain.PJJRatingSummaryImpl;
import com.pingjiujia.admin.domain.RatingPoints;
import com.pingjiujia.admin.domain.RatingPointsImpl;
import com.pingjiujia.admin.domain.UserCollection;
import com.pingjiujia.admin.domain.Wine;
import com.pingjiujia.admin.domain.WineImpl;
import com.pingjiujia.dao.CommentsDao;
import com.pingjiujia.dao.UserCollectionDao;
import com.pingjiujia.dao.WineDao;
import com.pingjiujia.domain.AnalysisResult;
import com.pingjiujia.domain.Condition;
import com.pingjiujia.domain.ReviewImpl;
import com.pingjiujia.domain.client.ReviewsImpl;

@Service("txjjCommentsService")
public class CommentsServiceImpl implements CommentsService {
	
	@Resource(name="txjjCommentsDao")
	private CommentsDao commentsDao;
	
	@Resource(name = "txjjWineDao")
	private WineDao wineDao;
	
	@Resource(name = "blProductDao")
	private ProductDao productDao;
	
	@Resource(name = "blRatingSummaryDao")
	RatingSummaryDao ratingSummaryDao;
	
	@Resource(name = "blReviewDetailDao")
	ReviewDetailDao reviewDetailDao;
	
	@Resource(name = "blCustomerDao")
	CustomerDao customerDao;
	
	@Resource(name = "blCatalogService")
	CatalogService catalogService;
	
	@Resource(name = "blSystemPropertiesService")
	SystemPropertiesService systemPropertiesService;
	
	@Resource(name = "blUserCollectionDao")
	UserCollectionDao userCollectionDao;

	public Set<Product> readProductsUserReviewed(String userName, int pageSize, int offset) {
		
		Set<Product> products = new HashSet<Product>();
		List<RatingSummary> ratingSummaries = commentsDao.readRatingSummaryByCustomerName(userName, pageSize, offset);
		List<UserCollection> userCollections = userCollectionDao.findUserCollectionByUserName(userName);
		if (null == userCollections || userCollections.size() ==0) {
			return null;
		}
		
		List<Long> userCollectedProductIds = new ArrayList<Long>();
		for (UserCollection userCollection : userCollections) {
			userCollectedProductIds.add(userCollection.getProductId());
		}
		for (RatingSummary ratingSummary : ratingSummaries) {
			try {
				Long productId = Long.parseLong(ratingSummary.getItemId());
				if (!userCollectedProductIds.contains(productId)) {
					//those are which user have reviewed but not collected now.
					continue;
				}
				Product product = productDao.readProductById(productId);
				List<String> mediaIds = readMediaIdsByProductId(product.getDefaultSku().getId());
				if (product instanceof WineImpl) {
					((WineImpl) product).setAverageRating(ratingSummary.getAverageRating());
					((WineImpl) product).setRatingCount(ratingSummary.getNumberOfRatings());
					((WineImpl) product).setReviewCount(ratingSummary.getNumberOfReviews());
					((WineImpl) product).setMediaIds(mediaIds);
				}
				
				products.add(product);
			} catch (Exception e) {
				continue;
			}
		}
		
		return products;
	}
	
	public Product readTxjjProductByProductId(Long productId) {
		Product product = productDao.readProductById(productId);
		List<String> mediaIds = readMediaIdsByProductId(product.getDefaultSku().getId());
		if ( product instanceof WineImpl) {
			RatingSummary ratingSummary = ratingSummaryDao.readRatingSummary(product.getId().toString(), RatingType.PRODUCT);
			((WineImpl) product).setRatingCount(ratingSummary.getNumberOfRatings());
			((WineImpl) product).setReviewCount(ratingSummary.getNumberOfReviews());
			((WineImpl) product).setMediaIds(mediaIds);
		}
		
		return product;
		
	}
	
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
	
	 	@Transactional("blTransactionManager")
	    public RatingSummary reviewItem(Customer customer, ReviewsImpl reviews)  {
	 		//save the user collection information
	 		Long productId = Long.valueOf(reviews.getProductId());
	 		UserCollection userCollection = userCollectionDao.findUserCollectionByUserNameAndProductId(reviews.getCustomerId(), productId);
	 		//first time to collect the wine
	 		if ( null == userCollection) {
				userCollection = userCollectionDao.create();
				userCollection.setCustomerId(customer.getId());
				userCollection.setProductId(productId);
				userCollection.setUserName(customer.getUsername());
				userCollectionDao.save(userCollection);
			}
	 		
	        RatingSummary ratingSummary = ratingSummaryDao.readRatingSummary(reviews.getProductId(), RatingType.PRODUCT);

	        if (ratingSummary == null) {
	            ratingSummary = new PJJRatingSummaryImpl(reviews.getProductId(), RatingType.PRODUCT);
	        }

	         RatingDetail ratingDetail = ratingSummaryDao.readRating(customer.getId(), ratingSummary.getId());
	        
	    	 Double xiangqi = reviews.getXiangqi();
	    	 Double suandu = reviews.getSuandu();
	    	 Double danning = reviews.getDanning();
	    	 Double jiuti = reviews.getJiuti();
	    	 Double seze = reviews.getSeze();
	    	 Double huiwei = reviews.getHuiwei();
	    	 Double compositeScore = 0.0d;
	    	 String reviewText = reviews.getComments();
	    	 
	    	 //care about the danning. this value is null if it's white wine
	    	 boolean isWhiteWine = (danning == null || danning == 0.0d);
	    	 Double fangcha = 0.0d;
	    	 Double averagePoint = 0.0d;
	    	 if (isWhiteWine) {
	    		 compositeScore = (xiangqi + suandu  + jiuti + seze + huiwei);
	    		 averagePoint = (xiangqi + suandu  + jiuti + seze + huiwei) / 5;
	    		 
	    		 fangcha = ( Math.pow((xiangqi - averagePoint), 2) + Math.pow(suandu - averagePoint, 2) + Math.pow(jiuti - averagePoint, 2) 
	    				      + Math.pow(seze - averagePoint, 2) + Math.pow(huiwei - averagePoint, 2) ) / 5;
			} else {
				compositeScore = (xiangqi + suandu  + jiuti + seze + huiwei + danning);
				averagePoint = (xiangqi + suandu  + jiuti + seze + huiwei + danning) / 6;
				
				fangcha = ( Math.pow((xiangqi - averagePoint), 2) + Math.pow(suandu - averagePoint, 2) + Math.pow(jiuti - averagePoint, 2) 
  				      + Math.pow(seze - averagePoint, 2) + Math.pow(huiwei - averagePoint, 2) + Math.pow(danning - averagePoint, 2) ) / 6;
			}
	    	 
	    	compositeScore += (40 - fangcha);

	    	//first rate
	        if (ratingDetail == null) {
	            ratingDetail = new RatingPointsImpl(ratingSummary, compositeScore, SystemTime.asDate(), customer);
	            
	        } else {
	        	ratingSummary.getRatings().remove(ratingDetail);
			}
	        ratingDetail.setRating(compositeScore);        	

	        if (ratingDetail instanceof RatingPoints) {
				((RatingPoints) ratingDetail).setXiangqi(xiangqi);
				((RatingPoints) ratingDetail).setSuandu(suandu);
				((RatingPoints) ratingDetail).setDanning(danning);
				((RatingPoints) ratingDetail).setJiuti(jiuti);
				((RatingPoints) ratingDetail).setSeze(seze);
				((RatingPoints) ratingDetail).setHuiwei(huiwei);
				((RatingPoints) ratingDetail).setCompositeScore(compositeScore);
				
				if (ratingSummary instanceof PJJRatingSummary) {
					reCalculateAveragePoints((PJJRatingSummary)ratingSummary, (RatingPoints) ratingDetail);
				}
				
			}
	        ratingSummary.getRatings().add(ratingDetail);

	        ReviewDetail reviewDetail = ratingSummaryDao.readReview(customer.getId(), ratingSummary.getId());

	        if (reviewDetail == null) {
	            reviewDetail = new ReviewDetailImpl(customer, SystemTime.asDate(), ratingDetail, reviewText, ratingSummary);
	        } else {
	            reviewDetail.setReviewText(reviewText);        	
	        }
	        
	        
	        if (!ratingSummary.getReviews().contains(reviewDetail)) {
	        	ratingSummary.getReviews().add(reviewDetail);
			}
	        
	        //save the data
	        ratingSummary = ratingSummaryDao.saveRatingSummary(ratingSummary);
	        
	        
	        return ratingSummary;
	    }
	 	
	 	private void reCalculateAveragePoints(PJJRatingSummary ratingSummary,
	 			RatingPoints ratingDetail) {
	 		    
	 		Double avgXiangqi = ratingSummary.getXiangqiAvg();
	 		Double inputXiangqi = ratingDetail.getXiangqi();
	 		int size = ratingSummary.getNumberOfRatings();
	 		//only handle those which input value exists.
	 		if (inputXiangqi != null && inputXiangqi > 0.0d) {
				
	 			// first time
	 			if(avgXiangqi == null || avgXiangqi == 0.0d) {
	 				avgXiangqi = inputXiangqi;
	 			} else {
	 				avgXiangqi = (avgXiangqi * size + inputXiangqi) / (size + 1);
	 			}
	 			
	 			ratingSummary.setXiangqiAvg(avgXiangqi);
			}
	 		
	 		// same as others.
	 		Double suanduAvg = ratingSummary.getSuanduAvg();
	 		Double inputSuandu = ratingDetail.getSuandu();
	 		//only handle those which input value exists.
	 		if (inputSuandu != null && inputSuandu > 0.0d) {
				
	 			// first time
	 			if(suanduAvg == null || suanduAvg == 0.0d) {
	 				suanduAvg = inputSuandu;
	 			} else {
	 				suanduAvg = (suanduAvg * size + inputSuandu) / (size + 1);
	 			}
	 			
	 			ratingSummary.setSuanduAvg(suanduAvg);
			}
	 		
	 		Double jiutiAvg = ratingSummary.getJiutiAvg();
	 		Double inputJiuti = ratingDetail.getJiuti();
	 		//only handle those which input value exists.
	 		if (inputJiuti != null && inputJiuti > 0.0d) {
				
	 			// first time
	 			if(jiutiAvg == null || jiutiAvg == 0.0d) {
	 				jiutiAvg = inputJiuti;
	 			} else {
	 				jiutiAvg = (jiutiAvg * size + inputJiuti) / (size + 1);
	 			}
	 			
	 			ratingSummary.setJiutiAvg(jiutiAvg);
			}
			
	 		//sezeAvg
	 		Double sezeAvg = ratingSummary.getSezeAvg();
	 		Double inputSeze = ratingDetail.getSeze();
	 		//only handle those which input value exists.
	 		if (inputSeze != null && inputSeze > 0.0d) {
				
	 			// first time
	 			if(sezeAvg == null || sezeAvg == 0.0d) {
	 				sezeAvg = inputSeze;
	 			} else {
	 				sezeAvg = (sezeAvg * size + inputSeze) / (size + 1);
	 			}
	 			
	 			ratingSummary.setSezeAvg(sezeAvg);
			}
	 		
	 		//huiweiAvg
	 		Double huiweiAvg = ratingSummary.getHuiweiAvg();
	 		Double inputHuiwei = ratingDetail.getHuiwei();
	 		//only handle those which input value exists.
	 		if (inputHuiwei != null && inputHuiwei > 0.0d) {
				
	 			// first time
	 			if(huiweiAvg == null || huiweiAvg == 0.0d) {
	 				huiweiAvg = inputHuiwei;
	 			} else {
	 				huiweiAvg = (huiweiAvg * size + inputHuiwei) / (size + 1);
	 			}
	 			
	 			ratingSummary.setHuiweiAvg(huiweiAvg);
			}
			
	 		//danning is a little different as others. those values which are 0 / null should be red wine.
	 		int whiteWineSize = 0;
	 		List<RatingDetail> ratingDetails = ratingSummary.getRatings();
	 		for (RatingDetail existedRatingDetail : ratingDetails) {
				if (existedRatingDetail instanceof RatingPoints) {
					if (((RatingPoints) existedRatingDetail).getDanning() != null && ((RatingPoints) existedRatingDetail).getDanning() > 0.0d) whiteWineSize ++;
				}
			}
	 		
	 		Double danningAvg = ratingSummary.getDanningAvg();
	 		Double inputDanning = ratingDetail.getDanning();
	 		//only handle those which input value exists.
	 		if (inputDanning != null && inputDanning > 0.0d) {
				
	 			
	 			if(danningAvg != null && danningAvg > 0.0d) {
	 				
	 				danningAvg = (danningAvg * size + inputDanning) / (whiteWineSize + 1);
	 			} else {
	 			// first time
	 				danningAvg = inputDanning;
	 			}
	 			
	 			ratingSummary.setDanningAvg(danningAvg);
			}
		}

		@Override
		public List<Product> readProductsByUserinput(String input) {
	 		
	 		SystemProperty systemProperty = systemPropertiesService.findSystemPropertyByName("TXJJ_SEARCH_SEPRATOR");
	 		String seprator = (systemProperty == null) ? " " : ((systemProperty.getValue() == null )? " " : systemProperty.getValue() );
	 		String[] keywords = input.split(seprator, -1);
	 		//query by keywords
	 		//the first not empty keyword is the supper one
	 		List<Product> products = new ArrayList<Product>();
	 		boolean isSupperWordFound = true;
	 		for (int i = 0; i < keywords.length; i++) {
	 			
	 			if (StringUtils.isBlank(keywords[i])) {
					continue;
				}
	 			
	 			if (isSupperWordFound) {
	 				products = wineDao.readProductsWithOneKeyword(keywords[i]);
	 				if (null == products || products.size() == 0)  return null;
	 				isSupperWordFound = false;
				} else {
					
					//filter the result by keyword
					products = filterWinesWithKeyword(keywords[0], products);
					
				}
	 			
			}
			return products;
		}
	 	
	 	
	 	private List<Product> filterWinesWithKeyword(String keyword, List<Product> products) {
	 		
	 		//temp results
	 		Set<Product> temProducts = new LinkedHashSet<Product>();
	 		//try the keyword as chateau name
	 		for(Product element : products) {
	 			
	 			if (element instanceof Wine) {
					
	 				String wineName = element.getName();
	 				//bad data in db.
	 				if (StringUtils.isBlank(wineName))  continue;
	 				
	 				//if found the chateau name likes the keyword, we select it as what we need.
	 				if (wineName.contains(keyword)) {
	 					temProducts.add(element);
					} 
	 				
	 				String chateauName = element.getManufacturer();
	 				
	 				//bad data in db.
	 				if (StringUtils.isBlank(chateauName))  continue;
	 				
	 				//if found the chateau name likes the keyword, we select it as what we need.
	 				if (chateauName.contains(keyword)) {
	 					temProducts.add(element);
					} 
	 					
				}
	 		}
	 		
	 		
	 		return new ArrayList<Product>(temProducts);		
		}


	public ReviewDetail saveReviewDetail(ReviewDetail reviewDetail) {
        return reviewDetailDao.saveReviewDetail(reviewDetail);
    }

	@Override
	public Product readTxjjProductByBarcode(String barcode, String type) {
		
		return commentsDao.readTxjjProductByBarcode(barcode, type);
	}
	
	@Override
	public Product readTxjjProductByBarcode(String barcode) {
		
		return commentsDao.readTxjjProductByBarcode(barcode);
	}

	public List<Product> readProductsByName(String searchName, int limit, int offset) {
		return commentsDao.readProductsByName(searchName, limit, offset);
	}
	
	public List<Product> readProductsByName(String searchName) {
		return commentsDao.readProductsByName(searchName);
	}

	@Transactional("blTransactionManager")
	public void removeUserCollection(UserCollection userCollection) {
		userCollectionDao.delete(userCollection);
	}

	@Override
	public UserCollection findUserCollectionByUserNameAndProductId(Long userId,
			Long productId) {
		
		return userCollectionDao.findUserCollectionByUserNameAndProductId(userId, productId);
	}
	
	
}
