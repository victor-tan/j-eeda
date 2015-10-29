$(document).ready(function() {
	if(order_no){
		document.title = order_no +' | '+document.title;
	}
	$('#menu_cost').addClass('active').find('ul').addClass('in');

	//设置一个变量值，用来保存当前的ID
	var parentId = "costCheckOrderbasic";
	$("#transferOrderMilestoneList").click(function(e){
		parentId = e.target.getAttribute("id");
	});
	$("#costCheckOrderbasic").click(function(e){
		parentId = e.target.getAttribute("id");
	});
    
	//点击保存的事件，保存运输单信息
	//transferOrderForm 不需要提交	
 	$("#saveCostCheckOrderBtn").click(function(e){
		//阻止a 的默认响应行为，不需要跳转
		e.preventDefault();
		//异步向后台提交数据
		$("#saveCostCheckOrderBtn").attr("disabled",true);
		$.post('/costCheckOrder/save', $("#costCheckOrderForm").serialize(), function(data){
			if(data.ID>0){
				$("#sorder_no").html('<strong>'+data.ORDER_NO+'<strong>');
			  	//$("#suser_name").html(data.USER_NAME);
			  	$("#create_stamp").html(data.CREATE_STAMP);
			  	$("#confirm_name").html(data.CONFIRM_NAME);
			  	$("#confirm_stamp").html(data.CONFIRM_STAMP);
			  	$("#remark").val(data.REMARK);
				
				$("#costCheckOrderId").val(data.ID);
			  	//$("#style").show();
			  	$("#departureConfirmationBtn").attr("disabled", false);
			  	contactUrl("edit?id",data.ID);
			  	//if("costCheckOrderbasic" == parentId){
			  	$.scojs_message('保存成功', $.scojs_message.TYPE_OK);
			  	$("#saveCostCheckOrderBtn").attr("disabled",false);
			  	$("#auditBtn").attr("disabled",false);
			  	//}
			}else{
				alert('数据保存失败。');
			}
		},'json');
 		//$.scojs_message('保存成功', $.scojs_message.TYPE_OK);
	});
	
    if($("#costCheckOrderStatus").text() == 'new'){
    	$("#costCheckOrderStatus").text('新建');
	}else{
		$("#printBtn").attr("disabled",true);
	}
    
	// 审核
	$("#auditBtn").click(function(e){
		//阻止a 的默认响应行为，不需要跳转
		e.preventDefault();
		//异步向后台提交数据
		var costCheckOrderId = $("#costCheckOrderId").val();
		$.post('/costCheckOrder/auditCostCheckOrder', {costCheckOrderId:costCheckOrderId}, function(data){
			/*console.log(data.success);*/
			if(data.arapAuditOrder.ID>0){
				$("#confirm_name").html(data.ul.C_NAME);
			  	$("#confirm_stamp").html(data.arapAuditOrder.CONFIRM_STAMP);
				$("#printBtn").attr("disabled",false);
				$("#costCheckOrderStatus").text("已确认");
				$("#saveCostCheckOrderBtn").attr("disabled",true);
			  	$("#auditBtn").attr("disabled",true);
			  	$("#addOrderBtn").attr("disabled",true);
			}
		},'json');
		
	});
	/*if($("#costCheckOrderStatus").text()!="新建"){
		
		$("#auditBtn").attr("disabled",false);
		$("#printBtn").attr("disabled",false);
		if($("#costCheckOrderStatus").text()!="已确认"){
			$("#auditBtn").attr("disabled",true);
			$("#saveCostCheckOrderBtn").attr("disabled",true);
		}
		
	}else{
		if($("#costCheckOrderId").val() !="" && $("#costCheckOrderId").val()!=null){
			$("#auditBtn").attr("disabled",false);
			$("#printBtn").attr("disabled",true);
		}
	}*/
	
	if($("#costCheckOrderStatus").text()=="新建"){
		$("#saveCostCheckOrderBtn").attr("disabled",false);
		$("#auditBtn").attr("disabled",true);
		$("#printBtn").attr("disabled",true);
	}else{
		$("#saveCostCheckOrderBtn").attr("disabled",true);
		$("#auditBtn").attr("disabled",true);
		$("#printBtn").attr("disabled",false);
	}
	/*--------------------------------------------------------------------*/
	var alerMsg='<div id="message_trigger_err" class="alert alert-danger alert-dismissable" style="display:none">'+
	    '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>'+
	    'Lorem ipsum dolor sit amet, consectetur adipisicing elit. <a href="#" class="alert-link">Alert Link</a>.'+
	    '</div>';
	$('body').append(alerMsg);

	$('#message_trigger_err').on('click', function(e) {
		e.preventDefault();
	});
	
	//设置一个变量值，用来保存当前的ID
	var parentId = "costCheckOrderbasic";
	$("#transferOrderMilestoneList").click(function(e){
		parentId = e.target.getAttribute("id");
	});
	$("#costCheckOrderbasic").click(function(e){
		parentId = e.target.getAttribute("id");
	});
	
	//datatable, 动态处理
    var costConfiremTable = $('#costConfirem-table').dataTable({
        "bFilter": false, //不需要默认的搜索框
        "bSort": false, 
        "sDom": "<'row-fluid'<'span6'l><'span6'f>r><'datatable-scroll't><'row-fluid'<'span12'i><'span12 center'p>>",
        "iDisplayLength": 10,
        "bServerSide": true,
    	  "oLanguage": {
            "sUrl": "/eeda/dataTables.ch.txt"
        },
        "fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
			$(nRow).attr('id', aData.DID);
			$(nRow).attr('ids', aData.ID);
			$(nRow).attr('order_ty', aData.BUSINESS_TYPE);
			return nRow;
		},
        "sAjaxSource": "/costCheckOrder/costConfirmListById",
        "aoColumns": [ 
            {"mDataProp":"BUSINESS_TYPE"},            	
            {"mDataProp":"SPNAME","sWidth":"150px"},
            {"mDataProp":null, 
                "fnRender": function(obj) {
                    if(obj.aData.STATUS=='new'){
                        return '新建';
                    }else if(obj.aData.STATUS=='checking'){
                        return '已发送对帐';
                    }else if(obj.aData.STATUS=='confirmed'){
                        return '已审核';
                    }else if(obj.aData.STATUS=='completed'){
                        return '已结算';
                    }else if(obj.aData.STATUS=='cancel'){
                        return '取消';
                    }
                    return obj.aData.STATUS;
                }
            },                           
            {"mDataProp":"RETURN_ORDER_COLLECTION"},  
		    {"mDataProp":null, 
                "fnRender": function(obj) {
                    return "未收款";
            }},
            {"mDataProp":"ORDER_NO"},
            {"mDataProp":"TRANSFER_ORDER_NO"},
            {"mDataProp":"CREATE_STAMP"},                 	
            {"mDataProp":"AMOUNT"},                        
            {"mDataProp":"VOLUME"},                        
            {"mDataProp":"WEIGHT"},                        
            {"mDataProp":"PAY_AMOUNT"},
            {"mDataProp":"CHANGE_AMOUNT"},
            {"mDataProp":"OFFICE_NAME"},                       
            {"mDataProp":"REMARK"}                         
        ]      
    });	
    
    $("#arapAuditList").click(function(){
    	var costCheckOrderId = $("#costCheckOrderId").val();
    	var orderNos = $("#orderNos").val();
    	var orderIds = $("#orderIds").val();
    	costConfiremTable.fnSettings().sAjaxSource = "/costCheckOrder/costConfirmListById?costCheckOrderId="+costCheckOrderId+"&orderNos="+orderNos+"&orderIds="+orderIds;
    	costConfiremTable.fnDraw(); 
    });
    
    $("#printBtn").on('click',function(){
    	var order_no = $("#sorder_no").text();
    	if(order_no != null && order_no != ""){
    		$.post('/report/printCheckOrder', {order_no:order_no}, function(data){
        		window.open(data);
        	});
    	}else{
    		$.scojs_message('当前单号为空', $.scojs_message.TYPE_ERROR);
    	}
    	
    });
    $("#costConfirem-table").on('blur', 'input', function(e){
		e.preventDefault();
		var orderNos = $("#orderNos").val();
		var ids=$("#orderIds").val();
		var paymentId = $(this).parent().parent().attr("id");
		var departId = $(this).parent().parent().attr("ids");
		var ty = $(this).parent().parent().attr("order_ty");
		var name = $(this).attr("name");
		var value = $(this).val();
		 if(isNaN(value)){      
			 alert("调整金额为数字类型");
		 }else{
			 $.post('/costCheckOrder/updateDepartOrderFinItem', {orderNos:orderNos,ty:ty,departId:departId,paymentId:paymentId,ids:ids, name:name, value:value}, function(data){
				 $.scojs_message('调整金额成功', $.scojs_message.TYPE_OK);
				 $("#debitAmount").html(data.changeAmount);
				 $("#costAmount").html(data.actualAmount); 
				 $("#total_amount").val(data.changeAmount);
		    	},'json');
		 }
	}); 
    var costMiscListTable = $('#costMiscList-table').dataTable({
        "bFilter": false, //不需要默认的搜索框
        "sDom": "<'row-fluid'<'span6'l><'span6'f>r><'datatable-scroll't><'row-fluid'<'span12'i><'span12 center'p>>",
        "iDisplayLength": 10,
        "bServerSide": true,
          "oLanguage": {
            "sUrl": "/eeda/dataTables.ch.txt"
        },
        "sAjaxSource": "/costCheckOrder/checkCostMiscList",
        "aoColumns": [   
            {"mDataProp":"MISC_ORDER_NO", "sWidth":"80px",
                "fnRender": function(obj) {
                    return "<a href='/costMiscOrder/edit?id="+obj.aData.ID+"'target='_blank'>"+obj.aData.MISC_ORDER_NO+"</a>";
                }},
            {"mDataProp":"CNAME", "sWidth":"200px"},
            {"mDataProp":"NAME", "sWidth":"200px"},
            {"mDataProp":"AMOUNT", "sWidth":"100px"},
            {"mDataProp":"REMARK", "sWidth":"200px"}                        
        ]      
    });
    
    $("#costMiscList").click(function(){
    	costMiscListTable.fnSettings().sAjaxSource = "/costCheckOrder/checkCostMiscList?costCheckOrderId="+$("#costCheckOrderId").val();
    	costMiscListTable.fnDraw();  
    });

    $('#datetimepicker').datetimepicker({  
        format: 'yyyy-MM-dd',  
        language: 'zh-CN', 
        autoclose: true,
        pickerPosition: "bottom-left"
    }).on('changeDate', function(ev){
        $(".bootstrap-datetimepicker-widget").hide();
        $('#departure_time').trigger('keyup');
    });	 
    
    $('#datetimepicker2').datetimepicker({  
    	format: 'yyyy-MM-dd',  
    	language: 'zh-CN', 
    	autoclose: true,
    	pickerPosition: "bottom-left"
    }).on('changeDate', function(ev){
    	$(".bootstrap-datetimepicker-widget").hide();
    	$('#arrival_time').trigger('keyup');
    });	
    
    var externalTab = $('#external-table').dataTable({
        "bFilter": false, //不需要默认的搜索框
        "sDom": "<'row-fluid'<'span6'l><'span6'f>r><'datatable-scroll't><'row-fluid'<'span12'i><'span12 center'p>>",
        "iDisplayLength": 10,
        "bServerSide": true,
    	  "oLanguage": {
            "sUrl": "/eeda/dataTables.ch.txt"
        },
        "sAjaxSource": "/costCheckOrder/externalMiscOrderList",
        "aoColumns": [      
	        { "mDataProp": null,
	            "fnRender": function(obj) {
	              return '<input type="checkbox" name="order_check_box" class="checkedOrUnchecked" value="'+obj.aData.ID+'">';
	            }
	        }, 
            {"mDataProp":"ORDER_NO","sWidth": "80px",
            	"fnRender": function(obj) {
        			return "<a href='/costMiscOrder/edit?id="+obj.aData.ID+"'target='_blank'>"+obj.aData.ORDER_NO+"</a>";
        		}},
            {"mDataProp":"TYPE","sWidth": "100px",
            	"fnRender": function(obj) {
                    if(obj.aData.TYPE=='ordinary_receivables'){
                        return '普通收款';
                    }else if(obj.aData.TYPE=='offset_payment'){
                        return '抵销货款';
                    }
                    return obj.aData.TYPE;
                }
            },
            {"mDataProp":"STATUS","sWidth": "100px",
                "fnRender": function(obj) {
                    if(obj.aData.STATUS=='new'){
                        return '新建';
                    }else if(obj.aData.STATUS=='checking'){
                        return '已发送对帐';
                    }else if(obj.aData.STATUS=='confirmed'){
                        return '已审核';
                    }else if(obj.aData.STATUS=='completed'){
                        return '已结算';
                    }else if(obj.aData.STATUS=='cancel'){
                        return '取消';
                    }
                    return obj.aData.STATUS;
                }
            },
            {"mDataProp":"CREATE_STAMP","sWidth": "150px"},
            {"mDataProp":"COST_ORDER_NO","sWidth": "150px"},
            {"mDataProp":"REMARK","sWidth": "150px"}                       
        ]      
    });	
  //datatable, 动态处理
    var addcheckedCostCheck = $('#addcheckedCostCheck-table').dataTable({
        "bProcessing": true, 
        "bFilter": false, //不需要默认的搜索框
        "bSort": true, 
        "sDom": "<'row-fluid'<'span6'l><'span6'f>r><'datatable-scroll't><'row-fluid'<'span12'i><'span12 center'p>>",
        "iDisplayLength": 10,
        "aLengthMenu": [ [10, 25, 50, 9999999], [10, 25, 50, "All"] ],
        "bServerSide": true,
    	  "oLanguage": {
            "sUrl": "/eeda/dataTables.ch.txt"
        },
        "fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
			$(nRow).attr('id', aData.DID);
			$(nRow).attr('ids', aData.ID);
			$(nRow).attr('order_ty', aData.BUSINESS_TYPE);
			return nRow;
		},
        "sAjaxSource": "/costCheckOrder/unSelectedList",
        "aoColumns": [ 
            { "mDataProp": null, "sWidth":"20px", "bSortable": false,
                "fnRender": function(obj) {
	               	 
                	var strcheck='<input  type="checkbox" name="order_check_box" spname="'+obj.aData.SPNAME+'" id="'+obj.aData.ID+'" class="checkedOrUnchecked" order_no="'+obj.aData.BUSINESS_TYPE+'">';;
                	//判断obj.aData.ID 是否存在 list id 
                	console.log(ids);
                	 for(var i=0;i<ids.length;i++){
                		 console.log(i + ":"+ids[i]);
                		 console.log("obj.aData.ID="+obj.aData.ID);
                         if(ids[i]==obj.aData.ID){                        	 
                        	 return strcheck= '<input   checked="checked" type="checkbox" name="order_check_box" spname="'+obj.aData.SPNAME+'" id="'+obj.aData.ID+'" class="checkedOrUnchecked" order_no="'+obj.aData.BUSINESS_TYPE+'">'; 
                        	
                         }
                     }
                	 return strcheck;
                }
            },
            {"mDataProp":"BUSINESS_TYPE", "sWidth":"80px"},            	
            {"mDataProp":"BOOKING_NOTE_NUMBER", "sWidth":"200px"},
            {"mDataProp":"SERIAL_NO", "sWidth":"200px"},
            {"mDataProp":"REF_NO", "sWidth":"200px"},
            {"mDataProp":"TO_NAME", "sWidth":"130px"},
            {"mDataProp":"PLANNING_TIME", "sWidth":"140px"},
            {"mDataProp":"AMOUNT", "sWidth":"40px"},
            {"mDataProp":"PAY_AMOUNT", "sWidth":"60px"},
            {"mDataProp":"CHANGE_AMOUNT","sWidth":"60px",
            	"fnRender": function(obj) {
                    if(obj.aData.CHANGE_AMOUNT!=''&& obj.aData.CHANGE_AMOUNT != null){
                        return "<input type='text' style='width:60px' name='change_amount' id='change' value='"+obj.aData.CHANGE_AMOUNT+"'/>";
                        
                    }
                    else {
                    	if(obj.aData.PAY_AMOUNT!=null){
                        return "<input type='text' style='width:60px' name='change_amount' value='"+obj.aData.PAY_AMOUNT+"'/>";
                    	}
                    	else{
                    		return "<input type='text' style='width:60px' name='change_amount' value='0'/>";
                    	}
                    }
                }
            },
            {"mDataProp":"OFFICE_NAME", "sWidth":"90px"}, 
            {"mDataProp":"SPNAME","sClass":"spname", "sWidth":"200px"},
            {"mDataProp":"CUSTOMER_NAME", "sWidth":"120px"},
            {"mDataProp":"CUSTOMER_ORDER_NO", "sWidth":"200px"},
            {"mDataProp":"ORDER_NO", "sWidth":"200px", 
                "fnRender": function(obj) {
                	var str = "";
                    if(obj.aData.ORDER_NO.indexOf("PS") > -1){
                        str = "<a href='/delivery/edit?id="+obj.aData.ID+"'target='_blank'>"+obj.aData.ORDER_NO+"</a>";
                    }else if(obj.aData.ORDER_NO.indexOf("PC") > -1||obj.aData.ORDER_NO.indexOf("DC") > -1){
                        str = "<a href='/pickupOrder/edit?id="+obj.aData.ID+"'target='_blank'>"+obj.aData.ORDER_NO+"</a>";
                    }else if(obj.aData.ORDER_NO.indexOf("FC") > -1){
                        str = "<a href='/departOrder/edit?id="+obj.aData.ID+"'target='_blank'>"+obj.aData.ORDER_NO+"</a>";
                    }else {
                        str = "<a href='/insuranceOrder/edit?id="+obj.aData.ID+"'target='_blank'>"+obj.aData.ORDER_NO+"</a>";
                    }
                    return str;
                }
            },
            {"mDataProp":"RECEIVINGUNIT", "sWidth":"130px"},
            {"mDataProp":"TRANSFER_ORDER_NO", "sWidth":"200px"},
            {"mDataProp":"CREATE_STAMP", "sWidth":"180px"}, 
            {"mDataProp":"RETURN_ORDER_COLLECTION", "sWidth":"90px"},  
		    {"mDataProp":null, "sWidth":"90px",
                "fnRender": function(obj) {
                    return "未收款";
            }},
            {"mDataProp":null, "sWidth": "160px", 
                "fnRender": function(obj) {
                    if(obj.aData.STATUS=='new'){
                        return '新建';
                    }else if(obj.aData.STATUS=='checking'){
                        return '已发送对帐';
                    }else if(obj.aData.STATUS=='confirmed'){
                        return '已审核';
                    }else if(obj.aData.STATUS=='completed'){
                        return '已结算';
                    }else if(obj.aData.STATUS=='cancel'){
                        return '取消';
                    }
                    return obj.aData.STATUS;
                }
            },                        
            {"mDataProp":"FROM_NAME", "sWidth":"150px"},   
            {"mDataProp":"VOLUME", "sWidth":"50px"},                        
            {"mDataProp":"WEIGHT", "sWidth":"40px"},                                           
            {"mDataProp":"REMARK", "sWidth":"150px"}                         
        ]     
    });	
    $("#addExternalMiscOrderBtn").click(function(){
    	externalTab.fnSettings().sAjaxSource = "/costCheckOrder/externalMiscOrderList";
    	externalTab.fnDraw();  
    }); 
    $("#addOrderFormBtn").click(function(){
    	var ids = $("#orderIds").val();
    	var OrderNos = $("#orderNos").val();
    	$.post('/costCheckOrder/create', {ids: ids, orderNos: OrderNos},
    			
		'json');
    });
    $("#addOrderFormBtn").click(function(){
    	var costCheckOrderId = $("#costCheckOrderId").val();
    	var orderNos = $("#orderNos").val();
    	var orderIds = $("#orderIds").val();
    	$('#addOrder').modal('hide');
    	$("#auditBtn").attr("disabled", true);
    	costConfiremTable.fnSettings().sAjaxSource = "/costCheckOrder/costConfirmListById?costCheckOrderId="+costCheckOrderId+"&orderNos="+orderNos+"&orderIds="+orderIds;
    	costConfiremTable.fnDraw(); 
    });
    var addIds = [$("#orderIds").val()];
    var orderNos = [$("#orderNos").val()];
    var isID = [$("#orderIds").val().split(",")];
    $("#addcheckedCostCheck-table").on('click', '.checkedOrUnchecked', function(e){
    	var a =$("#company").html();
    	var b =$(this).attr('spname');
    	if($(this).prop("checked") == true){
    		if(a!=b){
        		alert("供应商不一致");
        		return false;
        	}
    		var exist=$.inArray($(this).attr('id'),isID[0]);
    		if(exist>=0){
        		alert("单号已经存在明细");
        		return false;
        	}
    		addIds.push($(this).attr('id'));
    		orderNos.push($(this).attr('order_no'));
    		$("#orderIds").val(addIds);
    		$("#orderNos").val(orderNos);
    	}
    	else{
    		addIds.splice($.inArray($(this).attr('id'),addIds),1);
    		orderNos.splice($.inArray($(this).attr('order_no'),orderNos),1);
    		$("#orderIds").val(addIds);
    		$("#orderNos").val(orderNos);
    	}
    });
    // 未选中列表
	$("#external-table").on('click', '.checkedOrUnchecked', function(e){
		if($(this).prop("checked") == true){
			ids.push($(this).val());
			$("#micsOrderIds").val(ids);
		}			
	});
	var ids = [];
	// 已选中列表
	$("#external-table").on('click', '.checkedOrUnchecked', function(e){
		if($(this).prop("checked") == false){
			if(ids.length != 0){
				ids.splice($.inArray($(this).val(),ids),1);
				$("#micsOrderIds").val(ids);
			}
		}			
	});
	 var refreshCreateList = function() {
	    	var booking_id = $("#booking_id").val();
	    	var serial_no = $("#serial_no").val();
	    	var ispage = "costCheckOrder";
	    	addcheckedCostCheck.fnSettings().sAjaxSource = "/costCheckOrder/unSelectedList?booking_id="+booking_id
	    													+"&serial_no="+serial_no
	    													+"&ispage="+ispage;
	    	addcheckedCostCheck.fnDraw();
	    	
	    	
	    };
	 $("#booking_id,#serial_no").on('keyup',function(){
	    	refreshCreateList();
	    });
	if($("#costCheckOrderStatus").html()=="新建"){
		$("#addOrderBtn").attr("disabled", false);
	}
	$("#addExternalFormBtn").click(function(){
		var micsOrderIds = $("#micsOrderIds").val();
		var costCheckOrderId = $("#costCheckOrderId").val();
		$.post('/costCheckOrder/updateCostMiscOrder', {micsOrderIds: micsOrderIds, costCheckOrderId: costCheckOrderId}, function(data){
			if(data.ID > 0){
				$("#debitAmountSpan")[0].innerHTML = data.DEBIT_AMOUNT;
				$("#debitAmount")[0].innerHTML = data.DEBIT_AMOUNT;
				$("#costAmount")[0].innerHTML = data.COST_AMOUNT;
				$("#hiddenDebitAmount").val(data.DEBIT_AMOUNT);
				
				$('#addExternalMiscOrder').modal('hide');
		    	costMiscListTable.fnSettings().sAjaxSource = "/costCheckOrder/checkCostMiscList?costCheckOrderId="+$("#costCheckOrderId").val();
		    	costMiscListTable.fnDraw();  
			}
		},'json');
	});
} );