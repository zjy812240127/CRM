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
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<%--	JavaScript导包时要注意从上往下的顺序，比如现有JQuery，在导入基于JQuery的扩展包--%>
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

	<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
	<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
	<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>


	<script type="text/javascript">

	$(function(){
		// 为创建按钮绑定事件，打开添加操作的模态窗口
		$("#addBtn").click(function (){

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

			/**
			 * 操作模态窗口的方法：
			 * 		得到窗口的JQuery对象，调用modal方法，传递两个参数：
			 * 			show：打开模态窗口
			 * 			hide：隐藏窗口
			 */
			// alert("可以随意扩充点击该按钮的功能");
			// $("createActivityModal").modal("show");

			/**
			 * 在打开模态窗口前先走一次后台，取到数据库里的数据
			 * 		将数据填充到模态窗口中
			 * 	为了避免更新整个页面
			 * 		采用ajax来请求数据
			 */
			$.ajax({
				url: "getUserList.do",
				data: {
						// 直接查询不需要传递参数
						},
				type: "get",  // 增删改+账号密码登录用post
				dataType: "json",
				success: function (data){

					/**
					 * 		data数据格式是一个User数组
					 * 			[{User1},{User2},......]
					 */
					var html = "<option></option>"
					$.each(data,function (i,n){
						html += "<option value ='" + n.id + "'>" + n.name +"</option>";
					})
					// 动态更新下拉框列表
					$("#create-Owner").html(html);

					/**
					 * 给下拉列表填充默认值，是该登录的用户的名字.val()
					 * js中可以使用el表达式，直接从Session中获取用户的信息
					 * 		但是js中的el表达式必须用“”括起来
					 */
					var id = "${user.id}";
					// 填充下拉列表的默认值
					$("#create-Owner").val(id);

					// 以上模态窗口中的数据填充完后，再打开模态窗口
					$("#createActivityModal").modal("show");

				}
			})


		})

		/**
		 * 为保存按钮添加事件，来执行添加市场活动操作
		 */
		$("#saveBtn").click(function (){

			// alert("执行ajax添加市场活动");
			$.ajax({
				url: "save.do",
				type: "post",  // 增删改用post
				data: {
					"owner": $.trim($("#create-Owner").val()),
					"name": $.trim($("#create-name").val()),
					"startDate": $.trim($("#create-startDate").val()),
					"endDate": $.trim($("#create-endDate").val()),
					"cost": $.trim($("#create-cost").val()),
					"description": $.trim($("#create-description").val()),
				},
				dataType: "json",
				success: function (data){
					/**
					 * data:
					 * 		{"success":true/false}
					 */
					if (data.success){
						/**
						 * 添加成功后
						 * 		刷新市场活动列表（局部刷新）
						 * 		关闭添加操作的模态窗口
						 *
						 */

						// 清空添加操作模态窗口中的数据，以免下次再添加还存在已有的数据
						// 提交表单
						// $("#activityAddForm").submit();
						/**
						 * 对于表单的jquery对象，提供了submit方法让我们在js中可以提交表单
						 * 但是虽然idea为我们提示了存在表单的reset方法，该方法再jquery中无法用
						 * 		但是，我们可以用原生的dom对象来执行reset操作
						 * 			jquery对象转dom对象：
						 * 				jquery对象[下标]
						 * 			dom对象转jquery对象
						 * 				$(dom)
						 */
						// 使用原生js语言的dom对象中的reset方法来重置form中的数据
						$("#activityAddForm")[0].reset();

						$("#createActivityModal").modal("hide");

					}else{
						// 表示添加失败
						alert("添加市场活动失败");
					}
				}
			})

		})



        // 页面加载完毕后，执行一个函数，向后台发送一个ajax请求，局部刷新列表，分页+limit
        pageList(1,2);

		// 为查询按钮绑定刷新市场列表事件
        $("#searchBtn").click(function (){

        	// 点击查询按钮的时候，要将搜索框中的信息保存到隐藏域中，便于下次点击分页组件而不是查询的时候可以拿到原来的值
			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startDate").val()));
			$("#hidden-endDate").val($.trim($("#search-endDate").val()));
			pageList(1,2);
        })

        // 为删除按钮添加事件

        $("#deleteBtn").click(function (){
            // 被选中的市场活动
            var $xz = $("input[name = xz]:checked");
            if  ($xz.length == 0){
                alert("请选择要删除的对象");
            }  else {
                // 肯定选择了，可能是一个或者多个市场活动
                // 这里要传输好几个对象的id，一个键对应多个值，所以不能用ajax里data传统的数据结构传
                // data里面要填入自己拼接的param数据
				if(confirm("确定删除数据吗？")){
					var param = "";
					for (var i =0; i < $xz.length ; i++){
						// 因为每个标签的value值就是n.id，所以要取得每个dom对象的value值
						// $(dom对象) = jquery对象
						// jquery对象[下标] = dom对象
						param += "id=" + $($xz[i]).val();
						// 地址栏中多个参数之间用&隔开，所以，只要不是最后一个dom对象，在后面就要加一个符号&
						if (i < $xz.length - 1){
							param += "&";
						}

					}
					// alert(param);
					$.ajax({
						url: "delete.do",
						data: param ,
						type: "post",
						dataType:"json",
						success: function (data){
							// 只需要返回success或者false
							if (data.success){
								alert("删除成功")
								// 删除记录之后再次刷新展示列表
								pageList(1,2);
							}else {
								alert("删除市场活动失败");
							}
						}
					})
				}

            }

        })

		// 为全选按钮添加触发事件
		$("#qx").click(function (){
			// 选择器 name
			// 点击全选按钮，则name为xz的都被选中
			// prop(a,b)的意思：如果b条件满足了，那么执行a操作
			$("input[name=xz]").prop("checked",this.checked);
		})



		  // 选中所有选项则将全选按钮变为选中
		  // 		$("input[name=xz]").click(function(){
		  // 		 	alter(123);
		  // 		})
		  // 	以前做法不行，因为动态生成的元素，不能以普通形式来绑定事件
		  //
		  // 		动态生成的元素要以on的方式来触发事件
		  // 			$（需要绑定的元素的有效外层元素）.on(绑定事件的方式，需要绑定的元素的jquery对象，回调函数)
		  // 			有效外层：一直往外找，直到该元素不是js动态语句中拼接的（html中写死的）

		$("#activityBody").on("click",$("input[name=xz]"),function (){
			// 表单对象属性选择过滤器
			$("#qx").prop("checked",$("input[name=xz]").length == $("input[name=xz]:checked").length)
		})




        /**
         * 对于所有的关系型数据库，做前端的分页操作的基础组件
         *      pageNo 和 pageSize
         * @param pageNo  页码
         * @param pageSize  每页展现的记录数
         *
         * 哪些情况下要调用该方法，刷新市场活动列表
         *      1）点击左侧市场活动按钮
         *      2）点击添加、修改、删除按钮
         *      3）点击查询按钮
         *      4）点击分页组件按钮
         */
		function pageList(pageNo, pageSize){
		    // 每次刷新列表的时候将全选框的钩去掉，防止刷新后列表中的活动都没被选择，而全选框还是选中状态
            $("#qx").prop("checked",false);

		    // 查询前，将隐藏域中的数据取出来重新赋值给搜索框，避免用户输入残留的搜索内容，但不想按照该检索条件搜索
			$("#search-name").val($.trim($("#hidden-name").val()));
			$("#search-owner").val($.trim($("#hidden-owner").val()));
			$("#search-startDate").val($.trim($("#hidden-startDate").val()));
			$("#search-endDate").val($.trim($("#hidden-endDate").val()));

            $.ajax({
                url:"pageList.do",
                data:{
                        // 页码和数据条数每次查询都有
                        "pageNo": pageNo,
                        "pageSize": pageSize,
                        // 以下四个数据不一定每次查询都有数据，动态调用sql语句来避免为空字符串的情况 where if
                        "name": $.trim($("#search-name").val()),
                        "owner": $.trim($("#search-owner").val()),
                        "startDate": $.trim($("#search-startDate").val()),
                        "endDate": $.trim($("#search-endDate").val())
                    },
                type:"get",
                dataType:"json",
                success: function (data){
                    /**
                     * data
                     *      我们需要的：市场活动列表
                     *         [{市场活动1}，{2}，{3}] List<Activity> aList
                     *      分页插件需要的，查询到的总记录数
                     *          {"total": 100}  int total
                     *      返回的json数据：{"total": 100, "dataList":[{市场活动1}，{2}，{3}]}
                     *
                     *
                     */

                    // 动态改变市场活动列表
                    var html = "";

                    $.each(data.dataList, function (i,n){
                        /**
                         * data由于包含了两个属性，这里只要遍历市场活动列表属性就行
                         *      每一个n代表一个市场活动
                         */
						// alert(n.name);
                        // “ 与'相互嵌套，"里不能有'
                       html +=  '<tr class="active">';
                       html +=  '<td><input type="checkbox" name="xz" value ="'+n.id+'"/></td>';
                       html +=  '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.html\';">'+n.name+'</a></td>';
                       html +=  '<td>'+ n.owner + '</td>';
                       html +=  '<td>'+ n.startDate +'</td>';
                       html +=  '<td>'+ n.endDate +'</td>';
                       html +=  '</tr>';


                    })

                    // 更新市场活动列表
                    $("#activityBody").html(html);

                    // 计算总页数
					var totalPages = data.total % pageSize == 0 ? data.total/pageSize : parseInt(data.total/pageSize) + 1;
                    // 结合分页插件对前端进行分页信息
					$("#activityPage").bs_pagination({
						currentPage: pageNo, // 页码
						rowsPerPage: pageSize, // 每页显示的记录条数
						maxRowsPerPage: 20, // 每页最多显示的记录条数
						totalPages: totalPages, // 总页数
						totalRows: data.total, // 总记录条数

						visiblePageLinks: 3, // 显示几个卡片

						showGoToPage: true,
						showRowsPerPage: true,
						showRowsInfo: true,
						showRowsDefaultInfo: true,

						// 该回调函数是在点击分页组件的时候被调用的
						onChangePage : function(event, data){
							// pageList是我们上面写的方法，但是此处方法的两个参数是插件写的，不能改
							pageList(data.currentPage , data.rowsPerPage);
						}
					});



				}

            })




        }


	});
	
