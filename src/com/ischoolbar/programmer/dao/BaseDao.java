package com.ischoolbar.programmer.dao;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;






import com.ischoolbar.programmer.bean.Operator;
import com.ischoolbar.programmer.bean.Page;
import com.ischoolbar.programmer.bean.SearchProperty;
import com.ischoolbar.programmer.util.DbUtil;
import com.ischoolbar.programmer.util.StringUtil;

/**
 * 数据库操作基础类
 * @author llq
 *利用泛型和反射机制来抽象数据库基本的增删该查操作
 */
public class BaseDao<T> {
	
	public final int CURD_ADD = 1;
	
	public final int CURD_DELETE = 2;
	
	public final int CURD_UPDATE = 3;
	
	public final int CURD_SELECT = 4;
	
	public final int CURD_COUNT = 5;
	
	public Connection con = new DbUtil().getConnection();
	
	private Class<T> t;
	
	/**
	 * 构造函数中明确传进来的参数对象
	 */
	@SuppressWarnings("unchecked")
	public BaseDao(){
		Type genericSuperclass = getClass().getGenericSuperclass();
		if(genericSuperclass instanceof ParameterizedType){
			Type[] actualTypeArguments = ((ParameterizedType)genericSuperclass).getActualTypeArguments();
			if(actualTypeArguments.length > 0){
				t = (Class<T>) actualTypeArguments[0];
			}
		}
	}
	
