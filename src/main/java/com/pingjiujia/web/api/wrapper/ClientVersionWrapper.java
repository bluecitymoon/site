package com.pingjiujia.web.api.wrapper;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlElement;

import org.broadleafcommerce.core.web.api.wrapper.APIWrapper;
import org.broadleafcommerce.core.web.api.wrapper.BaseWrapper;

import com.pingjiujia.admin.domain.ClientVersion;

public class ClientVersionWrapper extends BaseWrapper implements APIWrapper<ClientVersion> {
	
	@XmlElement
	protected String version;
	
	@XmlElement
	protected String androidURL;
	
	@XmlElement
	protected String iphoneURL;
	
	@Override
	public void wrap(ClientVersion model, HttpServletRequest request) {
		
		version = model.getVersion();
		androidURL = model.getAndroidURL();
		iphoneURL = model.getIphoneURL();
		
	}

}
