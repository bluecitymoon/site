package com.pingjiujia.service;

import java.util.List;
import java.util.Set;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.broadleafcommerce.core.rating.domain.ReviewDetail;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.broadleafcommerce.core.web.api.wrapper.ProductWrapper;
import org.broadleafcommerce.profile.core.domain.Customer;

import com.pingjiujia.admin.domain.UserCollection;
import com.pingjiujia.domain.ReviewImpl;
import com.pingjiujia.domain.client.ReviewsImpl;

public interface CommentsService {
	
	Set<Product> readProductsUserReviewed(String userName, int pageSize, int offset);
	
	Product readTxjjProductByProductId(Long productId);
	
	/**
	 * 
	 * @param barcode
	 * @param type 1d/2d
	 * @return
	 */
	Product readTxjjProductByBarcode(String barcode, String type);
	
	/**
	 * 
	 * @param barcode
	 * @param 
	 * @return
	 */
	Product readTxjjProductByBarcode(String barcode);
	
	ReviewDetail saveReviewDetail(ReviewDetail reviewDetail);
	
	RatingSummary reviewItem(Customer customer, ReviewsImpl reviews);
	
	List<Product> readProductsByUserinput(String input);
	
	List<Product> readProductsByName(String searchName, int limit, int offset);
	
	List<Product> readProductsByName(String searchName);
	
	 /**
     * Remove the {@code UserCollection} instance from the datastore
     *
     * @param UserCollection the UserCollection to remove
     */
    public void removeUserCollection(UserCollection userCollection);    
    
    /**
     * one user can collect one product once.
     * @return
     */
    public UserCollection findUserCollectionByUserNameAndProductId(Long userId, Long productId);
}
