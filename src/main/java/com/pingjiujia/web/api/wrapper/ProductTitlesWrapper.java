package com.pingjiujia.web.api.wrapper;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.rating.domain.ReviewDetail;
import org.broadleafcommerce.core.web.api.wrapper.APIWrapper;
import org.broadleafcommerce.core.web.api.wrapper.BaseWrapper;
import org.broadleafcommerce.profile.core.domain.Customer;

import com.pingjiujia.admin.domain.PJJRatingSummary;
import com.pingjiujia.admin.domain.WineImpl;

@XmlRootElement(name = "wine_overview")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ProductTitlesWrapper extends BaseWrapper implements APIWrapper<Product> {

	@XmlElement
	protected Long productId;
	@XmlElement
	protected String name;
	@XmlElement
	protected String myScore;
	@XmlElement
	protected String myComments;
	@XmlElement
	protected List<String> mediaUrls;
	@XmlElement
	protected Date myReviewDate;
	
//	
//	@XmlElement
//	protected String rewards;  
//	
//	@XmlElement
//	protected String price;  
//	
//	@XmlElement
//	protected String currency;
//	
//	@XmlElement
//	protected String wineMaker;
//	
//	@XmlElement
//	protected String grap;
//	
//	@XmlElement
//	protected String type;
//	
//	@XmlElement
//	protected String producingArea;
//	
//	@XmlElement
//	protected String level;
//	
//	@XmlElement
//	protected String abv;
//	
//	@XmlElement
//	protected String body;
//	
//	@XmlElement
//	protected String taste;
//	
//	@XmlElement
//	protected String odour;
//	
//	@XmlElement
//	protected String barCodeOneD;
//	
//	@XmlElement
//	protected String barCodeTwoD;
//	
//	@XmlElement
//	protected Integer ratingCount;
//	
//	@XmlElement
//	protected Integer reviewCount;
//	
//	@XmlElement
//	protected Double averageRating;
//	
	
//	@XmlElement
//	protected Double xiangqiAvg;
//	
//	@XmlElement
//	protected Double suanduAvg;  
//	
//	@XmlElement
//	protected Double jiutiAvg;
//	
//	@XmlElement 
//	protected Double sezeAvg;  
//	
//	@XmlElement
//	protected Double huiweiAvg;
//	
//	@XmlElement
//	protected Double danningAvg;
	
	
	@Override
    public void wrap(Product model, HttpServletRequest request) {
        //First, call the super method to get the default behavior
        //super.wrap(model, request);
        //Next, cast the product passed in to HotSauce and use it to set the Scoville Units
//        this.abv = ((WineImpl)model).getAbv();
//        this.referenceYear = ((WineImpl)model).getReferenceYear();
		this.productId = model.getId();
		this.name = model.getName();
        this.myScore = ((WineImpl)model).getCompositeScore();
//        this.rewards = ((WineImpl)model).getRewards();
//        this.wineMaker = ((WineImpl)model).getWineMaker();
//        this.grap = ((WineImpl)model).getGrap();
//        this.type = ((WineImpl)model).getType();
//        this.producingArea= ((WineImpl)model).getProducingArea();
//        this.level = ((WineImpl)model).getLevel();
//        this.body = ((WineImpl)model).getBody();
//        this.taste = ((WineImpl)model).getTaste();
//        this.odour =  ((WineImpl)model).getOdour();
//        this.barCodeOneD = ((WineImpl)model).getBarCodeOneD();
//        this.barCodeTwoD = ((WineImpl)model).getBarCodeTwoD();
//        this.ratingCount = ((WineImpl)model).getRatingCount();
//        this.reviewCount = ((WineImpl)model).getReviewCount();
//        this.averageRating = ((WineImpl)model).getAverageRating();	
        this.mediaUrls = ((WineImpl)model).getMediaIds();
        
//        if (null != ((WineImpl) model).getDefaultSku() && null != ((WineImpl) model).getDefaultSku().getSalePrice()) {
//        	
//        	 this.price =((WineImpl) model).getDefaultSku().getSalePrice().getAmount().toString();
//             this.currency = ((WineImpl) model).getDefaultSku().getSalePrice().getCurrency().toString();
//		}
        PJJRatingSummary ratingSummary = ((WineImpl)model).getPjjRatingSummary();
        if (ratingSummary != null) {
//        	this.suanduAvg = ratingSummary.getSuanduAvg();  
//        	this.xiangqiAvg = ratingSummary.getXiangqiAvg();
//    		this.jiutiAvg = ratingSummary.getJiutiAvg();
//    		this.sezeAvg = ratingSummary.getSezeAvg();
//    		this.huiweiAvg = ratingSummary.getHuiweiAvg();
//    		this.danningAvg = ratingSummary.getDanningAvg();	
//    		if (this.ratingCount == null) {
//    			ratingCount = ratingSummary.getNumberOfRatings();
//			}
//    		if (this.reviewCount == null) {
//				reviewCount = ratingSummary.getNumberOfReviews();
//			}
        	List<ReviewDetail> reviewDetails = ratingSummary.getReviews();
        	String specifiComment = ((WineImpl)model).getSpecifiComment();
        	for (ReviewDetail reviewDetail : reviewDetails) {
				Customer customer = reviewDetail.getCustomer();
				if (customer.getUsername().equals(specifiComment)) {
					this.myComments = reviewDetail.getReviewText();
					this.myReviewDate = reviewDetail.getReviewSubmittedDate();
					break;
				}
			}
    		if (this.myScore == null) {
    			myScore = ratingSummary.getAverageRating() == null ? null : String.valueOf(ratingSummary.getAverageRating());
			}
    		
    		
		}
  
    }
}
