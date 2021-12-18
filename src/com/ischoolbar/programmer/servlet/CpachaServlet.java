package com.ischoolbar.programmer.servlet;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ischoolbar.programmer.util.CpachaUtil;
import com.ischoolbar.programmer.util.StringUtil;
/**
 * 验证码生成类
 * @author llq
 *
 */
public class CpachaServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4919529414762301338L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String method = req.getParameter("method");
		if("loginCpacha".equals(method)){
			getLoginCpacha(req,resp);
		}
	}

	private void getLoginCpacha(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		String vl = req.getParameter("vl");
		String fs = req.getParameter("fs");
		int vcodeLength = 4;
		int fontSize = 21;
		if(!StringUtil.isEmpty(vl)){
			vcodeLength = Integer.parseInt(vl);
		}
		if(!StringUtil.isEmpty(fs)){
			fontSize = Integer.parseInt(fs);
		}
		CpachaUtil cpachaUtil = new CpachaUtil(vcodeLength,fontSize);
		String generatorVCode = cpachaUtil.generatorVCode();
		//把生成的验证码放入session，用来登录时的验证
		req.getSession().setAttribute("loginCpacha", generatorVCode);
		BufferedImage generatorRotateVCodeImage = cpachaUtil.generatorRotateVCodeImage(generatorVCode, true);
		try {
			ImageIO.write(generatorRotateVCodeImage, "gif", resp.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