</script>
</head>
<body>
<input type="hidden" id="hidden-name"/>
<input type="hidden" id="hidden-owner"/>
<input type="hidden" id="hidden-startDate"/>
<input type="hidden" id="hidden-endDate"/>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">

					<form id = "activityAddForm" class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-Owner">

<%--								 	此处动态查询后端数据，由js语句添加数据--%>

								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-name">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
<%--								form-control 和 time属于两个不同的类，可以给一个对象增加多个类，用空格隔开，js中调用任意一个类都可以--%>
								<input type="text" class="form-control time" id="create-startDate" readonly>
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">

<%--					data-dismiss="modal":--%>
<%--					表示关闭模态窗口--%>

					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id = "saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-marketActivityOwner">
								  <option>zhangsan</option>
								  <option>lisi</option>
								  <option>wangwu</option>
								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-marketActivityName" value="发传单">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-startTime" value="2020-10-10">
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-endTime" value="2020-10-20">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost" value="5,000">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-describe">市场活动Marketing，是指品牌主办或参与的展览会议与公关市场活动，包括自行主办的各类研讨会、客户交流会、演示会、新产品发布会、体验会、答谢会、年会和出席参加并布展或演讲的展览会、研讨会、行业交流会、颁奖典礼等</textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" data-dismiss="modal">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="search-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button" id="searchBtn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">


