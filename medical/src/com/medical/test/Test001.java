package com.medical.test;

import java.io.File;
import java.time.Period;

import com.medical.util.FTPUtil;

public class Test001 {

	public static void main(String[] args) {
		FTPUtil ftputil =new FTPUtil("127.0.0.1",21,"bianshuai","bianshuai", "/photo/");
/*		try {
			ftputil.openConnect();
		      File file = new File("F:\\lme\\AUTORUN.APM");    
		      ftputil.upload(file); 
		      System.out.println("ÉÏ´«½áÊø");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		try {
			ftputil.openConnect();
			ftputil.downloadFile("/bian/","f://bianshuai");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
