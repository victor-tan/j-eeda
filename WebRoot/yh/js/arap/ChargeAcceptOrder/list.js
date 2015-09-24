
$(document).ready(function() {
	document.title = '复核收款 | '+document.title;
	
	if($("#page").val()=='return'){
    	$('a[href="#panel-2"]').tab('show');
    }
	
    $('#menu_finance').addClass('active').find('ul').addClass('in');
   
	//datatable, 动态处理
    var chargeNoAcceptOrderTab = $('#chargeNoAccept-table').dataTable({
        "bFilter": false, //不需要默认的搜索框
        "bSort": false, 
        "sDom": "<'row-fluid'<'span6'l><'span6'f>r><'datatable-scroll't><'row-fluid'<'span12'i><'span12 center'p>>",
        "bServerSide": true,
        "iDisplayLength": 100,
    	"oLanguage": {
            "sUrl": "/eeda/dataTables.ch.txt"
        },
        "fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
			$(nRow).attr({id: aData.ID}); 
			return nRow;
		},
        "sAjaxSource": "/chargeAcceptOrder/list?status=unCheck",
        "aoColumns": [   
	        { "mDataProp": null, "sWidth":"20px",
	            "fnRender": function(obj) {
	            	if(obj.aData.STATUS =="已收款确认" || obj.aData.STATUS =="收款确认中"){
	            		return "";
	            	}else{
	            		return '<input type="checkbox" name="order_check_box" class="checkedOrUnchecked" value="'+obj.aData.ID+'">';
	            	}
	              
	            }
            }, 
            {"mDataProp":"ORDER_NO","sWidth":"100px",
            	"fnRender": function(obj) {
        			return "<a href='/chargeInvoiceOrder/edit?id="+obj.aData.ID+"'target='_blank'>"+obj.aData.ORDER_NO+"</a>";
        		}},
            {"mDataProp":"ORDER_TYPE","sWidth":"80px",
        			"sClass":"order_type"
        	},   
            {"mDataProp":"INVOICE_NO","sWidth":"80px"},
            {"mDataProp":"STATUS","sWidth":"80px",
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
            /*{"mDataProp":"CHARGE_ORDER_NO"},
            {"mDataProp":"OFFICE_NAME"},
            {"mDataProp":"CNAME"},*/ 
            {"mDataProp":"TOTAL_AMOUNT","sWidth":"60px"},     
            {"mDataProp":null,"sWidth":"60px"},     
            {"mDataProp":null,"sWidth":"60px"},  
            {"mDataProp":"PAYEE","sWidth":"60px"},
            {"mDataProp":"CUSTOMER","sWidth":"120px"},
            {"mDataProp":"CNAME"},     
            {"mDataProp":null},     
            {"mDataProp":null},     
            {"mDataProp":null},     
            {"mDataProp":null},     
            {"mDataProp":null},     
            {"mDataProp":null},     
            {"mDataProp":null},     
            {"mDataProp":null},     
            {"mDataProp":null},     
            
            {"mDataProp":"REMARK","sWidth":"180px"},
            {"mDataProp":null},     
            {"mDataProp":null}                        
        ]      
    });
    
    $.post('/chargeMiscOrder/searchAllAccount',function(data){
		 if(data.length > 0){
			 var accountTypeSelect = $("#accountTypeSelect");
			 accountTypeSelect.empty();
			 var hideAccountId = $("#hideAccountId").val();
			 accountTypeSelect.append("<option ></option>");
			 for(var i=0; i<data.length; i++){
				 if(data[i].ID == hideAccountId){
					 accountTypeSelect.append("<option value='"+data[i].ID+"' selected='selected'>" + data[i].BANK_PERSON+ " " + data[i].BANK_NAME+ " " + data[i].ACCOUNT_NO + "</option>");
				 }else{
					 accountTypeSelect.append("<option value='"+data[i].ID+"'>" + data[i].BANK_PERSON+ " " + data[i].BANK_NAME+ " " + data[i].ACCOUNT_NO + "</option>");					 
				 }
			}
		}
	},'json');

   $("#paymentMethods").on('click', 'input', function(){
   	if($(this).val() == 'cash'){
   		$("#accountTypeDiv").hide();
   	}else{
   		$("#accountTypeDiv").show();    		
   	}
   });    
   
    var ids = [];
    // 未选中列表
	$("#chargeNoAccept-table").on('click', '.checkedOrUnchecked', function(e){
		$("#checkBtn").attr('disabled',false);
		if($(this).prop("checked") == true){
            var orderNo = $(this).parent().parent().find('.order_type').text();
            var orderObj=$(this).val()+":"+orderNo;
			ids.push(orderObj);
		}else{
			var array = [];
			for(id in ids){
				if($(this).val()+":"+$(this).parent().parent().find('.order_type').text()!= ids[id]){
					array.push(ids[id]);
				}
			}
			ids = array;
		}	
		$("#chargeIds").val(ids);
		if(ids.length != 0 ){
			$("#checkBtn").attr('disabled',false);
		}else{
			$("#checkBtn").attr('disabled',true);
		}
	});	
	
	
	$("#checkBtn").on('click', function(){
		$("#checkBtn").attr('disabled',true);
		$.post('chargeAcceptOrder/checkOrder?ids='+$("#chargeIds").val(),function(){
			chargeNoAcceptOrderTab.fnDraw();
			chargeAcceptOrderTab.fnDraw();
			ids = [];
		});
	});
	
	
	$("#status_filter").on('change',function(){
		var status = $("#status_filter").val();
		chargeNoAcceptOrderTab.fnSettings().sAjaxSource = "/chargeAcceptOrder/list?status="+status;
		chargeNoAcceptOrderTab.fnDraw(); 
	});
	
	
	//**
	//*****
	//*********
	//*************已复核
	//*********
	//*****
	//**
	
	
	//datatable, 动态处理
    var chargeAcceptOrderTab = $('#chargeAccept-table').dataTable({
        "bFilter": false, //不需要默认的搜索框
        "bSort": false, 
        "sDom": "<'row-fluid'<'span6'l><'span6'f>r><'datatable-scroll't><'row-fluid'<'span12'i><'span12 center'p>>",
        "bServerSide": true,
        "iDisplayLength": 100,
    	"oLanguage": {
            "sUrl": "/eeda/dataTables.ch.txt"
        },
        "fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
			$(nRow).attr({id: aData.ID}); 
			return nRow;
		},
        "sAjaxSource": "/chargeAcceptOrder/list?status=check",
        "aoColumns": [   
	        { "mDataProp": null, "sWidth":"20px",
	            "fnRender": function(obj) {
	            	if(obj.aData.STATUS =="已收款确认"  || obj.aData.STATUS =="收款确认中"){
	            		return "";
	            	}else{
	            		return '<input type="checkbox" name="order_check_box" class="checkedOrUnchecked" value="'+obj.aData.ID+'">';
	            	}
	              
	            }
            }, 
            {"mDataProp":"ORDER_NO","sWidth":"100px",
            	"fnRender": function(obj) {
        			return "<a href='/chargeInvoiceOrder/edit?id="+obj.aData.ID+"'target='_blank'>"+obj.aData.ORDER_NO+"</a>";
        		}},
            {"mDataProp":"ORDER_TYPE","sWidth":"80px",
        			"sClass":"order_type"
        	},   
            {"mDataProp":"INVOICE_NO","sWidth":"80px"},
            {"mDataProp":"STATUS","sWidth":"80px",
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
            {"mDataProp":"TOTAL_AMOUNT","sWidth":"60px"}, 
            /*{"mDataProp":"CHARGE_ORDER_NO"},
            {"mDataProp":"OFFICE_NAME"},
            {"mDataProp":"CNAME"},*/            
            {"mDataProp":null,"sWidth":"60px"},     
            {"mDataProp":null,"sWidth":"60px"},
            {"mDataProp":"PAYEE","sWidth":"60px"},
            {"mDataProp":"CUSTOMER","sWidth":"120px"},
            {"mDataProp":"CNAME"},
            {"mDataProp":null},     
            {"mDataProp":null},     
            {"mDataProp":null},     
            {"mDataProp":null},     
            {"mDataProp":null},     
            {"mDataProp":null},     
            {"mDataProp":null},     
            {"mDataProp":null},     
            {"mDataProp":null},    
            {"mDataProp":"REMARK","sWidth":"180px"},
            {"mDataProp":null},     
            {"mDataProp":null}                        
        ]      
    });
	
	
    var ids = [];
    // 未选中列表
	$("#chargeAccept-table").on('click', '.checkedOrUnchecked', function(e){
		$("#confirmBtn").attr('disabled',false);
		if($(this).prop("checked") == true){
            var orderNo = $(this).parent().parent().find('.order_type').text();
            var orderObj=$(this).val()+":"+orderNo;
            //var order = ids.pop();
			ids.push(orderObj);
		}else{
			var array = [];
			for(id in ids){
				if($(this).val()+":"+$(this).parent().parent().find('.order_type').text()!= ids[id]){
					array.push(ids[id]);
				}
			}
			ids = array;
		}	
		$("#chargeIds2").val(ids);
		if(ids.length != 0 ){
			$("#confirmBtn").attr('disabled',false);
		}else{
			$("#confirmBtn").attr('disabled',true);
		}
	});	
	
	
	$('#confirmBtn').click(function(e){
		$("#checkBtn").attr('disabled',true);
        e.preventDefault();
        $('#confirmForm').submit();
        ids = [];
    });
	
} );