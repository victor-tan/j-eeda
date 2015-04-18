$(document).ready(function() {
	document.title = '系统监控 | '+document.title;
    
	$('#transferOrderTypeTbody').dataTable({
		"bFilter": false, //不需要默认的搜索框
    	"bSort": false, // 不要排序
    	"sDom": "<'row-fluid'<'span6'l><'span6'f>r><'datatable-scroll't><'row-fluid'<'span12'i><'span12 center'p>>",
    	"iDisplayLength": 10,
    	"bServerSide": true,
    	"bLengthChange":false,
    	"oLanguage": {
    		"sUrl": "/eeda/dataTables.ch.txt"
    	},
    	"sAjaxSource": "/transferOrder/findTransferOrderType",
        "aoColumns": [
            { "mDataProp": "ORDER_NO"},
            {"mDataProp":null, "sWidth":"150px",
            	"fnRender": function(obj) {
            		return obj.aData.ROUTE_FROM + " —— " + obj.aData.ROUTE_TO;
            	}
            },
            { "mDataProp": "STATUS", "sWidth":"100px"},
            { "mDataProp": "CREATE_STAMP", "sWidth":"150px"},
            { "mDataProp": null},
        ]
    });
	
	$('#deliveryOrderTypeTbody').dataTable({
		"bFilter": false, //不需要默认的搜索框
    	"bSort": false, // 不要排序
    	"sDom": "<'row-fluid'<'span6'l><'span6'f>r><'datatable-scroll't><'row-fluid'<'span12'i><'span12 center'p>>",
    	"iDisplayLength": 10,
    	"bServerSide": true,
    	"bLengthChange":false,
    	"oLanguage": {
    		"sUrl": "/eeda/dataTables.ch.txt"
    	},
    	"sAjaxSource": "/delivery/findDeliveryOrderType",
        "aoColumns": [
            { "mDataProp": "ORDER_NO",
            	"fnRender":function(obj){
            		return  "<a href='/delivery/edit?id="+obj.aData.ID+"'target='_blank'>"+obj.aData.ORDER_NO+"</a>";
            	}},
            {"mDataProp":null, "sWidth":"150px",
            	"fnRender": function(obj) {
            		return obj.aData.ROUTE_FROM + " —— " + obj.aData.ROUTE_TO;
            	}
            },
            { "mDataProp": "STATUS", "sWidth":"100px"},
            { "mDataProp": "CREATE_STAMP", "sWidth":"150px"},
            { "mDataProp": null},
        ]
    });
	
	$('#returnOrderTypeTbody').dataTable({
		"bFilter": false, //不需要默认的搜索框
    	"bSort": false, // 不要排序
    	"sDom": "<'row-fluid'<'span6'l><'span6'f>r><'datatable-scroll't><'row-fluid'<'span12'i><'span12 center'p>>",
    	"iDisplayLength": 10,
    	"bServerSide": true,
    	"bLengthChange":false,
    	"oLanguage": {
    		"sUrl": "/eeda/dataTables.ch.txt"
    	},
    	"sAjaxSource": "/returnOrder/findReturnOrderType",
        "aoColumns": [
            { "mDataProp": "ORDER_NO",
            	"fnRender":function(obj){
            		return "<a href='/returnOrder/edit?id="+obj.aData.ID+"' target='_blank'>"+obj.aData.ORDER_NO+"</a>";
            	}},
            {"mDataProp":null, "sWidth":"150px",
            	"fnRender": function(obj) {
            		return obj.aData.ROUTE_FROM + " —— " + obj.aData.ROUTE_TO
            	}
            },
            { "mDataProp": "TRANSACTION_STATUS", "sWidth":"100px"},
            { "mDataProp": "CREATE_DATE", "sWidth":"150px"},
            { "mDataProp": "AMOUNT"},
        ]
    });
	$("#btn,#clbtn").on('click',function(){
		$("#exampleModal").css("display","none");
	});
});