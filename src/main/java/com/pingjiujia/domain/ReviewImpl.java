package com.pingjiujia.domain;

public class ReviewImpl implements Review {

	/**
	 * the customer's Id
	 */
	protected Long customerId;
	
	protected Long reviewDetailId;
	
	/**
	 * comments is saved in the table blc_reivew_detail
	 */
	protected String comments;
	
	/**
	 * customer's rating point is saved in the table blc_rating_detail
	 */
	protected Long ratingDetailId;
	
	protected Double ratingPoint;
	
	/**
	 * the summary point is saved in the table blc_rating_summary
	 */
	protected Double ratingSummaryId;
	
	protected Double averagePoints;

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getReviewDetailId() {
		return reviewDetailId;
	}

	public void setReviewDetailId(Long reviewDetailId) {
		this.reviewDetailId = reviewDetailId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Long getRatingDetailId() {
		return ratingDetailId;
	}

	public void setRatingDetailId(Long ratingDetailId) {
		this.ratingDetailId = ratingDetailId;
	}

	public Double getRatingPoint() {
		return ratingPoint;
	}

	public void setRatingPoint(Double ratingPoint) {
		this.ratingPoint = ratingPoint;
	}

	public Double getRatingSummaryId() {
		return ratingSummaryId;
	}

	public void setRatingSummaryId(Double ratingSummaryId) {
		this.ratingSummaryId = ratingSummaryId;
	}

	public Double getAveragePoints() {
		return averagePoints;
	}

	public void setAveragePoints(Double averagePoints) {
		this.averagePoints = averagePoints;
	}
	
}
