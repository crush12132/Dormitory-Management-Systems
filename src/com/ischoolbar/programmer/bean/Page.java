package com.ischoolbar.programmer.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * ��ҳ��Ϣ��װ
 * @author llq
 *
 */
public class Page<T> {
	private int pageNumber;//��ǰҳ��
	private int pageSize;//ÿҳ��ʾ����
	private int offset;//��Ӧ�����ݿ��е�ƫ����
	private int total;//�ܵļ�¼��
	private List<T> conten = new ArrayList<T>();
	private List<SearchProperty> searchProperties = new ArrayList<SearchProperty>();
	
	public Page(int pageNumber,int pageSize){
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.offset = (pageNumber - 1)*pageSize;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getConten() {
		return conten;
	}

	public void setConten(List<T> conten) {
		this.conten = conten;
	}

	public List<SearchProperty> getSearchProperties() {
		return searchProperties;
	}

	public void setSearchProperties(List<SearchProperty> searchProperties) {
		this.searchProperties = searchProperties;
	}
}
