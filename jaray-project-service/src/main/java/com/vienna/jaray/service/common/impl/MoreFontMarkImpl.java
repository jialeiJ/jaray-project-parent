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
 * 多文字水印实现类
 * @author Jaray
 */
@Slf4j
public class MoreFontMarkImpl implements Mark {

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
			// 设置水印字体信息
			g.setFont(new Font(FONT_NAME, FONT_STYLE, FONT_SIZE));
			g.setColor(Color.gray);
			// 获取计算后的文字真实宽度值
			int realWidth = FONT_SIZE * getTextLength(MARK_TEXT);
			int realHeight = FONT_SIZE;

			// 设置透明度
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, ALPHA));
			// 指定旋转角度和旋转中心
			g.rotate(Math.toRadians(30),bufferedImage.getWidth()/2,bufferedImage.getHeight()/2);

			int x = - width / 2;
			int y = - height / 2;

			// 循环打印水印
			while(x < width * 1.5){
				y = - height / 2;
				while(y < height * 1.5){
					g.drawString(MARK_TEXT, x, y);
					// 行间距
					y += realHeight + 30;
				}
				// 间距
				x += realWidth + 30;
			}
			//4.使用绘图工具对象将水印（文字/图片）绘制到缓存图片
			g.dispose();

			//5.输出缓存图像到目标文件
			ImageIO.write(bufferedImage, "JPG", os);

		}catch(Exception e){
			log.error("多文字水印异常", e);
		}

		return uploadPath + File.separator + logoFileName;
	}

	/**
	 * 判断文字是中文还是英文--获取文本宽度值---->中文：英文=2:1
	 * @param text
	 * @return
	 */
	public int getTextLength(String text){
		int length = text.length();

		for (int i = 0; i < text.length(); i++) {
			String s = String.valueOf(text.charAt(i));
			// 若是文字则length++
			if(s.getBytes().length>1){
				length++;
			}
		}

		length = length%2==0?length/2:length/2+1;
		return length;
	}

}
