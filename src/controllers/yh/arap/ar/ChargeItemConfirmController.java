package controllers.yh.arap.ar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Party;
import models.yh.profile.Contact;

import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import controllers.yh.LoginUserController;

public class ChargeItemConfirmController extends Controller {
    private Logger logger = Logger.getLogger(ChargeItemConfirmController.class);

    public void index() {
    	if(LoginUserController.isAuthenticated(this))
    	    render("/yh/arap/ChargeItemConfirm/ChargeItemConfirmList.html");
    }


    public void confirm() {
        String ids = getPara("ids");
        String[] idArray = ids.split(",");
        logger.debug(String.valueOf(idArray.length));

        String customerId = getPara("customerId");
        Party party = Party.dao.findById(customerId);

        Contact contact = Contact.dao.findById(party.get("contact_id").toString());
        setAttr("customer", contact);
    	setAttr("type", "CUSTOMER");
    	setAttr("classify", "receivable");
    	if(LoginUserController.isAuthenticated(this))
        render("/yh/arap/ChargeAcceptOrder/ChargeCheckOrderEdit.html");
    }

    // 应付条目列表
    public void list() {
        String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
        }

        String sqlTotal = "select count(1) total from return_order";
        Record rec = Db.findFirst(sqlTotal);
        logger.debug("total records:" + rec.getLong("total"));

        String sql = "select distinct ror.*, usl.user_name as creator_name, ifnull(tor.order_no,(select group_concat(tor.order_no separator '\r\n') from delivery_order dvr left join delivery_order_item doi on doi.delivery_id = dvr.id  left join transfer_order tor on tor.id = doi.transfer_order_id where dvr.id = ror.delivery_order_id)) transfer_order_no, dvr.order_no as delivery_order_no, ifnull(c.abbr,c2.abbr) cname,"
					+ " ifnull(tor.customer_order_no,tor2.customer_order_no) customer_order_no,ifnull(tor.route_from,tor2.route_from),ifnull(tor.route_to,dvr.route_to),"
					+ " ifnull(tofi.amount,(select sum(tofi.amount) from delivery_order dvr left join delivery_order_item doi on doi.delivery_id = dvr.id  left join transfer_order tor on tor.id = doi.transfer_order_id left join transfer_order_fin_item tofi on tor.id = tofi.order_id where dvr.id = ror.delivery_order_id)) contract_amount"
					+ " ,ifnull(dofi.amount,(select sum(dofi.amount) from delivery_order dvr left join delivery_order_item doi on doi.delivery_id = dvr.id  left join transfer_order tor on tor.id = doi.transfer_order_id left join depart_transfer dt on dt.order_id = tor.id left join depart_order dor on dor.id = dt.pickup_id left join depart_order_fin_item dofi on dofi.pickup_order_id = dor.id left join fin_item fi on fi.id = dofi.fin_item_id and fi.type='应收' and fi.name='提货费' where dvr.id = ror.delivery_order_id)) pickup_amount"
					+ " from return_order ror"
					+ " left join transfer_order tor on tor.id = ror.transfer_order_id left join party p on p.id = tor.customer_id left join contact c on c.id = p.contact_id "  
					+ " left join depart_transfer dt on dt.order_id = tor.id"
					+ " left join delivery_order dvr on ror.delivery_order_id = dvr.id left join delivery_order_item doi on doi.delivery_id = dvr.id  "
					+ " left join transfer_order tor2 on tor2.id = doi.transfer_order_id left join party p2 on p2.id = tor2.customer_id left join contact c2 on c2.id = p2.contact_id " 
					+ " left join transfer_order_fin_item tofi on tor.id = tofi.order_id left join depart_order dor on dor.id = dt.pickup_id left join depart_order_fin_item dofi on dofi.pickup_order_id = dor.id left join fin_item fi on fi.id = dofi.fin_item_id and fi.type='应收' and fi.name='提货费'"
					+ " left join transfer_order_fin_item tofi2 on tor.id = tofi2.order_id"
					+ " left join user_login  usl on usl.id=ror.creator order by ror.create_date desc " + sLimit;

        logger.debug("sql:" + sql);
        List<Record> BillingOrders = Db.find(sql);

        Map BillingOrderListMap = new HashMap();
        BillingOrderListMap.put("sEcho", pageIndex);
        BillingOrderListMap.put("iTotalRecords", rec.getLong("total"));
        BillingOrderListMap.put("iTotalDisplayRecords", rec.getLong("total"));

        BillingOrderListMap.put("aaData", BillingOrders);

        renderJson(BillingOrderListMap);
    }
    
}