	/**
	 * 所有新增插入操作抽象封装
	 * @param t
	 * @return
	 */
	public boolean add(T t){
		if(t== null)return false;
		String buildSql = buildSql(CURD_ADD);
		try {
			PreparedStatement prepareStatement = con.prepareStatement(buildSql);
			Field[] fields = t.getClass().getDeclaredFields();
			for(int i=1; i < fields.length; i++){
				fields[i].setAccessible(true);
				prepareStatement.setObject(i, fields[i].get(t));
			}
			return prepareStatement.executeUpdate() > 0;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 修改实体操作
	 * @param t
	 * @return
	 */
	public boolean update(T t){
		String sql = buildSql(CURD_UPDATE);
		try {
			PreparedStatement prepareStatement = con.prepareStatement(sql);
			Field[] declaredFields = this.t.getDeclaredFields();
			for(int i = 1; i<declaredFields.length; i++){
				declaredFields[i].setAccessible(true);
				prepareStatement.setObject(i, declaredFields[i].get(t));
			}
			declaredFields[0].setAccessible(true);
			prepareStatement.setObject(declaredFields.length, declaredFields[0].get(t));
			return prepareStatement.executeUpdate() > 0;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean delete(String[] ids){
		String sql = buildSql(CURD_DELETE) + StringUtils.join(ids, ",")+")";
		try {
			PreparedStatement prepareStatement = con.prepareStatement(sql);
			return prepareStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 抽象封装所有的分页查询列表操作
	 * @param page
	 * @return
	 */
	public Page<T> findList(Page<T> page){
		String sql = buildSql(CURD_SELECT);
		sql += buidSearchSql(page);
		sql += " limit " + page.getOffset() + "," + page.getPageSize();
		//System.out.println(sql);
		try {
			PreparedStatement prepareStatement = con.prepareStatement(sql);
			prepareStatement = setParams(page, prepareStatement);
			ResultSet executeQuery = prepareStatement.executeQuery();
			List<T> conten = page.getConten();
			while(executeQuery.next()){
				T entity = t.newInstance();
				Field[] declaredFields = t.getDeclaredFields();
				for(Field field :declaredFields){
					field.setAccessible(true);
					field.set(entity, executeQuery.getObject(StringUtil.convertToUnderLine(field.getName())));
				}
				conten.add(entity);
			}
			page.setConten(conten);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		page.setTotal(getTotal(page));
		return page;
	}
	
	/**
	 * 获取符合条件的所有记录数
	 * @param page
	 * @return
	 */
	public int getTotal(Page<T> page){
		String sql = buildSql(CURD_COUNT);
		sql += buidSearchSql(page);
		try {
			PreparedStatement prepareStatement = con.prepareStatement(sql);
			prepareStatement = setParams(page, prepareStatement);
			ResultSet executeQuery = prepareStatement.executeQuery();
			if(executeQuery.next()){
				return executeQuery.getInt("total");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
	
	/**
	 * 获取所有类型的字段名称
	 * @param t
	 * @return
	 */
	private List<String> getFields(T t){
		List<String> fieldsList = new ArrayList<String>();
		Field[] declaredFields = t.getClass().getDeclaredFields();
		for(Field field : declaredFields){
			fieldsList.add(field.getName());
		}
		return fieldsList;
	}
	
	/**
	 * 给构造的查询链接赋值参数
	 * @param page
	 * @param prepareStatement
	 * @return
	 */
	private PreparedStatement setParams(Page<T> page,PreparedStatement prepareStatement){
		List<SearchProperty> searchProperties = page.getSearchProperties();
		int index = 1;
		for(SearchProperty searchProperty : searchProperties){
			try {
				if(searchProperty.getOperator() != Operator.IN){
					prepareStatement.setObject(index++, searchProperty.getValue());
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return prepareStatement;
	}
	
	/**
	 * 构造查询sql语句
	 * @param page
	 * @return
	 */
	private String buidSearchSql(Page<T> page){
		String sql = "";
		List<SearchProperty> searchProperties = page.getSearchProperties();
		for(SearchProperty searchProperty : searchProperties){
			switch(searchProperty.getOperator()){
				case GT:{
					sql += " and " + StringUtil.convertToUnderLine(searchProperty.getKey()) + " > ?";
					break;
				}
				case GTE:{
					sql += " and " + StringUtil.convertToUnderLine(searchProperty.getKey()) + " >= ?";
					break;
				}
				case EQ:{
					sql += " and " + StringUtil.convertToUnderLine(searchProperty.getKey()) + " = ?";
					break;
				}
				case LT:{
					sql += " and " + StringUtil.convertToUnderLine(searchProperty.getKey()) + " < ?";
					break;
				}
				case LTE:{
					sql += " and " + StringUtil.convertToUnderLine(searchProperty.getKey()) + " <= ?";
					break;
				}
				case LIKE:{
					sql += " and " + StringUtil.convertToUnderLine(searchProperty.getKey()) + " like ?";
					break;
				}
				case NEQ:{
					sql += " and " + StringUtil.convertToUnderLine(searchProperty.getKey()) + " <> ?";
					break;
				}
				case IN:{
					sql += " and " + StringUtil.convertToUnderLine(searchProperty.getKey()) + " in (" + searchProperty.getValue() + ") ";
					break;
				}
			}
		}
		sql = sql.replaceFirst("and", "where");
		System.out.println(sql);
		return sql;
	}
	
	/**
	 * 构造一般查询语句
	 * @param type
	 * @return
	 */
	private String buildSql(int type) {
		// TODO Auto-generated method stub
		String sql = "";
		switch (type) {
		case CURD_ADD:{
			String sql1 = "insert into " + StringUtil.convertToUnderLine(t.getSimpleName()) + "(";
			Field[] declaredFields = t.getDeclaredFields();
			for(Field field : declaredFields){
				sql1 += StringUtil.convertToUnderLine(field.getName()) + ",";
			}
			sql1 = sql1.substring(0,sql1.length()-1) + ")";
			String sql2 = " values(null,";
			String[] params = new String[declaredFields.length-1];
			Arrays.fill(params, "?");
			sql2 += StringUtils.join(params, ",") + ")";
			sql = sql1 + sql2;
			break;
		}
		case CURD_SELECT:{
			sql = "select * from " + StringUtil.convertToUnderLine(t.getSimpleName());
			break;
		}
		case CURD_COUNT:{
			sql = "select count(*) as total from " + StringUtil.convertToUnderLine(t.getSimpleName());
			break;
		}
		case CURD_UPDATE:{
			sql = "update " + StringUtil.convertToUnderLine(t.getSimpleName()) + " set ";
			Field[] declaredFields = t.getDeclaredFields();
			for(Field field : declaredFields){
				if(!"id".equals(field.getName())){
					sql += StringUtil.convertToUnderLine(field.getName()) + " =?,";
				}
			}
			sql = sql.substring(0,sql.length()-1) + " where id = ?";
			break;
		}
		case CURD_DELETE:{
			sql = "delete from "+StringUtil.convertToUnderLine(t.getSimpleName())+" where id in(";
			break;
		}
		default:
			break;
		}
		System.out.println(sql);
		return sql;
	}

	/**
	 * 关闭数据库链接
	 */
	public void closeConnection(){
		if(con != null){
			try {
				con.close();
				System.out.println(t.getSimpleName()+"Dao建立的数据库链接成功关闭!");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
