package com.ischoolbar.programmer.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.management.loading.PrivateClassLoader;

import com.ischoolbar.programmer.config.BaseConfig;
/**
 * 
 * @author llq
 *数据库连util
 */
public class DbUtil {

	private String dbUrl = BaseConfig.dbUrl;
	private String dbUser = BaseConfig.dbUser;
	private String dbPassword = BaseConfig.dbPassword;
	private String jdbcName = BaseConfig.jdbcName;
	private Connection connection = null;
	public Connection getConnection(){
		try {
			Class.forName(jdbcName);
			connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			System.out.println("数据库链接成功！");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("数据库链接失败！");
			e.printStackTrace();
		}
		return connection;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DbUtil dbUtil = new DbUtil();
		dbUtil.getConnection();
	}

}