<%--					data-toggle="modal":--%>
<%--						表示点击该按钮会执行打开模态窗口的操作--%>
<%--					data-target="#createActivityModal"：--%>
<%--						表示要打开的模态窗口div的id是--%>
<%--					通过这种方法打开模态窗口有缺陷，无法对按钮功能进行扩充--%>
<%--					开发中对于触发模态窗口的操作不能写死在元素中，要由我们自己写js代码来实现--%>

				  <button type="button" class="btn btn-primary" id="addBtn" data-toggle="modal" data-target="#createActivityModal"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" data-toggle="modal" data-target="#editActivityModal"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" id="deleteBtn" class="btn btn-danger"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
<%--						<tr class="active">--%>
<%--							<td><input type="checkbox" /></td>--%>
<%--							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.html';">发传单</a></td>--%>
<%--                            <td>zhangsan</td>--%>
<%--							<td>2020-10-10</td>--%>
<%--							<td>2020-10-20</td>--%>
<%--						</tr>--%>
<%--                        <tr class="active">--%>
<%--                            <td><input type="checkbox" /></td>--%>
<%--                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.html';">发传单</a></td>--%>
<%--                            <td>zhangsan</td>--%>
<%--                            <td>2020-10-10</td>--%>
<%--                            <td>2020-10-20</td>--%>
<%--                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">
				<div id="activityPage"></div>
			</div>
			
		</div>
		
	</div>
</body>
</html>