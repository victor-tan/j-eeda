$(document).ready(function() {
    $('#menu_status').addClass('active').find('ul').addClass('in');
    
	//datatable, 动态处理
    var detailTable = $('#eeda-table').dataTable({
        "bFilter": false, //不需要默认的搜索框
        //"sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span12'i><'span12 center'p>>",
        "sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span12'i><'span12 center'p>>",
        //"sPaginationType": "bootstrap",
        "iDisplayLength": 10,
        "bServerSide": true,
    	"oLanguage": {
            "sUrl": "/eeda/dataTables.ch.txt"
        },
        "sAjaxSource": "/yh/departOrder/ownTransferMilestone",
        "aoColumns": [   
              {"mDataProp":"ORDER_NO",
              	"fnRender": function(obj) {
              			return "<a href='/yh/transferOrder/edit?id="+obj.aData.ID+"'>"+obj.aData.ORDER_NO+"</a>";
              		}},
             
              {"mDataProp":null,
                  "fnRender": function(obj) {
                  	if(obj.aData.STATUS==null){
                  		obj.aData.STATUS="";
                  	}
                      return obj.aData.STATUS+"<a id='edit_status' order_id="+obj.aData.ID+" data-target='#transferOrderMilestone' data-toggle='modal'><i class='fa fa-pencil fa-fw'></i></a>";
                  }},  
             
              {"mDataProp":"CARGO_NATURE",
              	"fnRender": function(obj) {
              		if(obj.aData.CARGO_NATURE == "cargo"){
              			return "普通货品";
              		}else if(obj.aData.CARGO_NATURE == "damageCargo"){
              			return "损坏货品";
              		}else if(obj.aData.CARGO_NATURE == "ATM"){
              			return "ATM";
              		}else{
              			return "";
              		}}},        	
      		{"mDataProp":"OPERATION_TYPE",
      			"fnRender": function(obj) {
      				if(obj.aData.OPERATION_TYPE == "out_source"){
      					return "外包";
      				}else if(obj.aData.OPERATION_TYPE == "own"){
      					return "自营";
      				}else{
      					return "";
      				}}},        	
              {"mDataProp":"PICKUP_MODE",
              	"fnRender": function(obj) {
              		if(obj.aData.PICKUP_MODE == "routeSP"){
              			return "干线供应商自提";
              		}else if(obj.aData.PICKUP_MODE == "pickupSP"){
              			return "外包供应商提货";
              		}else if(obj.aData.PICKUP_MODE == "own"){
              			return "源鸿自提";
              		}else{
              			return "";
              		}}},
              {"mDataProp":"ARRIVAL_MODE",
              	"fnRender": function(obj) {
              		if(obj.aData.ARRIVAL_MODE == "delivery"){
              			return "货品直送";
              		}else if(obj.aData.ARRIVAL_MODE == "gateIn"){
              			return "入中转仓";
              		}else{
              			return "";
              		}}},
              {"mDataProp":"ADDRESS"},
              {"mDataProp":"CREATE_STAMP"},
              {"mDataProp":"ORDER_TYPE",
              	"fnRender": function(obj) {
              		if(obj.aData.ORDER_TYPE == "salesOrder"){
              			return "销售订单";
              		}else if(obj.aData.ORDER_TYPE == "arrangementOrder"){
              			return "调拨订单";
              		}else if(obj.aData.ORDER_TYPE == "cargoReturnOrder"){
              			return "退货订单";
              		}else if(obj.aData.ORDER_TYPE == "damageReturnOrder"){
              			return "质量退单";
              		}else{
              			return "";
              		}}},
              {"mDataProp":"CNAME"},
              {"mDataProp":"SPNAME"},
              {"mDataProp":"ONAME"},
              {"mDataProp":"REMARK"},
              { 
                  "mDataProp": null, 
                  "fnRender": function(obj) {   
                  	if(obj.aData.STATUS=='已收货'){
                  		return "已收货";
                  	}else{
                  		return "<a class='btn btn-primary confirmReceipt' code='"+obj.aData.ID+"'>"+
                  		"收货确认"+
                  		"</a>";
                  	}
                  }
              }                                     
          ] 
    });	

    // 收货确认
    $("#eeda-table").on('click', '.confirmReceipt', function(e){
    	var orderId =$(this).attr("code");
    	if(confirm("确定收货吗？")){
    		$.post('/yh/transferOrderMilestone/receipt', {orderId:orderId}, function(data){    
    			if(data.success){
    				detailTable.fnDraw(); 		
                }else{
                    alert('收货出错');
                }
        	});
        } else {
            return;
        }
    });
    
    $("#eeda-table").on('click', '#edit_status', function(e){
    	e.preventDefault();	
    	var order=$(this).attr("order_id");
    	$("#milestoneDepartId").val(order);
    	$.post('/yh/departOrder/transferMilestoneList',{order_id:order},function(data){
			var transferOrderMilestoneTbody = $("#transferOrderMilestoneTbody");
			transferOrderMilestoneTbody.empty();
			for(var i = 0,j = 0; i < data.transferOrderMilestones.length,j < data.usernames.length; i++,j++)
			{
				transferOrderMilestoneTbody.append("<tr><th>"+data.transferOrderMilestones[i].STATUS+"</th><th>"+data.transferOrderMilestones[i].LOCATION+"</th><th>"+data.usernames[j]+"</th><th>"+data.transferOrderMilestones[i].CREATE_STAMP+"</th></tr>");
			}
		},'json');
    	
    });
    
    // 保存新里程碑
	$("#transferOrderMilestoneFormBtn").click(function(){
		$.post('/yh/departOrder/saveTransferMilestone',$("#transferOrderMilestoneForm").serialize(),function(data){
			var transferOrderMilestoneTbody = $("#transferOrderMilestoneTbody");
			transferOrderMilestoneTbody.append("<tr><th>"+data.transferOrderMilestone.STATUS+"</th><th>"+data.transferOrderMilestone.LOCATION+"</th><th>"+data.username+"</th><th>"+data.transferOrderMilestone.CREATE_STAMP+"</th></tr>");
			detailTable.fnDraw();  
		},'json');
		//$('#transferOrderMilestone').modal('hide');
	}); 
	
 
    
	 $('#orderNo_filter').on( 'keyup', function () {
	    	var orderNo = $("#orderNo_filter").val();
	        /*transferOrder.fnFilter(orderNo, 0, false, true);
	        transferOrder.fnDraw();*/
	    	var status = $("#status_filter").val();
	    	var address = $("#address_filter").val();
	    	var customer = $("#customer_filter").val();
	    	var sp = $("#sp_filter").val();
	    	var beginTime = $("#beginTime_filter").val();
	    	var endTime = $("#endTime_filter").val();
	    	var officeName = $("#officeName_filter").val();
	    	detailTable.fnSettings().sAjaxSource = "/yh/departOrder/ownTransferMilestone?orderNo="+orderNo+"&status="+status+"&address="+address+"&customer="+customer+"&sp="+sp+"&beginTime="+beginTime+"&endTime="+endTime+"&officeName="+officeName;
	    	detailTable.fnDraw(); 
	    } );
	 
	    $('select.status_filter').on( 'change', function () {
	    	var orderNo = $("#orderNo_filter").val();
	    	var status = $("#status_filter").val();
	    	var address = $("#address_filter").val();
	    	var customer = $("#customer_filter").val();
	    	var sp = $("#sp_filter").val();
	    	var beginTime = $("#beginTime_filter").val();
	    	var endTime = $("#endTime_filter").val();
	    	var officeName = $("#officeName_filter").val();
	    	detailTable.fnSettings().sAjaxSource = "/yh/departOrder/ownTransferMilestone?orderNo="+orderNo+"&status="+status+"&address="+address+"&customer="+customer+"&sp="+sp+"&beginTime="+beginTime+"&endTime="+endTime+"&officeName="+officeName;
	    	detailTable.fnDraw();
	    } );
	    
	    $('input.address_filter').on( 'keyup click', function () {
	    	var orderNo = $("#orderNo_filter").val();
	    	var status = $("#status_filter").val();
	    	var address = $("#address_filter").val();
	    	var customer = $("#customer_filter").val();
	    	var sp = $("#sp_filter").val();
	    	var beginTime = $("#beginTime_filter").val();
	    	var endTime = $("#endTime_filter").val();
	    	var officeName = $("#officeName_filter").val();
	    	detailTable.fnSettings().sAjaxSource = "/yh/departOrder/ownTransferMilestone?orderNo="+orderNo+"&status="+status+"&address="+address+"&customer="+customer+"&sp="+sp+"&beginTime="+beginTime+"&endTime="+endTime+"&officeName="+officeName;
	    	detailTable.fnDraw();
	    } );
	    
	    $('input.customer_filter').on( 'keyup click', function () {
	    	var orderNo = $("#orderNo_filter").val();
	    	var status = $("#status_filter").val();
	    	var address = $("#address_filter").val();
	    	var customer = $("#customer_filter").val();
	    	var sp = $("#sp_filter").val();
	    	var beginTime = $("#beginTime_filter").val();
	    	var endTime = $("#endTime_filter").val();
	    	var officeName = $("#officeName_filter").val();
	    	detailTable.fnSettings().sAjaxSource = "/yh/departOrder/ownTransferMilestone?orderNo="+orderNo+"&status="+status+"&address="+address+"&customer="+customer+"&sp="+sp+"&beginTime="+beginTime+"&endTime="+endTime+"&officeName="+officeName;
	    	detailTable.fnDraw();
	    } );
	    
	    $('input.sp_filter').on( 'keyup click', function () {
	    	var orderNo = $("#orderNo_filter").val();
	    	var status = $("#status_filter").val();
	    	var address = $("#address_filter").val();
	    	var customer = $("#customer_filter").val();
	    	var sp = $("#sp_filter").val();
	    	var beginTime = $("#beginTime_filter").val();
	    	var endTime = $("#endTime_filter").val();
	    	var officeName = $("#officeName_filter").val();
	    	detailTable.fnSettings().sAjaxSource = "/yh/departOrder/ownTransferMilestone?orderNo="+orderNo+"&status="+status+"&address="+address+"&customer="+customer+"&sp="+sp+"&beginTime="+beginTime+"&endTime="+endTime+"&officeName="+officeName;
	    	detailTable.fnDraw();
	    } );
	    
	    $('input.beginTime_filter').on( 'change input', function () {
	    	var orderNo = $("#orderNo_filter").val();
	    	var status = $("#status_filter").val();
	    	var address = $("#address_filter").val();
	    	var customer = $("#customer_filter").val();
	    	var sp = $("#sp_filter").val();
	    	var beginTime = $("#beginTime_filter").val();
	    	var endTime = $("#endTime_filter").val();
	    	var officeName = $("#officeName_filter").val();
	    	detailTable.fnSettings().sAjaxSource = "/yh/departOrder/ownTransferMilestone?orderNo="+orderNo+"&status="+status+"&address="+address+"&customer="+customer+"&sp="+sp+"&beginTime="+beginTime+"&endTime="+endTime+"&officeName="+officeName;
	    	detailTable.fnDraw();
	    } );
	    
	    $('#beginTime_filter').on('keyup', function () {
	    	var orderNo = $("#orderNo_filter").val();
	    	var status = $("#status_filter").val();
	    	var address = $("#address_filter").val();
	    	var customer = $("#customer_filter").val();
	    	var sp = $("#sp_filter").val();
	    	var beginTime = $("#beginTime_filter").val();
	    	var endTime = $("#endTime_filter").val();
	    	var officeName = $("#officeName_filter").val();
	    	detailTable.fnSettings().sAjaxSource = "/yh/departOrder/ownTransferMilestone?orderNo="+orderNo+"&status="+status+"&address="+address+"&customer="+customer+"&sp="+sp+"&beginTime="+beginTime+"&endTime="+endTime+"&officeName="+officeName;
	    	detailTable.fnDraw();
	    } );    
	    
	    $('#endTime_filter').on( 'keyup click', function () {
	    	var orderNo = $("#orderNo_filter").val();
	    	var status = $("#status_filter").val();
	    	var address = $("#address_filter").val();
	    	var customer = $("#customer_filter").val();
	    	var sp = $("#sp_filter").val();
	    	var beginTime = $("#beginTime_filter").val();
	    	var endTime = $("#endTime_filter").val();
	    	var officeName = $("#officeName_filter").val();
	    	detailTable.fnSettings().sAjaxSource = "/yh/departOrder/ownTransferMilestone?orderNo="+orderNo+"&status="+status+"&address="+address+"&customer="+customer+"&sp="+sp+"&beginTime="+beginTime+"&endTime="+endTime+"&officeName="+officeName;
	    	detailTable.fnDraw();
	    } );
	    
	    $('#datetimepicker').datetimepicker({  
	        format: 'yyyy-MM-dd',  
	        language: 'zh-CN'
	    }).on('changeDate', function(ev){
	        $('#beginTime_filter').trigger('keyup');
	    });

	    $('#datetimepicker2').datetimepicker({  
	        format: 'yyyy-MM-dd',  
	        language: 'zh-CN', 
	        autoclose: true,
	        pickerPosition: "bottom-left"
	    }).on('changeDate', function(ev){
	        $('#endTime_filter').trigger('keyup');
	    });
} );