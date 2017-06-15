var daytj =new Array();
var monthtj =new Array();
var yeartj =new Array();
var alltj =new Array();
var categories = ['预审','咨询','受理','即办','承诺','补办','挂起','办结'];//,'超期','提前'];
for (x in categories)
{	
	daytj.push(0);
	monthtj.push(0);
	yeartj.push(0);
	alltj.push(0);
	
}

$(function(){	
	workT();	
	onQuerypic(1,"审招委漂浮窗",2);
	onQuery(1,"动态要闻",7,'{{ConfigInfo.Dtyw_isout}}');
	onQuerypic(1,"动态要闻",4,'{{ConfigInfo.Dtyw_isout}}');
	onQuery(1,"考核考评",7,'{{ConfigInfo.Khkp_isout}}');
	onQuery(1,"改革创新",7,'{{ConfigInfo.Ggcx_isout}}');
	onQuery(1,"公示公告",7,'{{ConfigInfo.Gsgg_isout}}');
	//onQuery(1,"清单",4);
	//onQuery(1,"网上办事分厅",1);
	onbjTJ("day");
	onbjTJ("month");
	onbjTJ("year");
	onbjTJ("all");
	
});
function onQuery(start,cname,limit,isout){
	var cmd = new LEx.Command("app.icity.govservice.GovProjectCmd");
	cmd.setParameter("start",(start-1)*limit);
	//默认只显示通过审核的数据    cmd.setParameter("CHECKS@=", '1');
    cmd.setParameter("page",start);
	cmd.setParameter("limit", limit);
	cmd.setParameter("name", cname);
	cmd.setParameter("open", isout);
	//alert('{{webrg}}'.substring(0,4));	
	var ret =  cmd.execute("getContentInfo");
	if(!ret.error){
		if(ret.total>0){		
		if(cname=="动态要闻"){
			$('#dtywul').html(LEx.processDOMTemplate('MattersListTemplate',ret));
		}else if(cname=="考核考评"){
			$('#khkpul').html(LEx.processDOMTemplate('MattersListTemplate',ret));
		}else if(cname=="改革创新"){
			$('#ggcxul').html(LEx.processDOMTemplate('MattersListTemplate',ret));
		}else if(cname=="公示公告"){
			$('#gsggul').html(LEx.processDOMTemplate('MattersListTemplate1',ret));
		}//else if(cname=="清单"){
			//$('#qdul').html(LEx.processDOMTemplate('MattersListTemplate_tp',ret));
		//}else if(cname=="网上办事分厅"){
			//$('#wsbsftul').html(LEx.processDOMTemplate('MattersListTemplate',ret));
		//}
		}
	}else{
		LEx.alert(ret.error);
	}
}

function formatDate(obj) {
	return LEx.util.Format.formatDate(obj);
}
function onQuerypic(start,cname,limit,isout){
	var cmd = new LEx.Command("app.icity.govservice.GovProjectCmd");
	//默认只显示通过审核的数据
    cmd.setParameter("CHECKS@=", '1');
	cmd.setParameter("start",(start-1)*limit);
	cmd.setParameter("sort"," ID DESC");
	cmd.setParameter("limit", limit);
	cmd.setParameter("name", cname);
	cmd.setParameter("open", isout);
	cmd.setParameter("picModel", "1");
	var ret =  cmd.execute("getContentInfo");
	if(!ret.error){
		if(ret.total>0){		
		if(cname=="动态要闻"){
			var d_len = ret.total;
			if(d_len>limit){
				d_len=limit;
			}
			$('#indexpic').html(LEx.processDOMTemplate('MattersListTemplatep',ret));
			$('#p1').html(LEx.processDOMTemplate('MattersListTemplatep1',ret));
		}else if(cname=="审招委漂浮窗"){
			$('#ad1').html(LEx.processDOMTemplate('MattersListTemplatepfc',ret));
		}
		//}
		}
	}else{
		LEx.alert(ret.error);
	}
}

