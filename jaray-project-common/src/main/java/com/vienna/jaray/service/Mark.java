package com.vienna.jaray.service;

import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;

public interface Mark {
	public static final String MARK_TEXT = "贾雷";
	public static final String FONT_NAME = "宋体";
	public static final int FONT_STYLE = Font.BOLD;
	public static final int FONT_SIZE = 30;
	public static final Color FONT_COLOR = Color.red;
	//文字水印对应的位置
	public static final int X = 10;
	public static final int Y = 10;
	//透明化处理
	public static float ALPHA = 0.4F;
	//图片水印的文件名称
	public static String LOGO = "logo.png";

	public String watermark(InputStream image,String imageFileName,String uploadPath);
}
