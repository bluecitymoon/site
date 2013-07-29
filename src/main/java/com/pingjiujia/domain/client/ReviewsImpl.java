package com.pingjiujia.domain.client;

import javax.persistence.Column;

public class ReviewsImpl implements Reviews {
	protected String comments;
	protected Long customerId;
	protected Double rating;
	protected String productId;
	protected Double xiangqi;
	protected Double suandu;
	protected Double danning;
	protected Double jiuti;
	protected Double seze;
	protected Double huiwei;
	//use it as the rating points
	protected Double compositeScore;
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
	public Double getXiangqi() {
		return xiangqi;
	}
	public void setXiangqi(Double xiangqi) {
		this.xiangqi = xiangqi;
	}
	public Double getSuandu() {
		return suandu;
	}
	public void setSuandu(Double suandu) {
		this.suandu = suandu;
	}
	public Double getDanning() {
		return danning;
	}
	public void setDanning(Double danning) {
		this.danning = danning;
	}
	public Double getJiuti() {
		return jiuti;
	}
	public void setJiuti(Double jiuti) {
		this.jiuti = jiuti;
	}
	public Double getSeze() {
		return seze;
	}
	public void setSeze(Double seze) {
		this.seze = seze;
	}
	public Double getHuiwei() {
		return huiwei;
	}
	public void setHuiwei(Double huiwei) {
		this.huiwei = huiwei;
	}
	public Double getCompositeScore() {
		return compositeScore;
	}
	public void setCompositeScore(Double compositeScore) {
		this.compositeScore = compositeScore;
	}
	@Override
	public String toString() {
		return "ReviewsImpl [comments=" + comments + ", customerId="
				+ customerId + ", rating=" + rating + ", productId="
				+ productId + ", xiangqi=" + xiangqi + ", suandu=" + suandu
				+ ", danning=" + danning + ", jiuti=" + jiuti + ", seze="
				+ seze + ", huiwei=" + huiwei + ", compositeScore="
				+ compositeScore + "]";
	}
	
}
