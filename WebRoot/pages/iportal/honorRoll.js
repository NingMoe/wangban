var url=LEx.webPath+"icity/getHonorList";// 'http://61.153.219.52:6902/ServiceEntry.aspx';
//var token="";
var date=new Date();
var y =date.getFullYear()-1;
var yy =y;
var ys=y;
var yhd=y;
var s=4;
var m=date.getMonth();
if(m==0){
	y=y-1;
	m=12;
}
if(m==0||m==1||m==2){
	ys=ys-1;
	s=4;
}else if(m==3||m==4||m==5){
	s=1;
}else if(m==6||m==7||m==8){
	s=2;
}else if(m==9||m==10||m==11){
	s=3;
}

$(function(){
	 $("#myTab a").mousemove(function (e) {  
	        $(this).tab('show');  
	    });  
	zxryjpzs();
	hqck();
	jygr();
	bygr();
	jdkh();
	 ndkhyx();
	ndxjgzz();
	ndxjck();
	tzxmxjgr();
	zhckxjdw();
	yxckzr();
	tzxmxjck();
	hdxx();
	}); 
function GetHonorList(type,title,year,month){
	var ret="";		
		$.ajax({ 
		     type: 'POST', 
		     url: url , 
		     data:{"Service":"QueueCity.GetHonorList","token":"","Type":type,"Title":title,"Year":2015,"Month":month}, 
		     dataType: "json",
		     async: false
		}).done(function(data){
                 ret=data;
                });
//	ret={"Succ":1,"Honors":[{"Type":"月度考核","Title":"记优个人","Year":"2015","Month":"9","Remark":"描述","Sort":"排序号","Status":"1","Create_time":"2015-09-10 09:00:00","ItemList":[{"Title":"窗口1及工号1","Title_Other":"员工姓名1","ImagePath":"{{cp}}file/upload/C0EF6AE0EF594668812748E3FFA8C5C4.jpg|{{cp}}file/upload/C0EF6AE0EF594668812748E3FFA8C5C4.jpg|{{cp}}file/upload/C0EF6AE0EF594668812748E3FFA8C5C4.jpg","Content":"照片描述1"},{"Title":"窗口2及工号2","Title_Other":"员工姓名2","ImagePath":"{{cp}}file/upload/D634608828DC45A09787A0A618518E29.jpg|{{cp}}file/upload/C0EF6AE0EF594668812748E3FFA8C5C4.jpg|{{cp}}file/upload/C0EF6AE0EF594668812748E3FFA8C5C4.jpg","Content":"照片描述2"},{"Title":"窗口2及工号2","Title_Other":"员工姓名2","ImagePath":"{{cp}}file/upload/E1D86B5393F3493DA02F67A5B3793220.jpg|{{cp}}file/upload/C0EF6AE0EF594668812748E3FFA8C5C4.jpg|{{cp}}file/upload/C0EF6AE0EF594668812748E3FFA8C5C4.jpg","Content":"照片描述3"},{"Title":"窗口2及工号2","Title_Other":"员工姓名2","ImagePath":"{{cp}}file/upload/E1D86B5393F3493DA02F67A5B3793220.jpg|{{cp}}file/upload/E1D86B5393F3493DA02F67A5B3793220.jpg","Content":"照片描述4"},{"Title":"窗口2及工号2","Title_Other":"员工姓名2","ImagePath":"{{cp}}file/upload/E1D86B5393F3493DA02F67A5B3793220.jpg","Content":"照片描述5"},{"Title":"窗口2及工号2","Title_Other":"员工姓名2","ImagePath":"{{cp}}file/upload/E1D86B5393F3493DA02F67A5B3793220.jpg","Content":"照片描述6"},{"Title":"窗口2及工号2","Title_Other":"员工姓名2","ImagePath":"{{cp}}file/upload/E1D86B5393F3493DA02F67A5B3793220.jpg","Content":"照片描述7"},{"Title":"窗口2及工号2","Title_Other":"员工姓名2","ImagePath":"{{cp}}file/upload/E1D86B5393F3493DA02F67A5B3793220.jpg","Content":"照片描述7"}]}]};
		return ret;
		}
