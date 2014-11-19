package controllers.yh.carmanage;

import interceptor.SetAttrLoginUserInterceptor;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import models.DepartOrder;
import models.TransferOrder;
import models.TransferOrderMilestone;
import models.UserLogin;
import models.yh.carmanage.CarSummaryDetail;
import models.yh.carmanage.CarSummaryDetailOilFee;
import models.yh.carmanage.CarSummaryDetailOtherFee;
import models.yh.carmanage.CarSummaryDetailRouteFee;
import models.yh.carmanage.CarSummaryDetailSalary;
import models.yh.carmanage.CarSummaryOrder;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import controllers.yh.profile.CarinfoController;
import controllers.yh.util.PermissionConstant;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class CarSummaryController extends Controller {
	private Logger logger = Logger.getLogger(CarinfoController.class);
	Subject currentUser = SecurityUtils.getSubject();
	@RequiresPermissions(value = {PermissionConstant.PERMSSION_CS_LIST, PermissionConstant.PERMSSION_CS_CREATE,PermissionConstant.PERMSSION_CS_UPDATE}, logical=Logical.OR)
	public void index() {
       render("/yh/carmanage/carSummaryList.html");
    }
	
	//未处理调车单
	@RequiresPermissions(value = {PermissionConstant.PERMSSION_CS_CREATE})
	public void untreatedCarManageList(){
		
		Map orderMap = null;
		String status = getPara("status");
		String driver = getPara("driver");
		String car_no = getPara("car_no");
		String transferOrderNo = getPara("transferOrderNo");
		String create_stamp = getPara("create_stamp");
		
		String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
        }
        String sql = "";
        String sqlTotal = "";
        
        
		if (driver == null && status == null && car_no == null
				&& transferOrderNo == null && create_stamp == null ) {

	        // 获取总条数
	        sqlTotal = "select ifnull(count(0),0) total from depart_order dor "
                    + " where dor.status!='取消' and dor.car_summary_type = 'untreated' and combine_type = '"
	                + DepartOrder.COMBINE_TYPE_PICKUP + "' and (dor.status = '已入货场' or dor.status = '已入库' ) and dor.pickup_mode = 'own' group by dor.id order by dor.car_no,dor.create_stamp desc " + sLimit;

	        // 获取当前页的数据
	        sql = "select dor.*,"
            		+ " dor.driver contact_person,"
            		+ " dor.phone phone,"
            		+ " dor.car_type cartype,"
            		+ " u.user_name user_name,"
            		+ " o.office_name office_name,"
            		+ " (select sum(ifnull(toi.volume,p.volume)*toi.amount) from transfer_order_item toi left join product p on p.id=toi.product_id where toi.order_id in(select order_id from depart_transfer where pickup_id=dor.id)) volume,"
            		+ " (select sum(ifnull(nullif(toi.weight,0),p.weight)*toi.amount) from transfer_order_item toi left join product p on p.id=toi.product_id where toi.order_id in(select order_id from depart_transfer where pickup_id=dor.id)) weight, "
            		+ " (select group_concat(dt.transfer_order_no separator '\r\n')  from depart_transfer dt where pickup_id = dor.id)  as transfer_order_no  "
            		+ " from depart_order dor "
                    + " left join party p on dor.driver_id = p.id "
                    + " left join contact ct on p.contact_id = ct.id "
                    + " left join user_login u on u.id = dor.create_by "
                    + " left join depart_transfer dtf on dtf.pickup_id = dor.id "
                    + " left join transfer_order t_o on t_o.id = dtf.order_id "
                    + " left join office o on o.id = t_o.office_id "
                    + " where dor.status!='取消' and dor.car_summary_type = 'untreated' and combine_type = '"
	                + DepartOrder.COMBINE_TYPE_PICKUP + "' and (dor.status = '已入货场' or dor.status = '已入库' ) and dor.pickup_mode = 'own' group by dor.id order by dor.car_no,dor.create_stamp desc " + sLimit;
	        
		}else{
			
	        // 获取总条数
	        sqlTotal="select ifnull(count(0),0) total from depart_order dor "
                    + " left join party p on dor.driver_id = p.id "
                    + " left join contact ct on p.contact_id = ct.id "
                    + " left join user_login u on u.id = dor.create_by "
                    + " left join depart_transfer dtf on dtf.pickup_id = dor.id "
                    + " left join transfer_order t_o on t_o.id = dtf.order_id "
                    + " left join office o on o.id = t_o.office_id "
                    + " where dor.status!='取消' and dor.car_summary_type = 'untreated' and combine_type = '"
	                + DepartOrder.COMBINE_TYPE_PICKUP + "' and (dor.status = '已入货场' or dor.status = '已入库' ) and dor.pickup_mode = 'own' group by dor.id"
                    + " and ifnull(dor.driver, '') like '%"+driver+"%'"
					+ " and ifnull(dor.status, '') like '%"+status+"%'"
					+ " and ifnull(dor.car_no, '') like '%"+car_no+"%'"
					+ " and ifnull(dor.create_stamp, '') like '%"+create_stamp+"%'"
					+ " and ifnull(dtf.transfer_order_no, '') like '%"+transferOrderNo+"%'"
					+ " order by dor.car_no,dor.create_stamp desc " + sLimit;

	        // 获取当前页的数据
	        sql = "select dor.*,"
            		+ " dor.driver contact_person,"
            		+ " dor.phone phone,"
            		+ " dor.car_type cartype,"
            		+ " u.user_name user_name,"
            		+ " o.office_name office_name,"
            		+ " (select sum(ifnull(toi.volume,p.volume)*toi.amount) from transfer_order_item toi left join product p on p.id=toi.product_id where toi.order_id in(select order_id from depart_transfer where pickup_id=dor.id)) volume,"
            		+ " (select sum(ifnull(nullif(toi.weight,0),p.weight)*toi.amount) from transfer_order_item toi left join product p on p.id=toi.product_id where toi.order_id in(select order_id from depart_transfer where pickup_id=dor.id)) weight, "
            		+ " (select group_concat(dt.transfer_order_no separator '\r\n')  from depart_transfer dt where pickup_id = dor.id)  as transfer_order_no  "
            		+ " from depart_order dor "
                    + " left join party p on dor.driver_id = p.id "
                    + " left join contact ct on p.contact_id = ct.id "
                    + " left join user_login u on u.id = dor.create_by "
                    + " left join depart_transfer dtf on dtf.pickup_id = dor.id "
                    + " left join transfer_order t_o on t_o.id = dtf.order_id "
                    + " left join office o on o.id = t_o.office_id "
                    + " where dor.status!='取消' and dor.car_summary_type = 'untreated' and combine_type = '"
	                + DepartOrder.COMBINE_TYPE_PICKUP + "' and (dor.status = '已入货场' or dor.status = '已入库' ) and dor.pickup_mode = 'own'"
                    + " and ifnull(dor.driver, '') like '%"+driver+"%'"
					+ " and ifnull(dor.status, '') like '%"+status+"%'"
					+ " and ifnull(dor.car_no, '') like '%"+car_no+"%'"
					+ " and ifnull(dor.create_stamp, '') like '%"+create_stamp+"%'"
					+ " and ifnull(dtf.transfer_order_no, '') like '%"+transferOrderNo+"%'"
                    + " order by dor.car_no,dor.create_stamp desc " + sLimit;
		}
		//Record rec = Db.findFirst(sqlTotal);
        //logger.debug("total records:" + rec.getLong("total"));
        List<Record> orders = Db.find(sql);
		
	 	orderMap = new HashMap();
        orderMap.put("sEcho", pageIndex);
        //orderMap.put("iTotalRecords", rec.getLong("total"));
        //orderMap.put("iTotalDisplayRecords", rec.getLong("total"));
        orderMap.put("iTotalRecords", orders.size());
        orderMap.put("iTotalDisplayRecords", orders.size());
        orderMap.put("aaData", orders);
        renderJson(orderMap);
		
	}
	
	//行车单查询
	@RequiresPermissions(value = {PermissionConstant.PERMSSION_CS_LIST})
	public void carSummaryOrderList(){
		
		Map orderMap = null;
		String status = getPara("status");
		String driver = getPara("driver");
		String car_no = getPara("car_no");
		String transferOrderNo = getPara("transferOrderNo");
		String order_no = getPara("order_no");
		String start_data = getPara("start_data");
		 
		String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
        }

        // 获取总条数
        String sqlTotal = "";
        String sql = "";
		if (driver == null && status == null && car_no == null
				&& transferOrderNo == null && start_data == null ) {
			sqlTotal = "select count(0) total from car_summary_order";
			sql = "select cso.id,cso.order_no ,cso.status ,cso.car_no,cso.main_driver_name ,"
					+ "cso.month_refuel_amount,cso.deduct_apportion_amount,cso.actual_payment_amount,"
					+ "	(cso.next_start_car_amount + cso.month_refuel_amount) as total_cost ,"
					+ " (cso.finish_car_mileage - cso.start_car_mileage ) as carsummarymileage,"
					+ " (select group_concat(pickup_order_no SEPARATOR '\r\n' ) from car_summary_detail where car_summary_id = cso.id) as pickup_no,"
					+ " (select group_concat( dt.transfer_order_no SEPARATOR '\r\n' ) from depart_transfer dt"
					+ " where dt.pickup_id in(select pickup_order_id from car_summary_detail where car_summary_id = cso.id )) as transfer_order_no,"
					+ " (select turnout_time from depart_order where id = ( select min(pickup_order_id) from car_summary_detail where car_summary_id = cso.id)) as turnout_time,"
					+ " (select return_time from depart_order where id = ( select max(pickup_order_id) from car_summary_detail where car_summary_id = cso.id)) as return_time,"
					+ " (select sum(ifnull(toi.volume, p.volume) * toi.amount ) from transfer_order_item toi left join product p ON p.id = toi.product_id where toi.order_id IN "
					+ " (select order_id from depart_transfer where pickup_id IN ( select pickup_order_id from car_summary_detail where car_summary_id = cso.id ))) as volume,"
					+ " (select sum( ifnull(nullif(toi.weight, 0),p.weight) * toi.amount) from transfer_order_item toi left join product p on p.id = toi.product_id where toi.order_id IN "
					+ " (select order_id from depart_transfer where pickup_id IN ( select pickup_order_id from car_summary_detail where car_summary_id = cso.id ))) as weight,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 2) refuel_consume,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 3) subsidy,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 4) driver_salary,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 5) toll_charge,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 6) handling_charges,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 7) fine,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 8) deliveryman_salary,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 9) parking_charge,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 10) quarterage,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 11) weighing_charge,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 12) other_charges"
					+ " from car_summary_order cso"
					+ " order by cso.create_data desc " + sLimit;
	        
		}else{
			
			sqlTotal = "select count(0) total from car_summary_order cso"
					+ " left join car_summary_detail csd on csd.car_summary_id = cso.id"
					+ " left join depart_order dod on dod.id = csd.pickup_order_id "
					+ "	where ifnull(cso.status, '') like '%"+status+"%'"
					+ " and ifnull(cso.car_no, '') like '%"+car_no+"%'"
					+ " and ifnull(cso.main_driver_name, '') like '%"+driver+"%'"
					+ " and ifnull(cso.order_no, '') like '%"+order_no+"%'";
			
			sql = "select cso.id,cso.order_no ,cso.status ,cso.car_no,cso.main_driver_name ,"
					+ "cso.month_refuel_amount,cso.deduct_apportion_amount,cso.actual_payment_amount,"
					+ "	(cso.next_start_car_amount + cso.month_refuel_amount) as total_cost ,"
					+ " (cso.finish_car_mileage - cso.start_car_mileage ) as carsummarymileage,"
					+ " (select group_concat(pickup_order_no SEPARATOR '\r\n' ) from car_summary_detail where car_summary_id = cso.id) as pickup_no,"
					+ " (select group_concat( dt.transfer_order_no SEPARATOR '\r\n' ) from depart_transfer dt"
					+ " where dt.pickup_id in(select pickup_order_id from car_summary_detail where car_summary_id = cso.id )) as transfer_order_no,"
					+ " (select turnout_time from depart_order where id = ( select min(pickup_order_id) from car_summary_detail where car_summary_id = cso.id)) as turnout_time,"
					+ " (select return_time from depart_order where id = ( select max(pickup_order_id) from car_summary_detail where car_summary_id = cso.id)) as return_time,"
					+ " (select sum(ifnull(toi.volume, p.volume) * toi.amount ) from transfer_order_item toi left join product p ON p.id = toi.product_id where toi.order_id IN "
					+ " (select order_id from depart_transfer where pickup_id IN ( select pickup_order_id from car_summary_detail where car_summary_id = cso.id ))) as volume,"
					+ " (select sum( ifnull(nullif(toi.weight, 0),p.weight) * toi.amount) from transfer_order_item toi left join product p on p.id = toi.product_id where toi.order_id IN "
					+ " (select order_id from depart_transfer where pickup_id IN ( select pickup_order_id from car_summary_detail where car_summary_id = cso.id ))) as weight,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 2) refuel_consume,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 3) amount3,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 4) amount4,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 5) amount5,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 6) amount6,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 7) amount7,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 8) amount8,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 9) amount9,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 10) amount10,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 11) amount11,"
					+ " (select amount from car_summary_detail_other_fee where car_summary_id = cso.id and item = 12) amount12"
					+ " from car_summary_order cso"
					+ " left join car_summary_detail csd on csd.car_summary_id = cso.id"
					+ " left join depart_order dod on dod.id = csd.pickup_order_id "
					+ "	where ifnull(cso.status, '') like '%"+status+"%'"
					+ " and ifnull(cso.car_no, '') like '%"+car_no+"%'"
					+ " and ifnull(cso.main_driver_name, '') like '%"+driver+"%'"
					+ " and ifnull(cso.order_no, '') like '%"+order_no+"%'"
					//+ " and ifnull(dor.start_data, '') like '%"+transferOrderNo+"%'"
					+ " order by cso.create_data desc " + sLimit;
		}
		Record rec = Db.findFirst(sqlTotal);
        logger.debug("total records:" + rec.getLong("total"));
        List<Record> orders = Db.find(sql);
		
	 	orderMap = new HashMap();
        orderMap.put("sEcho", pageIndex);
        orderMap.put("iTotalRecords", rec.getLong("total"));
        orderMap.put("iTotalDisplayRecords", rec.getLong("total"));
        orderMap.put("aaData", orders);
        renderJson(orderMap);
		
		
	}
	
	//创建行车单
	@RequiresPermissions(value = {PermissionConstant.PERMSSION_CS_CREATE})
	public void createCarSummary(){
		String pickupIdsArray = getPara("pickupIds");
		setAttr("pickupIds", pickupIdsArray);
		if(!"".equals(pickupIdsArray) && pickupIdsArray != null){
			try {
				String pickupIds[] = pickupIdsArray.split(",");
				int num = 0;
				for (int i = 0; i < pickupIds.length; i++) {
					DepartOrder departOrder = DepartOrder.dao.findById(pickupIds[i]);
					if(num == 0){
						//车牌号
						setAttr("car_no", departOrder.get("car_no"));
						//主司机姓名
						setAttr("driver", departOrder.get("driver"));
					}
					num++;
				}
				//出车次
				setAttr("carNumber", pickupIds.length);
				//是否审核 isAudit
				setAttr("isAudit", "no");
				
			} catch (Exception e) {
				
			}
		}
		
		render("/yh/carmanage/carSummaryEdit.html");
	}
	
	//保存行车单
	@RequiresPermissions(value = {PermissionConstant.PERMSSION_CS_CREATE, PermissionConstant.PERMSSION_CS_UPDATE}, logical=Logical.OR)
	public void saveCarSummary(){
		//拼车单id
		String pickupIdArray = getPara("pickupIds");
		String pickupIds[] = pickupIdArray.split(",");
		//行车单信息
        String carSummaryId = getPara("car_summary_id");
        String carNo = getPara("car_no");
        String mainDriverName = getPara("main_driver_name");
        String mainDriverAmount = getPara("main_driver_amount").equals("") ?"0":getPara("main_driver_amount");
        String minorDriverName = getPara("minor_driver_name");
        String minorDriverAmount = getPara("minor_driver_amount").equals("") ?"0":getPara("minor_driver_amount");
        String startCarMileage = getPara("start_car_mileage").equals("") ?"0":getPara("start_car_mileage");
        String finishCarMileage = getPara("finish_car_mileage").equals("") ?"0":getPara("finish_car_mileage");
        String monthStartCarNext = getPara("month_start_car_next").equals("") ?"0":getPara("month_start_car_next");
        String monthCarRunMileage = getPara("month_car_run_mileage").equals("") ?"0":getPara("month_car_run_mileage");
        String monthRefuelAmount = getPara("month_refuel_amount").equals("") ?"0":getPara("month_refuel_amount");
        String nextStartCarAmount = getPara("next_start_car_amount").equals("") ?"0":getPara("next_start_car_amount");
        String deductApportionAmount = getPara("deduct_apportion_amount").equals("") ?"0":getPara("deduct_apportion_amount");
        String actualPaymentAmount = getPara("actual_payment_amount").equals("") ?"0":getPara("actual_payment_amount");
        boolean result;
        String order_no = null;
        if(carSummaryId.equals("") || carSummaryId == null){ //新建时
        	CarSummaryOrder carSummaryOrder = new CarSummaryOrder();
        	java.util.Date utilDate = new java.util.Date();
        	java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
        	//调车单号
        	CarSummaryOrder order = CarSummaryOrder.dao
    				.findFirst("select * from car_summary_order order by id desc limit 0,1");
        	if (order != null) {
	        	String num = order.get("order_no");
	            String str = num.substring(2, num.length());
	            Long oldTime = Long.parseLong(str);
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	            String format = sdf.format(new Date());
	            String time = format + "00001";
	            Long newTime = Long.parseLong(time);
	            if (oldTime >= newTime) {
	                order_no = String.valueOf((oldTime + 1));
	            } else {
	                order_no = String.valueOf(newTime);
	            }
        	} else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String format = sdf.format(new Date());
                order_no = format + "00001";
            }
        	
        	result = carSummaryOrder.set("order_no", "XC" + order_no).set("car_no", carNo).set("main_driver_name", mainDriverName)
            		.set("main_driver_amount", mainDriverAmount).set("minor_driver_name", minorDriverName)
            		.set("minor_driver_amount", minorDriverAmount).set("start_car_mileage", startCarMileage)
            		.set("finish_car_mileage", finishCarMileage).set("month_start_car_next", monthStartCarNext)
            		.set("month_car_run_mileage", monthCarRunMileage).set("month_refuel_amount", monthRefuelAmount)
            		.set("next_start_car_amount", nextStartCarAmount).set("deduct_apportion_amount", deductApportionAmount)
            		.set("actual_payment_amount", actualPaymentAmount).set("create_data", sqlDate)
            		.set("status", carSummaryOrder.CAR_SUMMARY_SYSTEM_NEW).save();
        	
        	if(result){
        		long id = carSummaryOrder.getLong("id");
        		List<Long> orderIds = new ArrayList<Long>();
        		
        		for (int i = 0; i < pickupIds.length; i++) {
        			//修改调车单状态为：已处理
        			DepartOrder departOrder = DepartOrder.dao.findById(pickupIds[i]);
        			departOrder.set("car_summary_type", "processed");
        			departOrder.update();
        			//插入从表数据
        			CarSummaryDetail carSummaryDetail = new CarSummaryDetail();
        			carSummaryDetail.set("car_summary_id", id);
        			carSummaryDetail.set("pickup_order_id", departOrder.get("id"));
        			carSummaryDetail.set("pickup_order_no", departOrder.get("depart_no"));
        			carSummaryDetail.save();
        			//记录运输单id
        			List<Record> recList = Db.find("select * from depart_transfer where pickup_id = "+departOrder.get("id"));
        			for (Record record : recList) {
        				orderIds.add(record.getLong("order_id"));
					}
				}
        		//创建费用合计表初始数据
        		initCarSummaryDetailOtherFeeData(id);
        		//创建行车里程碑
        		saveCarSummaryOrderMilestone(id,carSummaryOrder.CAR_SUMMARY_SYSTEM_NEW);
        		//设置默认运输单分摊比例
        		double number = 1.0D/orderIds.size();
        		BigDecimal b = new BigDecimal(number); 
        		double rate = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
        		for (Long orderId : orderIds) {
        			TransferOrder transferOrder = TransferOrder.dao.findById(orderId);
    				transferOrder.set("car_summary_order_share_ratio", rate);
    				transferOrder.update();
				}
        		
        		carSummaryId = Long.toString(id);
        	}
        }else{//修改时
        	CarSummaryOrder carSummary = CarSummaryOrder.dao.findById(carSummaryId);
        	if(carSummary != null){
        		carSummary.set("main_driver_name", mainDriverName)
	    		.set("main_driver_amount", mainDriverAmount).set("minor_driver_name", minorDriverName)
	    		.set("minor_driver_amount", minorDriverAmount).set("start_car_mileage", startCarMileage)
	    		.set("finish_car_mileage", finishCarMileage).set("month_start_car_next", monthStartCarNext)
	    		.set("month_car_run_mileage", monthCarRunMileage).set("month_refuel_amount", monthRefuelAmount)
	    		.set("next_start_car_amount", nextStartCarAmount).set("deduct_apportion_amount", deductApportionAmount)
	    		.set("actual_payment_amount", actualPaymentAmount).update();
        	}
        }
        //修改费用合计中的司机工资
        if(!"0".equals(mainDriverAmount) || !"0".equals(minorDriverAmount) ){
	        double amount = Double.parseDouble(mainDriverAmount)+Double.parseDouble(minorDriverAmount);
			String sql="update car_summary_detail_other_fee set amount = "+amount+" where amount_item  = '司机工资' and car_summary_id = "+carSummaryId;
			Db.update(sql);
        }
        //计算本次油耗
		if(!"0".equals(monthCarRunMileage)){
			updateNextFuelStandard(carSummaryId);
		}
        renderJson(carSummaryId);
	}
	
	//查询所有自营副司机
	public void searchAllDriver(){
		List<Record> carinfoList = Db.find("select * from carinfo where type = 'OWN'");
		renderJson(carinfoList);
	}
	
	//线路查询
	public void findAllAddress(){
		String pickupIds = getPara("pickupIds");
		
		String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
        }
        String sqlTotal = "select count(1) total from depart_order tor "
         		+ " left join depart_transfer dt on dt.pickup_id = tor.id "
         		+ " where tor.id in("+pickupIds+");";
    	 
        String sql = "select tor.*, tr.order_no ,c.abbr,tr.address address1,tor.address address2,"
         		+ "( select warehouse_name from  warehouse where id = tr.warehouse_id  ) address3 "
         		+ " from depart_order tor "
         		+ " left join depart_transfer dt on dt.pickup_id = tor.id "
         		+ " left join transfer_order tr on tr.id = dt.order_id "
         		+ " left join party p on p.id = tr.customer_id "
         		+ " left join contact c ON c.id = p.contact_id "
         		+ " where tor.id in("+pickupIds+");";
         
		Record rec = Db.findFirst(sqlTotal);
		List<Record> orders = Db.find(sql);
        
        HashMap orderMap = new HashMap();
        orderMap.put("sEcho", pageIndex);
        orderMap.put("iTotalRecords", rec.getLong("total"));
        orderMap.put("iTotalDisplayRecords", rec.getLong("total"));
        orderMap.put("aaData", orders);
        renderJson(orderMap);
		
	}
	// 货品信息
    public void findPickupOrderItems() {
    	String pickupIds = getPara("pickupIds");// 调车单id

        String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
        }
        String sqlTotal = "select count(0) total from depart_transfer dt left join transfer_order_item toi on toi.order_id = dt.pickup_id where dt.pickup_id  in(" + pickupIds + ")";
        
        logger.debug("sql :" + sqlTotal);
        Record rec = Db.findFirst(sqlTotal);
        logger.debug("total records:" + rec.getLong("total"));

        String sql = "select dor.depart_no,toi.id,ifnull(toi.item_name, pd.item_name) item_name,"
        		+ "ifnull(toi.item_no, pd.item_no) item_no,ifnull(toi.volume, pd.volume) * toi.amount volume,"
        		+ "ifnull(case toi.weight when 0.0 then null else toi.weight end,pd.weight ) * toi.amount weight,"
        		+ "c.abbr customer,tor.order_no,toi.amount,toi.remark"
        		+ " from depart_transfer dt"
        		+ " left join depart_order dor on dor.id = dt.pickup_id  "
        		+ " left join transfer_order tor on tor.id = dt.order_id"
        		+ " left join transfer_order_item toi on toi.order_id = dt.order_id"
        		+ " left join party p on p.id = tor.customer_id"
        		+ " left join contact c on c.id = p.contact_id"
        		+ " left join product pd on pd.id = toi.product_id"
        		+ " where dt.pickup_id  IN ("+pickupIds+")"
        		+ " order by dt.pickup_id " + sLimit;
        List<Record> departOrderitem = Db.find(sql);
        Map Map = new HashMap();
        Map.put("sEcho", pageIndex);
        Map.put("iTotalRecords", rec.getLong("total"));
        Map.put("iTotalDisplayRecords", rec.getLong("total"));
        Map.put("aaData", departOrderitem);
        renderJson(Map);
    }
    // 里程碑
    public void transferOrderMilestoneList() {
    	String pickupIds = getPara("pickupIds");// 调车单id

        String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
        }
        String sqlTotal = "select count(0) total from transfer_order_milestone tom"
        		+ " left join user_login u on u.id = tom.create_by"
        		+ " where tom.type = '"
                            + TransferOrderMilestone.TYPE_CAR_SUMMARY_MILESTONE + "' and tom.car_summary_id in(" + pickupIds+");";
        logger.debug("sql :" + sqlTotal);
        Record rec = Db.findFirst(sqlTotal);
        logger.debug("total records:" + rec.getLong("total"));

        String sql = "select tom.*,u.user_name from transfer_order_milestone tom"
        		+ " left join user_login u on u.id = tom.create_by"
        		+ " where tom.type = '"
                            + TransferOrderMilestone.TYPE_CAR_SUMMARY_MILESTONE + "' and tom.car_summary_id in(" + pickupIds+");";
        List<Record> departOrderitem = Db.find(sql);
        Map Map = new HashMap();
        Map.put("sEcho", pageIndex);
        Map.put("iTotalRecords", rec.getLong("total"));
        Map.put("iTotalDisplayRecords", rec.getLong("total"));
        Map.put("aaData", departOrderitem);
        renderJson(Map);
    }
    
    //查询路桥费明细
    public void findCarSummaryRouteFee(){
    	String car_summary_id = getPara("car_summary_id");// 调车单id
    	if(car_summary_id != ""){
	    	String sLimit = "";
	        String pageIndex = getPara("sEcho");
	        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
	            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
	        }
	        String sqlTotal = "select count(0) total from car_summary_detail_route_fee where car_summary_id ="+car_summary_id+";";
	        logger.debug("sql :" + sqlTotal);
	        Record rec = Db.findFirst(sqlTotal);
	        logger.debug("total records:" + rec.getLong("total"));
	
	        String sql = "select * from car_summary_detail_route_fee where car_summary_id ="+car_summary_id+";";
	        List<Record> departOrderitem = Db.find(sql);
	        Map Map = new HashMap();
	        Map.put("sEcho", pageIndex);
	        Map.put("iTotalRecords", rec.getLong("total"));
	        Map.put("iTotalDisplayRecords", rec.getLong("total"));
	        Map.put("aaData", departOrderitem);
	        renderJson(Map); 
    	}
    }
    // 添加路桥费明细
    public void addCarSummaryRouteFee() {
        String carSummaryId = getPara();
        if(carSummaryId != ""){
        	java.util.Date utilDate = new java.util.Date();
        	java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
            
        	String sql = "select * from car_summary_detail_route_fee where  car_summary_id ='"+carSummaryId+"' order by item  desc limit 0,1;";
        	List<CarSummaryDetailRouteFee> routeFeeList = CarSummaryDetailRouteFee.dao.find(sql);
        	long item = 1;
        	if(routeFeeList.size() > 0){
        		item += routeFeeList.get(0).getLong("item");
        	}
        	CarSummaryDetailRouteFee routeFee = new CarSummaryDetailRouteFee();
            routeFee.set("item", item).set("car_summary_id", carSummaryId).set("charge_data", sqlDate).save();
        }
        renderJson("{\"success\":true}");
    }
    //删除路桥费明细
    public void delCarSummaryRouteFee(){
    	String carSummaryRouteFeeId = getPara();
    	CarSummaryDetailRouteFee.dao.deleteById(carSummaryRouteFeeId);
    	renderJson("{\"success\":true}");
    }
    //修改路桥费明细
    public void updateCarSummaryDetailRouteFee(){
    	String carSummaryId = getPara("car_summary_id");
    	String routeFeeId = getPara("routeFeeId");
    	String name = getPara("name");
    	String value = getPara("value");
    	if(!"".equals(routeFeeId) && routeFeeId != null){
    		if("travel_amount".equals(name) && "".equals(value)){
    			value = "0";
    		}
    		CarSummaryDetailRouteFee carSummaryDetailrouteFee = CarSummaryDetailRouteFee.dao.findById(routeFeeId);
    		carSummaryDetailrouteFee.set(name, value);
    		carSummaryDetailrouteFee.update();
    		//修改费用合计中的路桥费
    		if("travel_amount".equals(name) && !"0".equals(value)){
    			Record rec = Db.findFirst("select sum(travel_amount) amount from car_summary_detail_route_fee where car_summary_id ="+carSummaryId);
    			String sql="update car_summary_detail_other_fee set amount = "+rec.getDouble("amount")+" where amount_item  = '路桥费' and car_summary_id = "+carSummaryId;
    			Db.update(sql);
    		}
    	}
    	renderJson("{\"success\":true}");
    }
    
    //查询加油记录
    public void findCarSummaryDetailOilFee(){
    	String car_summary_id = getPara("car_summary_id");// 调车单id
    	if(car_summary_id != ""){
	    	String sLimit = "";
	        String pageIndex = getPara("sEcho");
	        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
	            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
	        }
	        String sqlTotal = "select count(0) total from car_summary_detail_oil_fee where car_summary_id ="+car_summary_id+";";
	        logger.debug("sql :" + sqlTotal);
	        Record rec = Db.findFirst(sqlTotal);
	        logger.debug("total records:" + rec.getLong("total"));
	
	        String sql = "select * from car_summary_detail_oil_fee where car_summary_id ="+car_summary_id+";";
	        List<Record> departOrderitem = Db.find(sql);
	        Map Map = new HashMap();
	        Map.put("sEcho", pageIndex);
	        Map.put("iTotalRecords", rec.getLong("total"));
	        Map.put("iTotalDisplayRecords", rec.getLong("total"));
	        Map.put("aaData", departOrderitem);
	        renderJson(Map); 
    	}
    }
    // 添加加油记录
    public void addCarSummaryDetailOilFee() {
        String carSummaryId = getPara();
        if(carSummaryId != ""){
        	java.util.Date utilDate = new java.util.Date();
        	java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
        	
        	String sql = "select * from car_summary_detail_oil_fee where  car_summary_id ='"+carSummaryId+"' order by item  desc limit 0,1;";
        	List<CarSummaryDetailOilFee> oilFeeList = CarSummaryDetailOilFee.dao.find(sql);
        	long item = 1;
        	if(oilFeeList.size() > 0){
        		item += oilFeeList.get(0).getLong("item");
        	}
        	CarSummaryDetailOilFee oilFee = new CarSummaryDetailOilFee();
        	oilFee.set("item", item).set("car_summary_id", carSummaryId)
        	.set("refuel_data", sqlDate).set("payment_type", "油卡").save();
        }
        renderJson("{\"success\":true}");
    }
    //删除加油记录
    public void delCarSummaryDetailOilFee(){
    	String carSummaryDetailOilFeeId = getPara();
    	CarSummaryDetailOilFee.dao.deleteById(carSummaryDetailOilFeeId);
    	renderJson("{\"success\":true}");
    }
    //修改加油记录
    public void updateCarSummaryDetailOilFee(){
    	String carSummaryId = getPara("car_summary_id");
    	String routeFeeId = getPara("routeFeeId");
    	String name = getPara("name");
    	String value = getPara("value");
    	if(!"".equals(routeFeeId) && routeFeeId != null){
    		if("odometer_mileage".equals(name) || "refuel_unit_cost".equals(name)
    			|| "refuel_number".equals(name) || "refuel_amount".equals(name) 
    			|| "avg_econ".equals(name) || "load_amount".equals(name)){
    			if("".equals(value)){
    				value = "0";
    			}
    		}
    		CarSummaryDetailOilFee carSummaryDetailOilFee = CarSummaryDetailOilFee.dao.findById(routeFeeId);
    		carSummaryDetailOilFee.set(name, value);
    		carSummaryDetailOilFee.update();
    		
    		//修改费用合计中的本次加油
    		if("payment_type".equals(name) || "refuel_amount".equals(name) && !"0".equals(value)){
    			try {
    				Record rec = Db.findFirst("select ifnull(sum(refuel_amount),0) amount from car_summary_detail_oil_fee where payment_type = '油卡' and car_summary_id ="+carSummaryId);
        			String isDelete = "是";
        			if(rec.get("amount").equals(0)){
        				isDelete = "否";
        			}
        			String sql="update car_summary_detail_other_fee set amount = "+rec.get("amount")+",is_delete = '"+isDelete+"'  where amount_item  = '本次加油' and car_summary_id = "+carSummaryId;
        			Db.update(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
    		}
    		//修改费用合计中的本次油耗
    		if("refuel_unit_cost".equals(name) && !"0".equals(value)){
    			updateNextFuelStandard(carSummaryId);
    		}
    	}
    	renderJson("{\"success\":true}");
    }
    
    //查询送货员工资明细
    public void findCarSummaryDetailSalary(){
    	String carSummaryId = getPara("car_summary_id");// 调车单id
    	if(carSummaryId != ""){
	    	String sLimit = "";
	        String pageIndex = getPara("sEcho");
	        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
	            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
	        }
	        String sqlTotal = "select count(0) total from car_summary_detail_salary where car_summary_id ="+carSummaryId+";";
	        logger.debug("sql :" + sqlTotal);
	        Record rec = Db.findFirst(sqlTotal);
	        logger.debug("total records:" + rec.getLong("total"));
	
	        String sql = "select * from car_summary_detail_salary where car_summary_id ="+carSummaryId+";";
	        List<Record> departOrderitem = Db.find(sql);
	        Map Map = new HashMap();
	        Map.put("sEcho", pageIndex);
	        Map.put("iTotalRecords", rec.getLong("total"));
	        Map.put("iTotalDisplayRecords", rec.getLong("total"));
	        Map.put("aaData", departOrderitem);
	        renderJson(Map); 
    	}
    }
    // 添加送货员工资明细
    public void addCarSummaryDetailSalary() {
        String carSummaryId = getPara();
        if(carSummaryId != ""){
        	java.util.Date utilDate = new java.util.Date();
        	java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
        	String sql = "select * from car_summary_detail_salary where  car_summary_id ='"+carSummaryId+"' order by item  desc limit 0,1;";
        	List<CarSummaryDetailSalary> salaryList = CarSummaryDetailSalary.dao.find(sql);
        	long item = 1;
        	if(salaryList.size() > 0){
        		item += salaryList.get(0).getLong("item");
        	}
        	CarSummaryDetailSalary salary = new CarSummaryDetailSalary();
        	salary.set("item", item).set("car_summary_id", carSummaryId)
        	.set("create_data", sqlDate).save();
        }
        renderJson("{\"success\":true}");
    }
    //删除送货员工资明细
    public void delCarSummaryDatailSalary(){
    	String carSummaryDetailSalaryId = getPara();
    	CarSummaryDetailSalary.dao.deleteById(carSummaryDetailSalaryId);
    	renderJson("{\"success\":true}");
    }
    //修改送货员工资明细
    public void updateCarSummaryDetailSalary(){
    	String carSummaryId = getPara("car_summary_id");
    	String routeFeeId = getPara("routeFeeId");
    	String name = getPara("name");
    	String value = getPara("value");
    	if(!"".equals(routeFeeId) && routeFeeId != null){
    		if("deserved_amount".equals(name) && "".equals(value)){
    			value = "0";
    		}
    		CarSummaryDetailSalary salary = CarSummaryDetailSalary.dao.findById(routeFeeId);
    		salary.set(name, value);
    		salary.update();
    		//修改费用合计中的送货员工资
    		if("deserved_amount".equals(name) && !"0".equals(value)){
    			Record rec = Db.findFirst("select sum(deserved_amount) amount from car_summary_detail_salary where car_summary_id ="+carSummaryId);
    			String sql="update car_summary_detail_other_fee set amount="+rec.getDouble("amount")+" where amount_item  = '送货员工资' and car_summary_id = "+carSummaryId;
    			Db.update(sql);
    		}
    	}
    	renderJson("{\"success\":true}");
    }
    //查询费用合计
    public void findCarSummaryDetailOtherFee(){
    	String car_summary_id = getPara("car_summary_id");// 调车单id
    	if(car_summary_id != ""){
	    	String sLimit = "";
	        String pageIndex = getPara("sEcho");
	        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
	            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
	        }
	        String sqlTotal = "select count(0) total from car_summary_detail_other_fee where car_summary_id ="+car_summary_id+";";
	        logger.debug("sql :" + sqlTotal);
	        Record rec = Db.findFirst(sqlTotal);
	        logger.debug("total records:" + rec.getLong("total"));
	
	        String sql = "select * from car_summary_detail_other_fee where car_summary_id ="+car_summary_id+";";
	        List<Record> departOrderitem = Db.find(sql);
	        Map Map = new HashMap();
	        Map.put("sEcho", pageIndex);
	        Map.put("iTotalRecords", rec.getLong("total"));
	        Map.put("iTotalDisplayRecords", rec.getLong("total"));
	        Map.put("aaData", departOrderitem);
	        renderJson(Map); 
    	}
    }
    //修改费用合计(复杂的费用计算业务)
    public void updateCarSummaryDetailOtherFee(){
    	String routeFeeId = getPara("routeFeeId");
    	String name = getPara("name");
    	String value = getPara("value");
    	if(!"".equals(routeFeeId) && routeFeeId != null){
    		if("amount".equals(name) && "".equals(value)){
    			value = "0";
    		}
    		CarSummaryDetailOtherFee otherFee = CarSummaryDetailOtherFee.dao.findById(routeFeeId);
    		otherFee.set(name, value);
    		otherFee.update();
    	}
    	renderJson("{\"success\":true}");
    }
    //审核-撤销审核
    @RequiresPermissions(value = {PermissionConstant.PERMSSION_CS_APPROVAL})
    public void updateCarSummaryOrderStatus(){
    	String carSummaryId = getPara("carSummaryId");
    	String value = getPara("value").trim();
    	if(!"".equals(carSummaryId) && carSummaryId != null){
    		CarSummaryOrder order = CarSummaryOrder.dao.findById(carSummaryId);
    		if("审核".equals(value)){
    			value = order.CAR_SUMMARY_SYSTEM_CHECKED;
    		}else if("撤销审核".equals(value)){
    			value = order.CAR_SUMMARY_SYSTEM_REVOCATION;
    		}else if("报销".equals(value)){
    			value = order.CAR_SUMMARY_SYSTEM_REIMBURSEMENT;
    		}else{
    			value = "";
    		}
    		order.set("status", value);
    		order.update();
    		//修改行车里程碑状态
    		saveCarSummaryOrderMilestone(Long.parseLong(carSummaryId),value);
    	}
    	renderJson("{\"success\":true}");
    }
    //每次新增调车单时要添加的数据
    private void initCarSummaryDetailOtherFeeData(long carSummaryOrderId){
    	String name[] = {"car_summary_id","item","amount_item","is_delete"};
    	String items[] = {"本次加油","本次油耗","出车补贴","司机工资","路桥费","装卸费","罚款","送货员工资","停车费","住宿费","过磅费","其他费用"};
    	String isdeletes[] = {"是","否"};
    	for (int i = 0; i < items.length; i++) {
    		CarSummaryDetailOtherFee c = new CarSummaryDetailOtherFee();
    		if(i == 1 || i == 3 || i == 7)
    			c.set(name[0], carSummaryOrderId).set(name[1], i+1).set(name[2], items[i]).set(name[3], isdeletes[0]).set("amount", 0).save();
    		else
    			c.set(name[0], carSummaryOrderId).set(name[1], i+1).set(name[2], items[i]).set(name[3], isdeletes[1]).set("amount", 0).save();
		}
    }
    // 保存行车里程碑
 	private void saveCarSummaryOrderMilestone(long carSummaryOrderId,String status) {
 		TransferOrderMilestone transferOrderMilestone = new TransferOrderMilestone();
 		transferOrderMilestone.set("status", status);
 		String name = (String) currentUser.getPrincipal();
 		List<UserLogin> users = UserLogin.dao
 				.find("select * from user_login where user_name='" + name + "'");
 		transferOrderMilestone.set("create_by", users.get(0).get("id"));
 		transferOrderMilestone.set("location", "");
 		java.util.Date utilDate = new java.util.Date();
 		java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
 		transferOrderMilestone.set("create_stamp", sqlDate);
 		transferOrderMilestone.set("type",
 				TransferOrderMilestone.TYPE_CAR_SUMMARY_MILESTONE);
 		transferOrderMilestone.set("car_summary_id", carSummaryOrderId);
 		transferOrderMilestone.save();
 	}
 	//编辑行车单
 	@RequiresPermissions(value = {PermissionConstant.PERMSSION_CS_CREATE})
    public void edit(){
    	String carSummaryId = getPara("carSummaryId");
    	if(carSummaryId != "" && carSummaryId != null){
    		CarSummaryOrder carSummaryOrder = CarSummaryOrder.dao.findById(carSummaryId);
    		//车牌号
			setAttr("car_no", carSummaryOrder.get("car_no"));
			//主司机姓名
			setAttr("driver", carSummaryOrder.get("main_driver_name"));
			//出车次
			setAttr("carNumber", carSummaryOrder.get("month_start_car_next"));
			//是否审核 isAudit
			String status = carSummaryOrder.get("status");
			if(carSummaryOrder.CAR_SUMMARY_SYSTEM_NEW.equals(status) ||carSummaryOrder.CAR_SUMMARY_SYSTEM_REVOCATION.equals(status) )
				setAttr("isAudit", "no");
			else if(carSummaryOrder.CAR_SUMMARY_SYSTEM_CHECKED.equals(status) || carSummaryOrder.CAR_SUMMARY_SYSTEM_REIMBURSEMENT.equals(status))
				setAttr("isAudit", "yes");
			else
				setAttr("isAudit", "no");
			
			setAttr("carSummaryOrder", carSummaryOrder);
			
			Record rec = Db.findFirst("select group_concat(csd.pickup_order_id separator ',') pickupids  from car_summary_detail csd where csd.car_summary_id in("+carSummaryId+") ;");
			//拼车单号
			setAttr("pickupIds", rec.get("pickupids"));
    	}
    	render("/yh/carmanage/carSummaryEdit.html");
    }
    
    //查询运输单
    public void findTransferOrder(){
    	String pickupIds = getPara("pickupIds");// 调车单id
    	if(pickupIds != ""){
	    	String sLimit = "";
	        String pageIndex = getPara("sEcho");
	        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
	            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
	        }
	        String sqlTotal = "select count(0) total from depart_transfer dt where dt.pickup_id in(" + pickupIds+");";
	        logger.debug("sql :" + sqlTotal);
	        Record rec = Db.findFirst(sqlTotal);
	        logger.debug("total records:" + rec.getLong("total"));
	
	        String sql = "select dt.*, tr.order_no,c.abbr,tr.car_summary_order_share_ratio,tr.remark,"
	        		+ " (select sum(toi.amount) from transfer_order_item toi where toi.order_id = dt.order_id ) amount,"
	        		+ " (select sum( ifnull(toi.volume, p.volume) * toi.amount ) from transfer_order_item toi "
	        		+ " left join product p on p.id = toi.product_id where toi.order_id = dt.order_id ) volume,"
	        		+ " (select sum( ifnull( nullif(toi.weight, 0), p.weight ) * toi.amount ) from transfer_order_item toi"
	        		+ " left join product p on p.id = toi.product_id where toi.order_id = dt.order_id ) weight"
	        		+ " from depart_transfer dt"
	        		+ " left join transfer_order tr on tr.id = dt.order_id"
	        		+ " left join party p on p.id = tr.customer_id"
	        		+ " left join contact c on c.id = p.contact_id"
	        		+ " where dt.pickup_id in(" + pickupIds+")"
	        		+ " order by dt.pickup_id asc "+ sLimit;
	        List<Record> departOrderitem = Db.find(sql);
	        Map Map = new HashMap();
	        Map.put("sEcho", pageIndex);
	        Map.put("iTotalRecords", rec.getLong("total"));
	        Map.put("iTotalDisplayRecords", rec.getLong("total"));
	        Map.put("aaData", departOrderitem);
	        renderJson(Map); 
    	}
    }
    //修改运输单比例
    public void updateTransferOrderShareRatio(){
    	String[] orderIds  =  getPara("orderIds").split(",");
    	String[] rates  =  getPara("rates").split(",");
    	
    	if(orderIds.length > 0 && rates.length > 0){
	    	for (int i = 0; i < orderIds.length; i++) {
	    		double rate = Double.parseDouble(rates[i])/100;
	    		TransferOrder transferOrder = TransferOrder.dao.findById(orderIds[i]);
	    		transferOrder.set("car_summary_order_share_ratio", rate);
	    		transferOrder.update();
			}
    	}
    	renderJson("{\"success\":true}");
    }
    
    
    /**
     * 费用计算
     * return:本单总加油、本单出车成本、应扣分摊费用、实际应付费用
     */
    public void calculateCost(){
    	String carSummaryId = getPara("carSummaryId");
    	Map<String,Double > costMap = new HashMap<String, Double>();
    	CarSummaryOrder order =CarSummaryOrder.dao.findById(carSummaryId);
    	//出车成本
    	Record rec1 = Db.findFirst("select sum(amount) amount from car_summary_detail_other_fee where item != 1 and car_summary_id = "+carSummaryId+";");
    	if(rec1.get("amount") != null && rec1.get("amount") != ""){
    		costMap.put("next_start_car_amount", rec1.getDouble("amount"));
    		order.set("next_start_car_amount", rec1.getDouble("amount"));
    	}
    	//总加油费（油卡和现金）
    	Record rec2 = Db.findFirst("select sum(refuel_amount) amount from car_summary_detail_oil_fee where car_summary_id = "+carSummaryId+";");
    	if(rec2.get("amount") != null && rec2.get("amount") != ""){
    		costMap.put("month_refuel_amount ", rec2.getDouble("amount"));
    		order.set("month_refuel_amount", rec2.getDouble("amount"));
    	}
    	//应扣分摊费用
    	Record rec4 = Db.findFirst("select sum(amount) amount  from car_summary_detail_other_fee where is_delete = '是' and car_summary_id = "+carSummaryId+";");
    	if(rec4.get("amount") != null && rec4.get("amount") != ""){
    		BigDecimal b = new BigDecimal(rec4.getDouble("amount")); 
    		double money = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
    		costMap.put("deduct_apportion_amount ", money);
    		order.set("deduct_apportion_amount", money);
    	}
    	//实际应付费用
    	Record rec3 = Db.findFirst("select sum(amount) amount  from car_summary_detail_other_fee where is_delete = '否' and car_summary_id = "+carSummaryId+";");
    	Record rec5 = Db.findFirst("select sum(refuel_amount) amount from car_summary_detail_oil_fee where payment_type = '现金' and car_summary_id = "+carSummaryId+";");
    	if(rec3.get("amount") != null && rec3.get("amount") != ""){
    		BigDecimal b = new BigDecimal(rec3.getDouble("amount")); 
    		double money3 = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
    		if(rec5.get("amount") != null && rec5.get("amount") != ""){
    			BigDecimal c = new BigDecimal(rec5.getDouble("amount")); 
        		double money5 = c.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
    			costMap.put("actual_payment_amount ", money3 + money5);
        		order.set("actual_payment_amount", money3 + money5);
    		}else{
    			costMap.put("actual_payment_amount ", money3);
        		order.set("actual_payment_amount", money3);
    		}
    	}
    	order.update();
    	renderJson(costMap); 
    }
    //本次油耗
    public void updateNextFuelStandard(String carSummaryId){
    	CarSummaryOrder order = CarSummaryOrder.dao.findById(carSummaryId);
    	String carNo = order.getStr("car_no");
    	double mileage = 0;
    	double refuelCost = 0;
    	double hundredFuelStandard = 0;
    	if(order.get("month_car_run_mileage") != null && !"".equals(order.getDouble("month_car_run_mileage"))){
    		mileage = order.getDouble("month_car_run_mileage");//行驶里程
    	}
    	Record oilFee = Db.findFirst("select avg(refuel_unit_cost) refuel_cost from car_summary_detail_oil_fee where car_summary_id = "+carSummaryId);
    	if(oilFee.get("refuel_cost") != null && !"".equals(oilFee.get("refuel_cost"))){
    		refuelCost = oilFee.getDouble("refuel_cost");//平均油价
    	}
    	Record carinfo = Db.findFirst("select hundred_fuel_standard from carinfo where car_no = '"+carNo+"';");
    	if(carinfo.get("hundred_fuel_standard") != null && !"".equals(carinfo.get("hundred_fuel_standard"))){
    		hundredFuelStandard = carinfo.getDouble("hundred_fuel_standard");//百公里油耗
    	}
    	if(mileage > 0 && refuelCost > 0 && hundredFuelStandard > 0){
    		double number = mileage * refuelCost * hundredFuelStandard / 100;
    		BigDecimal b = new BigDecimal(number); 
    		double money = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
    		String sql = "update car_summary_detail_other_fee set amount = ? where car_summary_id = '"+carSummaryId+"' and item = 2 ;";
    		Db.update(sql, money);
    	}
    }
    
    
    
    
}
