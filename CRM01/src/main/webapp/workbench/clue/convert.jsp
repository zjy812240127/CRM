<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
String basePath = request.getScheme() +
"://"+request.getServerName() +
":" + request.getServerPort()+
request.getContextPath()+"/";


//	String fullname = request.getParameter("fullname");
//	String id = request.getParameter("id");
//	String appellation = request.getParameter("appellation");
//	String company = request.getParameter("company");
//	String owner = request.getParameter("owner");
%>


<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>"/>
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>


<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<script type="text/javascript">
	$(function(){
		// 设置时间选择插件，datetimepicker（bootstrap官网有对应的时间插件包可以下载 ）
		// .time表示所有class值为time的对象都绑定一个时间选择器
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});



		$("#isCreateTransaction").click(function(){
			if(this.checked){
				$("#create-transaction2").show(200);
			}else{
				$("#create-transaction2").hide(200);
			}
		});

		// 为放大镜绑定事件
		$("#openSearchModalBtn").click(function (){
			$("#searchActivityModal").modal("show");
		})

		// 为搜索市场活动源绑定事件
		$("#aname").keydown(function (event){

			// 如果敲击的是回车
			if(event.keyCode == 13){

				$.ajax({
					url:"changeSerachActivity.do",
					data:{
						"aName": $.trim($("#aname").val())
					},
					type:"get",
					dataType:"json",
					success: function (data){
						/**
						 * data:
						 * 		[{市场活动1}，{市场活动2}，{市场活动3}，]
						 */
						var html = "";
						$.each(data,function (i,n){
							html +='<tr>';
							html +='<td><input type="radio" value="'+n.id+'" name="xz"/></td>';
							html +='<td id="e'+n.id+'">'+n.name+'</td>';
							html +='<td>'+n.startDate+'</td>';
							html +='<td>'+n.endDate+'</td>';
							html +='<td>'+n.owner+'</td>';
							html +='</tr>';
						})
						$("#changeSA").html(html);
					}
				})

				return false;
			}


		})

		// 为提交按钮绑定事件
		$("#submitActivityBtn").click(function (){
			/**
			 * 把市场活动名赋给文本框
			 * 把市场活动id赋给隐藏域
			 * 关闭模态窗口
			 */
			// 取得选中的市场活动的id
			var $xz = $("input[name=xz]:checked");
			var id = $xz.val();

			// 取得选中的市场活动的name，td标签的html
			var name = $("#e"+id).html()
			$("#activityId").val(id);
			$("#activityName").val(name);
			$("#searchActivityModal").modal("hide");
		})

		// 为转换按钮绑定事件：分两种情况，需要为客户创建交易和不需要为客户创建交易
		// 因为不需要局部刷新，所以用传统请求的方式（非ajax）
		$("#convertBtn").click(function (){

			if($("#isCreateTransaction").prop("checked")){
				alert("需要创建交易")
				// 需要给后台传多个参数，考虑用提交表单的形式（post形式），可以传多个参数，长度不限
				$("#tramForm").submit();

			}else {
				alert("不需要创建交易")
				// 只需要给后台传一个线索id就行，直接地址栏形式跳转
				window.location.href="convert.do?clueId=${param.id}";
			}


		})

	});
</script>

</head>
<body>
	
	<!-- 搜索市场活动的模态窗口 -->
	<div class="modal fade" id="searchActivityModal" role="dialog" >
		<div class="modal-dialog" role="document" style="width: 90%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">搜索市场活动</h4>
				</div>
				<div class="modal-body">
					<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
						<form class="form-inline" role="form">
						  <div class="form-group has-feedback">
						    <input type="text" class="form-control" id="aname" style="width: 300px;" placeholder="请输入市场活动名称，支持模糊查询">
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td></td>
								<td>名称</td>
								<td>开始日期</td>
								<td>结束日期</td>
								<td>所有者</td>
								<td></td>
							</tr>
						</thead>
						<tbody id="changeSA">
<%--							<tr>--%>
<%--								<td><input type="radio" name="activity"/></td>--%>
<%--								<td>发传单</td>--%>
<%--								<td>2020-10-10</td>--%>
<%--								<td>2020-10-20</td>--%>
<%--								<td>zhangsan</td>--%>
<%--							</tr>--%>
<%--							<tr>--%>
<%--								<td><input type="radio" name="activity"/></td>--%>
<%--								<td>发传单</td>--%>
<%--								<td>2020-10-10</td>--%>
<%--								<td>2020-10-20</td>--%>
<%--								<td>zhangsan</td>--%>
<%--							</tr>--%>
						</tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" id="submitActivityBtn">提交</button>
				</div>
			</div>
		</div>
	</div>

	<div id="title" class="page-header" style="position: relative; left: 20px;">
<%--		<h4>转换线索 <small><%=fullname%><%=appellation%>-<%=company%></small></h4>--%>
<%--		用el表达式取值，但是由于这些参数不在域中，所以要用param取，相当于request.getParameter("key")--%>
		<h4>转换线索 <small>${param.fullname}${param.appellation}-${param.company}</small></h4>
	</div>
	<div id="create-customer" style="position: relative; left: 40px; height: 35px;">
<%--		新建客户：<%=company%>--%>
		新建客户：${param.company}
	</div>
	<div id="create-contact" style="position: relative; left: 40px; height: 35px;">
<%--		新建联系人：<%=fullname%><%=appellation%>--%>
		新建联系人：${param.fullname}${param.appellation}
	</div>
	<div id="create-transaction1" style="position: relative; left: 40px; height: 35px; top: 25px;">
		<input type="checkbox" id="isCreateTransaction"/>
		为客户创建交易
	</div>
	<div id="create-transaction2" style="position: relative; left: 40px; top: 20px; width: 80%; background-color: #F7F7F7; display: none;" >
	
		<form id = "tramForm" action="convert.do" method="post">
<%--			用一个标记参数来给后台判断是否需要创建交易--%>
			<input type="hidden" name = "flag" value="a">

		  <input type="hidden" name = "clueId" value="${param.id}">
			<div class="form-group" style="width: 400px; position: relative; left: 20px;">
		    <label for="amountOfMoney">金额</label>
		    <input type="text" class="form-control" id="amountOfMoney" name = "money">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="tradeName">交易名称</label>
		    <input type="text" class="form-control" id="tradeName" name = "name">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="expectedClosingDate">预计成交日期</label>
		    <input type="text" class="form-control time" id="expectedClosingDate" name = "expectedDate">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="stage">阶段</label>
		    <select id="stage"  class="form-control" name = "stage">
		    	<option></option>
		    	<c:forEach items="${stage}" var="s">
					<option value="${s.value}">${s.text}</option>
				</c:forEach>
		    </select>
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="activity">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);" id="openSearchModalBtn" style="text-decoration: none;"><span class="glyphicon glyphicon-search"></span></a></label>
<%--		    <input type="text" class="form-control" id="activityName" placeholder="点击上面搜索" readonly>--%>
		    <input type="text" class="form-control" id="activityName" placeholder="点击上面搜索" readonly>
		  	<input type="hidden" id = "activityId" name = "activityId"/>
		  </div>
		</form>
		
	</div>
	
	<div id="owner" style="position: relative; left: 40px; height: 35px; top: 50px;">
		记录的所有者：<br>
<%--		<b><%=owner%></b>--%>
		<b>${param.owner}</b>
	</div>
	<div id="operation" style="position: relative; left: 40px; height: 35px; top: 100px;">
		<input class="btn btn-primary" type="button" id="convertBtn" value="转换">
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input class="btn btn-default" type="button" value="取消">
	</div>
</body>
</html>