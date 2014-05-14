package controllers.yh.delivery;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.TransferOrderMilestone;
import models.DeliveryOrderMilestone;
import models.UserLogin;
import models.yh.delivery.DeliveryOrder;
import models.yh.profile.Contact;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import controllers.yh.LoginUserController;

public class DeliveryController extends Controller {
    private Logger logger = Logger.getLogger(DeliveryController.class);
    // in config route已经将路径默认设置为/yh
    // me.add("/yh", controllers.yh.AppController.class, "/yh");
    Subject currentUser = SecurityUtils.getSubject();

    private boolean isAuthenticated() {
        if (!currentUser.isAuthenticated()) {
        	if(LoginUserController.isAuthenticated(this))
            redirect("/yh/login");
            return false;
        }
        setAttr("userId", currentUser.getPrincipal());
        return true;
    }

    public void index() {
        if (!isAuthenticated())
            return;
        if(LoginUserController.isAuthenticated(this))
        render("/yh/delivery/deliveryOrderList.html");
    }

    public void add() {
        setAttr("saveOK", false);
        if(LoginUserController.isAuthenticated(this))
        render("/yh/delivery/deliveryOrderSearchTransfer.html");
    }

    public void edit() {
        String id = getPara();
        System.out.println(id);

        List<Record> deliveryId = Db
                .find("select TRANSFER_ORDER_ID from DELIVERY_ORDER where id ="
                        + id);
        // 客户信息
        List<Contact> customers = Contact.dao
                .find("select *,p.id as pid from contact c,party p,delivery_ORDER t where p.contact_id=c.id and t.customer_id = p.id and t.id ="
                        + id + "");
        Contact customer = customers.get(0);

        // 供应商信息
        List<Contact> customers3 = Contact.dao
                .find("select *,p.id as pid from contact c,party p,delivery_ORDER t where p.contact_id=c.id and t.sp_id = p.id and t.id ="
                        + id + "");
        Contact customer3 = customers3.get(0);

        // 收货人信息
        List<Contact> customers2 = Contact.dao
                .find("select *,p.id as pid from contact c,party p,delivery_ORDER t where p.contact_id=c.id and t.Notify_party_id = p.id and t.id ="
                        + id + "");
        Contact customer2 = customers2.get(0);

        String dd = deliveryId.get(0).get("TRANSFER_ORDER_ID").toString();
        setAttr("transferId", dd);
        setAttr("customer", customer);
        setAttr("customer2", customer2);
        setAttr("customer3", customer3);
        if(LoginUserController.isAuthenticated(this))
        render("/yh/delivery/deliveryOrderEdit.html");
       
    }

    // 配送单客户
    public void selectCustomer() {
        List<Contact> customer = Contact.dao
                .find("select * from contact where id in(select contact_id from party where id in(SELECT customer_id  FROM TRANSFER_ORDER group by customer_id)) and id='1'");
        renderJson(customer);
    }

    /*
     * // 供应商列表,列出最近使用的5个客户 public void selectSp() { List<Contact> contactjson =
     * Contact.dao .find(
     * "select * from contact c  where id in (select contact_id from party where party_type='SERVICE_PROVIDER' order by last_update_date desc limit 0,5)"
     * ); renderJson(contactjson); }
     */

    public void creat() {
        String id = getPara();
        System.out.println(id);
        List<Contact> customers = Contact.dao
                .find("select *,p.id as pid from contact c,party p,TRANSFER_ORDER t where p.contact_id=c.id and t.customer_id = p.id and t.id ="
                        + id + "");
        Contact customer = customers.get(0);

        List<Contact> customers2 = Contact.dao
                .find("select *,p.id as pid from contact c,party p,TRANSFER_ORDER t where p.contact_id=c.id and t.Notify_party_id = p.id and t.id ="
                        + id + "");
        Contact customer2 = customers2.get(0);
        System.out.println(customer2);
        setAttr("transferId", id);
        setAttr("customer", customer);
        setAttr("customer2", customer2);
        // setAttr("customer3", null);
        if(LoginUserController.isAuthenticated(this))
        render("/yh/delivery/deliveryOrderEdit.html");
    }

    // 创建 结构 行为
    public void deliveryOrderList() {

        String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null
                && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", "
                    + getPara("iDisplayLength");
        }

        // 获取总条数
        String totalWhere = "";
        String sql = "select count(1) total from user_login ";
        Record rec = Db.findFirst(sql + totalWhere);
        logger.debug("total records:" + rec.getLong("total"));

        // 获取当前页的数据
        List<Record> orders = Db.find("select * from user_login");
        Map orderMap = new HashMap();
        orderMap.put("sEcho", pageIndex);
        orderMap.put("iTotalRecords", rec.getLong("total"));
        orderMap.put("iTotalDisplayRecords", rec.getLong("total"));

        orderMap.put("aaData", orders);

