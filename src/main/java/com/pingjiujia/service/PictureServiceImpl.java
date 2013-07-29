package com.pingjiujia.service;

import java.io.File;

import javax.annotation.Resource;

import org.broadleafcommerce.core.media.domain.Media;

import com.pingjiujia.dao.MediaDao;

public class PictureServiceImpl implements PictureService {

	@Resource(name = "mediaDao")
	MediaDao mediaDao;
	
	
	@Override
	public File readImageFileByMediaId(Long mediaId) {
		Media media = mediaDao.readMediaById(mediaId);
		
		
		return null;
	}

}
