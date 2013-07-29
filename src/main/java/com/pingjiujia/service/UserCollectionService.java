package com.pingjiujia.service;

import java.util.List;

import com.pingjiujia.admin.domain.UserCollection;

public interface UserCollectionService {

	 /**
     * Persist a {@code UserCollection} instance to the datastore
     *
     * @param UserCollection the UserCollection to persist
     * @return the saved state of the passed in UserCollection
     */
    public UserCollection save(UserCollection userCollection);
    
	 /**
     * Remove the {@code UserCollection} instance from the datastore
     *
     * @param UserCollection the UserCollection to remove
     */
    public void delete(UserCollection userCollection);    
    /**
     * read the user collections by user name.
     * 
     * @param userName
     * @return
     */
    public List<UserCollection> findUserCollectionByUserName(String userName);
}
