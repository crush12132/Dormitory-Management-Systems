package com.ischoolbar.programmer.entity;

import java.sql.Date;

/**
 * ס��ʵ��
 * @author llq
 *
 */
public class Live {
	private int id;
	private int studentId;
	private int dormitoryId;//����id
	private Date liveDate;//��סʱ��
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStudentId() {
		return studentId;
	}
	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}
	public int getDormitoryId() {
		return dormitoryId;
	}
	public void setDormitoryId(int dormitoryId) {
		this.dormitoryId = dormitoryId;
	}
	public Date getLiveDate() {
		return liveDate;
	}
	public void setLiveDate(Date liveDate) {
		this.liveDate = liveDate;
	}
	
}
