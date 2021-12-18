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
import com.ischoolbar.programmer.dao.DormitoryManagerDao;
import com.ischoolbar.programmer.entity.Building;
import com.ischoolbar.programmer.entity.DormitoryManager;
import com.ischoolbar.programmer.util.StringUtil;
/**
 * 楼宇管理
 * @author llq
 *
 */
public class BuildingServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4900649015154740008L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String method = req.getParameter("method");
		if("toBuildingListView".equals(method)){
			req.getRequestDispatcher("view/buildingList.jsp").forward(req, resp);
		}
		if("AddBuilding".equals(method)){
			addBuilding(req,resp);
		}
		if("BuildingList".equals(method)){
			getBuildingList(req,resp);
		}
		if("EditBuilding".equals(method)){
			editBuilding(req,resp);
		}
		if("DeleteBuilding".equals(method)){
			deleteBuilding(req,resp);
		}
	}

	private void deleteBuilding(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		String[] ids = req.getParameterValues("ids[]");
		BuildingDao buildingDao = new BuildingDao();
		String msg = "";
		if(buildingDao.delete(ids)){
			msg = "success";
		}
		buildingDao.closeConnection();
		try {
			resp.getWriter().write(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void editBuilding(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// TODO Auto-generated method stub
		String name = req.getParameter("name");
		String location = req.getParameter("location");
		int dormitoryManagerId = 0;
		int id =0;
		try {
			dormitoryManagerId = Integer.parseInt(req.getParameter("dormitoryManagerId"));
			id = Integer.parseInt(req.getParameter("id"));
		} catch (Exception e) {
			// TODO: handle exception
			resp.getWriter().write("选择的宿管不正确!");
			return;
		}
		resp.setCharacterEncoding("utf-8");
		if(StringUtil.isEmpty(name)){
			resp.getWriter().write("名称不能为空!");
			return;
		}
		if(StringUtil.isEmpty(location)){
			resp.getWriter().write("所属位置不能为空!");
			return;
		}
		Building building = new Building();
		building.setName(name);
		building.setDormitoryManagerId(dormitoryManagerId);
		building.setLocation(location);
		building.setId(id);
		BuildingDao buildingDao = new BuildingDao();
		String msg = "修改失败!";
		if(buildingDao.update(building)){
			msg = "success";
		}
		buildingDao.closeConnection();
		resp.getWriter().write(msg);
	}

	private void addBuilding(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// TODO Auto-generated method stub
		String name = req.getParameter("name");
		String location = req.getParameter("location");
		int dormitoryManagerId = 0;
		try {
			dormitoryManagerId = Integer.parseInt(req.getParameter("dormitoryManagerId"));
		} catch (Exception e) {
			// TODO: handle exception
			resp.getWriter().write("选择的宿管不正确!");
			return;
		}
		resp.setCharacterEncoding("utf-8");
		if(StringUtil.isEmpty(name)){
			resp.getWriter().write("名称不能为空!");
			return;
		}
		if(StringUtil.isEmpty(location)){
			resp.getWriter().write("所属位置不能为空!");
			return;
		}
		Building building = new Building();
		building.setName(name);
		building.setDormitoryManagerId(dormitoryManagerId);
		building.setLocation(location);
		BuildingDao buildingDao = new BuildingDao();
		String msg = "添加失败!";
		if(buildingDao.add(building)){
			msg = "success";
		}
		buildingDao.closeConnection();
		resp.getWriter().write(msg);
	}

	private void getBuildingList(HttpServletRequest req,
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
		String name = req.getParameter("name");
		String dormitoryManagerId = req.getParameter("dormitoryManagerId");
		if(StringUtil.isEmpty(name)){
			name = "";
		}
		Map<String, Object> ret = new HashMap<String, Object>();
		BuildingDao buildingDao = new BuildingDao();
		Page<Building> page = new Page<Building>(pageNumber, pageSize);
		page.getSearchProperties().add(new SearchProperty("name", "%"+name+"%", Operator.LIKE));
		if(!StringUtil.isEmpty(dormitoryManagerId)){
			page.getSearchProperties().add(new SearchProperty("dormitory_manager_id", dormitoryManagerId, Operator.EQ));
		}
		//判断当前用户是否是宿管
		int userType = Integer.parseInt(req.getSession().getAttribute("userType").toString());
		if(userType == 3){
			//如果是宿管，则只能查看他自己的信息
			DormitoryManager loginedDormitoryManager = (DormitoryManager)req.getSession().getAttribute("user");
			page.getSearchProperties().add(new SearchProperty("dormitory_manager_id", loginedDormitoryManager.getId(), Operator.EQ));
		}
		Page<Building> findList = buildingDao.findList(page);
		ret.put("rows", findList.getConten());
		ret.put("total", findList.getTotal());
		buildingDao.closeConnection();
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
		BuildingDao buildingDao = new BuildingDao();
		Page<Building> page = new Page<Building>(1, 9999);
		//判断当前用户是否是宿管
		int userType = Integer.parseInt(req.getSession().getAttribute("userType").toString());
		if(userType == 3){
			//如果是宿管，则只能查看他自己的信息
			DormitoryManager loginedDormitoryManager = (DormitoryManager)req.getSession().getAttribute("user");
			page.getSearchProperties().add(new SearchProperty("dormitory_manager_id", loginedDormitoryManager.getId(), Operator.EQ));
		}
		page = buildingDao.findList(page);
		buildingDao.closeConnection();
		resp.setCharacterEncoding("utf-8");
		try {
			resp.getWriter().write(JSONArray.fromObject(page.getConten()).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
