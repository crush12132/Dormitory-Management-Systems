package com.ischoolbar.programmer.servlet;
import java.io.IOException;

import javax.servlet.ServletException;
/**
 * ��¼
 */
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.formula.functions.T;

import com.ischoolbar.programmer.bean.Operator;
import com.ischoolbar.programmer.bean.Page;
import com.ischoolbar.programmer.bean.SearchProperty;
import com.ischoolbar.programmer.config.BaseConfig;
import com.ischoolbar.programmer.dao.AdminDao;
import com.ischoolbar.programmer.dao.DormitoryManagerDao;
import com.ischoolbar.programmer.dao.StudentDao;
import com.ischoolbar.programmer.entity.Admin;
import com.ischoolbar.programmer.entity.DormitoryManager;
import com.ischoolbar.programmer.entity.Student;
import com.ischoolbar.programmer.util.StringUtil;

public class LoginServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5870852067427524781L;
	
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
		String name = req.getParameter("name");
		String password = req.getParameter("password");
		String vcode = req.getParameter("vcode");
		String msg = "success";
		if(StringUtil.isEmpty(name)){
			msg = "�û�������Ϊ��!";
		}
		if(StringUtil.isEmpty(password)){
			msg = "���벻��Ϊ��!";
		}
		if(StringUtil.isEmpty(vcode)){
			msg = "��֤�벻��Ϊ��!";
		}
		if("success".equals(msg)){
			Object loginCpacha = req.getSession().getAttribute("loginCpacha");
			if(loginCpacha == null){
				msg = "session�ѹ��ڣ���ˢ��ҳ������ԣ�";
			}else{
				if(!vcode.toUpperCase().equals(loginCpacha.toString().toUpperCase())){
					msg = "��֤�����";
				}
			}
			
		}
		if("success".equals(msg)){
			String typeString = req.getParameter("type");
			try {
				int type = Integer.parseInt(typeString);
				if(type == 1){
					//��������Ա�û�
					AdminDao adminDao = new AdminDao();
					Admin admin = adminDao.getAdmin(name);
					adminDao.closeConnection();
					if(admin == null){
						msg = "�����ڸ��û���";
					}
					if(admin != null){
						if(!password.equals(admin.getPassword())){
							msg = "�������";
						}else{
							if(admin.getStatus() == BaseConfig.ADMIN_STATUS_DISAABLE){
								msg = "���û�״̬�����ã�����ϵ����Ա��";
							}else{
								req.getSession().setAttribute("user", admin);
								req.getSession().setAttribute("userType", type);
							}
						}
					}
				}else if(type == 2){
					//ѧ����¼
					StudentDao studentDao = new StudentDao();
					Page<Student> page = new Page<Student>(1, 10);
					page.getSearchProperties().add(new SearchProperty("name", name, Operator.EQ));
					Page<Student> studentPage = studentDao.findList(page);
					studentDao.closeConnection();
					if(studentPage.getConten().size() == 0){
						msg = "�����ڸ��û���";
					}else{
						Student student = studentPage.getConten().get(0);
						if(!password.equals(student.getPassword())){
							msg = "�������";
						}else{
							req.getSession().setAttribute("user", student);
							req.getSession().setAttribute("userType", type);
						}
					}
					
				}else if(type == 3){
					//�޹ܵ�¼
					DormitoryManagerDao dormitoryManagerDao = new DormitoryManagerDao();
					Page<DormitoryManager> page = new Page<DormitoryManager>(1, 10);
					page.getSearchProperties().add(new SearchProperty("name", name, Operator.EQ));
					page = dormitoryManagerDao.findList(page);
					dormitoryManagerDao.closeConnection();
					if(page.getConten().size() == 0){
						msg = "�����ڸ��û���";
					}else{
						DormitoryManager dormitoryManager = page.getConten().get(0);
						if(!password.equals(dormitoryManager.getPassword())){
							msg = "�������";
						}else{
							req.getSession().setAttribute("user", dormitoryManager);
							req.getSession().setAttribute("userType", type);
						}
					}
				}else{
					msg = "�û����ʹ���";
				}
			} catch (Exception e) {
				// TODO: handle exception
				msg = "�û����ʹ���";
			}
		}
		resp.setCharacterEncoding("utf-8");
		resp.getWriter().write(msg);
	}
}
