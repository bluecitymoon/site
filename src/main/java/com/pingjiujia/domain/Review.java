package com.pingjiujia.domain;

import java.io.Serializable;

public interface Review extends Serializable {

	public Long getCustomerId() ;

	public void setCustomerId(Long customerId);
	public Long getReviewDetailId() ;

	public void setReviewDetailId(Long reviewDetailId) ;

	public String getComments() ;

	public void setComments(String comments) ;

	public Long getRatingDetailId() ;

	public void setRatingDetailId(Long ratingDetailId);

	public Double getRatingPoint() ;

	public void setRatingPoint(Double ratingPoint);

	public Double getRatingSummaryId() ;

	public void setRatingSummaryId(Double ratingSummaryId);

	public Double getAveragePoints() ;

	public void setAveragePoints(Double averagePoints);
	
}
