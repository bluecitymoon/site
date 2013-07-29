package com.pingjiujia.dao;



import java.util.List;

import com.pingjiujia.admin.domain.UserCollection;

public interface UserCollectionDao {
	 /**
     * Persist a {@code UserCollection} instance to the datastore
     *
     * @param UserCollection the UserCollection to persist
     * @return the saved state of the passed in UserCollection
     */
    public UserCollection save(UserCollection UserCollection);
    
	 /**
     * Remove the {@code UserCollection} instance from the datastore
     *
     * @param UserCollection the UserCollection to remove
     */
    public void delete(UserCollection userCollection);    

    /**
     * Create a new {@code UserCollection} instance. The system will use the configuration in
     * {@code /BroadleafCommerce/core/BroadleafCommerceFramework/src/main/resources/bl-framework-applicationContext-entity.xml}
     * to determine which polymorphic version of {@code UserCollection} to instantiate. To make Broadleaf instantiate your
     * extension of {@code UserCollection} by default, include an entity configuration bean in your application context xml similar to:
     * <p>
     * {@code
     *     <bean id="blEntityConfiguration" class="org.broadleafcommerce.common.persistence.EntityConfiguration">
	 *	        <property name="entityContexts">
	 *		        <list>
	 *			        <value>classpath:myCompany-applicationContext-entity.xml</value>
	 *		        </list>
	 *	        </property>
	 *      </bean>
     * }
     * </p>
     * Declare the same key for your desired entity in your entity xml that is used in the Broadleaf entity xml, but change the value to the fully
     * qualified classname of your entity extension.
     *
     * @return a {@code UserCollection} instance based on the Broadleaf entity configuration.
     */
    public UserCollection create();
    
    /**
     * read the user collections by user name.
     * 
     * @param userName
     * @return
     */
    public List<UserCollection> findUserCollectionByUserName(String userName);
    
    /**
     * read the user collections by user id.
     * 
     * @param userName
     * @return
     */
    public List<UserCollection> findUserCollectionByUserId(Long userId);
    
    /**
     * 
     * @param userCollectionId
     * @return
     */
    public UserCollection findUserCollectionById(Long userCollectionId);
    
    /**
     * one user can collect one product once.
     * @return
     */
    public UserCollection findUserCollectionByUserNameAndProductId(Long userId, Long productId);
}
