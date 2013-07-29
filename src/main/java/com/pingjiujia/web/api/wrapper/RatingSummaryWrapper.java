package com.pingjiujia.web.api.wrapper;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.broadleafcommerce.core.rating.domain.RatingDetail;
import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.broadleafcommerce.core.rating.domain.ReviewDetail;
import org.broadleafcommerce.core.web.api.wrapper.APIWrapper;
import org.broadleafcommerce.core.web.api.wrapper.BaseWrapper;

import com.google.gwt.place.shared.PlaceHistoryHandler.Historian;
import com.pingjiujia.admin.domain.PJJRatingSummary;

@XmlRootElement(name = "ratingSummary")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class RatingSummaryWrapper extends BaseWrapper implements APIWrapper<RatingSummary>{
	
	@XmlElement
    private Long id;

	@XmlElement
    private String productId;

//	@XmlElement
//    private String ratingTypeStr;

	@XmlElement
    protected Double averageCompositeScore;

	@XmlElement
	@XmlElementWrapper(name = "ratingDetail")
    protected List<RatingDetailWrapper> ratings;

//	@XmlElement
//	@XmlElementWrapper(name = "review")
//    protected List<ReviewWraper> reviews;
	@XmlElement
	protected Double xiangqiAvg;
	@XmlElement
	protected Double suanduAvg;  
	@XmlElement
	protected Double jiutiAvg;
	@XmlElement
	protected Double sezeAvg;  
	@XmlElement
	protected Double huiweiAvg;
	@XmlElement
	protected Double danningAvg;
	
	@Override
	public void wrap(RatingSummary model, HttpServletRequest request) {
		
		this.id = model.getId();
		this.productId = model.getItemId();
	//	this.ratingTypeStr = model.getRatingType().getType();
		this.averageCompositeScore = model.getAverageRating();
		
		List<RatingDetail> ratingDetails = model.getRatings();
		
		if (null != ratingDetails && !ratingDetails.isEmpty()) {
			ratings = new ArrayList<RatingDetailWrapper>();
			
			for (RatingDetail ratingDetail : ratingDetails) {
				RatingDetailWrapper ratingDetailWrapper = (RatingDetailWrapper) context.getBean(RatingDetailWrapper.class.getName());
				ratingDetailWrapper.wrap(ratingDetail, request);
				ratings.add(ratingDetailWrapper);
			}
		}
//		
//		List<ReviewDetail> reviewDetails = model.getReviews();
//		
//		if (null != reviewDetails && !reviewDetails.isEmpty()) {
//			reviews = new ArrayList<ReviewWraper>();
//			for(ReviewDetail reviewDetail : reviewDetails) {
//				ReviewWraper reviewWraper = (ReviewWraper) context.getBean(ReviewWraper.class.getName());
//				reviewWraper.wrap(reviewDetail, request);
//				reviews.add(reviewWraper);
//			}
//		}
		
		if (model instanceof PJJRatingSummary) {
			this.xiangqiAvg = ((PJJRatingSummary) model).getXiangqiAvg();
			this.suanduAvg = ((PJJRatingSummary) model).getSuanduAvg();
			this.jiutiAvg = ((PJJRatingSummary) model).getJiutiAvg();
			this.sezeAvg = ((PJJRatingSummary) model).getSezeAvg();
			this.huiweiAvg = ((PJJRatingSummary) model).getHuiweiAvg();
			this.danningAvg = ((PJJRatingSummary) model).getDanningAvg();
		}
	}

	@Override
	public String toString() {
		return "RatingSummaryWrapper [id=" + id + ", productId=" + productId
				+ ", averageCompositeScore=" + averageCompositeScore
				+ ", ratings=" + ratings + ", xiangqiAvg=" + xiangqiAvg
				+ ", suanduAvg=" + suanduAvg + ", jiutiAvg=" + jiutiAvg
				+ ", sezeAvg=" + sezeAvg + ", huiweiAvg=" + huiweiAvg
				+ ", danningAvg=" + danningAvg + "]";
	}

	

}
