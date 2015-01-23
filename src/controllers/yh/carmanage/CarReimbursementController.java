package controllers.yh.carmanage;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import models.yh.arap.ReimbursementOrder;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import controllers.yh.LoginUserController;
import controllers.yh.profile.CarinfoController;
import controllers.yh.util.OrderNoUtil;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class CarReimbursementController extends Controller {
	private Logger logger = Logger.getLogger(CarinfoController.class);
	Subject currentUser = SecurityUtils.getSubject();
	
	public void index() {
        HttpServletRequest re = getRequest();
        String url = re.getRequestURI();
        if (url.equals("/carreimbursement")) {
                render("/yh/carmanage/carReimbursementList.html");
        }
    }
	
	public void orderDetailList(){
		Map orderMap = null;
		String carSummaryOrderIds = getPara("car_summary_order_ids");
		
		String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
        }
        
		String sqlTotal = "select count(0) total from car_summary_order cso"
				+ " left join car_summary_detail csd on csd.car_summary_id = cso.id"
				+ " left join depart_order dod on dod.id = csd.pickup_order_id "
				+ "	where cso.id in ("+carSummaryOrderIds+")";
		
		String sql = "select cso.id,cso.order_no ,cso.status ,cso.car_no,cso.main_driver_name ,"
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
				+ " left join car_summary_detail csd on csd.car_summary_id = cso.id"
				+ " left join depart_order dod on dod.id = csd.pickup_order_id "
				+ "	where cso.id in ("+carSummaryOrderIds+") "
				//+ " and ifnull(dor.start_data, '') like '%"+transferOrderNo+"%'"
				+ " order by cso.create_data desc " + sLimit;
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
	
	//行车单查询
	public void carSummaryOrderList(){
		
		Map orderMap = null;
		String status = getPara("status");
		String driver = getPara("driver");
		String car_no = getPara("car_no");
		String transferOrderNo = getPara("transferOrderNo");
		String order_no = getPara("carSummaryOrderNo");
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
	
	//创建行车报销单
	public void createCarReimbursement(){
		String car_summary_order_ids = getPara("carSummeryIds");
		String[] ids=car_summary_order_ids.split(",");
		for(int i=0;i<ids.length;i++){
			String id = ids[i];
			
		}
		setAttr("car_summary_order_ids", car_summary_order_ids);
		render("/yh/carmanage/carReimbursementEdit.html");
	}
	
	//保存行车报销单
	public void saveCarReimbursement(){
		String orderId = getPara("order_id");
		String car_summary_order_ids = getPara("car_summary_order_ids");
		
		ReimbursementOrder order = null;
		if(orderId==null || "".equals(orderId)){
			order = new ReimbursementOrder();
			//行车报销单号
        	String sql = "select * from reimbursement_order order by id desc limit 0,1";
        	String orderNo = OrderNoUtil.getOrderNo(sql,"XCBX");
        	order.set("order_no", orderNo);
        	order.set("car_summery_order_ids", car_summary_order_ids);
        	order.set("create_id", LoginUserController.getLoginUserId(this));
			order.set("status", ReimbursementOrder.ORDER_STATUS_NEW);
			order.set("create_stamp", new Date());
			order.set("remark", getPara("remark"));
			order.save();
		}else{
			order = ReimbursementOrder.dao.findById(orderId);
			order.set("remark", getPara("remark"));
			order.update();
		}
				
		//order.set("creator", "test");
		renderJson(order);
	}
}
