package com.vienna.jaray.controller.system;


import com.vienna.jaray.annotation.LogAnnotation;
import com.vienna.jaray.common.ResponseResult;
import com.vienna.jaray.entity.ImageInfo;
import com.vienna.jaray.service.common.Mark;
import com.vienna.jaray.service.common.UploadService;
import com.vienna.jaray.service.common.impl.MoreFontMarkImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/sys/waterMark")
public class SysWaterMarkController {

	private ImageInfo imageInfo = new ImageInfo();

    @LogAnnotation(value = "水印上传图片")
    @PostMapping("/uploadFile")
    public ResponseResult watermark(@RequestParam("file") MultipartFile file, String userId, String userNme) throws Exception{
        log.info("上传文件用户为：{}-{}", userNme, userId);

        if (file.isEmpty()) {
            return ResponseResult.fail();
        }

        String imageFileName = file.getOriginalFilename();
        log.info("上传文件名为：{}", imageFileName);

        String suffixName = imageFileName.substring(imageFileName.lastIndexOf("."));
        log.info("上传文件后缀名为：{}", suffixName);

        long fileSize = file.getSize();
        log.info("上传文件大小为：{}", fileSize);

		//源图片文件
		UploadService uploadService = new UploadService();
        String uploadPath = System.getProperty("user.dir") + File.separator + "image" + File.separator +"watermark";
		File uploadFile = new File(uploadPath + "logo_" + imageFileName);

        FileUtils.copyInputStreamToFile(file.getInputStream(), uploadFile);
        uploadService.setFile(uploadFile);
        uploadService.setImageFileName(imageFileName);
        uploadService.setUploadPath(uploadPath);
		imageInfo.setImageURL(uploadService.uploadImage());

		// 文字水印图片文件
//		Mark markImpl = new FontMarkImpl();
//		imageInfo.setLogoImageURL(markImpl.watermark(file.getInputStream(), imageFileName, uploadService.getUploadPath()));

		// 图片水印图片文件
//		Mark imageMarkImpl = new ImageMarkImpl();
//		imageInfo.setLogoImageURL(imageMarkImpl.watermark(file.getInputStream(), imageFileName, uploadService.getUploadPath()));

		// 文字水印图片文件
		Mark moreFontMarkImpl = new MoreFontMarkImpl();
		imageInfo.setLogoImageURL(moreFontMarkImpl.watermark(file.getInputStream(), imageFileName, uploadService.getUploadPath()));

		// 文字水印图片文件
//		Mark moreImageMarkImpl = new MoreImageMarkImpl();
//		imageInfo.setLogoImageURL(moreImageMarkImpl.watermark(file.getInputStream(), imageFileName, uploadService.getUploadPath()));

		return ResponseResult.success().add("imageInfo", imageInfo);
	}

	public ImageInfo getImageInfo() {
		return imageInfo;
	}

	public void setImageInfo(ImageInfo imageInfo) {
		this.imageInfo = imageInfo;
	}
}
