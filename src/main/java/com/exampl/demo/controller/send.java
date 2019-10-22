package com.exampl.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.exampl.demo.faceidentify.BaseFunctions;
import com.exampl.demo.faceidentify.Normalimage;
import com.exampl.demo.faceidentify_I.BaseFunctions_I;
import com.exampl.demo.faceidentify_I.Normalimage_I;





@Controller
public class send {
	private String picret;
	@RequestMapping("/picup")
	@ResponseBody
	public String getimg(@RequestParam("pic") String pic) {
		//File f = new File("E:"+"face"+".jpg");
		//ImageIO.write(imgbuff, "jpg", f);
		pic=pic.substring(pic.indexOf(",")+1);
		BaseFunctions_I temp=new BaseFunctions();
		System.out.println(temp.ISExistFace(pic));
		Normalimage_I A=new Normalimage();
	 picret=(A.SignFace(pic));
		//System.out.println(pic);
		return picret;
		}
	/*public String result(ModelMap map,@RequestParam String result) {
		map.addAttribute(picret, result);
		System.out.println("回返启动");
		return result;}*/
	
}
