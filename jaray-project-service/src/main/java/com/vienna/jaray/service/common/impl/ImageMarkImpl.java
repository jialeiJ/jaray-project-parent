package com.vienna.jaray.service.common.impl;

import com.vienna.jaray.service.common.Mark;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 单图片水印实现类
 * @author Jaray
 */
@Slf4j
public class ImageMarkImpl implements Mark {

	@Override
	public String watermark(InputStream image, String imageFileName,
							String uploadPath) {

		String logoFileName = "logo_"+imageFileName;
		log.info("image: {} imageFileName: {} uploadPath: {} realUploadPath: {}", image, imageFileName, uploadPath);

		try(OutputStream os = new FileOutputStream(uploadPath + File.separator + logoFileName);){
			Image img = ImageIO.read(image);
			int width = img.getWidth(null);
			int height = img.getHeight(null);
			// 1.创建图片缓存对象
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
			// 2.创建Java绘图工具对象
			Graphics2D g = bufferedImage.createGraphics();
			// 3.使用绘图工具对象将原图绘制到缓存图片对象中去
			g.drawImage(img, 0, 0, width, height, null);

			// 设置透明度
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, ALPHA));
			// 指定旋转角度和旋转中心
			g.rotate(Math.toRadians(0),bufferedImage.getWidth()/2,bufferedImage.getHeight()/2);


			File file = new File("E:\\image\\watermark\\course_selected.png");
			Image waterMarkImg = ImageIO.read(file);
			int waterMarkImgWidth = waterMarkImg.getWidth(null);
			int waterMarkImgHeight = waterMarkImg.getHeight(null);
			g.drawImage(waterMarkImg, 0, 0, waterMarkImgWidth, waterMarkImgHeight, null);

			//4.使用绘图工具对象将水印（文字/图片）绘制到缓存图片
			g.dispose();

			//5.输出缓存图像到目标文件
			ImageIO.write(bufferedImage, "JPG", os);

		}catch(Exception e){
			log.error("多图片水印异常", e);
		}

		return uploadPath + File.separator + logoFileName;
	}

}