function zxryjpzs(){
	
	var data=GetHonorList("中心荣誉","奖牌展示",y,"");
	if(data.Honors.length>0){
	$('#home').html(LEx.processDOMTemplate('MattersListTemplatep_3gundong',data));
}
	var data1=GetHonorList("中心荣誉","锦旗展示",y,"");
	if(data1.Honors.length>0){
	$('#home1').html(LEx.processDOMTemplate('MattersListTemplatep_4gundong',data1));
	}}
function zxryjqzs(){
	var data=GetHonorList("中心荣誉","锦旗展示",y,"");
	if(data.Honors.length>0){
	$('#profile').html(LEx.processDOMTemplate('MattersListTemplatep_4gundong',data));
	}
	

}
function hqck(){
	var data=GetHonorList("月度考核","红旗窗口",y,m);
	if(data.Honors.length>0){
	$('#hqck').html(LEx.processDOMTemplate('MattersListTemplatep_3liebiao',data));
	}}
function jygr(){
	var data=GetHonorList("月度考核","记优个人",y,m);
	if(data.Honors.length>0){
	$('#jygr').html(LEx.processDOMTemplate('MattersListTemplatep_4liebiao',data));
	}}
function bygr(){
	var data=GetHonorList("月度考核","表扬个人",y,m);
	if(data.Honors.length>0){
	$('#bygr').html(LEx.processDOMTemplate('MattersListTemplatep_4liebiao',data));
	}}
function jdkh(){
	var data=GetHonorList("季度考核","",ys,s);
	if(data.Honors.length>0){
	$('#jdkh').html(LEx.processDOMTemplate('MattersListTemplatep_4jiantou',data));
	}}
function ndxjck(){
	var data=GetHonorList("年度考核","年度先进窗口",yy,"");
	if(data.Honors.length>0){
	$('#ndxjck').html(LEx.processDOMTemplate('MattersListTemplatep_3liebiaock',data));
	}}
function tzxmxjck(){
	var data=GetHonorList("年度考核","投资项目审批服务先进窗口",yy,"");	
	if(data.Honors.length>0){
	$('#tzxmxjck').html(LEx.processDOMTemplate('MattersListTemplatep_3liebiaock',data));
	}}
function zhckxjdw(){
	var data=GetHonorList("年度考核","综合窗口建设先进单位",yy,"");
	if(data.Honors.length>0){
	$('#zhckxjdw').html(LEx.processDOMTemplate('MattersListTemplatep_3liebiaock',data));
	}}
function yxckzr(){
	var data=GetHonorList("年度考核","优秀窗口主任",yy,"");
	if(data.Honors.length>0){
	$('#yxckzr').html(LEx.processDOMTemplate('MattersListTemplatep_4liebiao',data));
	}}
function ndkhyx(){
	var data=GetHonorList("年度考核","年度考核优秀",yy,"");
	if(data.Honors.length>0){
	$('#ndkhyx').html(LEx.processDOMTemplate('MattersListTemplatep_4liebiao',data));
	}}
function ndxjgzz(){
	var data=GetHonorList("年度考核","年度先进工作者",yy,"");
	if(data.Honors.length>0){
	$('#ndxjgzz').html(LEx.processDOMTemplate('MattersListTemplatep_4jiantou1',data));
	}}
function tzxmxjgr(){
	var data=GetHonorList("年度考核","投资项目审批服务先进个人",yy,"");
	if(data.Honors.length>0){
	$('#tzxmxjgr').html(LEx.processDOMTemplate('MattersListTemplatep_4liebiao',data));
}}
function hdxx(){
	var data=GetHonorList("活动信息","活动信息",yhd,"");
	if(data.Honors.length>0){
	$('#hdxx').html(LEx.processDOMTemplate('MattersListTemplatep_hdxx',data));
}}
