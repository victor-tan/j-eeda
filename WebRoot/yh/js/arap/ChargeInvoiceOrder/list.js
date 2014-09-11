
$(document).ready(function() {

    $('#menu_charge').addClass('active').find('ul').addClass('in');
    $('#eeda-table').dataTable({
        "bFilter": false, //不需要默认的搜索框
        "sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span12'i><'span12 center'p>>",
        "iDisplayLength": 10,
        "bServerSide": true,
    	"oLanguage": {
            "sUrl": "/eeda/dataTables.ch.txt"
        },
        "sAjaxSource": "/yh/chargeInvoiceOrder/list",
        "aoColumns": [   
            {"mDataProp":"ID", "bVisible": false},
            {"mDataProp":"ORDER_NO",
            	"fnRender": function(obj) {
        			return "<a href='/yh/chargeInvoiceOrder/edit?id="+obj.aData.ID+"'>"+obj.aData.ORDER_NO+"</a>";
        		}},
            {"mDataProp":"CNAME"},
            {"mDataProp":"STATUS",
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
            {"mDataProp":"RETURN_ORDER_NO"},
            {"mDataProp":"TRANSFER_ORDER_NO"},
            {"mDataProp":"DELIVERY_ORDER_NO"},            
            {"mDataProp":"CREATOR_NAME"},        	
            {"mDataProp":"CREATE_STAMP"},
            {"mDataProp":"REMARK"},
            { 
                "mDataProp": null, 
                "sWidth": "8%",                
                "fnRender": function(obj) {
                    return	"<a class='btn btn-danger' href='#'"+obj.aData.ID+"'>"+
                                "<i class='fa fa-trash-o fa-fw'></i>"+ 
                                "取消"+
                            "</a>";
                }
            }                         
        ]      
    });	
} );