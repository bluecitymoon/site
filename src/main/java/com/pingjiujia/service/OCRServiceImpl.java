package com.pingjiujia.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.broadleafcommerce.common.config.domain.SystemProperty;
import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.springframework.stereotype.Service;


@Service("ocrservice")
public class OCRServiceImpl implements OCRService {
	private Logger logger = Logger.getLogger(OCRServiceImpl.class);

	@Resource( name="blSystemPropertiesService" )
	SystemPropertiesService systemPropertiesService;

	@Override
	/**
	 * 
	 * @param absolutImageFilePath the absolute path of the image file.
	 * 
	 * @return
	 */
	public List<String> readContentFromImage(String absolutImageFilePath, String fileName, String currentMillis) {

		SystemProperty ocrInstallationDir = systemPropertiesService.findSystemPropertyByName("TXJJ_OCR_INSTALLATION_DIR");
		
		String ocrInstallationDirName = "";
		if (null == ocrInstallationDir || StringUtils.isEmpty(ocrInstallationDir.getValue())) {
			logger.error("ocr installation directory not cofigured.");
		} else {
			ocrInstallationDirName = ocrInstallationDir.getValue();
		}
		
		SystemProperty pathToStoreOCRparseResultDir = systemPropertiesService.findSystemPropertyByName("TXJJ_OCR_PARSE_RESULT_FILE_PATH");
		
		String prefixName = fileName.split("\\.", -1)[0];
		
		String parseResultFile = pathToStoreOCRparseResultDir.getValue() + File.separator + currentMillis + "_" + prefixName;
		
		
		Properties props=System.getProperties();
		String osName = props.getProperty("os.name");
		
		String command = "";
		if (osName.equalsIgnoreCase("Linux")) {
			//linux
			command = "tesseract " + absolutImageFilePath + " " + parseResultFile + " -l eng";
		} else {
			//windows
			 command = "cmd.exe /c "+ ocrInstallationDirName + File.separator +"tesseract.exe " + absolutImageFilePath + " " + parseResultFile + " -l eng";
		}
		//
		
		int termination = -1;
		try {
			Process ocrpProcess = Runtime.getRuntime().exec(command);
			termination = ocrpProcess.waitFor();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		List<String> result = null;
		
		if (termination == 0) {
			String pathname = parseResultFile + ".txt";
			File file = new File(pathname);
			
			try {
				BufferedReader bReader = new BufferedReader(new FileReader(file));
				String str;
				while ((str = bReader.readLine()) != null) {
					//remove the messy words
				    char[] elements = str.trim().toCharArray();
				    char[] tempResult = new char[elements.length];
				    int index = 0;
				    for (int i = 0; i < elements.length; i++) {
						if (Character.isLetterOrDigit(elements[i])) {
							tempResult[index] = elements[i];
							index ++;
						}
					}
				    if (tempResult.length > 0) {
				    	str = new String(tempResult);
					}
					if (str.trim() == "" || str.length() < 2) {
						continue;
					}
					
					if ( null == result) {
						result = new ArrayList<String>();
					}
					String trimStr = str.trim();
					result.add(trimStr);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		return result;
	}

}
