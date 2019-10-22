package com.exampl.demo.faceidentify;

import java.awt.Color;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.aip.face.AipFace;

import com.exampl.demo.faceidentify_I.*;

public class Normalimage implements Normalimage_I {

	private AipFace client;

	public Normalimage() {
		// 获取AipFace
		client = BaseFunctions.getClient();
	}

	@Override
	public String SignFace(String imageB64) {
		return SignFace(imageB64, 2);
	}

	@Override
	public String SignFace(String imageB64, int num) {
		// TODO Auto-generated method stub
		JSONArray locations = getlocations(imageB64, num);

		// Base64转化为image
		BufferedImage Image = Base642Image(imageB64);

		// 画框
		for (int i = 0; i < locations.length(); i++) {

			JSONObject location = locations.getJSONObject(i).getJSONObject("location");

			// 获得画框边界参数
			int[] Squre = getSqure(location);

			// 画框
			try {

				Image = Signup(Image, Squre);

			} catch (NullPointerException e) {
				continue;
			}
		}

		// 将图片转化为BASE64
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		String Signedimage = "0";
		try {

			ImageIO.write(Image, "jpg", bos);

			/*
			 * 测试时使用 File f = new File("E:\\大学\\学习\\驭光实习\\测试图片\\imagesmyimage.png");
			 * ImageIO.write(Image, "jpg", f);
			 */
			Signedimage = com.baidu.aip.util.Base64Util.encode(bos.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("图片载入错误");
		}
		return Signedimage;
	}

	/**
	 * 根据location信息计算人脸框位置参数
	 * 
	 * @param location
	 * @return 根据数组顺序分为up，down，left，right， null说明角度过于极端
	 */
	private int[] getSqure(JSONObject location) {
		// TODO Auto-generated method stub
		// 计算绘制参数
		int l_top, l_left, l_width, l_height;
		double l_rotation;
		l_top = location.getInt("top");
		l_left = location.getInt("left");
		l_width = location.getInt("width");
		l_height = location.getInt("height");
		l_rotation = location.getInt("rotation") * Math.PI / 180;

		// 计算方框相关参数 按识别出的脸部角度计算边界
		int up, down, left, right;
		if (l_rotation < 0 && l_rotation > -90) {
			up = l_top + (int) (l_width * Math.sin(l_rotation));// 上边界
			down = l_top + (int) (l_height * Math.cos(l_rotation));// 下边界
			left = l_left;// 左边界
			right = l_left + (int) (l_width * Math.cos(l_rotation) - l_height * Math.sin(l_rotation));// 右边界
			int[] ret = { up, down, left, right };
			return ret;
		} else if (l_rotation >= 0 && l_rotation < +90) {
			up = l_top;// 上边界
			down = l_top + (int) (l_width * Math.sin(l_rotation) + l_height * Math.cos(l_rotation));// 下边界
			left = l_left - (int) (l_height * Math.sin(l_rotation));// 左边界
			right = l_left + (int) (l_width * Math.cos(l_rotation));// 右边界
			int[] ret = { up, down, left, right };
			return ret;
		} else
			return null;// 角度过于极端则忽略人脸,返回null

	}

	private JSONArray getlocations(String imageB64, int num) {
		// 设置人数
		HashMap<String, String> map = new HashMap<>();
		map.put("max_face_num", new Integer(num).toString());
		// 人脸检测检测 获取脸的location
		JSONObject res = client.detect(imageB64, "BASE64", map);

		// 处理location

		JSONArray locations = res.getJSONObject("result").getJSONArray("face_list");
		return locations;
	}

	private BufferedImage Signup(BufferedImage image, int[] Squre) throws NullPointerException {
		int up = Squre[0], down = Squre[1], left = Squre[2], right = Squre[3];
		// TODO Auto-generated method stub
		// 绘制竖线
		for (int i = up; i <= down; i++) {
			image.setRGB(left-1, i, Color.blue.getRGB());
			image.setRGB(left, i, Color.blue.getRGB());
			image.setRGB(left+1, i, Color.blue.getRGB());
			image.setRGB(right-1, i, Color.blue.getRGB());
			image.setRGB(right, i, Color.blue.getRGB());
			image.setRGB(right+1, i, Color.blue.getRGB());
		}
		// 绘制横线
		for (int i = left; i <= right; i++) {
			image.setRGB(i, up-1, Color.blue.getRGB());
			image.setRGB(i, up, Color.blue.getRGB());
			image.setRGB(i, up+1, Color.blue.getRGB());
			image.setRGB(i, down-1, Color.blue.getRGB());
			image.setRGB(i, down, Color.blue.getRGB());
			image.setRGB(i, down+1, Color.blue.getRGB());
		}
		return image;
	}

	private BufferedImage Base642Image(String imageB64) {
		// TODO Auto-generated method stub
		if (imageB64 == null)
			return null;
		byte[] decoder = com.baidu.aip.util.Base64Util.decode(imageB64);

		InputStream buffin = new ByteArrayInputStream(decoder, 0, decoder.length);
		try {
			BufferedImage image = ImageIO.read(buffin);
			return image;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	@Override
	public int CutoutFace(String imageB64, String savepath) {
		// TODO Auto-generated method stub
		return CutoutFace(imageB64, savepath,2);
	}
	@Override
	public int CutoutFace(String imageB64, String savepath,int num) {
		// TODO Auto-generated method stub
		JSONArray locations = getlocations(imageB64, num);
		//总计输入人脸数目
		int Facesum=0;
		// Base64转化为image
		BufferedImage Image = Base642Image(imageB64);

		// 输出人脸
		for (int i = 0; i < locations.length(); i++) {

			JSONObject location = locations.getJSONObject(i).getJSONObject("location");

			// 获得画框边界参数
			int[] Squre = getSqure(location);

			// 输出人脸文件
			try {

				if(Saveface(Image, Squre,savepath,Facesum))
					Facesum++;
			} catch (NullPointerException e) {
				continue;
			}
		}
		return Facesum;
	}

	private boolean Saveface(BufferedImage image, int[] Squre, String savepath,int Facesum) {
		// TODO Auto-generated method stub
		int up = Squre[0], down = Squre[1], left = Squre[2], right = Squre[3];
		BufferedImage imageout=new BufferedImage(right-left+1,down-up+1,BufferedImage.TYPE_INT_RGB);
		// 抠出人脸
		for (int i = up; i <= down; i++) {
			for (int j = left; j <= right; j++) {
				imageout.setRGB(j-left, i-up ,image.getRGB(j, i));
			}
		}	
		//输出图片		
		try {
			File f = new File(savepath+"face"+Facesum+".jpg");
			ImageIO.write(imageout, "jpg", f);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
	}

	

}
