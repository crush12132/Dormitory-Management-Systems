package com.ischoolbar.programmer.servlet;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ischoolbar.programmer.bean.Operator;
import com.ischoolbar.programmer.bean.Page;
import com.ischoolbar.programmer.bean.SearchProperty;
import com.ischoolbar.programmer.dao.BuildingDao;
import com.ischoolbar.programmer.dao.DormitoryDao;
import com.ischoolbar.programmer.dao.LiveDao;
import com.ischoolbar.programmer.entity.Building;
import com.ischoolbar.programmer.entity.Dormitory;
import com.ischoolbar.programmer.entity.DormitoryManager;
import com.ischoolbar.programmer.entity.Live;
import com.ischoolbar.programmer.entity.Student;
import com.ischoolbar.programmer.util.StringUtil;

public class LiveServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4848959249721049750L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String method = req.getParameter("method");
		if("toLiveListView".equals(method)){
			req.getRequestDispatcher("view/liveList.jsp").forward(req, resp);
		}
		if("AddLive".equals(method)){
			addLive(req,resp);
		}
		if("LiveList".equals(method)){
			liveList(req,resp);
		}
		if("EditLive".equals(method)){
			editLive(req,resp);
		}
		if("DeleteLive".equals(method)){
			deleteLive(req,resp);
		}
	}

	private void deleteLive(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		String[] ids = req.getParameterValues("ids[]");
		String[] dormitoryIds = req.getParameterValues("dormitoryIds[]");
		LiveDao liveDao = new LiveDao();
		DormitoryDao dormitoryDao = new DormitoryDao();
		String msg = "删除失败";
		resp.setCharacterEncoding("utf-8");
		if(liveDao.delete(ids)){
			for(String dormitoryId : dormitoryIds){
				dormitoryDao.updateLivedNumber(Integer.parseInt(dormitoryId), -1);
			}
			msg = "success";
		}
		liveDao.closeConnection();
		dormitoryDao.closeConnection();
		try {
			resp.getWriter().write(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void editLive(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// TODO Auto-generated method stub
		int studentId = 0;
		int dormitoryId = 0;
		int oldDormitoryId = 0;
		int id = 0;
		String msg = "success";
		resp.setCharacterEncoding("utf-8");
		try {
			studentId = Integer.parseInt(req.getParameter("studentId"));
			dormitoryId = Integer.parseInt(req.getParameter("dormitoryId"));
			oldDormitoryId = Integer.parseInt(req.getParameter("oldDormitoryId"));
			id = Integer.parseInt(req.getParameter("id"));
		} catch (Exception e) {
			// TODO: handle exception
			msg = "所选学生信息或宿舍信息有误！";
		}
		DormitoryDao dormitoryDao = new DormitoryDao();
		if(dormitoryDao.isFull(dormitoryId)){
			msg = "该宿舍已经住满，请更换宿舍！";
			resp.getWriter().write(msg);
			return;
		}
		Live live = new Live();
		live.setDormitoryId(dormitoryId);
		live.setStudentId(studentId);
		live.setLiveDate(new Date(System.currentTimeMillis()));
		live.setId(id);
		LiveDao liveDao = new LiveDao();
//		if(liveDao.isLived(studentId)){
//			msg = "该学生已经办理住宿，请勿重复入住！";
//			resp.getWriter().write(msg);
//			return;
//		}
		if(!liveDao.update(live)){
			msg = "调整失败！";
		}
		liveDao.closeConnection();
		if(!dormitoryDao.updateLivedNumber(dormitoryId, 1)&&!dormitoryDao.updateLivedNumber(oldDormitoryId, -1)){
			msg = "更新宿舍信息失败！";
		}
		if(!dormitoryDao.updateLivedNumber(oldDormitoryId, -1)){
			msg = "更新宿舍信息失败！";
		}
		dormitoryDao.closeConnection();
		resp.getWriter().write(msg);
	}

	private void liveList(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		int pageNumber = Integer.parseInt(req.getParameter("page"));
		int pageSize = Integer.parseInt(req.getParameter("rows"));
		String studentId = req.getParameter("studentId");
		String dormitoryId = req.getParameter("dormitoryId");
		
		Map<String, Object> ret = new HashMap<String, Object>();
		LiveDao liveDao = new LiveDao();
		Page<Live> page = new Page<Live>(pageNumber, pageSize);
		if(!StringUtil.isEmpty(studentId)){
			page.getSearchProperties().add(new SearchProperty("student_id", studentId, Operator.EQ));
		}
		if(!StringUtil.isEmpty(dormitoryId)){
			page.getSearchProperties().add(new SearchProperty("dormitory_id", dormitoryId, Operator.EQ));
		}
		//判断当前用户是否是宿管
		int userType = Integer.parseInt(req.getSession().getAttribute("userType").toString());
		if(userType == 3){
			//如果是宿管，则只能查看他自己的信息
			DormitoryManager loginedDormitoryManager = (DormitoryManager)req.getSession().getAttribute("user");
			BuildingDao buildingDao = new BuildingDao();
			Page<Building> buildPage = new Page<Building>(1, 10);
			buildPage.getSearchProperties().add(new SearchProperty("dormitory_manager_id", loginedDormitoryManager.getId(), Operator.EQ));
			buildPage = buildingDao.findList(buildPage);
			buildingDao.closeConnection();
			DormitoryDao dormitoryDao = new DormitoryDao();
			Page<Dormitory> dormitoryPage = new Page<Dormitory>(1, 10);
			dormitoryPage.getSearchProperties().add(new SearchProperty("building_id", buildPage.getConten().get(0).getId(), Operator.EQ));
			dormitoryPage = dormitoryDao.findList(dormitoryPage);
			dormitoryDao.closeConnection();
			List<Dormitory> dormitoryList = dormitoryPage.getConten();
			String dormitoryIds = "";
			for(Dormitory dormitory : dormitoryList){
				dormitoryIds += dormitory.getId() + ",";
			}
			dormitoryIds = dormitoryIds.substring(0,dormitoryIds.length()-1);
			page.getSearchProperties().add(new SearchProperty("dormitory_id",dormitoryIds , Operator.IN));
		}else if(userType == 2){
			//学生，只能查看自己的住宿信息
			Student loginedStudent = (Student)req.getSession().getAttribute("user");
			page.getSearchProperties().add(new SearchProperty("student_id", loginedStudent.getId(), Operator.EQ));
		}
		Page<Live> findList = liveDao.findList(page);
		ret.put("rows", findList.getConten());
		ret.put("total", findList.getTotal());
		liveDao.closeConnection();
		resp.setCharacterEncoding("utf-8");
		try {
			resp.getWriter().write(JSONObject.toJSONString(ret));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addLive(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// TODO Auto-generated method stub
		int studentId = 0;
		int dormitoryId = 0;
		String msg = "success";
		resp.setCharacterEncoding("utf-8");
		try {
			studentId = Integer.parseInt(req.getParameter("studentId"));
			dormitoryId = Integer.parseInt(req.getParameter("dormitoryId"));
		} catch (Exception e) {
			// TODO: handle exception
			msg = "所选学生信息或宿舍信息有误！";
		}
		DormitoryDao dormitoryDao = new DormitoryDao();
		if(dormitoryDao.isFull(dormitoryId)){
			msg = "该宿舍已经住满，请更换宿舍！";
			resp.getWriter().write(msg);
			return;
		}
		Live live = new Live();
		live.setDormitoryId(dormitoryId);
		live.setStudentId(studentId);
		live.setLiveDate(new Date(System.currentTimeMillis()));
		LiveDao liveDao = new LiveDao();
		if(liveDao.isLived(studentId)){
			msg = "该学生已经办理住宿，请勿重复入住！";
			resp.getWriter().write(msg);
			return;
		}
		if(!liveDao.add(live)){
			msg = "添加失败！";
		}
		liveDao.closeConnection();
		if(!dormitoryDao.updateLivedNumber(dormitoryId, 1)){
			msg = "更新宿舍信息失败！";
		}
		dormitoryDao.closeConnection();
		resp.getWriter().write(msg);
	}
}
