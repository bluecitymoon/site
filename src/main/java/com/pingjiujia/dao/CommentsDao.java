package com.pingjiujia.dao;

import java.util.List;

import javax.annotation.Nonnull;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.rating.domain.RatingSummary;

public interface CommentsDao {
	
	/**
	 * read all products information that reviewed by the specified user.
	 * @param userName
	 * @param pageSize
	 * @return
	 */
	List<RatingSummary> readRatingSummaryByCustomerName(String userName, int pageSize, int offset);

	Product readTxjjProductByBarcode(String barcode, String type);
	
	Product readTxjjProductByBarcode(String barcode);
	
	/**
     * Find all {@code Product} instances whose name starts with
     * or is equal to the passed in search parameter
     *
     * @param searchName the partial or whole name to match
     * @return the list of product instances that were search hits
     */
    @Nonnull
    public List<Product> readProductsByName(@Nonnull String searchName, int limit, int offset);

	List<Product> readProductsByName(@Nonnull String searchName);
	
}
