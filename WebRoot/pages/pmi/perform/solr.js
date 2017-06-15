function initSolr(index){
	//alert(index);
	switch(index){
		//事项初始化
		case 1:
			initHandler("genProject","事项");
		break;
		//材料初始化
		case 2:
			initHandler("genMaterial","材料");
		break;
		//法规初始化
		case 3:
			initHandler("genLaw","法规");
		break;
		//单位初始化
		case 4:
			initHandler("genDepart","单位");
		break;
		//内容初始化
		case 5:
			initHandler("genContent","内容");
		break;
	}
}
function initHandler(type,name){
	var command = new LEx.Command("app.icity.search.SearchGenCmd");
	var ret = command.execute(type);;
	if(ret.state == 1){
		LEx.dialog({title:"系统提示",content:name+"初始化成功！",icon: 'succeed',lock:true});
	}else{
		LEx.dialog({title:"系统提示",content:name+"初始化失败，错误内容为："+ret.message,icon: 'error',lock:true});
	}
}