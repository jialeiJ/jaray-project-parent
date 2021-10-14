package com.vienna.jaray.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 上传服务类
 * @author Jaray
 */
@Slf4j
@Data
public class UploadService {
	private File file;
	private String imageFileName;
	private String uploadPath;
	
	public String uploadImage(){
		log.info("file: {}", getFile());
		try(InputStream is = new FileInputStream(getFile());
			OutputStream os = new FileOutputStream(getUploadPath() + File.separator + getImageFileName());){

			byte[] buffer = new byte[1024];
			int len = 0;
			
			while((len = is.read(buffer))>0){
				os.write(buffer);
			}
		}catch(Exception e){
			log.error("图片上传异常", e);
		}
		return getUploadPath() + File.separator + getImageFileName();
	}
}
