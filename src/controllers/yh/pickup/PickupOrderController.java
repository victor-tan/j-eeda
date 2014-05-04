package controllers.yh.pickup;import java.util.HashMap;import java.util.List;import java.util.Map;import models.yh.profile.Contact;import org.apache.shiro.SecurityUtils;import org.apache.shiro.subject.Subject;import com.jfinal.core.Controller;import com.jfinal.log.Logger;import com.jfinal.plugin.activerecord.Db;import com.jfinal.plugin.activerecord.Record;public class PickupOrderController extends Controller {    private Logger logger = Logger.getLogger(PickupOrderController.class);    // in config route已经将路径默认设置为/yh    // me.add("/yh", controllers.yh.AppController.class, "/yh");    Subject currentUser = SecurityUtils.getSubject();    private boolean isAuthenticated() {        if (!currentUser.isAuthenticated()) {            redirect("/yh/login");            return false;        }        setAttr("userId", currentUser.getPrincipal());        return true;    }    public void index() {        if (!isAuthenticated())            return;        render("/yh/pickup/pickupOrderList.html");    }    public void add() {        setAttr("saveOK", false);        render("/yh/pickup/pickupOrderSearchTransfer.html");    }    // 配送单客户    public void selectCustomer() {        List<Contact> customer = Contact.dao                .find("select * from contact where id in(select contact_id from party where id in(SELECT customer_id  FROM TRANSFER_ORDER group by customer_id)) and id='1'");        renderJson(customer);    }    // 供应商列表,列出最近使用的5个客户    public void selectSp() {        List<Contact> contactjson = Contact.dao                .find("select * from contact c  where id in (select contact_id from party where party_type='SERVICE_PROVIDER' order by last_update_date desc limit 0,5)");        renderJson(contactjson);    }    public void creat() {        String id = getPara();        System.out.println(id);        List<Contact> customer = Contact.dao                .find("select * from contact where id in(select contact_id from party where id in(SELECT customer_id  FROM TRANSFER_ORDER where id="                        + id + " group by customer_id))");        System.out.println(customer);        setAttr("customer", customer);        render("/yh/pickup/editPickupOrder.html");    }    // 创建 结构 行为    public void deliveryOrderList() {        String sLimit = "";        String pageIndex = getPara("sEcho");        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");        }        // 获取总条数        String totalWhere = "";        String sql = "select count(1) total from user_login ";        Record rec = Db.findFirst(sql + totalWhere);        logger.debug("total records:" + rec.getLong("total"));        // 获取当前页的数据        List<Record> orders = Db.find("select * from user_login");        Map orderMap = new HashMap();        orderMap.put("sEcho", pageIndex);        orderMap.put("iTotalRecords", rec.getLong("total"));        orderMap.put("iTotalDisplayRecords", rec.getLong("total"));        orderMap.put("aaData", orders);        renderJson(orderMap);    }    public void SearchTransfer() {        String sLimit = "";        String pageIndex = getPara("sEcho");        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");        }        String sqlTotal = "select count(1) total from transfer_order";        Record rec = Db.findFirst(sqlTotal);        logger.debug("total records:" + rec.getLong("total"));        String sql = "select * from transfer_order where arrival_mode='入中转仓'";        List<Record> transferOrders = Db.find(sql);        Map transferOrderListMap = new HashMap();        transferOrderListMap.put("sEcho", pageIndex);        transferOrderListMap.put("iTotalRecords", rec.getLong("total"));        transferOrderListMap.put("iTotalDisplayRecords", rec.getLong("total"));        transferOrderListMap.put("aaData", transferOrders);        renderJson(transferOrderListMap);    }}