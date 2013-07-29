package com.pingjiujia.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.core.media.domain.Media;
import org.broadleafcommerce.core.media.domain.MediaImpl;
import org.springframework.stereotype.Repository;

@Repository("mediaDao")
public class MediaDaoImpl implements MediaDao {
	@PersistenceContext(unitName="blPU")
	protected EntityManager em;
	 
	@Override
	public Media readMediaById(Long mediaId) {
		return em.find(MediaImpl.class, mediaId);
	}

}
