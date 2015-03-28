$(document).ready(function() {
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
		$.post('/costCheckOrder/save', $("#costCheckOrderForm").serialize(), function(data){
			if(data.ID>0){
				$("#costCheckOrderId").val(data.ID);
			  	//$("#style").show();
			  	$("#departureConfirmationBtn").attr("disabled", false);
			  	//if("costCheckOrderbasic" == parentId){
			  		$.scojs_message('保存成功', $.scojs_message.TYPE_OK);
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
		$("#printBtn").show();
	}
    
	// 审核
	$("#auditBtn").click(function(e){
		//阻止a 的默认响应行为，不需要跳转
		e.preventDefault();
		//异步向后台提交数据
		var costCheckOrderId = $("#costCheckOrderId").val();
		$.post('/costCheckOrder/auditCostCheckOrder', {costCheckOrderId:costCheckOrderId}, function(data){
			/*console.log(data.success);*/
			if(data.success){
				$("#printBtn").attr("disabled",false);
				$("#costCheckOrderStatus").text("已确认");
			}
		},'json');
		
	});
	if($("#costCheckOrderStatus").text()!="新建"){
		
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
            {"mDataProp":"OFFICE_NAME"},                       
            {"mDataProp":"REMARK", }                         
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
    	var order_no = $("#order_no").text();
    	$.post('/report/printCheckOrder', {order_no:order_no}, function(data){
    		window.open(data);
    	});
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

    $("#addExternalMiscOrderBtn").click(function(){
    	externalTab.fnSettings().sAjaxSource = "/costCheckOrder/externalMiscOrderList";
    	externalTab.fnDraw();  
    });    

    var ids = [];
    // 未选中列表
	$("#external-table").on('click', '.checkedOrUnchecked', function(e){
		if($(this).prop("checked") == true){
			ids.push($(this).val());
			$("#micsOrderIds").val(ids);
		}			
	});
	
	// 已选中列表
	$("#external-table").on('click', '.checkedOrUnchecked', function(e){
		if($(this).prop("checked") == false){
			if(ids.length != 0){
				ids.splice($.inArray($(this).val(),ids),1);
				$("#micsOrderIds").val(ids);
			}
		}			
	});
	
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