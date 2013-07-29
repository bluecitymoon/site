package com.pingjiujia.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

@Repository("txjjCommentsDao")
public class CommentsDaoImpl implements CommentsDao {

	// private String base_sql =
	// "select distinct w.abv from blc_customer c , blc_rating_detail d , blc_rating_summary s, blc_product p, txjj_wine w "
	// + " where c.customer_id = d.customer_id "
	// + " and s.rating_summary_id = d.rating_summary_id "
	// + " and p.product_id = s.item_id "
	// + " and p.product_id = w.product_id "
	// + " and c.USER_NAME = ";

	@PersistenceContext(unitName = "blPU")
	protected EntityManager em;
	
	@Override
	public List<RatingSummary> readRatingSummaryByCustomerName(String userName, int pageSize, int offset) {
		String qlquery = "SELECT ratingSummary FROM org.broadleafcommerce.core.rating.domain.ReviewDetail reviewDetail " +
				"JOIN reviewDetail.customer customer " +
				"JOIN reviewDetail.ratingSummary ratingSummary " +
				"WHERE customer.username = :name";// +
				//" reviewDetail.reviewStatus = :status";
		
		Query query = em.createQuery(qlquery);
		query.setParameter("name", userName);
		//query.setParameter("status", "PENDING");//collect it
		query.setHint(QueryHints.HINT_CACHEABLE, true);
	    query.setMaxResults(pageSize);
	    query.setFirstResult(offset);
	    
		return (List<RatingSummary>) query.getResultList();
	}


	@Override
	public Product readTxjjProductByBarcode(String barcode, String type) {
		
		String qlString = "SELECT wine FROM com.pingjiujia.admin.domain.Wine wine WHERE wine.barCodeOneD = :barcode";
		
		Query query = em.createQuery(qlString);
		query.setParameter("barcode", barcode);
		query.setHint(QueryHints.HINT_CACHEABLE, true);
		
		List result = query.getResultList();
		
		if (result != null && !result.isEmpty()) {
			return (Product) result.get(0);
		}
		
		return null;
	}
	
	@Override
	public Product readTxjjProductByBarcode(String barcode) {
		
		String qlString = "SELECT wine FROM com.pingjiujia.admin.domain.Wine wine WHERE wine.barCodeOneD = :barcode or wine.barCodeTwoD = :barcode";
		
		Query query = em.createQuery(qlString);
		query.setParameter("barcode", barcode);
		query.setHint(QueryHints.HINT_CACHEABLE, true);
		
		List result = query.getResultList();
		
		if (result != null && !result.isEmpty()) {
			return (Product) result.get(0);
		}
		
		return null;
	}

//	@Override
//	public Product readProductById(Long productId) {
//		
//		Product product =  (Product) em.find(ProductImpl.class, productId);
//		Long itemId = product.getId();
//		
//		RatingSummary ratingSummary = 
//		     
//		return null;
//	}
//	
	@Override
    public List<Product> readProductsByName(String searchName, int limit, int offset) {
		String sql = "SELECT product FROM org.broadleafcommerce.core.catalog.domain.Product product WHERE product.defaultSku.name LIKE :name";
		Query query = em.createQuery(sql);
		query.setParameter("name", '%' + searchName + '%');
		query.setHint(QueryHints.HINT_CACHEABLE, true);
		query.setFirstResult(offset);
	    query.setMaxResults(limit);
	    
        return query.getResultList();
    }
	
	@Override
    public List<Product> readProductsByName(String searchName) {
		String sql = "SELECT product FROM org.broadleafcommerce.core.catalog.domain.Product product WHERE product.defaultSku.name LIKE :name";
		Query query = em.createQuery(sql);
		query.setParameter("name", '%' + searchName + '%');
		query.setHint(QueryHints.HINT_CACHEABLE, true);
		
        return query.getResultList();
    }
}
