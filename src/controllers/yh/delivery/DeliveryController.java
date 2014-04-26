package controllers.yh.delivery;import java.util.List;import java.util.HashMap;import java.util.Map;import org.apache.shiro.SecurityUtils;import org.apache.shiro.subject.Subject;import models.UserLogin;import models.yh.profile.Contact;import com.jfinal.core.Controller;import com.jfinal.log.Logger;import com.jfinal.plugin.activerecord.Db;import com.jfinal.plugin.activerecord.Record;public class DeliveryController extends Controller {	private Logger logger = Logger.getLogger(DeliveryController.class);	// in config route已经将路径默认设置为/yh	// me.add("/yh", controllers.yh.AppController.class, "/yh");	Subject currentUser = SecurityUtils.getSubject();	private boolean isAuthenticated() {		if (!currentUser.isAuthenticated()) {			redirect("/yh/login");			return false;		}		setAttr("userId", currentUser.getPrincipal());		return true;	}	public void index() {		if (!isAuthenticated())			return;						render("/yh/delivery/deliveryOrderList.html");	}		public void add() {        setAttr("saveOK", false);        render("/yh/delivery/deliveryOrderSearchTransfer.html");    }	 // 供应商列表,列出最近使用的5个客户    public void selectSp() {        List<Contact> contactjson = Contact.dao.find("select * from contact c  where id in (select contact_id from party where party_type='SERVICE_PROVIDER' order by last_update_date desc limit 0,5)");        renderJson(contactjson);    }    public void creat(){                render("/yh/delivery/deliveryOrderEdit.html");    }        public void deliveryOrderList(){                String sLimit = "";        String pageIndex = getPara("sEcho");        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");        }                //获取总条数        String totalWhere="";        String sql = "select count(1) total from user_login ";        Record rec = Db.findFirst(sql + totalWhere);        logger.debug("total records:" + rec.getLong("total"));                //获取当前页的数据        List<Record> orders = Db.find("select * from user_login");        Map orderMap = new HashMap();        orderMap.put("sEcho", pageIndex);        orderMap.put("iTotalRecords", rec.getLong("total"));        orderMap.put("iTotalDisplayRecords", rec.getLong("total"));        orderMap.put("aaData", orders);                renderJson(orderMap);    }        public void SearchTransfer(){                        String sLimit = "";            String pageIndex = getPara("sEcho");            if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {                sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");            }                        //获取总条数            String totalWhere="";            String sql = "select count(1) total from user_login ";            Record rec = Db.findFirst(sql + totalWhere);            logger.debug("total records:" + rec.getLong("total"));                        //获取当前页的数据            List<Record> orders = Db.find("select * from user_login");            Map orderMap = new HashMap();            orderMap.put("sEcho", pageIndex);            orderMap.put("iTotalRecords", rec.getLong("total"));            orderMap.put("iTotalDisplayRecords", rec.getLong("total"));                orderMap.put("aaData", orders);                        renderJson(orderMap);        }}