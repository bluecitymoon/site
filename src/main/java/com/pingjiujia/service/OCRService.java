package com.pingjiujia.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface OCRService {

	/**
	 * 
	 * @param absolutImageFilePath
	 * @return
	 */
	public List<String> readContentFromImage(String absolutImageFilePath, String fileName, String currentMillis);
}
