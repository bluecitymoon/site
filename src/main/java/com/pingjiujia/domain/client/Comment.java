package com.pingjiujia.domain.client;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Comment implements Serializable{
	
	@XmlElement public String comments;
	@XmlElement public Long	productId;
	@XmlElement public Long customerId;
	@XmlElement public Double rating;
	
	
	public Comment() {
	}

	public Comment(String comments, Long productId, Long customerId,
			Double rating) {
		super();
		this.comments = comments;
		this.productId = productId;
		this.customerId = customerId;
		this.rating = rating;
	}

	@Override
	public String toString() {
		return "Comment [comments=" + comments + ", productId=" + productId
				+ ", customerId=" + customerId + ", rating=" + rating + "]";
	}
	
	

}
