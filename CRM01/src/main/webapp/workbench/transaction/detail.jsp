<%@ page import="com.zjut.domain.DicValue" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.zjut.domain.DicType" %>
<%@ page import="com.zjut.domain.Tran" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
String basePath = request.getScheme() +
"://"+request.getServerName() +
":" + request.getServerPort()+
request.getContextPath()+"/";


// 准备字典类型为stage的字典值列表
	List<DicValue> dvList = (List<DicValue>) application.getAttribute("stage");

	// 准备阶段和可能性之间的对应关系
	Map< String, String> pMap = (Map<String, String>) application.getAttribute("pMap");
	// 根据pMap，准备pMap中的key集合
	Set<String> set = pMap.keySet();

	// 准备前面正常阶段和后面丢失阶段的分界点下标
	int point = 0;
	for(int i = 0; i<dvList.size(); i++){
		// 取得每一个字典值(DicValue表中的一行记录)
		DicValue dv = dvList.get(i);
		// 取得每一行记录的value属性值stage
		String stage = dv.getValue();
		// 根据stage取得possibility
		String possibility = pMap.get(stage);
		// 如果找到了可能性为0，由于查出来的结果是按orderNo排序的，说明找到的第一个0就是分界点
		if("0".equals(possibility)){
			point = i;
			break;
		}
	}
%>

<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>"/>
	<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />

<style type="text/css">
.mystage{
	font-size: 20px;
	vertical-align: middle;
	cursor: pointer;
}
.closingDate{
	font-size : 15px;
	cursor: pointer;
	vertical-align: middle;
}
</style>
	
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

