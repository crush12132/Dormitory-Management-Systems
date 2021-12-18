package com.ischoolbar.programmer.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ischoolbar.programmer.entity.Admin;

public class AdminDao extends BaseDao<Admin> {
	/**
	 * 根据用户名查找用户信息
	 * @param name
	 * @return
	 */
	public Admin getAdmin(String name){
		Admin admin = null;
		String sql = "select * from admin where name = '" + name + "'";
		try {
			PreparedStatement prepareStatement = con.prepareStatement(sql);
			ResultSet executeQuery = prepareStatement.executeQuery();
			if(executeQuery.next()){
				admin = new Admin();
				admin.setId(executeQuery.getInt("id"));
				admin.setName(executeQuery.getString("name"));
				admin.setPassword(executeQuery.getString("password"));
				admin.setStatus(executeQuery.getInt("status"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return admin;
	}
}