        renderJson(orderMap);
    }

    public void SearchTransfer() {

        String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null
                && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", "
                    + getPara("iDisplayLength");
        }

        String sqlTotal = "select count(1) total from transfer_order where arrival_mode='gateIn'";
        Record rec = Db.findFirst(sqlTotal);
        logger.debug("total records:" + rec.getLong("total"));

        String sql = "select * from transfer_order where arrival_mode='gateIn'";
        List<Record> transferOrders = Db.find(sql);

        Map transferOrderListMap = new HashMap();
        transferOrderListMap.put("sEcho", pageIndex);
        transferOrderListMap.put("iTotalRecords", rec.getLong("total"));
        transferOrderListMap.put("iTotalDisplayRecords", rec.getLong("total"));
        transferOrderListMap.put("aaData", transferOrders);

        renderJson(transferOrderListMap);
    }

    public void deliveryList() {
        String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null
                && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", "
                    + getPara("iDisplayLength");
        }

        String sqlTotal = "select count(1) total from transfer_order";
        Record rec = Db.findFirst(sqlTotal);
        logger.debug("total records:" + rec.getLong("total"));

        String sql = "select * from delivery_order";

        List<Record> transferOrders = Db.find(sql);

        Map transferOrderListMap = new HashMap();
        transferOrderListMap.put("sEcho", pageIndex);
        transferOrderListMap.put("iTotalRecords", rec.getLong("total"));
        transferOrderListMap.put("iTotalDisplayRecords", rec.getLong("total"));

        transferOrderListMap.put("aaData", transferOrders);

        renderJson(transferOrderListMap);
    }

    public void cancel() {
        String id = getPara();
        System.out.println(id);
        DeliveryOrder.dao.findById(id).set("Status", "取消").update();
        renderJson("{\"success\":true}");
    }

    // 查找供应商
    public void searchSp() {
        String input = getPara("input");
        List<Record> locationList = Collections.EMPTY_LIST;
        if (input.trim().length() > 0) {

            locationList = Db
                    .find("select *,p.id as pid from contact c,party p where p.contact_id= c.id and p.party_type ='SERVICE_PROVIDER' and (c.company_name like '%"
                            + input
                            + "%' or c.contact_person like '%"
                            + input
                            + "%' or c.email like '%"
                            + input
                            + "%' or c.mobile like '%"
                            + input
                            + "%' or c.phone like '%"
                            + input
                            + "%' or c.address like '%"
                            + input
                            + "%' or c.postal_code like '%"
                            + input
                            + "%') limit 0,10");
        }
        renderJson(locationList);
    }

    public void orderList() {
        String trandferOrderId = getPara("trandferOrderId");
        System.out.println(trandferOrderId);
        String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null
                && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", "
                    + getPara("iDisplayLength");
        }

        String sqlTotal = "select count(1) total from transfer_order_item where order_id ="
                + trandferOrderId;
        Record rec = Db.findFirst(sqlTotal);
        logger.debug("total records:" + rec.getLong("total"));

        String sql = "select * from transfer_order_item where order_id ="
                + trandferOrderId;

        List<Record> transferOrders = Db.find(sql);

        Map transferOrderListMap = new HashMap();
        transferOrderListMap.put("sEcho", pageIndex);
        transferOrderListMap.put("iTotalRecords", rec.getLong("total"));
        transferOrderListMap.put("iTotalDisplayRecords", rec.getLong("total"));

        transferOrderListMap.put("aaData", transferOrders);

        renderJson(transferOrderListMap);
    }

    public void deliverySave() {
        DeliveryOrder deliveryOrder = new DeliveryOrder();
        DeliveryOrder order = DeliveryOrder.dao
                .findFirst("select * from delivery_order order by order_no desc limit 0,1");

        String num = order.get("order_no");
        String order_no = String.valueOf((Long.parseLong(num) + 1));

        deliveryOrder.set("Order_no", order_no)
                .set("Transfer_order_id", getPara("tranferid"))
                .set("Customer_id", getPara("customer_id"))
                .set("Sp_id", getPara("cid"))
                .set("Notify_party_id", getPara("notify_id"))
                .set("Status", "新建");
        deliveryOrder.save();
        renderJson(deliveryOrder.get("id"));
    }

    // 运输单ATM序列号
    public void serialNo() {
        String id = getPara("id");
        System.out.println(id);
        List<Record> transferOrders = Db
                .find("SELECT serial_no  FROM TRANSFER_ORDER_ITEM_DETAIL where ORDER_ID  = "
                        + id);
        renderJson(transferOrders);
    }

    // 发车确认
    public void departureConfirmation() {
        Long delivery_id = Long.parseLong(getPara("deliveryid"));
        System.out.println(delivery_id);
        DeliveryOrder deliveryOrder = DeliveryOrder.dao.findById(delivery_id);
        deliveryOrder.set("status", "配送在途");
        deliveryOrder.update();

        Map<String, Object> map = new HashMap<String, Object>();
        DeliveryOrderMilestone deliveryOrderMilestone = new DeliveryOrderMilestone();
        deliveryOrderMilestone.set("status", "已发车");
        String name = (String) currentUser.getPrincipal();
        List<UserLogin> users = UserLogin.dao
                .find("select * from user_login where user_name='" + name + "'");
        deliveryOrderMilestone.set("create_by", users.get(0).get("id"));
        deliveryOrderMilestone.set("location", "");
        java.util.Date utilDate = new java.util.Date();
        java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
        deliveryOrderMilestone.set("create_stamp", sqlDate);
        deliveryOrderMilestone.set("order_id", getPara("order_id"));
        deliveryOrderMilestone.save();
        map.put("transferOrderMilestone", deliveryOrderMilestone);
        UserLogin userLogin = UserLogin.dao.findById(deliveryOrderMilestone
                .get("create_by"));
        String username = userLogin.get("user_name");
        map.put("username", username);
        renderJson(map);
    }
}
