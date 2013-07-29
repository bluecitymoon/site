package com.pingjiujia.web.api.wrapper;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.broadleafcommerce.core.rating.domain.ReviewDetail;
import org.broadleafcommerce.core.web.api.wrapper.APIWrapper;
import org.broadleafcommerce.core.web.api.wrapper.BaseWrapper;
import org.broadleafcommerce.core.web.api.wrapper.CustomerWrapper;


@XmlRootElement(name = "review")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ReviewWraper extends BaseWrapper implements APIWrapper<ReviewDetail>{

	@XmlElement
	protected CustomerWrapper customerWrapper;
	
	/**
	 * comments is saved in the table blc_reivew_detail
	 */
	@XmlElement
	protected String comments;
	
	/**
	 * the customer's review point
	 */
	@XmlElement
	protected Double ratingPoint;
	
	@XmlElement
	protected Date reviewSubmitDate;
	
	
	/**
	 * average point
	 */
	@XmlElement
	protected Double averagePoints;
	
	@Override
	public void wrap(ReviewDetail model, HttpServletRequest request) {
		
		if (null != model.getCustomer()) {
			customerWrapper = (CustomerWrapper) context.getBean(CustomerWrapper.class.getName());
			customerWrapper.wrap(model.getCustomer(), request);
		}
		
		this.comments = model.getReviewText();
		this.reviewSubmitDate = model.getReviewSubmittedDate();
	}

}
