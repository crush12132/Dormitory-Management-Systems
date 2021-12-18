<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>宿舍列表</title>
	<link rel="stylesheet" type="text/css" href="easyui/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="easyui/themes/icon.css">
	<link rel="stylesheet" type="text/css" href="easyui/css/demo.css">
	<script type="text/javascript" src="easyui/jquery.min.js"></script>
	<script type="text/javascript" src="easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="easyui/js/validateExtends.js"></script>
	<script type="text/javascript">
	$(function() {	
		//datagrid初始化 
	    $('#dataList').datagrid({ 
	        title:'宿舍列表', 
	        iconCls:'icon-more',//图标 
	        border: true, 
	        collapsible:false,//是否可折叠的 
	        fit: true,//自动大小 
	        method: "post",
	        url:"DormitoryServlet?method=DormitoryList&t="+new Date().getTime(),
	        idField:'id', 
	        singleSelect:false,//是否单选 
	        pagination:true,//分页控件 
	        rownumbers:true,//行号 
	        remoteSort: false,
	        columns: [[  
				{field:'chk',checkbox: true,width:50},
 		        {field:'id',title:'ID',width:50, sortable: true},    
 		        {field:'sn',title:'宿舍编号',width:200},
 		        {field:'buildingId',title:'所属楼宇',width:200, formatter:function(value,rowData,rowIndex){
 		        	var data = $("#search-building").combobox("getData");
 		        	for(var i=0; i<data.length; i++){
 		        		if(value == data[i].id){
 		        			return data[i].name;
 		        		}
 		        	}
 		        	return value;
 		        }},    
 		        {field:'floor',title:'所属楼层',width:200},
 		        {field:'maxNumber',title:'最大可住人数',width:200},
 		        {field:'livedNumber',title:'已住人数',width:200},
	 		]], 
	        toolbar: "#toolbar",
	        onBeforeLoad:function(){
	        	try{
	        		var data = $("#search-building").combobox("getData");
	        		if(data.length == 0){
	        			preGetBuilding();
	        		}
	        	}catch(error){
	        		preGetBuilding();
	        	}
	        	
	        }
	    });
		
		function preGetBuilding(){
			//添加宿管下拉框
		  	$("#search-building").combobox({
		  		url: "BuildingServlet?method=BuildingList&from=combox",
		  		onLoadSuccess: function(){
			  		//默认选择第一条数据
					//var data = $(this).combobox("getData");
					//$(this).combobox("setValue", data[0].id);
		  		}
		  	});
		}
		
	    //设置分页控件 
	    var p = $('#dataList').datagrid('getPager'); 
	    $(p).pagination({ 
	        pageSize: 10,//每页显示的记录条数，默认为10 
	        pageList: [10,20,30,50,100],//可以设置每页记录条数的列表 
	        beforePageText: '第',//页数文本框前显示的汉字 
	        afterPageText: '页    共 {pages} 页', 
	        displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录', 
	    }); 
	    //设置工具类按钮
	    $("#add").click(function(){
	    	$("#addDialog").dialog("open");
	    });
	    //修改
	    $("#edit").click(function(){
	    	var selectRows = $("#dataList").datagrid("getSelections");
        	if(selectRows.length != 1){
            	$.messager.alert("消息提醒", "请选择一条数据进行操作!", "warning");
            } else{
		    	$("#editDialog").dialog("open");
            }
	    });
	    //删除
	    $("#delete").click(function(){
	    	var selectRows = $("#dataList").datagrid("getSelections");
        	var selectLength = selectRows.length;
        	if(selectLength == 0){
            	$.messager.alert("消息提醒", "请选择数据进行删除!", "warning");
            } else{
            	var ids = [];
            	$(selectRows).each(function(i, row){
            		ids[i] = row.id;
            	});
            	$.messager.confirm("消息提醒", "确定删除宿舍信息吗？(该宿舍信息下所属的住宿信息须先删掉，否则会删除不成功！)", function(r){
            		if(r){
            			$.ajax({
							type: "post",
							url: "DormitoryServlet?method=DeleteDormitory",
							data: {ids: ids},
							success: function(msg){
								if(msg == "success"){
									$.messager.alert("消息提醒","删除成功!","info");
									//刷新表格
									$("#dataList").datagrid("reload");
									$("#dataList").datagrid("uncheckAll");
								} else{
									$.messager.alert("消息提醒","删除失败!","warning");
									return;
								}
							}
						});
            		}
            	});
            }
	    });
	    
	  	
	  	//设置添加宿舍窗口
	    $("#addDialog").dialog({
	    	title: "添加宿舍",
	    	width: 420,
	    	height: 300,
	    	iconCls: "icon-add",
	    	modal: true,
	    	collapsible: false,
	    	minimizable: false,
	    	maximizable: false,
	    	draggable: true,
	    	closed: true,
	    	buttons: [
	    		{
					text:'添加',
					plain: true,
					iconCls:'icon-add',
					handler:function(){
						var validate = $("#addForm").form("validate");
						if(!validate){
							$.messager.alert("消息提醒","请检查你输入的数据!","warning");
							return;
						} else{
							$.ajax({
								type: "post",
								url: "DormitoryServlet?method=AddDormitory",
								data: $("#addForm").serialize(),
								success: function(msg){
									if(msg == "success"){
										$.messager.alert("消息提醒","添加成功!","info");
										//关闭窗口
										$("#addDialog").dialog("close");
										//清空原表格数据
										$("#add_sn").textbox('setValue', "");
										//$("#add_sex").combobox('setValue', "男");
										$("#add_floor").textbox('setValue', "");
										
										//重新刷新页面数据
							  			$('#dataList').datagrid("reload");
										
									} else{
										$.messager.alert("消息提醒",msg,"warning");
										return;
									}
								}
							});
						}
					}
				},
				{
					text:'重置',
					plain: true,
					iconCls:'icon-reload',
					handler:function(){
						$("#add_sn").textbox('setValue', "");
						$("#add_floor").textbox('setValue', "");
						//$("#add_sex").combobox('setValue', "男");
					}
				},
			]
	    });
	  	
	  	//设置编辑宿舍窗口
	    $("#editDialog").dialog({
	    	title: "修改宿舍信息",
	    	width: 420,
	    	height: 300,
	    	iconCls: "icon-edit",
	    	modal: true,
	    	collapsible: false,
	    	minimizable: false,
	    	maximizable: false,
	    	draggable: true,
	    	closed: true,
	    	buttons: [
	    		{
					text:'提交',
					plain: true,
					iconCls:'icon-edit',
					handler:function(){
						var validate = $("#editForm").form("validate");
						if(!validate){
							$.messager.alert("消息提醒","请检查你输入的数据!","warning");
							return;
						} else{
							$.ajax({
								type: "post",
								url: "DormitoryServlet?method=EditDormitory&t="+new Date().getTime(),
								data: $("#editForm").serialize(),
								success: function(msg){
									if(msg == "success"){
										$.messager.alert("消息提醒","更新成功!","info");
										//关闭窗口
										$("#editDialog").dialog("close");
										//刷新表格
										$("#dataList").datagrid("reload");
										$("#dataList").datagrid("uncheckAll");
							  			
									} else{
										$.messager.alert("消息提醒","更新失败!","warning");
										return;
									}
								}
							});
						}
					}
				},
				{
					text:'重置',
					plain: true,
					iconCls:'icon-reload',
					handler:function(){
						//清空表单
						$("#edit_sn").textbox('setValue', "");
						$("#edit_floor").textbox('setValue', "");
						//$("#edit_sex").combobox('setValue', "男");
					}
				}
			],
			onBeforeOpen: function(){
				var selectRow = $("#dataList").datagrid("getSelected");
				//设置值
				$("#edit-id").val(selectRow.id);
				$("#edit_sn").textbox('setValue', selectRow.sn);
				$("#edit_floor").textbox('setValue', selectRow.floor);
				$("#edit_buildingId").combobox('setValue', selectRow.buildingId);
				$("#edit_maxNumber").numberbox('setValue', selectRow.maxNumber);
			}
	    });
	   
	  	//搜索按钮监听
	  	$("#search").click(function(){
	  		$('#dataList').datagrid('load',{
	  			sn:$("#search-sn").textbox('getValue'),
	  			buildingId:$("#search-building").combobox('getValue')
	  		});
	  	});
	  	
	  	//添加宿舍下拉框
	  	$("#add_buildingId").combobox({
	  		url: "BuildingServlet?method=BuildingList&from=combox",
	  		onLoadSuccess: function(){
		  		//默认选择第一条数据
				var data = $(this).combobox("getData");;
				$(this).combobox("setValue", data[0].id);
	  		}
	  	});
	  	
	  //添加宿舍下拉框
	  	$("#edit_buildingId").combobox({
	  		url: "BuildingServlet?method=BuildingList&from=combox",
	  		onLoadSuccess: function(){
		  		//默认选择第一条数据
				var data = $(this).combobox("getData");;
				$(this).combobox("setValue", data[0].id);
	  		}
	  	});
	  	
	  //修改宿管下拉框
	  	$("#edit_dormitoryManagerId").combobox({
	  		url: "DormitoryManagerServlet?method=DormitoryManagerList&from=combox",
	  		onLoadSuccess: function(){
		  		//默认选择第一条数据
				var data = $(this).combobox("getData");;
				$(this).combobox("setValue", data[0].id);
	  		}
	  	});
	  	
	  //下拉框通用属性
	  	$("#add_buildingId,#search-building,#edit_buildingId").combobox({
	  		width: "150",
	  		height: "auto",
	  		valueField: "id",
	  		textField: "name",
	  		multiple: false, //可多选
	  		editable: false, //不可编辑
	  		method: "post",
	  	});
	});
	</script>
