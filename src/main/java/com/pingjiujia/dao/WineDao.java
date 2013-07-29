package com.pingjiujia.dao;

import java.util.List;

import org.broadleafcommerce.core.catalog.domain.Product;

public interface WineDao {
	
	public static final String CHECK_IF_CHATEAU_SQL = "select distinct manufacture from blc_product t where t.MANUFACTURE like ?1 ";
	
	List<Product> readProductsByChateauName(String chateau);
	
	List<Product> readProductsByWineName(String name);
	
	List<Product> readProductsByConditions(String chateau, String name, String referenceYear);
	
	/**
	 * this keyword may be a manufacture name, 
	 * @param supperKeyword
	 * @return
	 */
	List<Product> readProductsWithOneKeyword(String supperKeyword);

}
