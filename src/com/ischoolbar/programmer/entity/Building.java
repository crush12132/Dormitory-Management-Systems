package com.ischoolbar.programmer.entity;
/**
 * ¥��ʵ��
 * @author llq
 *
 */
public class Building {
	private int id;
	private String name;//¥������
	private String location;//¥������λ��
	private int dormitoryManagerId;//�����޹�
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getDormitoryManagerId() {
		return dormitoryManagerId;
	}
	public void setDormitoryManagerId(int dormitoryManagerId) {
		this.dormitoryManagerId = dormitoryManagerId;
	}
	
	
}
