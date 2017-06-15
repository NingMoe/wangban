var mytagData = {};
LEx.tagCmd = {
		myTag:function(ext){
			var command = new LEx.Command("one.tag.tagCmd");
			var key = ext;
			if(LEx.isNotNull(ext)){
				command.setParameter("ext", ext);
			}else{
				key = 'mytag'
			}
			if(mytagData.key){
				return mytagData.key;
			}
			var ret = command.execute("myTag");
			var re = {};
			if (!ret.error) {
				for(var i=0;i<ret.data.length;i++){
					re[ret.data[i].KEYWORD] = true;
				}
			} else {
				alert(command.error);// 打印错误信息
			}
			mytagData.key = re;
			return re;
		}
	};