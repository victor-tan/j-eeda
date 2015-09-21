$(document).ready(function() {
	document.title = '配送单查询 | '+document.title;
	$('#menu_deliver').addClass('active').find('ul').addClass('in');
	var dataTable =$('#eeda-table3').dataTable({
        "bProcessing": true, //table载入数据时，是否显示‘loading...’提示
		"bFilter": false, //不需要默认的搜索框
        //"sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span12'i><'span12 center'p>>",
        "sDom": "<'row-fluid'<'span6'l><'span6'f>r><'datatable-scroll't><'row-fluid'<'span12'i><'span12 center'p>>",
        //"sPaginationType": "bootstrap",
        "iDisplayLength": 10,
        "bServerSide": true,
        "bSort": false,
    	"oLanguage": {
            "sUrl": "/eeda/dataTables.ch.txt"
        },
        "sAjaxSource": "/delivery/deliveryList",
        "aoColumns": [  
			{"mDataProp":"ORDER_NO",//运输单号
				"sWidth":"120px",
				"fnRender": function(obj) {
					if(Delivery.isUpdate || Delivery.isComplete){
						return "<a href='/delivery/edit?id="+obj.aData.ID+"'target='_blank'>"+obj.aData.ORDER_NO+"</a>";
					}else{
						return obj.aData.ORDER_NO;
					}
				}
			},
            {"mDataProp":"CUSTOMER" , "sWidth":"70px"},//客户
            {"mDataProp":"SERIAL_NO", "sWidth":"100px"},//序列号
            {"mDataProp":"ITEM_NO","sWidth":"90px"},//型号
            {"mDataProp":"AMOUNT","sWidth":"50px"},//数量
            {"mDataProp":"PCS_AMOUNT", "sWidth":"50px"},//件数
            {"mDataProp":"BUSINESS_STAMP","sWidth":"100px"},//配送时间
            {"mDataProp":"COMPANY","sWidth":"150px"},//地址
            {"mDataProp":"DRIVER","sWidth":"70px"},//联系人
            {"mDataProp":"PHONE","sWidth":"80px"},//联系电话
            {"mDataProp":"OFFICE_NAME","sWidth":"80px"},//网点
            {"mDataProp":"PLAN_TIME","sWidth":"100px"},//计划时间
            {"mDataProp":"TRANSFER_ORDER_NO","sWidth":"100px"},
            {"mDataProp":"CUSTOMER_ORDER_NO","sWidth":"100px"},//客户订单号
            {"mDataProp":"STATUS","sWidth":"70px"},//状态
            {"mDataProp":"CARGO_NATURE",
            	"sWidth":"80px",
            	"fnRender": function(obj) {
            		if(obj.aData.CARGO_NATURE == 'ATM'){
            			return 'ATM柜员机';
            		}else if(obj.aData.CARGO_NATURE == 'cargo'){
            			return '普通货品';
            		}else{
            			return '贵重物品';
            		}
            		
            	}
            },
            {"mDataProp":"PICKUP_MODE",
            	"sWidth":"100px",
            	"fnRender": function(obj) {
            		if(obj.aData.PICKUP_MODE == "own"){
            			return "干线供应商自提";
            		}else if(obj.aData.PICKUP_MODE == "pickupSP"){
            			return "外包供应商提货";
            		}else{
            			//return deliver_tran.ex_type;
            			return null;
            			
            		}}},//提货方式
            {"mDataProp":"WAREHOUSE_NAME","sWidth":"100px"},//仓库
            {"mDataProp":"TID","sWidth":"70px"},
            { 
                "mDataProp": null,  
                "bVisible":false,
                "fnRender": function(obj) {                    
                    return "<a class='btn btn-danger cancelbutton' title='取消单据' code='"+obj.aData.ID+"'>"+
                                "<i class='fa fa-trash-o fa-fw'></i>"+ 
                            "</a>";
                }
            }                         
        ]      
    });	
	
	
	$("#dowmload").on('click', function(e){
    	if(confirm("确认下载吗？")){
    		window.location.href="/delivery/downloadDeliveryOrderTemplate";
    	}
    });
	
	    
	$("#eeda-table3").on('click', '.cancelbutton', function(e){
		e.preventDefault();
        //异步向后台提交数据
	    var r=confirm("是否取消单据！");   
	    if(r==true){
	    	var id = $(this).attr('code');
			$.post('/delivery/cancel/'+id,function(data){
                 //保存成功后，刷新列表
                 console.log(data);
                 if(data.success){
                	 dataTable.fnDraw();
                 }else{
                     alert('取消失败');
                 }
            },'json');
		}else{
			return false;   
		}
	});

	$('#datetimepicker').datetimepicker({  
        format: 'yyyy-MM-dd',  
        language: 'zh-CN'
    }).on('changeDate', function(ev){
    	$(".bootstrap-datetimepicker-widget").hide();
        $('#beginTime_filter').trigger('keyup');
    });


    $('#datetimepicker2').datetimepicker({  
        format: 'yyyy-MM-dd',  
        language: 'zh-CN', 
        autoclose: true,
        pickerPosition: "bottom-left"
    }).on('changeDate', function(ev){
    	$(".bootstrap-datetimepicker-widget").hide();
        $('#endTime_filter').trigger('keyup');
    });
	
    var refreshData=function(){
    	var orderNo_filter = $("#orderNo_filter").val();
      	var transfer_filter = $("#transfer_filter").val();
    	var status_filter = $("#status_filter").val();
      	var customer_filter = $("#customer_filter").val();    	
      	var sp_filter = $("#sp_filter").val();
      	var beginTime_filter = $("#beginTime_filter").val();
      	var endTime_filter = $("#endTime_filter").val();
      	var warehouse = $("#warehouse").val();
      	var serial_no = $("#serial_no").val();
      	var delivery_no = $("#delivery_no").val();
      	var address_filter = $("#address_filter").val();
      	dataTable.fnSettings().sAjaxSource = "/delivery/deliveryList?orderNo_filter="+orderNo_filter+"&address_filter="+address_filter+"&transfer_filter="+transfer_filter+"&status_filter="+status_filter+"&customer_filter="+customer_filter+"&sp_filter="+sp_filter+"&beginTime_filter="+beginTime_filter+"&endTime_filter="+endTime_filter+"&warehouse="+warehouse+"&serial_no="+serial_no+"&delivery_no="+delivery_no;
      	dataTable.fnDraw();
    };
    
    
    //条件筛选
	$("#orderNo_filter,#delivery_no,#transfer_filter ,#status_filter,#beginTime_filter,#endTime_filter,#warehouse,#serial_no,#address_filter").on('keyup', function () {    	 	
		refreshData();
      });
	$("#status_filter").on('change',function(){
		refreshData();
	});
	/*===================获取客户================================*/
	 //获取所有客户
	 $('#customer_filter').on('keyup click', function(){
        var inputStr = $('#customer_filter').val();
        
        $.get("/customerContract/search", {locationName:inputStr}, function(data){
            console.log(data);
            var companyList =$("#companyList");
            companyList.empty();
            for(var i = 0; i < data.length; i++)
            {
                companyList.append("<li><a tabindex='-1' class='fromLocationItem' post_code='"+data[i].POSTAL_CODE+"' contact_person='"+data[i].CONTACT_PERSON+"' email='"+data[i].EMAIL+"' phone='"+data[i].PHONE+"' partyId='"+data[i].PID+"' address='"+data[i].ADDRESS+"', company_name='"+data[i].COMPANY_NAME+"', >"+data[i].ABBR+"</a></li>");
            }
            if(data.length>0)
                companyList.show();
            $("#companyList").css({ 
            	left:$(this).position().left+"px", 
            	top:$(this).position().top+32+"px" 
            }); 
        },'json');
        if(inputStr==''){
        	dataTable.fnFilter('', 2);
        }
    });


	//选中某个客户时候
    $('#companyList').on('click', '.fromLocationItem', function(e){        
        $('#customer_filter').val($(this).text());
        $("#companyList").hide();
        var inputStr = $('#customer_filter').val();
        if(inputStr!=null){
        	refreshData();
        }
    });
	// 没选中客户，焦点离开，隐藏列表
    $('#customer_filter').on('blur', function(){
        $('#companyList').hide();
    });

    //当用户只点击了滚动条，没选客户，再点击页面别的地方时，隐藏列表
    $('#companyList').on('blur', function(){
        $('#companyList').hide();
    });

    $('#companyList').on('mousedown', function(){
        return false;//阻止事件回流，不触发 $('#spMessage').on('blur'
    });
	    
	    
    //获取供应商的list，选中信息在下方展示其他信息
    $('#sp_filter').on('keyup click', function(){
    	
		var inputStr = $('#sp_filter').val();
		console.log("157 sp_filter:"+inputStr);
		if(inputStr == ""){
			var pageSpName = $("#pageSpName");
			pageSpName.empty();
			var pageSpAddress = $("#pageSpAddress");
			pageSpAddress.empty();
			$('#sp_id').val($(this).attr(''));
		}
		//console.log(inputStr);
		$.get('/transferOrder/searchSp', {input:inputStr}, function(data){
			//console.log(data);
			var spList =$("#spList");
			spList.empty();
			for(var i = 0; i < data.length; i++)
			{
				var abbr = data[i].ABBR;
				if(abbr == null){
					abbr = '';
				}
				var company_name = data[i].COMPANY_NAME;
				if(company_name == null){
					company_name = '';
				}
				var contact_person = data[i].CONTACT_PERSON;
				if(contact_person == null){
					contact_person = '';
				}
				var phone = data[i].PHONE;
				if(phone == null){
					phone = '';
				}
				spList.append("<li><a tabindex='-1' class='fromLocationItem' chargeType='"+data[i].CHARGE_TYPE+"' partyId='"+data[i].PID+"' post_code='"+data[i].POSTAL_CODE+"' contact_person='"+data[i].CONTACT_PERSON+"' email='"+data[i].EMAIL+"' phone='"+data[i].PHONE+"' spid='"+data[i].ID+"' address='"+data[i].ADDRESS+"', company_name='"+data[i].COMPANY_NAME+"', >"+abbr+" "+company_name+" "+contact_person+" "+phone+"</a></li>");
			}
		},'json');

		$("#spList").css({ 
        	left:$(this).position().left+"px", 
        	top:$(this).position().top+32+"px" 
        }); 
        $('#spList').show();
	});

	// 没选中供应商，焦点离开，隐藏列表
	$('#sp_filter').on('blur', function(){
 		$('#spList').hide();
 	});

	//当用户只点击了滚动条，没选供应商，再点击页面别的地方时，隐藏列表
	$('#spList').on('blur', function(){
 		$('#spList').hide();
 	});

	$('#spList').on('mousedown', function(){
		return false;//阻止事件回流，不触发 $('#spMessage').on('blur'
	});

	// 选中供应商
	$('#spList').on('mousedown', '.fromLocationItem', function(e){
		console.log($('#spList').is(":focus"));
		var message = $(this).text();
		$('#sp_filter').val(message.substring(0, message.indexOf(" ")));
		$('#sp_id').val($(this).attr('partyId'));
		var pageSpName = $("#sp_filter");
		pageSpName.empty();
		var pageSpAddress = $("#pageSpAddress");
		pageSpAddress.empty();
		pageSpAddress.append($(this).attr('address'));
		var contact_person = $(this).attr('contact_person');
		if(contact_person == 'null'){
			contact_person = '';
		}
		pageSpName.append(contact_person+'&nbsp;');
		var phone = $(this).attr('phone');
		if(phone == 'null'){
			phone = '';
		}
		pageSpName.append(phone); 
		pageSpAddress.empty();
		var address = $(this).attr('address');
		if(address == 'null'){
			address = '';
		}
		pageSpAddress.append(address);
        $('#spList').hide();
        
        refreshData();
    });
	
	// 导入配送单
    $("#fileUploadBtn").click(function(){
    	$("#toFileUpload").click();
    });
    
	$('#toFileUpload').fileupload({
        dataType: 'json',
        done: function (e, data) {
        	$("#footer").show();
        	$("#centerBody").empty().append("<h4>"+data.result.cause+"</h4>");
        	dataTable.fnDraw();
        },  
        progressall: function (e, data) {//设置上传进度事件的回调函数  
        	$('#centerBody').empty().append('<img src="/yh/image/loading5.gif" width="20%"><h4>导入过程可能需要一点时间，请勿退出页面！</h4>');
        	$('#myModal').modal('show');
        	$("#footer").hide();
        } 
    });
		
		
		
});