<script type="text/javascript">

	//默认情况下取消和保存按钮是隐藏的
	var cancelAndSaveBtnDefault = true;
	
	$(function(){
		$("#remark").focus(function(){
			if(cancelAndSaveBtnDefault){
				//设置remarkDiv的高度为130px
				$("#remarkDiv").css("height","130px");
				//显示
				$("#cancelAndSaveBtn").show("2000");
				cancelAndSaveBtnDefault = false;
			}
		});
		
		$("#cancelBtn").click(function(){
			//显示
			$("#cancelAndSaveBtn").hide();
			//设置remarkDiv的高度为130px
			$("#remarkDiv").css("height","90px");
			cancelAndSaveBtnDefault = true;
		});
		
		$(".remarkDiv").mouseover(function(){
			$(this).children("div").children("div").show();
		});
		
		$(".remarkDiv").mouseout(function(){
			$(this).children("div").children("div").hide();
		});
		
		$(".myHref").mouseover(function(){
			$(this).children("span").css("color","red");
		});
		
		$(".myHref").mouseout(function(){
			$(this).children("span").css("color","#E6E6E6");
		});
		
		
		//阶段提示框
		$(".mystage").popover({
            trigger:'manual',
            placement : 'bottom',
            html: 'true',
            animation: false
        }).on("mouseenter", function () {
                    var _this = this;
                    $(this).popover("show");
                    $(this).siblings(".popover").on("mouseleave", function () {
                        $(_this).popover('hide');
                    });
                }).on("mouseleave", function () {
                    var _this = this;
                    setTimeout(function () {
                        if (!$(".popover:hover").length) {
                            $(_this).popover("hide")
                        }
                    }, 100);
                });

		// 页面加载完毕后，局部刷新交易历史列表
		showHistoryList();

	});

	function showHistoryList(){
		$.ajax({
			url:"getHistoryList.do",
			data:{
				"tranId": "${tran.id}"
			},
			type:"get",
			dataType:"json",
			success: function (data){
				alert("返回交易历史记录列表")
				/**
				 * data:
				 * 		[{交易历史1}，{交易历史1}，{交易历史1}，。。]
				 */
				var html = "";
				$.each(data,function (i,n){
					html +='	<tr>';
					html +='		<td>'+ n.stage +'</td>';
					html +='		<td>'+ n.money +'</td>';
					html +='		<td>'+ n.possibility +'</td>';
					html +='		<td>' +n.expectedDate + '</td>';
					html +='		<td>'+n.createTime+'</td>';
					html +='		<td>'+ n.createBy +'</td>';
					html +='	</tr>';
				})
				$("#tbodyList").html(html);

			}

		})
	}

	/**
	 * 改变交易流程中的阶段
	 * 		stage：需要改变的阶段
	 * 		i:需要改变的阶段对应的下标
	 *
	 * @param stage
	 * @param i
	 */
	function changeStage(stage,i){
		// alert("改变交易阶段")
		$.ajax({
			url:"changeStage.do",
			data:{
				"stage": stage,
				"tranId": "${tran.id}",
				"money":"${tran.money}",  // 生成交易历史
				"expectedDate":"${tran.expectedDate}"  // 生成交易历史
			},
			type:"post",
			dataType: "json",
			success: function (data){
				/**
				 * data:
				 * 		{"success":true/false, "tran": tran}
				 */
				if(data.success){
					alert("交易状态后端修改成功")
					// 需要在前端修改 阶段、可能性、修改人、修改时间
					$("#stage").html(data.tran.stage);
					$("#possibility").html(data.tran.possibility);
					$("#editBy").html(data.tran.editBy);
					$("#editTime").html(data.tran.editTime);

					//修改图标显示
					changeIcon(stage,i);


				}else{
					alert("交易状态修改失败")
				}
			}
		})
	}

	function changeIcon(stage,index1){
		// 当前阶段
		var currentStage = stage;
		// 当前阶段可能性
		var currentPossibility = $("#possibility").html();
		// 当前阶段的下标
		var index = index1;
		// 前面正常阶段和后面丢失阶段的分界点下标
		var point = "<%=point%>";

		alert("当前阶段" + currentStage);
		alert("当前阶段可能性" + currentPossibility);
		alert("当前阶段下标" + index);
		alert("分界点" + point);

		// 如果当前阶段可能性为零，前七个一定为黑圈，后两个为红叉和黑叉
		if("0"==currentPossibility){
			for(var i =0; i<point; i++){
				// 前七个为黑圈
				// 黑圈------------------------
				// 1.移除原有样式
				$("#" + i).removeClass();
				// 2.添加新样式
				$("#"+i).addClass("glyphicon glyphicon-record mystage");
				// 3.为新样式赋予颜色
				$("#"+i).css("color","#000000");
			}
			// 后两个一个为红叉一个黑叉
			for(var i= point; i< <%=dvList.size()%>; i++){
				if(i == index){
					// 红叉-----------------------------------
					// 1.移除原有样式
					$("#" + i).removeClass();
					// 2.添加新样式
					$("#"+i).addClass("glyphicon glyphicon-remove mystage");
					// 3.为新样式赋予颜色
					$("#"+i).css("color","#FF0000")
				}else {
					// 黑叉-----------------------------------
					// 1.移除原有样式
					$("#" + i).removeClass();
					// 2.添加新样式
					$("#"+i).addClass("glyphicon glyphicon-remove mystage");
					// 3.为新样式赋予颜色
					$("#"+i).css("color","#000000")
				}

			}

			// 如果不为零，则前七个可能为绿圈、绿色标记、黑圈,后两个为黑叉
		}else{

			for(var i=0; i<point; i++){
				// 如果是当前阶段
				if(i == index){
					// 绿色标记-------------------
					// 1.移除原有样式
					$("#" + i).removeClass();
					// 2.添加新样式
					$("#"+i).addClass("glyphicon glyphicon-ok-circle mystage");
					// 3.为新样式赋予颜色
					$("#"+i).css("color","#90F790")


				// 如果小于当前阶段
				} else if(i<index){
					// 绿圈---------------------
					// 1.移除原有样式
					$("#" + i).removeClass();
					// 2.添加新样式
					$("#"+i).addClass("glyphiconglyphicon-map-marker mystage");
					// 3.为新样式赋予颜色
					$("#"+i).css("color","#90F790")


				// 如果大于当前阶段
				}else {
					// 黑圈--------------------------
					// 1.移除原有样式
					$("#" + i).removeClass();
					// 2.添加新样式
					$("#"+i).addClass("glyphicon glyphicon-record mystage");
					// 3.为新样式赋予颜色
					$("#"+i).css("color","#000000")
				}
			}

			for(var i= point; i< <%=dvList.size()%>; i++){
				// 黑叉-------------------------------
				// 1.移除原有样式
				$("#" + i).removeClass();
				// 2.添加新样式
				$("#"+i).addClass("glyphicon glyphicon-remove mystage");
				// 3.为新样式赋予颜色
				$("#"+i).css("color","#000000")

			}
			}




	}
	
	
