package com.pingjiujia.web.api.wrapper;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.broadleafcommerce.core.rating.domain.RatingDetail;
import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.broadleafcommerce.core.rating.domain.ReviewDetail;
import org.broadleafcommerce.core.web.api.wrapper.APIWrapper;
import org.broadleafcommerce.core.web.api.wrapper.BaseWrapper;
import org.broadleafcommerce.core.web.api.wrapper.CustomerWrapper;

import com.pingjiujia.admin.domain.RatingPoints;


@XmlRootElement(name = "ratingDetail")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class RatingDetailWrapper extends BaseWrapper implements APIWrapper<RatingDetail> {

	@XmlElement
	private Long reviewDetailId;
	
	@XmlElement
	protected Long  customerId;
	@XmlElement
	protected String  customerUserName;
	
//	@XmlElement
//	protected Double rating;
	
//	@XmlElement
//	protected CustomerWrapper customerWrapper;
	@XmlElement
	protected String reviewComments;
	@XmlElement
	protected Double compositeScore;
	@XmlElement
	protected Double xiangqi;
	@XmlElement
	protected Double suandu;
	@XmlElement
	protected Double danning;
	@XmlElement
	protected Double jiuti;
	@XmlElement
	protected Double seze;
	@XmlElement
	protected Double huiwei;
	
	@XmlElement
	protected Date ratingSubmittedDate;


	@Override
	public void wrap(RatingDetail model, HttpServletRequest request) {

		this.reviewDetailId = model.getId();
//		this.rating = model.getRating();
		this.ratingSubmittedDate = model.getRatingSubmittedDate();
		
		RatingSummary ratingSummary = model.getRatingSummary();
		List<ReviewDetail> reviewDetails = ratingSummary.getReviews();
		
		if (null != model.getCustomer()) {
//			customerWrapper = (CustomerWrapper) context.getBean(CustomerWrapper.class.getName());
//			customerWrapper.wrap(model.getCustomer(), request);
			customerId = model.getCustomer().getId();
			customerUserName = model.getCustomer().getUsername();
		}
		
		if (model instanceof RatingPoints) {
			this.xiangqi = ((RatingPoints) model).getXiangqi();
			this.suandu = ((RatingPoints) model).getSuandu();
			this.danning = ((RatingPoints) model).getDanning();
			this.jiuti = ((RatingPoints) model).getJiuti();
			this.seze = ((RatingPoints) model).getSeze();
			this.huiwei = ((RatingPoints) model).getHuiwei();
			this.compositeScore = ((RatingPoints) model).getCompositeScore();
			
		}
		
		this.reviewComments = getCommentsByCustomerId(reviewDetails, customerId);
		
	}
	
	private String getCommentsByCustomerId(List<ReviewDetail> reviewDetails, Long customerId) {
		
		for (ReviewDetail reviewDetail : reviewDetails) {
			
			if (reviewDetail.getCustomer().getId() == customerId) {
				return reviewDetail.getReviewText();
			}
		}
		
		return null;
	}

}
