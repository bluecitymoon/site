package com.pingjiujia.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.springframework.stereotype.Repository;

import com.pingjiujia.admin.domain.WineImpl;

@Repository("txjjWineDao")
public class WineDaoImpl implements WineDao {
	
	@PersistenceContext(unitName="blPU")
    protected EntityManager em;

	/**
	 * try to read the wine information by chateau's name.
	 */
	@Override
	public List<Product> readProductsByChateauName(String chateau) {
		
		// Set up the criteria query that specifies we want to return Products
    	CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
		Root<WineImpl> product = criteria.from(WineImpl.class);
		criteria.select(product);
		
		criteria.where(builder.like(product.get("name").as(String.class), "% "+ chateau + "%" ));
		
		List<Product> result = (List<Product>) em.createQuery(criteria).getResultList();
		
		return result;
	}

	@Override
	public List<Product> readProductsByWineName(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean queryIfExistsWithSQL(String name, String sql) {
		
		Query query = em.createNativeQuery(sql);
		query.setParameter(1, "%" + name + "%");
		List result = query.getResultList();
		
		return (result != null && result.size() > 0);
	}

	@Override
	public List<Product> readProductsByConditions(String chateau, String name,
			String referenceYear) {
		
		return null;
	}

	@Override
	public List<Product> readProductsWithOneKeyword(String supperKeyword) {
		if (StringUtils.isEmpty(supperKeyword)) {
			return null;
		}
		// Set up the criteria query that specifies we want to return Products
    	CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
		Root<WineImpl> product = criteria.from(WineImpl.class);
		
		// We also want to filter on attributes from sku and productAttributes
		Join<Product, Sku> sku = product.join("defaultSku");
		criteria.select(product);
		
		Predicate namePredicate          =   builder.like(sku.get("name").as(String.class), '%'+ supperKeyword + '%' );
		Predicate manufacturePredicate   =   builder.like(product.get("manufacturer").as(String.class), '%'+ supperKeyword + '%' );
		Predicate referenceYearPredicate =   builder.like(product.get("referenceYear").as(String.class), '%' + supperKeyword + '%' );
		
		criteria.where( builder.or(namePredicate, manufacturePredicate, referenceYearPredicate) );
		
		List<Product> result = (List<Product>) em.createQuery(criteria).getResultList();
		
		return result;
	}

//	@Override
//	public List<Product> readProductsByIds(List<Long> productIds) {
//		// TODO Auto-generated method stub
//		return super.readProductsByIds(productIds);
//	}
//
//	@Override
//	public List<Product> readProductsByName(String searchName, int limit,
//			int offset) {
//		TypedQuery<Product> query = em.createNamedQuery("BC_READ_PRODUCTS_BY_NAME", Product.class);
//        query.setParameter("name", "%" + searchName + '%');
//        query.setFirstResult(offset);
//        query.setMaxResults(limit);
//        
//
//        return query.getResultList();
//	}
	
	

}
