$.fn.dataTableExt.afnFiltering.push(
    function( oSettings, aData, iDataIndex ) {


    	if(filterBuildingNo() && filterBuildingUnit() && filterRoomNo() && isTypeInRange() && isAreaInRange() && isAmountInRange() ){
    		return true;
    	}
    	return false;

    	function filterBuildingNo(){
    		var buildingNo = parseInt($("#fitler_building_no").val())*1;
        	if(buildingNo){
        		var iCellValue = aData[0];
        		var cellValue= $(iCellValue)[3].innerText.trim();
        		var index = cellValue.indexOf('栋');
        		if(index>=0){
        			var tempBuildingNo = cellValue.substr(0, index);
        			if(tempBuildingNo){
        				if(buildingNo==tempBuildingNo){
        					return true;
        				}
        				return false;
        			}        			
        		}        		
        		return false;
        	}        	
        	return true; 
    	}

    	function filterBuildingUnit(){
    		var buildingUnit = parseInt($("#fitler_building_unit").val())*1;
        	if(buildingUnit){
        		var iCellValue = aData[0];
        		var cellValue= $(iCellValue)[3].innerText.trim();
        		var startIndex = cellValue.indexOf('-')+1;
        		var endIndex = cellValue.indexOf('单元');
        		if(endIndex>=0){
        			var tempBuildingUnit = cellValue.substr(startIndex, endIndex-startIndex);
        			if(tempBuildingUnit){
        				if(buildingUnit==tempBuildingUnit){
        					return true;
        				}
        				return false;
        			}        			
        		}        		
        		return false;
        	}        	
        	return true;        	
    	}

    	function filterRoomNo(){
    		var roomNo = parseInt($("#fitler_room_no").val())*1;
        	if(roomNo){
        		var iCellValue = aData[0];
        		var cellValue= $(iCellValue)[3].innerText.trim();
        		var startIndex = cellValue.lastIndexOf('-')+1;
        		var endIndex = cellValue.indexOf('房');
        		if(endIndex>=0){
        			var tempRoomNo = cellValue.substr(startIndex, endIndex-startIndex);
        			if(tempRoomNo){
        				if(tempRoomNo.indexOf(roomNo)>=0){
        					return true;
        				}
        				return false;
        			}        			
        		}        		
        		return false;
        	}        	
        	return true;        	
    	}

        function isTypeInRange(){
        	var typeVal = $("#type").val();
        	if('allDepartment'==typeVal){
        		var iCellValue = aData[2].substr(0, 1);
        		if(iCellValue<=6){
        			return true;
        		}
        		return false;
        	}        	
        	return true;        	
        };

    	function isAreaInRange(){
    		var iColumn = 4;

			var iMin = parseInt($('#area_min').val())*1;
			var iMax = parseInt($('#area_max').val())*1;
	         
	        var iCellValue = aData[iColumn] == "" ? 0 : aData[iColumn]*1;
	        if (!iMin && !iMax){
	            return true;
	        }else if ( !iMin && iCellValue <= iMax ){
	            return true;
	        }
	        else if ( iMin <= iCellValue && !iMax ){
	            return true;
	        }
	        else if ( iMin <= iCellValue && iCellValue <= iMax ){
	            return true;
	        }
	        return false;
    	};

    	function isAmountInRange(){
    		var iColumn = 5;
    		var range, iCellValue, iMin, iMax;
	        var statusVal = $("#status").val();

	        if(statusVal==''){
	        	return true;
	        }else if(statusVal=='出租' || statusVal=='已租'){
	        	iCellValue = aData[iColumn].substr(0, aData[iColumn].length-2);
	        	iMin = parseInt($('#rent_min').val())*1;
				iMax = parseInt($('#rent_max').val())*1;
	        }else{
	        	iCellValue = aData[iColumn].substr(0, aData[iColumn].length-1);
	        	iMin = parseInt($('#total_min').val())*1;
				iMax = parseInt($('#total_max').val())*1;
	        }

	        if ( !iMin && !iMax){
	            return true;
	        }else if ( !iMin && iCellValue <= iMax ){
	            return true;
	        }else if ( iMin <= iCellValue && !iMax ){
	            return true;
	        }else if ( iMin <= iCellValue && iCellValue <= iMax ){
	            return true;
	        }
	        return false;
    	};
        
    }
);


