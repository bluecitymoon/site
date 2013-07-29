package com.pingjiujia.web.api.endpoint;

import java.io.IOException;
import java.util.Properties;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Properties props=System.getProperties();
		String osName = props.getProperty("os.name");
		
		System.out.println(osName);
		
		
		String command = "tesseract " + "/home/isaac/1361512780123_temp.jpg" + " " + "/home/isaac/result" + " -l eng";
		
		int termination = -1;
		try {
			Process ocrpProcess = Runtime.getRuntime().exec(command);
			termination = ocrpProcess.waitFor();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