</script>

</head>
<body>
	
	<!-- 返回按钮 -->
	<div style="position: relative; top: 35px; left: 10px;">
		<a href="javascript:void(0);" onclick="window.history.back();"><span class="glyphicon glyphicon-arrow-left" style="font-size: 20px; color: #DDDDDD"></span></a>
	</div>
	
	<!-- 大标题 -->
	<div style="position: relative; left: 40px; top: -30px;">
		<div class="page-header">
			<h3>${tran.name}<small>￥${tran.money}</small></h3>
		</div>
		<div style="position: relative; height: 50px; width: 250px;  top: -72px; left: 700px;">
			<button type="button" class="btn btn-default" onclick="window.location.href='edit.html';"><span class="glyphicon glyphicon-edit"></span> 编辑</button>
			<button type="button" class="btn btn-danger"><span class="glyphicon glyphicon-minus"></span> 删除</button>
		</div>
	</div>

	<!-- 阶段状态 -->
	<div style="position: relative; left: 40px; top: -50px;">
		阶段&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

		<%
			// 准备当前阶段
			Tran tran = (Tran) request.getAttribute("tran");
			String currentStage = tran.getStage();

			// 准备当前阶段的可能性
			String currentPossibility = pMap.get(currentStage);

			// 判断当前阶段
			// 如果当前阶段可能性为0，则前七个图表为黑圈，后两个一个黑叉一个红叉
			if("0".equals(currentPossibility)){
				for(int i =0; i<dvList.size(); i++){
					DicValue dv  = dvList.get(i);
					String listStage = dv.getValue();
					String listPossibility = pMap.get(listStage);

					if("0".equals(listPossibility)){
						// 说明是后两个，如果是当前阶段则为红叉
						if(listPossibility.equals(currentPossibility)){
							// 红叉----------------------------------------------

								%>
									<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')"
										  class="glyphicon glyphicon-remove mystage"
			  							data-toggle="popover" data-placement="bottom"
										  data-content="<%=dv.getText()%>"
			  							style="color: #FF0000;"></span>
		                        		-------------

									<%

                                }else {
                                    // 说明不是当前阶段，为黑叉
                                    // 黑叉-----------------------------------------
									%>

									<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')"
			 						 class="glyphicon glyphicon-remove mystage"
										  data-toggle="popover" data-placement="bottom" data-content="<%=dv.getText()%>"
			 						 style="color: #000000;"></span>
									-------------
									<%


                                }

                            }else {
                                // 不为零，说明遍历的是前七个，图标为黑圈
                                // 黑圈-------------------------------------------
									%>

							 <span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')"
			  					class="glyphicon glyphicon-record mystage"
			  					data-toggle="popover" data-placement="bottom" data-content="<%=dv.getText()%>"
								  style="color: #000000;"></span>
								-------------

									<%

                            }


                        }

                        // 当前阶段可能性不为零，前七个图标可能是绿圈，绿色标记，黑圈，后两个一定是黑叉
                    }else {

                        // 准备当前阶段的下标
                        int index = 0;
                        for(int i=0; i<dvList.size(); i++){
                            DicValue dv = dvList.get(i);
                            String stage = dv.getValue();
                            if(stage.equals(currentStage)){
                                index = i;
                                break;
                            }
                        }

                        for(int i=0; i<dvList.size(); i++){
                            DicValue dv = dvList.get(i);
                            String listStage = dv.getValue();
                            String listPossibilty = pMap.get(listStage);


                            if("0".equals(listPossibilty)){
                                // 说明是后两个，一定是黑叉
                                // 黑叉-----------------------------------
									%>

						<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')"
						  class="glyphicon glyphicon-remove mystage"
			  				data-toggle="popover" data-placement="bottom" data-content="<%=dv.getText()%>"
			  				style="color: #000000;"></span>
							-------------
									<%



                            }else{
                                // 前七个，可能是绿圈、绿色标记和黑圈
                                // 如果是当前阶段，则为绿色标记
                                if(i == index){
                                    // 绿色标记-----------------------------------
									%>

					<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')"
			 			 class="glyphicon glyphicon-ok-circle mystage"
			  				data-toggle="popover" data-placement="bottom" data-content="<%=dv.getText()%>"
			  				style="color: #90F790;"></span>
							-------------
									<%

                                    // 如果小于当前阶段，则为绿圈
                                }else if(i < index){
                                    // 绿圈-------------------------------------------
									%>

							<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')"
			  				class="glyphicon glyphicon-map-marker mystage"
			  				data-toggle="popover" data-placement="bottom" data-content="<%=dv.getText()%>"
			  				style="color: #90F790;"></span>
							-------------
							<%

                                    // 当前阶段之后，为黑圈
                                }else {
                                    // 黑圈-----------------------------------------
							%>

						<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')"
			  			class="glyphicon glyphicon-record mystage"
			  			data-toggle="popover" data-placement="bottom" data-content="<%=dv.getText()%>"
			  			style="color: #000000;"></span>
						-------------

						<%


                                }
                            }
                        }

                    }
                %>

        <%--		<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="资质审查" style="color: #90F790;"></span>--%>
