<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2021/6/4
  Time: 9:08
  To change this template use File | Settings | File Templates.

       需求分析：
            查询交易表中的每个阶段的记录的数量，按照从多到少向下排列形成一张漏斗图

             sql：
                select

                stage, count(*)

                from tbl_tran

                group by stage
                分组查询每个stage的记录条数



--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() +
            "://"+request.getServerName() +
            ":" + request.getServerPort()+
            request.getContextPath()+"/";
%>

<html>
<head>
    <base href="<%=basePath%>"/>
    <title>Title</title>
    <meta charset="utf-8">
    <!-- 引入 ECharts 文件 -->
    <script src="ECharts/echarts.min.js"></script>
    <script src="jquery/jquery-1.11.1-min.js"></script>

    <script>
        $(function (){
            // 页面加载完毕后绘制统计图
            getCharts();
        })


        function getCharts(){
            // alert("绘制图表")
            // 基于准备好的dom，初始化echarts实例
            var myChart = echarts.init(document.getElementById('main'));

            $.ajax({
                url:"getEcharts.do",
                type:"get",
                data:{},
                dataType:"json",
                success: function (data){

                    /**
                     * data:
                     *      {"total":100, "dataList":[{value: 60, name: '访问'},{value: 40, name: '咨询'}...]}
                     *
                     */
                    // 指定图表的配置项和数据，option就是我们要画的图
                    option = {
                        title: {
                            text: '交易漏斗图',
                            subtext: '统计交易阶段数量的漏斗图'
                        },
                        // tooltip: {
                        //     trigger: 'item',
                        //     formatter: "{a} <br/>{b} : {c}%"
                        // },
                        toolbox: {
                            feature: {
                                dataView: {readOnly: false},
                                restore: {},
                                saveAsImage: {}
                            }
                        },
                        legend: {
                            data: ['展现','点击','访问','咨询','订单']
                        },

                        series: [
                            {
                                name:'交易漏斗图',
                                type:'funnel',
                                left: '10%',
                                top: 60,
                                //x2: 80,
                                bottom: 60,
                                width: '80%',
                                // height: {totalHeight} - y - y2,
                                min: 0,
                                max: data.total,
                                minSize: '0%',
                                maxSize: '100%',
                                sort: 'descending',
                                gap: 2,
                                label: {
                                    show: true,
                                    position: 'inside'
                                },
                                labelLine: {
                                    length: 10,
                                    lineStyle: {
                                        width: 1,
                                        type: 'solid'
                                    }
                                },
                                itemStyle: {
                                    borderColor: '#fff',
                                    borderWidth: 1
                                },
                                emphasis: {
                                    label: {
                                        fontSize: 20
                                    }
                                },
                                data: data.dataList

                                //     [
                                //     {value: 60, name: '访问'},
                                //     {value: 40, name: '咨询'},
                                //     {value: 20, name: '订单'},
                                //     {value: 80, name: '点击'},
                                //     {value: 100, name: '展现'}
                                // ]
                            }
                        ]
                    };

                    // 使用刚指定的配置项和数据显示图表。
                    myChart.setOption(option);

                }
            })





        }


    </script>

</head>
<body>
123

<!-- 为 ECharts 准备一个具备大小（宽高）的 DOM -->
<div id="main" style="width: 600px;height:400px;"></div>
</body>
</html>
