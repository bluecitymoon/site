package com.pingjiujia.web.api.wrapper;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.broadleafcommerce.core.web.api.wrapper.APIUnwrapper;
import org.broadleafcommerce.core.web.api.wrapper.APIWrapper;
import org.broadleafcommerce.core.web.api.wrapper.BaseWrapper;
import org.springframework.context.ApplicationContext;

import com.pingjiujia.domain.client.Reviews;
import com.pingjiujia.domain.client.ReviewsImpl;

@XmlRootElement(name = "commentsMap")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ReviewsWrapper extends BaseWrapper implements APIWrapper<Reviews>, APIUnwrapper<Reviews>{
	
	@XmlElement
	protected String comments;
	
	@XmlElement
	protected Long customerId;
	
	@XmlElement
	protected Double rating;
	
	@XmlElement
	protected String productId;

	@Override
	public void wrap(Reviews model, HttpServletRequest request) {
		this.comments = model.getComments();
		this.customerId = model.getCustomerId();
		this.rating = model.getRating();
		this.productId = model.getProductId();
	}

	@Override
	public Reviews unwrap(HttpServletRequest request, ApplicationContext context) {
		
		Reviews reviews = new ReviewsImpl();
		reviews.setComments(comments);
		reviews.setCustomerId(customerId);
		reviews.setRating(rating);
		reviews.setProductId(productId);
		
		return reviews;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	

}
