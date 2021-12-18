package com.ischoolbar.programmer.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ischoolbar.programmer.entity.Dormitory;
/**
 * �������ݿ����
 * @author llq
 *
 */
public class DormitoryDao extends BaseDao<Dormitory> {
	
	/**
	 * ��������Ƿ�ס��
	 * @param dormitoryId
	 * @return
	 */
	public boolean isFull(int dormitoryId){
		String sql = "select max_number,lived_number from dormitory where id = " + dormitoryId;
		try {
			PreparedStatement prepareStatement = con.prepareStatement(sql);
			ResultSet executeQuery = prepareStatement.executeQuery();
			if(executeQuery.next()){
				return executeQuery.getInt("lived_number") >= executeQuery.getInt("max_number");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * ����������ס����
	 * @param dormitoryId
	 * @param number
	 * @return
	 */
	public boolean updateLivedNumber(int dormitoryId,int number){
		String sql = " update dormitory set lived_number = lived_number ";
		if(number > 0){
			sql += " + " + number + " where id = " + dormitoryId ;
		}else{
			sql += " - " + Math.abs(number) + " where id = " + dormitoryId ;
		}
		try {
			PreparedStatement prepareStatement = con.prepareStatement(sql);
			return prepareStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
