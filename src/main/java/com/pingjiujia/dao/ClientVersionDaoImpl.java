package com.pingjiujia.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import com.pingjiujia.admin.domain.ClientVersion;

@Repository("txjjClientVersionDao")
public class ClientVersionDaoImpl implements ClientVersionDao {
	
	@PersistenceContext(unitName="blPU")
    protected EntityManager em;
	
	@Resource(name="blEntityConfiguration")
	protected EntityConfiguration entityConfiguration;
	
	@Override
	public ClientVersion findLastestVersion() {
		String sql = "SELECT clientVersion FROM com.pingjiujia.admin.domain.ClientVersion clientVersion";
		Query query = em.createQuery(sql);
		query.setHint(QueryHints.HINT_CACHEABLE, true);
	    
        List<ClientVersion> resultList = query.getResultList();
        if (resultList !=null && resultList.size() > 0) {
			return resultList.get(0);
		}
        
        return null;
	}

}