</head>
<body>
	<!-- 宿舍列表 -->
	<table id="dataList" cellspacing="0" cellpadding="0"> 
	    
	</table> 
	<!-- 工具栏 -->
	<div id="toolbar">
		<c:if test="${userType != 2}">
		<div style="float: left;"><a id="add" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true">添加</a></div>
			<div style="float: left;" class="datagrid-btn-separator"></div>
		</c:if>
		<div style="float: left;"><a id="edit" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true">修改</a></div>
			<div style="float: left;" class="datagrid-btn-separator"></div>
		<c:if test="${userType != 2}">
		<div style="float: left;"><a id="delete" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-some-delete',plain:true">删除</a></div>
		<div style="float: left;" class="datagrid-btn-separator"></div>
		</c:if>
		<div style="float: left; margin-left: 10px;">所属楼宇：<input id="search-building" class="easyui-combobox"/></div>
		<div style="float: left; margin-left: 10px;margin-top: 5px;">编号：<input id="search-sn" class="easyui-textbox" /></div>
		<div ><a id="search" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true">搜索</a></div>
	</div>
	
	<!-- 添加宿舍窗口 -->
	<div id="addDialog" style="padding: 10px">  
    	<form id="addForm" method="post">
	    	<table cellpadding="8" >
	    		<tr>
	    			<td>编号:</td>
	    			<td><input id="add_sn" style="width: 200px; height: 30px;" class="easyui-textbox" type="text" name="sn" data-options="required:true, missingMessage:'请填写编号'" /></td>
	    		</tr>
	    		<tr>
	    			<td>所属楼宇:</td>
	    			<td><select id="add_buildingId" class="easyui-combobox" data-options="editable: false, panelHeight: 'auto'" name="buildingId"></select></td>
	    		</tr>
	    		<tr>
	    			<td>所属楼层:</td>
	    			<td><input id="add_floor" style="width: 200px; height: 30px;" class="easyui-textbox" type="text" name="floor" data-options="required:true, missingMessage:'请填写所属楼层'"  /></td>
	    		</tr>
	    		<tr>
	    			<td>最大可住人数:</td>
	    			<td><input id="add_maxNumber" style="width: 200px; height: 30px;" class="easyui-numberbox" type="text" name="maxNumber" data-options="required:true, missingMessage:'请填写最大可住人数'"  /></td>
	    		</tr>
	    		
	    	</table>
	    </form>
	</div>
	
	<!-- 修改宿舍窗口 -->
	<div id="editDialog" style="padding: 10px">
		<form id="editForm" method="post">
	    	<input type="hidden" id="edit-id" name="id" />
	    	<table cellpadding="8" >
	    		<tr>
	    			<td>编号:</td>
	    			<td><input id="edit_sn" style="width: 200px; height: 30px;" class="easyui-textbox" type="text" name="sn" data-options="required:true, missingMessage:'请填写编号'" /></td>
	    		</tr>
	    		<tr>
	    			<td>所属楼宇:</td>
	    			<td><select id="edit_buildingId" class="easyui-combobox" data-options="editable: false, panelHeight: 'auto'" name="buildingId"></select></td>
	    		</tr>
	    		<tr>
	    			<td>所属楼层:</td>
	    			<td><input id="edit_floor" style="width: 200px; height: 30px;" class="easyui-textbox" type="text" name="floor" data-options="required:true, missingMessage:'请填写所属楼层'"  /></td>
	    		</tr>
	    		<tr>
	    			<td>最大可住人数:</td>
	    			<td><input id="edit_maxNumber" style="width: 200px; height: 30px;" class="easyui-numberbox" type="text" name="maxNumber" data-options="required:true, missingMessage:'请填写最大可住人数'"  /></td>
	    		</tr>
	    		
	    	</table>
	    </form>
	</div>
	
</body>
</html>