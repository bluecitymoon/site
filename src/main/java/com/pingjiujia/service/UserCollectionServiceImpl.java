package com.pingjiujia.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pingjiujia.admin.domain.UserCollection;
import com.pingjiujia.dao.UserCollectionDao;

@Service("txjjUserCollectionService")
public class UserCollectionServiceImpl implements UserCollectionService {

	@Resource(name = "blUserCollectionDao")
	private UserCollectionDao userCollectionDao;
	
	@Override
	@Transactional("blTransactionManager")
	public UserCollection save(UserCollection userCollection) {
		return userCollectionDao.save(userCollection);
	}

	@Override
	@Transactional("blTransactionManager")
	public void delete(UserCollection userCollection) {
		userCollectionDao.delete(userCollection);
	}

	@Override
	public List<UserCollection> findUserCollectionByUserName(String userName) {
		return userCollectionDao.findUserCollectionByUserName(userName);
	}

}