function onbjTJ(tjsj){
	var cmd = new LEx.Command("app.icity.ServiceCmd");//zs_szw.app.project.ProjectIndexCmd");
	//cmd.setParameter("XZQHDM@=", '{{webrg}}');
	cmd.setParameter("type", tjsj);
	var ret =  cmd.execute("getBjtjCacheZs");//getStateTotalZS");
	//var retstr='[{"ACCEPT":9,"CHENGNUO":0,"COMPLETE":1,"CONSULT":0,"CORRECTION":0,"EARLYFINISH":0,"INTERNET":10,"JIBAN":0,"OVERTIME":0,"SUSPEND":0,"time":"2015-09-18"}]';
	//var ret=JSON.parse(retstr);
	if(ret==''||ret==null){
				if(tjsj=="day"){  
				daytj[0]= 0;
				daytj[1]= 0;
				daytj[2]= 0;
				daytj[3]= 0;
				daytj[4]= 0;
				daytj[5]= 0;
				daytj[6]= 0;
				daytj[7]= 0;
				daytj[8]= 0;
				daytj[9]= 0;
				//当天办件统计
					      }
				if(tjsj=="month"){  
					monthtj[0]= 0;
					monthtj[1]= 0;
					monthtj[2]= 0;
					monthtj[3]= 0;
					monthtj[4]= 0;
					monthtj[5]= 0;
					monthtj[6]= 0;
					monthtj[7]= 0;
					monthtj[8]= 0;
					monthtj[9]= 0;
						      }//本月办件统计
     			if(tjsj=="year") {  
					yeartj[0]= 0;
					yeartj[1]= 0;
					yeartj[2]= 0;
					yeartj[3]= 0;
					yeartj[4]= 0;
					yeartj[5]= 0;
					yeartj[6]= 0;
					yeartj[7]= 0;
					yeartj[8]= 0;
					yeartj[9]= 0;
						      }//本年办件统计
				if(tjsj=="all") {  
					alltj[0]= 0;
					alltj[1]= 0;
					alltj[2]= 0;
					alltj[3]= 0;
					alltj[4]= 0;
					alltj[5]= 0;
					alltj[6]= 0;
					alltj[7]= 0;
					alltj[8]= 0;
					alltj[9]= 0;
								}//全部办件统计
				
	//		});
		}
	else{
		if(tjsj=="day"){  
			daytj[0]= ret[0].INTERNET;
			daytj[1]= ret[0].CONSULT;
			daytj[2]= ret[0].ACCEPT;
			daytj[3]= ret[0].JIBAN;
			daytj[4]= ret[0].CHENGNUO;
			daytj[5]= ret[0].CORRECTION;
			daytj[6]= ret[0].SUSPEND;
			daytj[7]= ret[0].COMPLETE;
			daytj[8]= ret[0].OVERTIME;
			daytj[9]= ret[0].EARLYFINISH;
			//当天办件统计
				      }
			if(tjsj=="month"){  
				monthtj[0]= ret[0].INTERNET;
				monthtj[1]= ret[0].CONSULT;
				monthtj[2]= ret[0].ACCEPT;
				monthtj[3]= ret[0].JIBAN;
				monthtj[4]= ret[0].CHENGNUO;
				monthtj[5]= ret[0].CORRECTION;
				monthtj[6]= ret[0].SUSPEND;
				monthtj[7]= ret[0].COMPLETE;
				monthtj[8]= ret[0].OVERTIME;
				monthtj[9]= ret[0].EARLYFINISH;
					      }//本月办件统计
 			if(tjsj=="year") {  
				yeartj[0]= ret[0].INTERNET;
				yeartj[1]= ret[0].CONSULT;
				yeartj[2]= ret[0].ACCEPT;
				yeartj[3]= ret[0].JIBAN;
				yeartj[4]= ret[0].CHENGNUO;
				yeartj[5]= ret[0].CORRECTION;
				yeartj[6]= ret[0].SUSPEND;
				yeartj[7]= ret[0].COMPLETE;
				yeartj[8]= ret[0].OVERTIME;
				yeartj[9]= ret[0].EARLYFINISH;
					      }//本年办件统计
			if(tjsj=="all") {  
				alltj[0]= ret[0].INTERNET;
				alltj[1]= ret[0].CONSULT;
				alltj[2]= ret[0].ACCEPT;
				alltj[3]= ret[0].JIBAN;
				alltj[4]= ret[0].CHENGNUO;
				alltj[5]= ret[0].CORRECTION;
				alltj[6]= ret[0].SUSPEND;
				alltj[7]= ret[0].COMPLETE;
				alltj[8]= ret[0].OVERTIME;
				alltj[9]= ret[0].EARLYFINISH;
					      }//全部办件统计
			
			
//		});
	}
		
	//}else{
	//	LEx.alert(ret.error);
	//}	
    var colors = Highcharts.getOptions().colors,
        name = '', 
        data = [{
                y:daytj[0],
                color: colors[0] 
            }, {
                y: daytj[1],
                color: colors[1] 
            }, {
                y: daytj[2],
                color: colors[2] 
            }, {
                y: daytj[3],
                color: colors[3] 
            }, {
                y: daytj[4],
                color: colors[4] 
            },{
                y: daytj[5],
                color: colors[5] 
            },{
                y: daytj[6],
                color: colors[6] 
            },{
                y: daytj[7],
                color: colors[7] 
            }],
		    data2 = [{
                y:monthtj[0],
                color: colors[0] 
            }, {
                y: monthtj[1],
                color: colors[1] 
            }, {
                y: monthtj[2],
                color: colors[2] 
            }, {
                y: monthtj[3],
                color: colors[3] 
            }, {
                y: monthtj[4],
                color: colors[4] 
            },{
                y: monthtj[5],
                color: colors[5] 
            },{
                y: monthtj[6],
                color: colors[6] 
            },{
                y: monthtj[7],
                color: colors[7] 
            }],
			data3 = [{
                y:yeartj[0],
                color: colors[0] 
            }, {
                y: yeartj[1],
                color: colors[1] 
            }, {
                y: yeartj[2],
                color: colors[2] 
            }, {
                y: yeartj[3],
                color: colors[3] 
            }, {
                y: yeartj[4],
                color: colors[4] 
            },{
                y: yeartj[5],
                color: colors[5] 
            },{
                y: yeartj[6],
                color: colors[6] 
            },{
                y: yeartj[7],
                color: colors[7] 
            }],
            data4 = [{
                y:alltj[0],
                color: colors[0] 
            }, {
                y: alltj[1],
                color: colors[1] 
            }, {
                y: alltj[2],
                color: colors[2] 
            }, {
                y: alltj[3],
                color: colors[3] 
            }, {
                y: alltj[4],
                color: colors[4] 
            },{
                y: alltj[5],
                color: colors[5] 
            },{
                y: alltj[6],
                color: colors[6] 
            },{
                y: alltj[7],
                color: colors[7] 
            }];

/*    function setChart(name, categories, data) { alert(data);
	chart.xAxis[0].setCategories(categories, false);
	chart.series[0].remove(false);
	chart.addSeries({
		name: name,
		data: data 
	}, false);
	chart.redraw();
    }*/

    var chart = $('#container').highcharts({
        chart: {
            type: 'column'
        },
        
        xAxis: {
            categories: categories
        },
        yAxis: {
            title: {
                
            }
        },
        plotOptions: {
            column: {
                cursor: 'pointer', 
                dataLabels: {
                    enabled: true,
                    color: colors[0],
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y +'';
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                s = this.x +':<b>'+ this.y +'</b>'; 
                return s;
            }
        },
        series: [{
            name: name,
            data: data,
            color: 'white'
        }],
        exporting: {
            enabled: false
        }
    })
    .highcharts(); // return chart

	var chart = $('#container2').highcharts({
        chart: {
            type: 'column'
        },
        
        xAxis: {
            categories: categories
        },
        yAxis: {
            title: {
                
            }
        },
        plotOptions: {
            column: {
                cursor: 'pointer', 
                dataLabels: {
                    enabled: true,
                    color: colors[0],
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y +'';
                    }
                }
            }
        },
        tooltip: {
            formatter: function() { 
				s = this.x +':<b>'+ this.y +'</b>'; 
                return s;
            }
        },
        series: [{
            name: name,
            data: data2,
            color: 'white'
        }],
        exporting: {
            enabled: false
        }
    })
    .highcharts();

	var chart = $('#container3').highcharts({
        chart: {
            type: 'column'
        },
        
        xAxis: {
            categories: categories
        },
        yAxis: {
            title: {
                
            }
        },
        plotOptions: {
            column: {
                cursor: 'pointer', 
                dataLabels: {
                    enabled: true,
                    color: colors[0],
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y +'';
                    }
                }
            }
        },
        tooltip: {
            formatter: function() { 
                s = this.x +':<b>'+ this.y +'</b>'; 
                return s;
            }
        },
        series: [{
            name: name,
            data: data3,
            color: 'white'
        }],
        exporting: {
            enabled: false
        }
    })
    .highcharts();
    
    	var chart = $('#container4').highcharts({
        chart: {
            type: 'column'
        },
        
        xAxis: {
            categories: categories
        },
        yAxis: {
            title: {
                
            }
        },
        plotOptions: {
            column: {
                cursor: 'pointer', 
                dataLabels: {
                    enabled: true,
                    color: colors[0],
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y +'';
                    }
                }
            }
        },
        tooltip: {
            formatter: function() { 
				s = this.x +':<b>'+ this.y +'</b>'; 
                return s;
            }
        },
        series: [{
            name: name,
            data: data4,
            color: 'white'
        }],
        exporting: {
            enabled: false
        }
    })
    .highcharts();

}
