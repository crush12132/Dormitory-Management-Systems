package com.ischoolbar.programmer.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ischoolbar.programmer.bean.Operator;
import com.ischoolbar.programmer.bean.Page;
import com.ischoolbar.programmer.bean.SearchProperty;
import com.ischoolbar.programmer.dao.BuildingDao;
import com.ischoolbar.programmer.dao.StudentDao;
import com.ischoolbar.programmer.entity.Building;
import com.ischoolbar.programmer.entity.DormitoryManager;
import com.ischoolbar.programmer.entity.Student;
import com.ischoolbar.programmer.util.StringUtil;

public class StudentServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1028698240374315446L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String method = req.getParameter("method");
		if("toStudentListView".equals(method)){
			req.getRequestDispatcher("view/studentList.jsp").forward(req, resp);
		}
		if("AddStudent".equals(method)){
			addStudent(req,resp);
		}
		if("StudentList".equals(method)){
			getStudentList(req,resp);
		}
		if("EditStudent".equals(method)){
			editStudent(req,resp);
		}
		if("DeleteStudent".equals(method)){
			deleteStudent(req,resp);
		}
	}

	private void deleteStudent(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		String[] ids = req.getParameterValues("ids[]");
		StudentDao studentDao = new StudentDao();
		String msg = "";
		if(studentDao.delete(ids)){
			msg = "success";
		}
		studentDao.closeConnection();
		try {
			resp.getWriter().write(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void editStudent(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		Integer id = StringUtil.isEmpty(req.getParameter("id")) ? 0 : Integer.parseInt(req.getParameter("id"));
		String sn = req.getParameter("sn");
		String name = req.getParameter("name");
		String password = req.getParameter("password");
		String sex = req.getParameter("sex");
		Student student = new Student();
		student.setId(id);
		student.setName(name);
		student.setSex(sex);
		student.setPassword(password);
		student.setSn(sn);
		StudentDao studentDao = new StudentDao();
		String msg = "";
		if(studentDao.update(student)){
			msg = "success";
		}
		studentDao.closeConnection();
		try {
			resp.getWriter().write(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getStudentList(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		String from = req.getParameter("from");
		//如果来自下拉框查询
		if("combox".equals(from)){
			returnByCombox(req,resp);
			return;
		}
		int pageNumber = Integer.parseInt(req.getParameter("page"));
		int pageSize = Integer.parseInt(req.getParameter("rows"));
		String name = req.getParameter("name");
		if(StringUtil.isEmpty(name)){
			name = "";
		}
		Student student = new Student();
		Map<String, Object> ret = new HashMap<String, Object>();
		student.setName(name);
		StudentDao studentDao = new StudentDao();
		Page<Student> page = new Page<Student>(pageNumber, pageSize);
		page.getSearchProperties().add(new SearchProperty("name", "%"+name+"%", Operator.LIKE));
		//判断当前用户是否是学生
		int userType = Integer.parseInt(req.getSession().getAttribute("userType").toString());
		if(userType == 2){
			//如果是学生，则只能查看他自己的信息
			Student loginedStudent = (Student)req.getSession().getAttribute("user");
			page.getSearchProperties().add(new SearchProperty("id", loginedStudent.getId(), Operator.EQ));
		}
		Page<Student> findList = studentDao.findList(page);
		ret.put("rows", findList.getConten());
		ret.put("total", findList.getTotal());
		studentDao.closeConnection();
		resp.setCharacterEncoding("utf-8");
		try {
			resp.getWriter().write(JSONObject.fromObject(ret).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void returnByCombox(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		StudentDao studentDao = new StudentDao();
		Page<Student> page = new Page<Student>(1, 9999);
		//判断当前用户是否是学生
		int userType = Integer.parseInt(req.getSession().getAttribute("userType").toString());
		if(userType == 2){
			//如果是学生，则只能查看他自己的信息
			Student loginedStudent = (Student)req.getSession().getAttribute("user");
			page.getSearchProperties().add(new SearchProperty("id", loginedStudent.getId(), Operator.EQ));
		}
		page = studentDao.findList(page);
		studentDao.closeConnection();
		resp.setCharacterEncoding("utf-8");
		try {
			resp.getWriter().write(JSONArray.fromObject(page.getConten()).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addStudent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// TODO Auto-generated method stub
		String name = req.getParameter("name");
		String password = req.getParameter("password");
		String sex = req.getParameter("sex");
		resp.setCharacterEncoding("utf-8");
		if(StringUtil.isEmpty(name)){
			resp.getWriter().write("姓名不能为空!");
			return;
		}
		if(StringUtil.isEmpty(password)){
			resp.getWriter().write("密码不能为空!");
			return;
		}
		if(StringUtil.isEmpty(sex)){
			resp.getWriter().write("性别不能为空!");
			return;
		}
		Student student = new Student();
		student.setName(name);
		student.setPassword(password);
		student.setSex(sex);
		student.setSn(StringUtil.generateSn("S", ""));
		StudentDao studentDao = new StudentDao();
		String msg = "添加失败!";
		if(studentDao.add(student)){
			msg = "success";
		}
		studentDao.closeConnection();
		resp.getWriter().write(msg);
	}
}
