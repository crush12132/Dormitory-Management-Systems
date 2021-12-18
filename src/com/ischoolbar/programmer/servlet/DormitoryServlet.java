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
import com.ischoolbar.programmer.dao.DormitoryDao;
import com.ischoolbar.programmer.entity.Building;
import com.ischoolbar.programmer.entity.Dormitory;
import com.ischoolbar.programmer.entity.DormitoryManager;
import com.ischoolbar.programmer.util.StringUtil;
/**
 * 宿舍管理
 * @author llq
 *
 */
public class DormitoryServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7273589046901421591L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String method = req.getParameter("method");
		if("toDormitoryListView".equals(method)){
			req.getRequestDispatcher("view/dormitoryList.jsp").forward(req, resp);
		}
		if("AddDormitory".equals(method)){
			addDormitory(req,resp);
		}
		if("DormitoryList".equals(method)){
			getDormitoryList(req,resp);
		}
		if("EditDormitory".equals(method)){
			editDormitory(req,resp);
		}
		if("DeleteDormitory".equals(method)){
			deleteDormitory(req,resp);
		}
	}

	private void deleteDormitory(HttpServletRequest req,
			HttpServletResponse resp) {
		// TODO Auto-generated method stub
		String[] ids = req.getParameterValues("ids[]");
		DormitoryDao dormitoryDao = new DormitoryDao();
		String msg = "删除失败";
		if(dormitoryDao.delete(ids)){
			msg = "success";
		}
		dormitoryDao.closeConnection();
		try {
			resp.getWriter().write(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void editDormitory(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// TODO Auto-generated method stub
		String sn = req.getParameter("sn");
		String floor = req.getParameter("floor");
		int buildingId = 0;
		int maxNumber = 0;
		int id = 0;
		resp.setCharacterEncoding("utf-8");
		try {
			buildingId = Integer.parseInt(req.getParameter("buildingId"));
			maxNumber = Integer.parseInt(req.getParameter("maxNumber"));
			id = Integer.parseInt(req.getParameter("id"));
		} catch (Exception e) {
			// TODO: handle exception
			resp.getWriter().write("选择的宿管不正确!");
			return;
		}
		resp.setCharacterEncoding("utf-8");
		if(StringUtil.isEmpty(sn)){
			resp.getWriter().write("编号不能为空!");
			return;
		}
		if(StringUtil.isEmpty(floor)){
			resp.getWriter().write("所属楼层不能为空!");
			return;
		}
		Dormitory dormitory = new Dormitory();
		dormitory.setSn(sn);
		dormitory.setBuildingId(buildingId);
		dormitory.setFloor(floor);
		dormitory.setMaxNumber(maxNumber);
		dormitory.setId(id);
		DormitoryDao dormitoryDao = new DormitoryDao();
		String msg = "修改失败!";
		if(dormitoryDao.update(dormitory)){
			msg = "success";
		}
		dormitoryDao.closeConnection();
		resp.getWriter().write(msg);
	}

	private void getDormitoryList(HttpServletRequest req,
			HttpServletResponse resp) {
		// TODO Auto-generated method stub
		String from = req.getParameter("from");
		//如果来自下拉框查询
		if("combox".equals(from)){
			returnByCombox(req,resp);
			return;
		}
		int pageNumber = Integer.parseInt(req.getParameter("page"));
		int pageSize = Integer.parseInt(req.getParameter("rows"));
		String sn = req.getParameter("sn");
		String buildingId = req.getParameter("buildingId");
		Map<String, Object> ret = new HashMap<String, Object>();
		DormitoryDao dormitoryDao = new DormitoryDao();
		Page<Dormitory> page = new Page<Dormitory>(pageNumber, pageSize);
		if(!StringUtil.isEmpty(sn)){
			page.getSearchProperties().add(new SearchProperty("sn", sn, Operator.EQ));
		}
		
		if(!StringUtil.isEmpty(buildingId)){
			page.getSearchProperties().add(new SearchProperty("building_id", buildingId, Operator.EQ));
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
			page.getSearchProperties().add(new SearchProperty("building_id", buildPage.getConten().get(0).getId(), Operator.EQ));
		}
		Page<Dormitory> findList = dormitoryDao.findList(page);
		ret.put("rows", findList.getConten());
		ret.put("total", findList.getTotal());
		dormitoryDao.closeConnection();
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
		DormitoryDao dormitoryDao = new DormitoryDao();
		Page<Dormitory> page = new Page<Dormitory>(1, 9999);
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
			page.getSearchProperties().add(new SearchProperty("building_id", buildPage.getConten().get(0).getId(), Operator.EQ));
		}
		page = dormitoryDao.findList(page);
		dormitoryDao.closeConnection();
		resp.setCharacterEncoding("utf-8");
		try {
			resp.getWriter().write(JSONArray.fromObject(page.getConten()).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addDormitory(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// TODO Auto-generated method stub
		String sn = req.getParameter("sn");
		String floor = req.getParameter("floor");
		int buildingId = 0;
		int maxNumber = 0;
		resp.setCharacterEncoding("utf-8");
		try {
			buildingId = Integer.parseInt(req.getParameter("buildingId"));
			maxNumber = Integer.parseInt(req.getParameter("maxNumber"));
		} catch (Exception e) {
			// TODO: handle exception
			resp.getWriter().write("选择的宿管不正确!");
			return;
		}
		resp.setCharacterEncoding("utf-8");
		if(StringUtil.isEmpty(sn)){
			resp.getWriter().write("编号不能为空!");
			return;
		}
		if(StringUtil.isEmpty(floor)){
			resp.getWriter().write("所属楼层不能为空!");
			return;
		}
		Dormitory dormitory = new Dormitory();
		dormitory.setSn(sn);
		dormitory.setBuildingId(buildingId);
		dormitory.setFloor(floor);
		dormitory.setMaxNumber(maxNumber);
		DormitoryDao dormitoryDao = new DormitoryDao();
		String msg = "添加失败!";
		if(dormitoryDao.add(dormitory)){
			msg = "success";
		}
		dormitoryDao.closeConnection();
		resp.getWriter().write(msg);
	}
}
