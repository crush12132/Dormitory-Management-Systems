package com.ischoolbar.programmer.entity;
/**
 * ����ʵ��
 * @author llq
 *
 */
public class Dormitory {
	private int id;
	private String sn;//������
	private int buildingId;//����¥��
	private String floor;//����¥��
	private int maxNumber;//����ס����
	private int livedNumber;//��ס����
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public int getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(int buildingId) {
		this.buildingId = buildingId;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	
	public int getMaxNumber() {
		return maxNumber;
	}
	public void setMaxNumber(int maxNumber) {
		this.maxNumber = maxNumber;
	}
	public int getLivedNumber() {
		return livedNumber;
	}
	public void setLivedNumber(int livedNumber) {
		this.livedNumber = livedNumber;
	}
	
}
