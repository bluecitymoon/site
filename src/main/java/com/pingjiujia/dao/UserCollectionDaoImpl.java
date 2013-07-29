package com.pingjiujia.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import com.pingjiujia.admin.domain.UserCollection;


@Repository("blUserCollectionDao")
public class UserCollectionDaoImpl implements UserCollectionDao {

	@PersistenceContext(unitName="blPU")
    protected EntityManager em;
	
	@Resource(name="blEntityConfiguration")
	protected EntityConfiguration entityConfiguration;

	@Override
	public UserCollection save(UserCollection userCollection) {
		return em.merge(userCollection);
	}

	@Override
	public void delete(UserCollection userCollection) {
		if (!em.contains(userCollection)) {
			userCollection = findUserCollectionById(userCollection.getId());
		}
		
		em.remove(userCollection);
	}

	@Override
	public UserCollection create() {
		return (UserCollection) entityConfiguration.createEntityInstance(UserCollection.class.getName());
	}

	@Override
	public List<UserCollection> findUserCollectionByUserName(String userName) {
		String sql = "SELECT userCollection FROM com.pingjiujia.admin.domain.UserCollection userCollection WHERE userCollection.userName = :name";
		Query query = em.createQuery(sql);
		query.setParameter("name",  userName );
		query.setHint(QueryHints.HINT_CACHEABLE, true);
	    
        return query.getResultList();
	}

	@Override
	public List<UserCollection> findUserCollectionByUserId(Long userId) {
		String sql = "SELECT userCollection FROM com.pingjiujia.admin.domain.UserCollection userCollection WHERE userCollection.customerId = :userId";
		Query query = em.createQuery(sql);
		query.setParameter("userId",  userId );
		query.setHint(QueryHints.HINT_CACHEABLE, true);
	    
        return query.getResultList();
	}

	@Override
	public UserCollection findUserCollectionById(Long userCollectionId) {
		return em.find(UserCollection.class, userCollectionId);
	}

	@Override
	public UserCollection findUserCollectionByUserNameAndProductId(Long userId, Long productId) {
		
		String sql = "SELECT userCollection FROM com.pingjiujia.admin.domain.UserCollection userCollection WHERE userCollection.customerId = :userId and userCollection.productId = :productId";
		Query query = em.createQuery(sql);
		query.setParameter("userId",  userId );
		query.setParameter("productId", productId);
		query.setHint(QueryHints.HINT_CACHEABLE, true);
	    
		UserCollection userCollection = null;
		try {
			userCollection = (UserCollection) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
		return userCollection;
	}
	
}
