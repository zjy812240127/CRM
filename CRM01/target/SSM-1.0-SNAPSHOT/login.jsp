<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
String basePath = request.getScheme() +
"://"+request.getServerName() +
":" + request.getServerPort()+
request.getContextPath()+"/";
%>

<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>"/>
<meta charset="UTF-8">
<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="js/jquery-3.4.1.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

	<script>
		$(function (){

		    // 设置顶层窗口和当前窗口，格式固定不用背
		    if(window.top != window){
		        window.top.location = window.location;
            }

			// 每次刷新清空里面的内容
			$("#loginAct").val("");

			// 页面加载完后自动获取光标闪烁，自动失去交点则：blur();
			$("#loginAct").focus();

			// 为登录按钮绑定事件
			$("#loginBtn").click(function (){
				login();
			})

			$(window).keydown(function (event){
				// alert(event.keyCode);
				// event可以读取按下的键
				if (event.keyCode == 13){
					// enter键的ascii为13
					login();
				}
			})

		})

		// 重复的功能代码封装成函数，要写在$（function()）外面
		function login(){
			// alert("验证登录");

			/**
			 * 验证账号密码不能为空
			 * 取得输入的账号密码
			 * 用$.trim(文本)去除左右空格
			 */
			var loginAct = $.trim($("#loginAct").val());
			var loginPwd = $.trim($("#loginPwd").val());
			if(loginAct == "" || loginPwd == ""){
				// .html("")往标签中间写入数据
				$("#msg").html("账号密码不能为空");
				// 如果账号密码未输入，则在此处退出函数，停止执行后续代码
				return false;

			}

			$.ajax({
				url: "queryUser.do",
				data:
						{
							"loginAct":loginAct,
							"loginPwd":loginPwd
						},
				type:"post",
				dataType: "json",
				success: function (data){
					/**
					 * data返回参数:
					 * 		{"success": true/false; "msg":什么原因导致的登录失败}
					 */

					// 如果登录成功
					if (data.success){
						// 跳转到工作台的初始地址
						window.location.href = "workbench/index.jsp";
					} else{
						// 失败就更新登录异常信息
						// data是返回的JsonResult对象，读取的是它的属性
						$("#msg").html(data.message);
						// $("#msg").html(data.success);
					}
				}
			})

		}
	</script>


</head>
<body>

<div style="position: absolute; top: 0px; left: 0px; width: 60%;">
	<img src="image/IMG_7114.JPG" style="width: 100%; height: 90%; position: relative; top: 50px;">
</div>
<div id="top" style="height: 50px; background-color: #3C3C3C; width: 100%;">
	<div style="position: absolute; top: 5px; left: 0px; font-size: 30px; font-weight: 400; color: white; font-family: 'times new roman'">CRM &nbsp;<span style="font-size: 12px;">&copy;2017&nbsp;动力节点</span></div>
</div>

<div style="position: absolute; top: 120px; right: 100px;width:450px;height:400px;border:1px solid #D5D5D5">
	<div style="position: absolute; top: 0px; right: 60px;">
		<div class="page-header">
			<h1>登录</h1>
		</div>
		<form action="workbench/index.jsp" class="form-horizontal" role="form">
			<div class="form-group form-group-lg">
				<div style="width: 350px;">
					<input class="form-control" type="text" placeholder="用户名" id="loginAct">
				</div>
				<div style="width: 350px; position: relative;top: 20px;">
					<input class="form-control" type="password" placeholder="密码" id="loginPwd">
				</div>
				<div class="checkbox"  style="position: relative;top: 30px; left: 10px;">
<%--					登录错误提示消息--%>
					<span id="msg" style="color: red"></span>

				</div>
<%--				类型submit改为button，防止未输入账号密码点击直接跳转页面，可以设置按钮的触发事件来调整页面--%>
				<button type="button" id = "loginBtn" class="btn btn-primary btn-lg btn-block"  style="width: 350px; position: relative;top: 45px;">登录</button>
			</div>
		</form>
	</div>
</div>
</body>
</html>