$(document).ready(function() {
	
	//datatable, 静态处理
	var oTable = $('.datatable').dataTable({
		"sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span12'i><'span12 center'p>>",
		"sPaginationType": "bootstrap",
		"oLanguage": {
			"sUrl": "dataTables.ch.txt"
		},
		"aoColumnDefs": [
	      { "sWidth": "15%", "aTargets": [ 0 ] },
	      { "sWidth": "8%", "aTargets": [ 1 ] },
	      { "sWidth": "5%", "aTargets": [ 2 ] },
	      { "sWidth": "5%", "aTargets": [ 3 ] },
	      { "sWidth": "8%", "aTargets": [ 4 ] },
	      { "sWidth": "10%", "aTargets": [ 5 ] },
	      { "sWidth": "15%", "aTargets": [ 6 ] },
	      { "sWidth": "25%", "aTargets": [ 7 ] },
	      { "sWidth": "7%", "aTargets": [ 8 ] },
	      { "sWidth": "10%", "aTargets": [ 9 ] }					      
	    ],
	    "aaSorting": [[ 9, "desc" ]]
	} );
	/* //datatable, 动态处理
    $('#eeda-table').dataTable({
    	"oLanguage": {
            "sUrl": "dataTables.ch.txt"
        },
        "sPaginationType": "full_numbers",
        "bProcessing": true,
        "bServerSide": true,
        "sAjaxSource": "listLeads",
        "aoColumns": [   
        	{"mData":"TITLE"},
        	{"mData":"STATUS"},
            {"mData":"TYPE"},
            {"mData":"REGION"},
            {"mData":"INTRO"},
        	{"mData":"REMARK"},
            {"mData":"LOWEST_PRICE"},
            {"mData":"AGENT_FEE"},
            {"mData":"INTRODUCER"},
        	{"mData":"SALES"},
            {"mData":"FOLLOWER"},
            {"mData":"FOLLOWER_PHONE"},
            {"mData":"OWNER"},
            <% if(shiro.hasAnyRole("admin,property_mananger,property_internal_user")){ %>
        	{"mData":"OWNER_PHONE"},
        	<%}%>
        	{"mData":"CREATOR"},
            {"mData":"STATUS"},
            {"mData":"CREATE_DATE"}                        
        ],
        "aoColumnDefs": [ {
	      "aTargets": [ 0 ],
	      "mData": "download_link",
	      "mRender": function ( data, type, full ) {
	        return '<a href="'+data+'">Download</a>';
	      }
	    }]
    });*/

	var getFilterVal=function(){
		return $("#status").val()+' '+$("#type").val()+' '+$("#region").val();
	}
	
	$("#resetBtn").on("click", function(e){
		e.preventDefault();
		var input_box = $('#eeda-table_filter input').first();
        input_box.val('');

        $("#status").val('').trigger('change');
        $("#type").val('').trigger('change');
        $("#region").val('').trigger('change');

        $('#area_min').val('').trigger('change');
		$('#area_max').val('').trigger('change');
		$('#rent_min').val('').trigger('change');
		$('#rent_max').val('').trigger('change');
		$('#total_min').val('').trigger('change');
		$('#total_max').val('').trigger('change');
		$('#fitler_building_no').val('').trigger('change');
		$('#fitler_building_unit').val('').trigger('change');
		$('#fitler_room_no').val('').trigger('change');
        
        $('#totalFilterDiv').hide();
        $('#rentFilterDiv').hide();

        oTable.fnFilter('', null, false, true);
        
    });

	$("#status").on("change", function(){
        var typeVal = $(this).val();
        oTable.fnFilter(typeVal, 1, false, true);
        if(typeVal==''){
        	$('#totalFilterDiv').hide();
        	$('#rentFilterDiv').hide();
        }else if(typeVal=='出租' || typeVal=='已租' ){
        	$('#totalFilterDiv').hide();
        	$('#rentFilterDiv').show();
        }else{
        	$('#totalFilterDiv').show();
        	$('#rentFilterDiv').hide();
        }

    });

    $("#type").on("change", function(){
        var typeVal = $(this).val();
        if('allDepartment'!=typeVal){
        	oTable.fnFilter(typeVal, 2, false, true);
        }else{
        	oTable.fnFilter('', 2, false, true);
        	oTable.fnDraw(); 
        }        
    });
    
    $("#region").on("change", function(){
        var typeVal = $(this).val();
        oTable.fnFilter(typeVal, 3, false, true);
    });

    /* Add event listeners to the two range filtering inputs */
	$('#area_min').on("keyup", function() {
		oTable.fnDraw(); 
	});
	$('#area_max').on("keyup", function() {
		oTable.fnDraw(); 
	});
	$('#rent_min').on("keyup", function() {
		oTable.fnDraw(); 
	});
	$('#rent_max').on("keyup", function() {
		oTable.fnDraw(); 
	});
	$('#total_min').on("keyup", function() {
		oTable.fnDraw(); 
	});
	$('#total_max').on("keyup", function() {
		oTable.fnDraw(); 
	});
	$('#fitler_building_no').on("keyup", function() {
		oTable.fnDraw(); 
	});
	$('#fitler_building_unit').on("keyup", function() {
		oTable.fnDraw(); 
	});
	$('#fitler_room_no').on("keyup", function() {
		oTable.fnDraw(); 
	});


	jQuery.fn.limit=function(){ 
	    var self = $("td[limit]"); 
	    self.each(function(){ 
	        var objString = $(this).text(); 
	        var objLength = $(this).text().length; 
	        var num = $(this).attr("limit"); 
	        if(objLength > num){ 
				$(this).attr("title",objString); 
	            objString = $(this).text(objString.substring(0,num) + "..."); 
	        } 
	    }) 
	} 

	$("#eeda-table").limit(); 
	
	
} );