<%--		-------------%>
<%--		<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="需求分析" style="color: #90F790;"></span>--%>
<%--		-------------%>
<%--		<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="价值建议" style="color: #90F790;"></span>--%>
<%--		-------------%>
<%--		<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="确定决策者" style="color: #90F790;"></span>--%>
<%--		-------------%>
<%--		<span class="glyphicon glyphicon-map-marker mystage" data-toggle="popover" data-placement="bottom" data-content="提案/报价" style="color: #90F790;"></span>--%>
<%--		-------------%>
<%--		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="谈判/复审"></span>--%>
<%--		-------------%>
<%--		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="成交"></span>--%>
<%--		-------------%>
<%--		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="丢失的线索"></span>--%>
<%--		-------------%>
<%--		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="因竞争丢失关闭"></span>--%>
<%--		-------------%>
<%--		<span class="closingDate">${tran.expectedDate}</span>--%>
	</div>
	
	<!-- 详细信息 -->
	<div style="position: relative; top: 0px;">
		<div style="position: relative; left: 40px; height: 30px;">
			<div style="width: 300px; color: gray;">所有者</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.owner}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">金额</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.money}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 10px;">
			<div style="width: 300px; color: gray;">名称</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.name}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">预计成交日期</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.expectedDate}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 20px;">
			<div style="width: 300px; color: gray;">客户名称</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.customerId}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">阶段</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="stage">${tran.stage}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 30px;">
			<div style="width: 300px; color: gray;">类型</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.type}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">可能性</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="possibility">${tran.possibility}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 40px;">
			<div style="width: 300px; color: gray;">来源</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.source}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">市场活动源</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.activityId}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 50px;">
			<div style="width: 300px; color: gray;">联系人名称</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${tran.contactsId}</b></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 60px;">
			<div style="width: 300px; color: gray;">创建者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${tran.createBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;">${tran.createTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 70px;">
			<div style="width: 300px; color: gray;">修改者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b id="editBy">${tran.editBy}&nbsp;&nbsp;</b><small id="editTime" style="font-size: 10px; color: gray;">${tran.editTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 80px;">
			<div style="width: 300px; color: gray;">描述</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					${tran.description}
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 90px;">
			<div style="width: 300px; color: gray;">联系纪要</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					&nbsp;
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 100px;">
			<div style="width: 300px; color: gray;">下次联系时间</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>&nbsp;</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
	</div>
	
	<!-- 备注 -->
	<div style="position: relative; top: 100px; left: 40px;">
		<div class="page-header">
			<h4>备注</h4>
		</div>
		
		<!-- 备注1 -->
		<div class="remarkDiv" style="height: 60px;">
			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
			<div style="position: relative; top: -40px; left: 40px;" >
				<h5>哎呦！</h5>
				<font color="gray">交易</font> <font color="gray">-</font> <b>动力节点-交易01</b> <small style="color: gray;"> 2017-01-22 10:10:10 由zhangsan</small>
				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
				</div>
			</div>
		</div>
		
		<!-- 备注2 -->
		<div class="remarkDiv" style="height: 60px;">
			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
			<div style="position: relative; top: -40px; left: 40px;" >
				<h5>呵呵！</h5>
				<font color="gray">交易</font> <font color="gray">-</font> <b>动力节点-交易01</b> <small style="color: gray;"> 2017-01-22 10:20:10 由zhangsan</small>
				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
				</div>
			</div>
		</div>
		
		<div id="remarkDiv" style="background-color: #E6E6E6; width: 870px; height: 90px;">
			<form role="form" style="position: relative;top: 10px; left: 10px;">
				<textarea id="remark" class="form-control" style="width: 850px; resize : none;" rows="2"  placeholder="添加备注..."></textarea>
				<p id="cancelAndSaveBtn" style="position: relative;left: 737px; top: 10px; display: none;">
					<button id="cancelBtn" type="button" class="btn btn-default">取消</button>
					<button type="button" class="btn btn-primary">保存</button>
				</p>
			</form>
		</div>
	</div>
	
	<!-- 阶段历史 -->
	<div>
		<div style="position: relative; top: 100px; left: 40px;">
			<div class="page-header">
				<h4>阶段历史</h4>
			</div>
			<div style="position: relative;top: 0px;">
				<table id="activityTable" class="table table-hover" style="width: 900px;">
					<thead>
						<tr style="color: #B3B3B3;">
							<td>阶段</td>
							<td>金额</td>
							<td>可能性</td>
							<td>预计成交日期</td>
							<td>创建时间</td>
							<td>创建人</td>
						</tr>
					</thead>
					<tbody id="tbodyList">
<%--						<tr>--%>
<%--							<td>资质审查</td>--%>
<%--							<td>5,000</td>--%>
<%--							<td>10</td>--%>
<%--							<td>2017-02-07</td>--%>
<%--							<td>2016-10-10 10:10:10</td>--%>
<%--							<td>zhangsan</td>--%>
<%--						</tr>--%>
<%--						<tr>--%>
<%--							<td>需求分析</td>--%>
<%--							<td>5,000</td>--%>
<%--							<td>20</td>--%>
<%--							<td>2017-02-07</td>--%>
<%--							<td>2016-10-20 10:10:10</td>--%>
<%--							<td>zhangsan</td>--%>
<%--						</tr>--%>
<%--						<tr>--%>
<%--							<td>谈判/复审</td>--%>
<%--							<td>5,000</td>--%>
<%--							<td>90</td>--%>
<%--							<td>2017-02-07</td>--%>
<%--							<td>2017-02-09 10:10:10</td>--%>
<%--							<td>zhangsan</td>--%>
<%--						</tr>--%>
					</tbody>
				</table>
			</div>
			
		</div>
	</div>
	
	<div style="height: 200px;"></div>
	
</body>
</html>