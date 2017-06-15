/**
 * 文档评分
 */
 $(document).ready(function(){
		var score_grade;//定义用户对当前文档的评分等级变量
		var score_now = $(".cent_result_num:first").html(); //获取应用的当前评分
		//评分
		function score(){
		    var star_b = $(".app_cent_score b");
		    var score_word = $(".score_word");
		    var score_result = $(".score_result");
		    var i=0,j=0;
		    var len = star_b.length;
		    var score_words = ['很差','差','一般',"好","很好"];
		    if(recently_grade > 0) {
		    	for(j=0; j<recently_grade/2; j++) {
	        		star_b.eq(j).addClass("star");
	        	}
		    	score_grade = recently_grade;
		    	$("#grade").css({display:"none"});
				score_result.html("您已经评论过了！谢谢！<br />");
		    }
		    for(i=0; i<len; i++){
		    	star_b.eq(i).attr("index",i);
		    	star_b.eq(i).hover(
		    		function() {
		    			var pos = parseInt($(this).attr("index"));
			        	for(j=0; j<=pos; j++) {
			        		star_b.eq(j).addClass("star");
			        	}
			        	for(j=pos+1; j<len; j++) {
			        		star_b.eq(j).removeClass("star");
			        	}
			        	score_word.html(score_words[$(this).attr("index")]);
		    		},
		    		function() {
		    			for(j=0; j<len; j++) {
		    				star_b.eq(j).removeClass("star");
			        	}
		    			if(score_grade) {
			    			for(j=0; j<parseInt(score_grade)/2; j++) {
				        		star_b.eq(j).addClass("star");
				        	}
		    				score_word.html("已评");
		    			} else {
				        	score_word.html("&nbsp;");
		    			}
		    		}
		    	);
		        star_b.eq(i).click(function(){
		        	if(doc_id){
		        		if(score_grade==null){
		        			score_grade=(parseInt($(this).attr("index"))+1)*2;
						    $.ajax({
								url:CONST.SERVICE_URL + "/index.php?r=Docpreview/getScore",
								data:{doc_id:doc_id,score:score_grade},
								type:'POST',
								success:function(){
								    	score_result.html("该文档评论成功！您当前打分为 "+score_grade+" 分");
								}
							});
		        		}else{
		        			$("#grade").css({display:"none"});
		        			score_result.html("您已经评论过了！谢谢！<br />");
		        		}
		        	}
		        });
		    }
		}
		score();
		function initStars() {
			//根据分数自动调整star的定位
			var width = (score_now/10)*100+"%";
			$(".app_cent_result_index").width(width);
		}
		initStars();
	});