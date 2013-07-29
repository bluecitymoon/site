package com.pingjiujia.domain.client;

import java.io.Serializable;

public interface Reviews extends Serializable{
	
	public String getComments();
	public void setComments(String comments);
	public Long getCustomerId();
	public void setCustomerId(Long customerId);
	public Double getRating();
	public void setRating(Double rating);
	public String getProductId();
	public void setProductId(String productId);
	public Double getXiangqi();
	public void setXiangqi(Double xiangqi);
	public Double getSuandu();
	public void setSuandu(Double suandu);
	public Double getDanning();
	public void setDanning(Double danning);
	public Double getJiuti() ;
	public void setJiuti(Double jiuti);
	public Double getSeze();
	public void setSeze(Double seze);
	public Double getHuiwei() ;
	public void setHuiwei(Double huiwei) ;
	public Double getCompositeScore() ;
	public void setCompositeScore(Double compositeScore) ;
}
