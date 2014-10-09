$(document).ready(function() {
	
	//已有的目的地(计件)
	var locationToId1 = [];
	var locationToId2 = [];
	var locationToId3 = [];
	//编辑前的目的地
	var startTo = [];
	
	$('#menu_contract').addClass('active').find('ul').addClass('in');
		var contractId=$('#contractId').val();
		var dataTable = $('#dataTables-example').dataTable({
        //"sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span12'i><'span12 center'p>>",
	        "sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span12'i><'span12 center'p>>",
	        //"sPaginationType": "bootstrap",
	        "iDisplayLength": 10,
	    	"oLanguage": {
	            "sUrl": "/eeda/dataTables.ch.txt"
	        },
	        "bProcessing": true,
	        "bServerSide": true,
	        "sAjaxSource": "/yh/spContract/routeEdit?routId="+contractId,
	        "aoColumns": [  
				{"mDataProp":"PRICETYPE", "bVisible":false},
				{"mDataProp":"ITEM_NAME"},   
	            {"mDataProp":"UNIT"},
	            {"mDataProp":"LOCATION_FROM"},
	            {"mDataProp":"LOCATION_TO"},
	            {"mDataProp":"FIN_ITEM_NAME"},
	            {"mDataProp":"AMOUNT"},
	            {"mDataProp":"KILOMETER"},
	            {"mDataProp":null,
	            	"fnRender": function(obj) {
	            		if(obj.aData.TO_ID != "" && obj.aData.TO_ID != null){
	            			var toid = [];
	            			toid = obj.aData.TO_ID.split(" ");
	            			if(toid[toid.length-1] == " "){
	            				if(toid[toid.length-2] == " "){
	            					locationToId1.push(toid[toid.length-3]);
	            				}else{
	            					locationToId1.push(toid[toid.length-2]);
	            				}
	            			}else{
	            				locationToId1.push(toid[toid.length-1]);
	            			}
	            		}
	                    return buildRange(obj.aData.DAYFROM, obj.aData.DAYTO);
	            }},
	            { 
	                "mDataProp": null, 
	                "sWidth": "8%",                
	                "fnRender": function(obj) {                    
	                    return "<a class='btn btn-success contractRouteEdit' code='"+obj.aData.ID+"'>"+
	                                "<i class='fa fa-edit fa-fw'></i>"+
	                                "编辑"+
	                            "</a>"+
	                            "<a class='btn btn-danger routeDelete' code2='"+obj.aData.ID+"'>"+
	                                "<i class='fa fa-trash-o fa-fw'></i>"+ 
	                                "删除"+
	                            "</a>";
	                }
	            }                         
	        ]
	     });
		var dataTable2 = $('#dataTables-example2').dataTable({
	        //"sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span12'i><'span12 center'p>>",
		        "sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span12'i><'span12 center'p>>",
		        //"sPaginationType": "bootstrap",
		        "iDisplayLength": 10,
		    	"oLanguage": {
		            "sUrl": "/eeda/dataTables.ch.txt"
		        },
		        "bProcessing": true,
		        "bServerSide": true,
		        "sAjaxSource": "/yh/spContract/routeEdit2?routId="+contractId,
		        "aoColumns": [  
					{"mDataProp":"PRICETYPE", "bVisible":false},
					{"mDataProp":"CARTYPE"},
					{"mDataProp":"CARLENGTH"},
					{"mDataProp":"LOCATION_FROM"},
		            {"mDataProp":"LOCATION_TO"},
		            {"mDataProp":"FIN_ITEM_NAME"},
		            {"mDataProp":"AMOUNT"},
		            {"mDataProp":null,
		            	"fnRender": function(obj) {  
		            		if(obj.aData.TO_ID != "" && obj.aData.TO_ID != null){
		            			var toid = [];
		            			toid = obj.aData.TO_ID.split(" ");
		            			if(toid[toid.length-1] == " "){
		            				if(toid[toid.length-2] == " "){
		            					locationToId2.push(toid[toid.length-3]);
		            				}else{
		            					locationToId2.push(toid[toid.length-2]);
		            				}
		            			}else{
		            				locationToId2.push(toid[toid.length-1]);
		            			}
		            		}
		                    return buildRange(obj.aData.DAYFROM, obj.aData.DAYTO);
		                            
		            }},
		            {"mDataProp":"ITEM_NAME"},  
		            { 
		                "mDataProp": null, 
		                "sWidth": "8%",                
		                "fnRender": function(obj) {                    
		                    return "<a class='btn btn-success contractRouteEdit' code='"+obj.aData.ID+"'>"+
		                                "<i class='fa fa-edit fa-fw'></i>"+
		                                "编辑"+
		                            "</a>"+
		                            "<a class='btn btn-danger routeDelete' code2='"+obj.aData.ID+"'>"+
		                                "<i class='fa fa-trash-o fa-fw'></i>"+ 
		                                "删除"+
		                            "</a>";
		                }
		            }                         
		        ]
		     });
		var dataTable3 = $('#dataTables-example3').dataTable({
	        //"sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span12'i><'span12 center'p>>",
		        "sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span12'i><'span12 center'p>>",
		        //"sPaginationType": "bootstrap",
		        "iDisplayLength": 10,
		    	"oLanguage": {
		            "sUrl": "/eeda/dataTables.ch.txt"
		        },
		        "bProcessing": true,
		        "bServerSide": true,
		        "sAjaxSource": "/yh/spContract/routeEdit3?routId="+contractId,
		        "aoColumns": [  
					{"mDataProp":"PRICETYPE", "bVisible":false},
					{"mDataProp":"LTLUNITTYPE",
						"fnRender": function(obj) {
							var reStr='';
							if('perTon'==obj.aData.LTLUNITTYPE){
								reStr="每吨";
							}
							if('perCBM'==obj.aData.LTLUNITTYPE){
								reStr="每立方米";
							}
							if('perKg'==obj.aData.LTLUNITTYPE){
								reStr="每公斤";
							}
		                    return reStr;
		                }
					},
					{"mDataProp":null,
						"fnRender": function(obj) {    
							if(obj.aData.TO_ID != "" && obj.aData.TO_ID != null){
		            			var toid = [];
		            			toid = obj.aData.TO_ID.split(" ");
		            			if(toid[toid.length-1] == " "){
		            				if(toid[toid.length-2] == " "){
		            					locationToId3.push(toid[toid.length-3]);
		            				}else{
		            					locationToId3.push(toid[toid.length-2]);
		            				}
		            			}else{
		            				locationToId3.push(toid[toid.length-1]);
		            			}
		            		}
		                    return buildRange(obj.aData.AMOUNTFROM, obj.aData.AMOUNTTO);
		                            
		                }},
	                {"mDataProp":"LOCATION_FROM"},
		            {"mDataProp":"LOCATION_TO"},
		            {"mDataProp":"FIN_ITEM_NAME"},
		            {"mDataProp":"AMOUNT"},
		            {"mDataProp":null,
		            	"fnRender": function(obj) {                    
		                    return buildRange(obj.aData.DAYFROM, obj.aData.DAYTO);
		                            
		                }},
		                {"mDataProp":"ITEM_NAME"},  
		            { 
		                "mDataProp": null, 
		                "sWidth": "8%",                
		                "fnRender": function(obj) {                    
		                    return "<a class='btn btn-success contractRouteEdit' code='"+obj.aData.ID+"'>"+
		                                "<i class='fa fa-edit fa-fw'></i>"+
		                                "编辑"+
		                            "</a>"+
		                            "<a class='btn btn-danger routeDelete' code2='"+obj.aData.ID+"'>"+
		                                "<i class='fa fa-trash-o fa-fw'></i>"+ 
		                                "删除"+
		                            "</a>";
		                }
		            }                         
		        ]
		     });
	    
		var buildRange=function(from, to){
			if(from && to){
				return +from+"-"+to;
			}
			if(from && !to){
				return from;
			}
			if(!from && to){
				return '0-'+to;
			}
			return '';
		 }
	     //计件编辑
		 $("#dataTables-example").on('click', '.contractRouteEdit', function(){
			 $("#mbProvinceFrom").get(0).selectedIndex=0;
	     	 $("#mbProvinceTo").get(0).selectedIndex=0;
	     	 $("#cmbCityFrom").empty();
	     	 $("#cmbCityTo").empty();
	     	 $("#cmbAreaFrom").empty();
	     	 $("#cmbAreaTo").empty();
			 var contractId = $("#routeContractId").val();
			 var id = $(this).attr('code');
			 $.post('/yh/customerContract/contractRouteEdit/'+id,{contractId:contractId},function(data){
                 //保存成功后，刷新列表
                 console.log(data);
                 if(data[0] !=null){
                	 var priceType = $("#routeTabs .active").attr("price-type");
                	 showPriceElements(priceType);
                	 $('#myModal').modal('show');
                	 $('#routeId').val(data[0].ID);
                	 $('#from_id').val(data[0].FROM_ID);
                	 $('#to_id').val(data[0].TO_ID);
                	 $("#fin_item_list").val(data[0].FIN_ITEM_ID);
                	 $('#price').val(data[0].AMOUNT);
                	 $('#routeItemId').val(data[0].ID);
                	 $('#day').val(data[0].DAYFROM);
                	 $('#day2').val(data[0].DAYTO);
                	 $('#unit2').val(data[0].UNIT);
                	 $('#productId').val(data[0].PID);
                	 $('#itemNameMessage').val(data[0].ITEM_NAME);
                	 searchAllLocationFrom(data[0].FROM_ID);
                	 searchAllLocationTo(data[0].TO_ID);
                	 startTo = [];
                	 startTo = data[0].TO_ID.split(" ");
                 }else{
                     alert('取消失败');
                 }
             },'json');
		  });
			
		//整车编辑
		 $("#dataTables-example2").on('click', '.contractRouteEdit', function(){
			 $("#mbProvinceFrom").get(0).selectedIndex=0;
	     	 $("#mbProvinceTo").get(0).selectedIndex=0;
	     	 $("#cmbCityFrom").empty();
	     	 $("#cmbCityTo").empty();
	     	 $("#cmbAreaFrom").empty();
	     	 $("#cmbAreaTo").empty();
			 var contractId = $("#routeContractId").val();
			 var id = $(this).attr('code');
			 $.post('/yh/customerContract/contractRouteEdit/'+id,{contractId:contractId},function(data){
                 //保存成功后，刷新列表
                 console.log(data);
                 if(data[0] !=null){
                 	var priceType = $("#routeTabs .active").attr("price-type");
                	 showPriceElements(priceType);

                	 $('#myModal').modal('show');
                	 $('#routeId').val(data[0].ID);
                	 $('#from_id').val(data[0].FROM_ID);
                	 $('#to_id').val(data[0].TO_ID);
                	 $("#fin_item_list").val(data[0].FIN_ITEM_ID);
                	 $('#price').val(data[0].AMOUNT);
                	 $('#routeItemId').val(data[0].ID);
                	 $('#day').val(data[0].DAYFROM);
                	 $('#day2').val(data[0].DAYTO);
                	 $('#carLength2').val(data[0].CARLENGTH);
                	 $('#carType2').val(data[0].CARTYPE);
                	 $('#itemNameMessage').val(data[0].ITEM_NAME);
                	 searchAllLocationFrom(data[0].FROM_ID);
                	 searchAllLocationTo(data[0].TO_ID);
                	 startTo = [];
                	 startTo = data[0].TO_ID.split(" ");
                	 //$('#optionsRadiosInline2').prop('checked', true).trigger('change');
                 }else{
                     alert('取消失败');
                 }
             },'json');
		  });
		//零担编辑
		 $("#dataTables-example3").on('click', '.contractRouteEdit', function(){
			 $("#mbProvinceFrom").get(0).selectedIndex=0;
	     	 $("#mbProvinceTo").get(0).selectedIndex=0;
	     	 $("#cmbCityFrom").empty();
	     	 $("#cmbCityTo").empty();
	     	 $("#cmbAreaFrom").empty();
	     	 $("#cmbAreaTo").empty();
			 var contractId = $("#routeContractId").val();
			 var id = $(this).attr('code');
			 $.post('/yh/customerContract/contractRouteEdit/'+id,{contractId:contractId},function(data){
                 //保存成功后，刷新列表
                 console.log(data);
                 if(data[0] !=null){
                	var priceType = $("#routeTabs .active").attr("price-type");
                	 showPriceElements(priceType);
                	 
                	 $('#myModal').modal('show');
                	 $('#routeId').val(data[0].ID);
                	 $('#from_id').val(data[0].FROM_ID);
                	 $('#to_id').val(data[0].TO_ID);
                	 $("#fin_item_list").val(data[0].FIN_ITEM_ID);
                	 $('#price').val(data[0].AMOUNT);
                	 $('#routeItemId').val(data[0].ID);
                	 $('#day').val(data[0].DAYFROM);
                	 $('#day2').val(data[0].DAYTO);
                	 $('#amountFrom').val(data[0].AMOUNTFROM);
                	 $('#amountTo').val(data[0].AMOUNTTO);
                	 $('#itemNameMessage').val(data[0].ITEM_NAME);
                	 searchAllLocationFrom(data[0].FROM_ID);
                	 searchAllLocationTo(data[0].TO_ID);
                	 startTo = [];
                	 startTo = data[0].TO_ID.split(" ");
                	 //console.log($('#typeRadio').val(data[0].LTLUNITTYPE));
                	 //$('#optionsRadiosInline3').prop('checked', true).trigger('change');
                	
                	
                	 if(data[0].LTLUNITTYPE==$('#optionsRadiosIn1').val()){
                		 $('#optionsRadiosIn1').prop('checked', true).trigger('change'); 
                	 }
                	 if(data[0].LTLUNITTYPE==$('#optionsRadiosIn2').val()){
                		 $('#optionsRadiosIn2').prop('checked', true).trigger('change'); 
                	 }
                	 if(data[0].LTLUNITTYPE==$('#optionsRadiosIn3').val()){
                		 $('#optionsRadiosIn3').prop('checked', true).trigger('change'); 
                	 }
                	 
                	 
                 }else{
                     alert('取消失败');
                 }
             },'json');
		  });
		 //计件删除
		 $("#dataTables-example").on('click', '.routeDelete', function(){
			 var id = $(this).attr('code2');
			 $.post('/yh/customerContract/routeDelete/'+id,function(data){
                 //保存成功后，刷新列表
                 console.log(data);
                 if(data.success){
                	 locationToId1 = [];
                	 dataTable.fnDraw();
                 }else{
                     alert('取消失败');
                 }
             },'json');
			});
		 //整车删除
		 $("#dataTables-example2").on('click', '.routeDelete', function(){
			 var id = $(this).attr('code2');
			 $.post('/yh/customerContract/routeDelete/'+id,function(data){
                 //保存成功后，刷新列表
                 console.log(data);
                 if(data.success){
                	 locationToId2 = [];
                	 dataTable2.fnDraw();
                 }else{
                     alert('取消失败');
                 }
             },'json');
			});
		 //零担删除
		 $("#dataTables-example3").on('click', '.routeDelete', function(){
			 var id = $(this).attr('code2');
			 $.post('/yh/customerContract/routeDelete/'+id,function(data){
                 //保存成功后，刷新列表
                 console.log(data);
                 if(data.success){
                	 locationToId3 = [];
                	 dataTable3.fnDraw();
                 }else{
                     alert('取消失败');
                 }
             },'json');
			});
	
		//from表单验证
		var validate = $('#customerForm').validate({
	        rules: {
	          contract_name: {
	            required: true
	          },
	          companyName:{//form 中 name为必填
	            required: true
	          }
	        },
	        messages : {
	             
	        	contract_name : {required:  "不能为空"}, 
	        	companyName: {required :"不能为空"},
	        }
	    });
	
        //点击button显示添加合同干线div
        $("#addPriceBtn").click(function(){
        	$("#mbProvinceFrom").get(0).selectedIndex=0;
	     	$("#mbProvinceTo").get(0).selectedIndex=0;
	     	$("#cmbCityFrom").empty();
	     	$("#cmbCityTo").empty();
	     	$("#cmbAreaFrom").empty();
	     	$("#cmbAreaTo").empty();
	     	$("#price").val("");
	     	$("#day").val("");
	     	$("#day2").val("");
	     	$("#itemNameMessage").val("");
	     	
        	
        	var priceType = $("#routeTabs .active").attr("price-type");
        	
        	showPriceElements(priceType);

        	var contractId = $("#routeContractId").val();
        	if(contractId != ""){
        		//$("#routeItemFormDiv").show();
        		$("#routeItemId").val("");
        	}else{
        		alert("请先添加合同！");
        		return false;
        	}
        	$("#routeItemForm")[0].reset;  
        	searchAllLocationFrom($("#locationCode").val());
        });
        

	    var showPriceElements=function(priceType){
	    	$("#priceTypeHidden").val(priceType);
	    	if(priceType=="perUnit"){
	    		$("#priceType").text('计件');
	    		$("#unit").show();
	    		$("#carType").hide();
	    		$("#carLength").hide();
	    		$("#ltlUnitType").hide();
	    	}else if(priceType=="perCar"){
	    		$("#priceType").text('整车');
	    		$("#carType").show();
	    		$("#carLength").show();
	    		$("#ltlUnitType").hide();
	    		$("#unit").hide();
	    		//$("#cargoNature").hide();
	    	}else if(priceType=="perCargo"){
	    		$("#priceType").text('零担');
				$("#carType").hide();
	    		$("#carLength").hide();
	    		$("#ltlUnitType").show();
	    		$("#unit").hide();
	    	}
	    };
	    //定义一个全局的信息提示框
	    var alerMsg='<div id="message_trigger_err" class="alert alert-danger alert-dismissable" style="display:none">'+
				    '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>'+
				    'Lorem ipsum dolor sit amet, consectetur adipisicing elit. <a href="#" class="alert-link">Alert Link</a>.'+
				    '</div>';
		$('body').append(alerMsg);
		
		$('#message_trigger_err').on('click', function(e) {
			e.preventDefault();
		});
		var tijiao = function(locationtoid,datatable){
			$.post('/yh/customerContract/routeAdd', $("#routeItemForm").serialize(), function(data){
                //保存成功后，刷新列表
                console.log(data);
                if(data.success){
                	$('#myModal').modal('hide');
                	$('#reset').click();
                	locationtoid = [];
                	datatable.fnDraw();
                }else{
                    alert('数据保存失败。');
                }
            },'json');
		}
        //点击保存的事件，保存干线信息
        //routeItemForm 不需要提交
        $("#saveRouteBtn").click(function(e){
        	 
        	//新增时目的地id
        	var saveid = [];
		    var price = $("#price").val();
			$("#tishi").text("");
			if(price == "" || price == null){
				$("#tishi").text("金额不能为空！");
				return false;
			}
			if(isNaN(price)){
				$("#tishi").text("请输入数字");
				return false;
			}
	    	//阻止a 的默认响应行为，不需要跳转
            e.preventDefault();
            //重设初始地、目的地值
            $("#hideProvinceFrom").val("");
            $("#hideCityFrom").val("");
            $("#hideDistrictFrom").val("");
            $("#hideProvinceTo").val("");
            $("#hideCityTo").val("");
            $("#hideDistrictTo").val("");
            var mbProvinceFrom = $("#mbProvinceFrom").find("option:selected").text();
            var cmbCityFrom = $("#cmbCityFrom").find("option:selected").text();
            var cmbAreaFrom = $("#cmbAreaFrom").find("option:selected").text();
            var mbProvinceTo = $("#mbProvinceTo").find("option:selected").text();
            var cmbCityTo = $("#cmbCityTo").find("option:selected").text();
            var cmbAreaTo = $("#cmbAreaTo").find("option:selected").text();
            if(mbProvinceFrom!="--请选择省份--"){
            	$("#hideProvinceFrom").val(mbProvinceFrom);
            }
            if(cmbCityFrom!="--请选择城市--"){
            	$("#hideCityFrom").val(cmbCityFrom);
            }
            if(cmbAreaFrom!="--请选择区(县)--"){
            	$("#hideDistrictFrom").val(cmbAreaFrom);
            }
            if(mbProvinceTo!="--请选择省份--"){
            	saveid.push($("#mbProvinceTo").val());
            	$("#hideProvinceTo").val(mbProvinceTo);
            }
            if(cmbCityTo == "--请选择城市--"){
            	alert("请选择目的地城市！");
            	return false;
            }else{
            	saveid.push($("#cmbCityTo").val());
            	$("#hideCityTo").val(cmbCityTo);
            }
            if(cmbAreaTo!="--请选择区(县)--"){
            	saveid.push($("#cmbAreaTo").val());
            	$("#hideDistrictTo").val(cmbAreaTo);
            }
            
            //当前选中的目的地id
        	var toid = saveid[saveid.length-1];
            var id =  $('#routeItemId').val();
            var priceType = $("#routeTabs .active").attr("price-type");
            $("#priceTypeHidden").val(priceType);
	    	if(priceType=="perUnit"){//计费
	    		if(id == null || id ==""){//新增合同运价时
	    			var result = true;
	                for(var i=0;i<locationToId1.length;i++){
	                    if(locationToId1[i] == toid){
	                    	alert("目的地已存在！");
	                    	result = false;
	                    	break;
	                    }
	            	}
	                if(result){
	                	tijiao(locationToId1,dataTable);
	                }
	            }else{//修改合同运价
	            	var result = false;
	            	var startid = startTo[startTo.length-1];
	            	if(startid == "" || startid == null){
	            		startid = startTo[startTo.length-2];
	            	}
	        		for(var k=0;k<locationToId1.length;k++){
	        			if(toid == locationToId1[k]){
	        				result = true;
	        				break;
	        			}else{
	        				result = false;
	        			}
	            	}
	        		if(result){
	        			if(toid == startid){
	        				tijiao(locationToId1,dataTable);
	        			}else{
	        				alert("目的地已存在！");
	        			}
	        		}else{
	        			tijiao(locationToId1,dataTable);
	        		}
	            }
	    	}else if(priceType=="perCar"){//整车
	    		if(id == null || id ==""){//新增合同运价时
	    			var result = true;
	                for(var i=0;i<locationToId2.length;i++){
	                    if(locationToId2[i] == toid){
	                    	alert("目的地已存在！");
	                    	result = false;
	                    	break;
	                    }
	            	}
	                if(result){
	                	tijiao(locationToId2,dataTable2);
	                }
	            }else{//修改合同运价
	            	var result = false;
	            	var startid = startTo[startTo.length-1];
	            	if(startid == "" || startid == null){
	            		startid = startTo[startTo.length-2];
	            	}
	        		for(var k=0;k<locationToId2.length;k++){
	        			if(toid == locationToId2[k]){
	        				result = true;
	        				break;
	        			}else{
	        				result = false;
	        			}
	            	}
	        		if(result){
	        			if(toid == startid){
	        				tijiao(locationToId2,dataTable2);
	        			}else{
	        				alert("目的地已存在！");
	        			}
	        		}else{
	        			tijiao(locationToId2,dataTable2);
	        		}
	            }
	    	}else if(priceType=="perCargo"){//零担
	    		if(id == null || id ==""){//新增合同运价时
	    			var result = true;
	                for(var i=0;i<locationToId3.length;i++){
	                    if(locationToId3[i] == toid){
	                    	alert("目的地已存在！");
	                    	result = false;
	                    	break;
	                    }
	            	}
	                if(result){
	                	tijiao(locationToId3,dataTable3);
	                }
	            }else{//修改合同运价
	            	var result = false;
	            	var startid = startTo[startTo.length-1];
	            	if(startid == "" || startid == null){
	            		startid = startTo[startTo.length-2];
	            	}
	        		for(var k=0;k<locationToId3.length;k++){
	        			if(toid == locationToId3[k]){
	        				result = true;
	        				break;
	        			}else{
	        				result = false;
	        			}
	            	}
	        		if(result){
	        			if(toid == startid){
	        				tijiao(locationToId3,dataTable3);
	        			}else{
	        				alert("目的地已存在！");
	        			}
	        		}else{
	        			tijiao(locationToId3,dataTable3);
	        		}
	            }
	    	}
        });

        //获取客户的list，选中信息自动填写其他信息
        $('#companyName').on('keyup click', function(){
			var inputStr = $('#companyName').val();
			var type = $("#type2").val();
			var type2 = $("#type3").val();
			 var urlSource;
			if(type=='CUSTOMER'||type2=='CUSTOMER'){
				urlSource ="/yh/customerContract/search";
			}else{
				urlSource ="/yh/spContract/search2";
			}
			$.get(urlSource, {locationName:inputStr}, function(data){
				console.log(data);
				var companyList =$("#companyList");
				companyList.empty();
				for(var i = 0; i < data.length; i++)
				{
					companyList.append("<li><a tabindex='-1' class='fromLocationItem' post_code='"+data[i].POSTAL_CODE+"' contact_person='"+data[i].CONTACT_PERSON+"' email='"+data[i].EMAIL+"' phone='"+data[i].PHONE+"' partyId='"+data[i].PID+"' address='"+data[i].ADDRESS+"', company_name='"+data[i].COMPANY_NAME+"', location='"+data[i].LOCATION+"'>"+data[i].COMPANY_NAME+"</a></li>");
				}
			},'json');
			$("#companyList").css({ 
	        	left:$(this).position().left+"px", 
	        	top:$(this).position().top+32+"px" 
	        }); 
	        $('#companyList').show();
		});
        $('#companyName').on('blur', function(){
			$("#companyList").hide();
		});
		
		$('#companyList').on('blur', function(){
	 		$('#companyList').hide();
	 	});

		$('#companyList').on('mousedown', function(){
			return false;//阻止事件回流，不触发 $('#spMessage').on('blur'
		});
	
		$('#companyList').on('click', '.fromLocationItem', function(e){
			//方法已经对了，只是没有取对值
			$('#companyName').val($(this).text());
        	$("#companyList").hide();
        	$('#name').val($(this).attr('contact_person'));
        	$('#address').val($(this).attr('address'));
        	$('#phone').val($(this).attr('phone'));
        	$('#post_code').val($(this).attr('post_code'));
        	$('#email').val($(this).attr('email'));
        	$('#partyid').val($(this).attr('partyId'));
        	$('#locationCode').val($(this).attr('location'));
        });
		
		
		 //添加合同
		$("#saveContract").click(function(e){
	            //阻止a 的默认响应行为，不需要跳转			    
	            e.preventDefault();
	            //提交前，校验数据
	            if(!$("#customerForm").valid())
	            	return;
	            //异步向后台提交数据
	            $.post('/yh/customerContract/save', $("#customerForm").serialize(), function(contractId){
	                    
	                    if(contractId>0){
	                        //alert("添加合同成功！");
	                    	//$("#style").show();
	                    	//已经有一个重复的contractId 在前面了
	                    	$('#routeContractId').val(contractId);
	                    	dataTable.fnSettings().sAjaxSource="/yh/spContract/routeEdit?routId="+contractId;
	                    	dataTable2.fnSettings().sAjaxSource="/yh/spContract/routeEdit2?routId="+contractId;
	                    	dataTable3.fnSettings().sAjaxSource="/yh/spContract/routeEdit3?routId="+contractId;
	                    	$.scojs_message('保存成功', $.scojs_message.TYPE_OK);
	                    }else{
	                        alert('数据保存失败。');
	                    }
	                    
	                },'json');
	            
	        });
		 
		
		//选择出发地点
		$('#fromName').on('keyup click', function(){
			var inputStr = $('#fromName').val();
			$.get('/yh/customerContract/searchlocation', {locationName:inputStr}, function(data){
				console.log(data);
				var fromLocationList =$("#fromLocationList");
				fromLocationList.empty();
				for(var i = 0; i < data.length; i++)
				{
					fromLocationList.append("<li><a tabindex='-1' class='fromLocationItem' code='"+data[i].CODE+"'>"+data[i].NAME+"</a></li>");
				}
				fromLocationList.show();
			},'json');
			$("#fromLocationList").css({ 
	        	left:$(this).position().left+"px", 
	        	top:$(this).position().top+30+"px" 
	        }); 
			
		});
		//失去焦点隐藏
		$('#fromName').on('blur', function(){
			$("#fromLocationList").hide();
		});
		

		$('#fromLocationList').on('blur', function(){
	 		$('#fromLocationList').hide();
	 	});

		$('#fromLocationList').on('mousedown', function(){
			return false;//阻止事件回流，不触发 $('#spMessage').on('blur'
		});
		
		$('#fromLocationList').on('mousedown', '.fromLocationItem', function(e){
			$('#from_id').val($(this).attr('code'));
			$('#fromName').val($(this).text());
        	$("#fromLocationList").hide();
        	 /*var inputStr = $('#fromName').val();
			 var inputStr2 = $('#toName').val();
			 if(inputStr!=''&&inputStr2!=''){
				 $.get('/yh/spContract/searchRoute', {fromName:inputStr,toName:inputStr2}, function(data){
					 for(var i = 0; i < data.length; i++){
						 	$('#routeItemId').val(data[i].RID);
					 }
				 },'json');
			 }*/
    	});
		
		//选择目的地点
		$('#toName').on('keyup click', function(){
			var inputStr = $('#toName').val();
			$.get('/yh/customerContract/searchlocation', {locationName:inputStr}, function(data){
				
				var toLocationList =$("#toLocationList");
				toLocationList.empty();
				for(var i = 0; i < data.length; i++)
				{
					toLocationList.append("<li><a tabindex='-1' class='fromLocationItem' code='"+data[i].CODE+"'>"+data[i].NAME+"</a></li>");
				}
				toLocationList.show();
			},'json');
			$("#toLocationList").css({ 
	        	left:$(this).position().left+"px", 
	        	top:$(this).position().top+30+"px" 
	        }); 
			
		});
		
		//失去焦点隐藏
		$('#toName').on('blur', function(){
			$("#toLocationList").hide(1);
		});

		$('#toLocationList').on('blur', function(){
	 		$('#toLocationList').hide();
	 	});

		$('#toLocationList').on('mousedown', function(){
			return false;//阻止事件回流，不触发 $('#spMessage').on('blur'
		});
		$('#toLocationList').on('mousedown', '.fromLocationItem', function(e){
			$('#to_id').val($(this).attr('code'));
			$('#toName').val($(this).text());
        	$("#toLocationList").hide();
        	/* var inputStr = $('#fromName').val();
			 var inputStr2 = $('#toName').val();
			 if(inputStr!=''&&inputStr2!=''){
				 $.get('/yh/spContract/searchRoute', {fromName:inputStr,toName:inputStr2}, function(data){
					 for(var i = 0; i < data.length; i++){
						 	$('#routeId').val(data[i].RID);
					 }
				 },'json');
			 }*/
    	});
		
		//datatable, 动态处理
	    $('#eeda-table').dataTable({
	        //"sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span12'i><'span12 center'p>>",
	        "sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span12'i><'span12 center'p>>",
	        //"sPaginationType": "bootstrap",
	        "iDisplayLength": 10,
	    	"oLanguage": {
	            "sUrl": "/eeda/dataTables.ch.txt"
	        },
	        "sAjaxSource": "/yh/delivery/deliveryList",
	        "aoColumns": [   
	            
	            {"mDataProp":"ORDER_NO"},
	            {"mDataProp":"TRANSFER_ORDER_ID"},
	            {"mDataProp":"CUSTOMER_ID"},        	
	            {"mDataProp":"SP_ID"},
	            {"mDataProp":"NOTIFY_PARTY_ID"},
	            {"mDataProp":"STATUS"},
	            { 
	                "mDataProp": null, 
	                "sWidth": "8%",                
	                "fnRender": function(obj) {                    
	                    return "<a class='btn btn-success ' href='/yh/transferOrder/edit/"+obj.aData.ID+"'>"+
	                                "<i class='fa fa-edit fa-fw'></i>"+
	                                "查看"+
	                            "</a>"+
	                            "<a class='btn btn-danger cancelbutton' code='"+obj.aData.ID+"'>"+
	                                "<i class='fa fa-trash-o fa-fw'></i>"+ 
	                                "取消"+
	                            "</a>";
	                }
	            }                         
	        ]      
	    });

	    $("#changePage").click(function(){
	    	var type= $("#type3").val();
	    	var type2= $("#type2").val();
	    	if(type=='CUSTOMER'||type2=='CUSTOMER'){
	    		window.location.href="/yh/customerContract";
	    	}if(type=='SERVICE_PROVIDER'||type2=='SERVICE_PROVIDER'){
	    		window.location.href="/yh/spContract";
	    	}if(type=='DELIVERY_SERVICE_PROVIDER'||type2=='DELIVERY_SERVICE_PROVIDER'){
	    		window.location.href="/yh/deliverySpContract";
	    	}
	    });

	    $(function(){
	    	 var type= $("#type2").val();
	    	 var type2= $("#type3").val();
	    	 $('#reset').hide();
	    	 if(type!=null||type!=''){
		 	    if(type=='CUSTOMER'){
		 	    	$("#labeltext").html("创建新客户合同");
		 	    }if(type=='SERVICE_PROVIDER'){
		 	    	$("#labeltext").html("创建干线供应商合同");
		 	    }if(type=='DELIVERY_SERVICE_PROVIDER'){
		 	    	$("#labeltext").html("创建配送供应商合同");
		 	    }
	    	 }if(type2!=null||type2!=''){
	    		 if(type2=='CUSTOMER'){
			 	    	$("#labeltext").html("编辑新客户合同");
			 	    }if(type2=='SERVICE_PROVIDER'){
			 	    	$("#labeltext").html("编辑干线供应商合同");
			 	    }if(type2=='DELIVERY_SERVICE_PROVIDER'){
			 	    	$("#labeltext").html("编辑配送供应商合同");
			 	    }
	    	 }
	    }) ;
	   
	   
	   
	  //获取货品的名称list，选中信息在下方展示其他信息
		$('#itemNameMessage').on('keyup click', function(){
			var inputStr = $('#itemNameMessage').val();
			var customerId = $('#partyid').val();
			$.get('/yh/customerContract/searchItemName', {input:inputStr,customerId:customerId}, function(data){
				console.log(data);
				var itemNameList =$("#itemNameList");
				itemNameList.empty();
				for(var i = 0; i < data.length; i++)
				{
					var item_name = data[i].ITEM_NAME;
					if(item_name == null){
						item_name = '';
					}
					itemNameList.append("<li><a tabindex='-1' class='fromLocationItem' id='"+data[i].ID+"' item_desc='"+data[i].ITEM_DESC+"' item_no='"+data[i].ITEM_NO+"' expire_date='"+data[i].EXPIRE_DATE+"' lot_no='"+data[i].LOT_NO+"' total_quantity='"+data[i].TOTAL_QUANTITY+"' unit_price='"+data[i].UNIT_PRICE+"' unit_cost='"+data[i].UNIT_COST+"' uom='"+data[i].UOM+"', caton_no='"+data[i].CATON_NO+"', >"+data[i].ITEM_NAME+"</a></li>");
				}
			},'json');		
			$("#itemNameList").css({ 
				left:$(this).position().left+"px", 
				top:$(this).position().top+32+"px" 
			}); 
			$('#itemNameList').show();        
		});
		$('#itemNameMessage').on('blur', function(){
		$("#itemNameList").hide();
		});
		$('#itemNameList').on('blur', function(){
			$('#itemNameList').hide();
		});

	 	$('#itemNameList').on('mousedown', function(){
		return false;//阻止事件回流，不触发 $('#spMessage').on('blur'
	 	});
	 // 选中产品名
		$('#itemNameList').on('mousedown', '.fromLocationItem', function(e){
			$("#itemNameMessage").val($(this).text());
			if($(this).attr('item_no') == 'null'){
				$("#item_no").val('');
			}else{
				$("#itemNoMessage").val($(this).attr('item_no'));
			}
			
			$("#productId").val($(this).attr('id'));
			$('#itemNameList').hide();
		});  	
		
		//开始时间点击后隐藏
		$('#datetimepicker').datetimepicker({  
	        format: 'yyyy-MM-dd',  
	        language: 'zh-CN'
	    }).on('changeDate', function(ev){
	        $(".bootstrap-datetimepicker-widget").hide();
	        $('#beginTime_filter').trigger('keyup');
	    });
		//结束时间点击后隐藏
		$('#datetimepicker2').datetimepicker({  
	        format: 'yyyy-MM-dd',  
	        language: 'zh-CN', 
	        autoclose: true,
	        pickerPosition: "bottom-left"
	    }).on('changeDate', function(ev){
	        $(".bootstrap-datetimepicker-widget").hide();
	        $('#endTime_filter').trigger('keyup');
	    });
		
		$("#close,#cancel").click(function(){
			$("#routeItemForm")[0].reset;
		});
		

	    //获取全国省份
	    $(function(){
	     	var province = $("#mbProvinceFrom");
	     	$.post('/yh/serviceProvider/province',function(data){
	     		province.append("<option>--请选择省份--</option>");
					var hideProvince = $("#hideProvinceFrom").val();
	     		for(var i = 0; i < data.length; i++)
					{
						if(data[i].NAME == hideProvince){
	     				province.append("<option value= "+data[i].CODE+" selected='selected'>"+data[i].NAME+"</option>");
	     				
	     				
						}else{
	     				province.append("<option value= "+data[i].CODE+">"+data[i].NAME+"</option>");						
						}
					}
	     		
	     	},'json');
	    });
	    
	    //获取省份的城市
	    $('#mbProvinceFrom').on('change', function(){
				var inputStr = $(this).val();
				$.get('/yh/serviceProvider/city', {id:inputStr}, function(data){
					var cmbCity =$("#cmbCityFrom");
					cmbCity.empty();
					cmbCity.append("<option>--请选择城市--</option>");
					for(var i = 0; i < data.length; i++)
					{
						cmbCity.append("<option value= "+data[i].CODE+">"+data[i].NAME+"</option>");						
					}
				},'json');
			});
	    
	    //获取城市的区县
	    $('#cmbCityFrom').on('change', function(){
				var inputStr = $(this).val();
				var code = $("#locationForm").val(inputStr);
				$.get('/yh/serviceProvider/area', {id:inputStr}, function(data){
					var cmbArea =$("#cmbAreaFrom");
					cmbArea.empty();
					cmbArea.append("<option>--请选择区(县)--</option>");
					for(var i = 0; i < data.length; i++)
					{
						cmbArea.append("<option value= "+data[i].CODE+">"+data[i].NAME+"</option>");	
					}
				},'json');
			});
	    
	    $('#cmbAreaFrom').on('change', function(){
				var inputStr = $(this).val();
				var code = $("#locationForm").val(inputStr);
			});         
	    

	    //获取全国省份
	    $(function(){
	     	var province = $("#mbProvinceTo");
	     	$.post('/yh/serviceProvider/province',function(data){
	     		province.append("<option>--请选择省份--</option>");
				var hideProvince = $("#hideProvinceTo").val();
	     		for(var i = 0; i < data.length; i++)
					{
						if(data[i].NAME == hideProvince){
	     				province.append("<option value= "+data[i].CODE+" selected='selected'>"+data[i].NAME+"</option>");
	     				
	     				
						}else{
	     				province.append("<option value= "+data[i].CODE+">"+data[i].NAME+"</option>");						
						}
					}
	     		
	     	},'json');
	    });
	    
	    //获取省份的城市
	    $('#mbProvinceTo').on('change', function(){
				var inputStr = $(this).val();
				$.get('/yh/serviceProvider/city', {id:inputStr}, function(data){
					var cmbCity =$("#cmbCityTo");
					cmbCity.empty();
					cmbCity.append("<option>--请选择城市--</option>");
					for(var i = 0; i < data.length; i++)
					{
						cmbCity.append("<option value= "+data[i].CODE+">"+data[i].NAME+"</option>");						
					}
				},'json');
			});
	    
	    //获取城市的区县
	    $('#cmbCityTo').on('change', function(){
				var inputStr = $(this).val();
				var code = $("#locationTo").val(inputStr);
				$.get('/yh/serviceProvider/area', {id:inputStr}, function(data){
					var cmbArea =$("#cmbAreaTo");
					cmbArea.empty();
					cmbArea.append("<option>--请选择区(县)--</option>");
					for(var i = 0; i < data.length; i++)
					{
						cmbArea.append("<option value= "+data[i].CODE+">"+data[i].NAME+"</option>");	
					}
				},'json');
			});
	    
	    $('#cmbAreaTo').on('change', function(){
				var inputStr = $(this).val();
				var code = $("#locationTo").val(inputStr);
			});
	

    var searchAllLocationFrom = function(locationFrom){
    	//var locationFrom = $('#hideLocationFrom').val();
    	$.get('/yh/transferOrder/searchLocationFrom', {locationFrom:locationFrom}, function(data){
    		console.log(data);			
    		var provinceVal = data.PROVINCE;
    		var cityVal = data.CITY;
    		var districtVal = data.DISTRICT;
	        $.get('/yh/serviceProvider/searchAllLocation', {province:provinceVal, city:cityVal}, function(data){	
		        //获取全国省份
	         	var province = $("#mbProvinceFrom");
	     		province.empty();
	     		province.append("<option>--请选择省份--</option>");
	     		for(var i = 0; i < data.provinceLocations.length; i++){
					if(data.provinceLocations[i].NAME == provinceVal){
						$("#locationForm").val(data.provinceLocations[i].CODE);
						province.append("<option value= "+data.provinceLocations[i].CODE+" selected='selected'>"+data.provinceLocations[i].NAME+"</option>");
					}else{
						province.append("<option value= "+data.provinceLocations[i].CODE+">"+data.provinceLocations[i].NAME+"</option>");						
					}
				}

				var cmbCity =$("#cmbCityFrom");
	     		cmbCity.empty();
				cmbCity.append("<option>--请选择城市--</option>");
				for(var i = 0; i < data.cityLocations.length; i++)
				{
					if(data.cityLocations[i].NAME == cityVal){
						$("#locationForm").val(data.cityLocations[i].CODE);
						cmbCity.append("<option value= "+data.cityLocations[i].CODE+" selected='selected'>"+data.cityLocations[i].NAME+"</option>");
					}else{
						cmbCity.append("<option value= "+data.cityLocations[i].CODE+">"+data.cityLocations[i].NAME+"</option>");						
					}
				}
				
				if(data.districtLocations.length > 0){
    				var cmbArea =$("#cmbAreaFrom");
    				cmbArea.empty();
    				cmbArea.append("<option>--请选择区(县)--</option>");
    				for(var i = 0; i < data.districtLocations.length; i++)
    				{
    					if(data.districtLocations[i].NAME == districtVal){
    						$("#locationForm").val(data.districtLocations[i].CODE);
    						cmbArea.append("<option value= "+data.districtLocations[i].CODE+" selected='selected'>"+data.districtLocations[i].NAME+"</option>");
    					}else{
    						cmbArea.append("<option value= "+data.districtLocations[i].CODE+">"+data.districtLocations[i].NAME+"</option>");						
    					}
    				}
    			}else{
    				var cmbArea =$("#cmbAreaFrom");
    				cmbArea.empty();
    			}
	        },'json');
    	},'json');
    };
    
    var searchAllLocationTo = function(locationTo){
    	//var locationFrom = $('#hideLocationFrom').val();
    	$.get('/yh/transferOrder/searchLocationFrom', {locationFrom:locationTo}, function(data){
    		console.log(data);			
    		var provinceVal = data.PROVINCE;
    		var cityVal = data.CITY;
    		var districtVal = data.DISTRICT;
    		$.get('/yh/serviceProvider/searchAllLocation', {province:provinceVal, city:cityVal}, function(data){	
    			//获取全国省份
    			var province = $("#mbProvinceTo");
    			province.empty();
    			province.append("<option>--请选择省份--</option>");
    			for(var i = 0; i < data.provinceLocations.length; i++){
    				if(data.provinceLocations[i].NAME == provinceVal){
						$("#locationTo").val(data.provinceLocations[i].CODE);
    					province.append("<option value= "+data.provinceLocations[i].CODE+" selected='selected'>"+data.provinceLocations[i].NAME+"</option>");
    				}else{
    					province.append("<option value= "+data.provinceLocations[i].CODE+">"+data.provinceLocations[i].NAME+"</option>");						
    				}
    			}
    			
    			var cmbCity =$("#cmbCityTo");
    			cmbCity.empty();
    			cmbCity.append("<option>--请选择城市--</option>");
    			for(var i = 0; i < data.cityLocations.length; i++)
    			{
    				if(data.cityLocations[i].NAME == cityVal){
						$("#locationTo").val(data.cityLocations[i].CODE);
    					cmbCity.append("<option value= "+data.cityLocations[i].CODE+" selected='selected'>"+data.cityLocations[i].NAME+"</option>");
    				}else{
    					cmbCity.append("<option value= "+data.cityLocations[i].CODE+">"+data.cityLocations[i].NAME+"</option>");						
    				}
    			}
    			
    			if(data.districtLocations.length > 0){
    				var cmbArea =$("#cmbAreaTo");
    				cmbArea.empty();
    				cmbArea.append("<option>--请选择区(县)--</option>");
    				for(var i = 0; i < data.districtLocations.length; i++)
    				{
    					if(data.districtLocations[i].NAME == districtVal){
    						$("#locationTo").val(data.districtLocations[i].CODE);
    						cmbArea.append("<option value= "+data.districtLocations[i].CODE+" selected='selected'>"+data.districtLocations[i].NAME+"</option>");
    					}else{
    						cmbArea.append("<option value= "+data.districtLocations[i].CODE+">"+data.districtLocations[i].NAME+"</option>");						
    					}
    				}
    			}else{
    				var cmbArea =$("#cmbAreaTo");
    				cmbArea.empty();
    			}
    		},'json');
    	},'json');
    };
});

