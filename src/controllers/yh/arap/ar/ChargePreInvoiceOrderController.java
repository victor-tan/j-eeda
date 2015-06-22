package controllers.yh.arap.ar;

import interceptor.SetAttrLoginUserInterceptor;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Account;
import models.ArapChargeInvoiceApplication;
import models.ArapChargeInvoiceApplicationItem;
import models.ArapChargeOrder;
import models.Party;
import models.UserLogin;
import models.yh.profile.Contact;

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

import controllers.yh.util.OrderNoGenerator;
import controllers.yh.util.OrderNoUtil;
import controllers.yh.util.PermissionConstant;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class ChargePreInvoiceOrderController extends Controller {
    private Logger logger = Logger.getLogger(ChargePreInvoiceOrderController.class);
	Subject currentUser = SecurityUtils.getSubject();
	@RequiresPermissions(value = {PermissionConstant.PERMSSION_CPIO_LIST})
    public void index() {
    	   render("/yh/arap/ChargePreInvoiceOrder/ChargePreInvoiceOrderList.html");
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
        render("/yh/arap/ChargeAcceptOrder/ChargeCheckOrderEdit.html");
    }

    // 应付条目列表
    @RequiresPermissions(value = {PermissionConstant.PERMSSION_CPIO_LIST})
    public void list() {
        String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
        }

        String customer = getPara("customer");
        String beginTime = getPara("beginTime");
        String endTime = getPara("endTime");
        String status = getPara("status");
        String orderNo = getPara("orderNo");
        
        String sqlTotal = "";
        String sql = "select aaia.*,"
        		+ " c.abbr cname,"
        		+ " ul.user_name as create_by,"
        		+ " ul2.user_name audit_by,"
        		+ " ul3.user_name approval_by ,"
        		+ " (select case "
        		+ "	when aci.status = '已收款确认' then aci.status "
        		+ " when aci.status ='已审批' then '已记录'"
				+ "	when aci.status != '已收款确认' and aci.status != '' then '开票记录中'"
				+ " when aciao.status = '已审批' then '已开票'"
				+ "	else aciao.status"
				+ "	end "
				+ "	from arap_charge_invoice_application_order aciao"
				+ "	left join arap_charge_invoice aci on aciao.invoice_order_id = aci.id"
				+ "	where aciao.id = aaia.id) as order_status "
        		+ " from arap_charge_invoice_application_order aaia "
				+ " left join party p on p.id = aaia.payee_id"
				+ " left join contact c on c.id = p.contact_id"
				+ " left join user_login ul on ul.id = aaia.create_by"
				+ " left join user_login ul2 on ul2.id = aaia.audit_by"
				+ " left join user_login ul3 on ul3.id = aaia.approver_by ";
         String condition = "";
        if(customer == null && beginTime == null && endTime == null 
        		&& status == null && orderNo ==null ){
        	condition = " ";
        }else{
        	if (beginTime == null || "".equals(beginTime)) {
				beginTime = "1-1-1";
			}
			if (endTime == null || "".equals(endTime)) {
				endTime = "9999-12-31";
			}
			
			condition = " where ifnull(c.abbr,'') like '%" + customer + "%' "
					+ " and ifnull(aaia.order_no,'') like '%" + orderNo + "%' "
					+ " and aaia.create_stamp between '" + beginTime + "' and '" + endTime + "' ";
			if(status != null && !"".equals(status)){
				condition = condition + " and aaia.status = '" + status +"' ";
			}
        }
        
       sqlTotal = "select count(1) total from arap_charge_invoice_application_order aaia "
				+ " left join party p on p.id = aaia.payee_id"
				+ " left join contact c on c.id = p.contact_id"
				+ " left join user_login ul on ul.id = aaia.create_by"
				+ " left join user_login ul2 on ul2.id = aaia.audit_by"
				+ " left join user_login ul3 on ul3.id = aaia.approver_by ";
        
       
        

        Record rec = Db.findFirst(sqlTotal + condition);
        logger.debug("total records:" + rec.getLong("total"));
        
        List<Record> BillingOrders = Db.find(sql + condition + " order by aaia.create_stamp desc "+ sLimit);

        Map BillingOrderListMap = new HashMap();
        BillingOrderListMap.put("sEcho", pageIndex);
        BillingOrderListMap.put("iTotalRecords", rec.getLong("total"));
        BillingOrderListMap.put("iTotalDisplayRecords", rec.getLong("total"));

        BillingOrderListMap.put("aaData", BillingOrders);

        renderJson(BillingOrderListMap);
    }
    @RequiresPermissions(value = {PermissionConstant.PERMSSION_CPIO_CREATE})
	public void create() {
		String ids = getPara("ids");

		setAttr("chargeCheckOrderIds", ids);
		if(ids != null && !"".equals(ids)){
			String[] idArray = ids.split(",");
			logger.debug(String.valueOf(idArray.length));
			Double totalAmount = 0.0;
			for(int i=0;i<idArray.length;i++){
				ArapChargeOrder rOrder = ArapChargeOrder.dao.findById(idArray[i]);
				Double chargeTotalAmount = rOrder.getDouble("charge_amount")==null?0.0:rOrder.getDouble("charge_amount");
				totalAmount = totalAmount + chargeTotalAmount;
			}
			setAttr("totalAmount", totalAmount);
			
			ArapChargeOrder arapChargeOrder = ArapChargeOrder.dao.findById(idArray[0]);
			Long customerId = arapChargeOrder.get("payee_id");
			if (!"".equals(customerId) && customerId != null) {
				Party party = Party.dao.findById(customerId);
				setAttr("party", party);
				Contact contact = Contact.dao.findById(party.get("contact_id"));
				setAttr("customer", contact);
				setAttr("type", "CUSTOMER");
				setAttr("classify", "");
			}
		}

		setAttr("saveOK", false);
		String name = (String) currentUser.getPrincipal();
		List<UserLogin> users = UserLogin.dao
				.find("select * from user_login where user_name='" + name + "'");
		setAttr("create_by", users.get(0).get("id"));
		
		UserLogin userLogin = UserLogin.dao.findById(users.get(0).get("id"));
		setAttr("userLogin", userLogin);

		setAttr("status", "new");
			render("/yh/arap/ChargePreInvoiceOrder/ChargePreInvoiceOrderEdit.html");
	}
    @RequiresPermissions(value = {PermissionConstant.PERMSSION_CPIO_CREATE,PermissionConstant.PERMSSION_CPIO_UPDATE},logical=Logical.OR)
	public void save() {
		ArapChargeInvoiceApplication arapAuditInvoiceApplication = null;
		String chargePreInvoiceOrderId = getPara("chargePreInvoiceOrderId");
		String paymentMethod = getPara("paymentMethod");
		if (!"".equals(chargePreInvoiceOrderId) && chargePreInvoiceOrderId != null) {
			arapAuditInvoiceApplication = ArapChargeInvoiceApplication.dao.findById(chargePreInvoiceOrderId);
            //arapAuditInvoiceApplication.set("status", "new");
			arapAuditInvoiceApplication.set("create_by", getPara("create_by"));
			arapAuditInvoiceApplication.set("create_stamp", new Date());
			arapAuditInvoiceApplication.set("remark", getPara("remark"));
			arapAuditInvoiceApplication.set("last_modified_by", getPara("create_by"));
			arapAuditInvoiceApplication.set("last_modified_stamp", new Date());
			arapAuditInvoiceApplication.set("payment_method", paymentMethod);
			if("transfers".equals(paymentMethod)){
				if(getPara("accountTypeSelect") != null && !"".equals(getPara("accountTypeSelect"))){
					arapAuditInvoiceApplication.set("account_id", getPara("accountTypeSelect"));
				}
			}else{
				arapAuditInvoiceApplication.set("account_id", null);				
			}
			arapAuditInvoiceApplication.update();
		} else {
			arapAuditInvoiceApplication = new ArapChargeInvoiceApplication();
			arapAuditInvoiceApplication.set("order_no", OrderNoGenerator.getNextOrderNo("YSSQ"));
			arapAuditInvoiceApplication.set("status", "新建");
			arapAuditInvoiceApplication.set("payee_id", getPara("customer_id"));
			arapAuditInvoiceApplication.set("create_by", getPara("create_by"));
			arapAuditInvoiceApplication.set("create_stamp", new Date());
			arapAuditInvoiceApplication.set("total_amount", getPara("total_amount"));
			arapAuditInvoiceApplication.set("remark", getPara("remark"));
			arapAuditInvoiceApplication.set("payment_method", getPara("paymentMethod"));
			if("transfers".equals(paymentMethod)){
				if(getPara("accountTypeSelect") != null && !"".equals(getPara("accountTypeSelect"))){
					arapAuditInvoiceApplication.set("account_id", getPara("accountTypeSelect"));
				}
			}else{
				arapAuditInvoiceApplication.set("account_id", null);				
			}
			arapAuditInvoiceApplication.save();

			String chargeCheckOrderIds = getPara("chargeCheckOrderIds");
			String[] chargeCheckOrderIdsArr = chargeCheckOrderIds.split(",");
			for (int i = 0; i < chargeCheckOrderIdsArr.length; i++) {
				ArapChargeInvoiceApplicationItem arapAuditInvoiceApplicationItem = new ArapChargeInvoiceApplicationItem();
				arapAuditInvoiceApplicationItem.set("invoice_application_id", arapAuditInvoiceApplication.get("id"));
				arapAuditInvoiceApplicationItem.set("charge_order_id", chargeCheckOrderIdsArr[i]);
				arapAuditInvoiceApplicationItem.save();
				
				ArapChargeOrder arapAuditOrder = ArapChargeOrder.dao.findById(chargeCheckOrderIdsArr[i]);
				arapAuditOrder.set("status", "开票申请中");
				arapAuditOrder.update();
			}
		}
		renderJson(arapAuditInvoiceApplication);;
	}
	
	// 审核
    @RequiresPermissions(value = {PermissionConstant.PERMSSION_CPIO_APPROVAL})
	public void auditChargePreInvoiceOrder(){
		String chargePreInvoiceOrderId = getPara("chargePreInvoiceOrderId");
		ArapChargeInvoiceApplication arapAuditOrder = null;
		Map map =new HashMap();
		if(chargePreInvoiceOrderId != null && !"".equals(chargePreInvoiceOrderId)){
			arapAuditOrder = ArapChargeInvoiceApplication.dao.findById(chargePreInvoiceOrderId);
			arapAuditOrder.set("status", "已审核");
            String name = (String) currentUser.getPrincipal();
			List<UserLogin> users = UserLogin.dao.find("select * from user_login where user_name='" + name + "'");
			arapAuditOrder.set("audit_by", users.get(0).get("id"));
			arapAuditOrder.set("audit_stamp", new Date());
			arapAuditOrder.update();
			map.put("arapAuditOrder", arapAuditOrder);
			map.put("user", users.get(0));
		}
		
        renderJson(map);
	}
	
	// 审批
    @RequiresPermissions(value = {PermissionConstant.PERMSSION_CPIO_CONFIRMATION})
	public void approvalChargePreInvoiceOrder(){
		String chargePreInvoiceOrderId = getPara("chargePreInvoiceOrderId");
		Map map =new HashMap();
		if(chargePreInvoiceOrderId != null && !"".equals(chargePreInvoiceOrderId)){
			ArapChargeInvoiceApplication arapAuditOrder = ArapChargeInvoiceApplication.dao.findById(chargePreInvoiceOrderId);
			arapAuditOrder.set("status", "已审批");
            String name = (String) currentUser.getPrincipal();
			List<UserLogin> users = UserLogin.dao.find("select * from user_login where user_name='" + name + "'");
			arapAuditOrder.set("approver_by", users.get(0).get("id"));
			arapAuditOrder.set("approval_stamp", new Date());
			arapAuditOrder.update();
			map.put("arapAuditOrder", arapAuditOrder);
			map.put("user", users.get(0));
		}
		renderJson(map);
	}
    @RequiresPermissions(value = {PermissionConstant.PERMSSION_CPIO_UPDATE})
	public void edit() throws ParseException {
		String id = getPara("id");
		ArapChargeInvoiceApplication arapAuditInvoiceApplication = ArapChargeInvoiceApplication.dao.findById(id);
		Long customerId = arapAuditInvoiceApplication.getLong("payee_id");
		if (!"".equals(customerId) && customerId != null) {
			Party party = Party.dao.findById(customerId);
			setAttr("party", party);
			Contact contact = Contact.dao.findById(party.get("contact_id")
					.toString());
			setAttr("customer", contact);
		}
		String chargeCheckOrderIds = "";
		List<ArapChargeInvoiceApplicationItem> arapAuditInvoiceApplicationItems = ArapChargeInvoiceApplicationItem.dao.find("select * from arap_charge_invoice_application_item where invoice_application_id = ?", id);
		for(ArapChargeInvoiceApplicationItem arapAuditInvoiceApplicationItem : arapAuditInvoiceApplicationItems){
			chargeCheckOrderIds += arapAuditInvoiceApplicationItem.get("charge_order_id") + ",";
		}
		chargeCheckOrderIds = chargeCheckOrderIds.substring(0, chargeCheckOrderIds.length() - 1);
		setAttr("chargeCheckOrderIds", chargeCheckOrderIds);
		UserLogin userLogin = UserLogin.dao.findById(arapAuditInvoiceApplication.get("create_by"));
		setAttr("userLogin", userLogin);
		if(!"".equals(arapAuditInvoiceApplication.get("audit_by")) && arapAuditInvoiceApplication.get("audit_by") != null){
			UserLogin auditName = UserLogin.dao.findById(arapAuditInvoiceApplication.get("audit_by"));
			setAttr("auditName", auditName);
			setAttr("auditData", arapAuditInvoiceApplication.get("audit_stamp"));
		}
		if(!"".equals(arapAuditInvoiceApplication.get("approver_by")) && arapAuditInvoiceApplication.get("approver_by") != null){
			UserLogin approvalName = UserLogin.dao.findById(arapAuditInvoiceApplication.get("approver_by"));
			setAttr("approvalName", approvalName);
			setAttr("approvalData", arapAuditInvoiceApplication.get("approval_stamp"));
		}
		setAttr("arapAuditInvoiceApplication", arapAuditInvoiceApplication);
			render("/yh/arap/ChargePreInvoiceOrder/ChargePreInvoiceOrderEdit.html");
	}
    
	@RequiresPermissions(value = {PermissionConstant.PERMSSION_CPIO_CREATE})
	public void chargeCheckOrderList() {
		String returnOrderIds = getPara("returnOrderIds");
		String sLimit = "";
		String pageIndex = getPara("sEcho");
		if (getPara("iDisplayStart") != null
				&& getPara("iDisplayLength") != null) {
			sLimit = " LIMIT " + getPara("iDisplayStart") + ", "
					+ getPara("iDisplayLength");
		}
		Map orderMap = new HashMap();
		
		
		String customer = getPara("customer");
		String beginTime = getPara("beginTime");
		String endTime = getPara("endTime");
		String office = getPara("office");
		String status = getPara("status");
		String orderNo = getPara("orderNo");
		String sql ="select distinct aao.*, usl.user_name as creator_name,c.abbr cname"
					+ " from arap_charge_order aao "
					+ " left join party p on p.id = aao.payee_id "
					+ " left join contact c on c.id = p.contact_id"
					+ " left join user_login usl on usl.id=aao.create_by"
					+ " where aao.status = '已确认' ";
		String sqlTotal ="";
		String condition = "";
		//TODO 网点与对账单状态未做
		
		sqlTotal = "select count(1) total from arap_charge_order where status = '已确认'";
		if(customer == null && beginTime == null && endTime == null 
				&& office == null && status == null && orderNo == null ){
			condition = " order by aao.create_stamp desc ";
		}else{
			if (beginTime == null || "".equals(beginTime)) {
				beginTime = "1-1-1";
			}
			if (endTime == null || "".equals(endTime)) {
				endTime = "9999-12-31";
			}
			
			condition = " and ifnull(c.abbr,'') like '%" + customer + "%' "
					+ " and ifnull(aao.order_no,'') like '%" + orderNo + "%' "
					+ " and aao.create_stamp between '" + beginTime + "' and '" + endTime +"' "
					+ " order by aao.create_stamp desc ";
			
		}

		sqlTotal = "select count(1) total from (" + sql + condition + ") as A";
		Record rec = Db.findFirst(sqlTotal);
		logger.debug("total records:" + rec.getLong("total"));
		List<Record> orders = Db.find(sql + condition + sLimit);
		
		orderMap.put("sEcho", pageIndex);
		orderMap.put("iTotalRecords", rec.getLong("total"));
		orderMap.put("iTotalDisplayRecords", rec.getLong("total"));
		orderMap.put("aaData", orders);

		orderMap.put("sEcho", pageIndex);
		orderMap.put("iTotalRecords", rec.getLong("total"));
		orderMap.put("iTotalDisplayRecords", rec.getLong("total"));
		orderMap.put("aaData", orders);

		renderJson(orderMap);
	}
	
	@RequiresPermissions(value = {PermissionConstant.PERMSSION_CPIO_CREATE})
	public void chargeOrderListByIds() {
		String chargeCheckOrderIds = getPara("chargeCheckOrderIds");
		String sLimit = "";
		if(chargeCheckOrderIds == null || "".equals(chargeCheckOrderIds)){
			chargeCheckOrderIds = "-1";
		}
		String pageIndex = getPara("sEcho");
		if (getPara("iDisplayStart") != null
				&& getPara("iDisplayLength") != null) {
			sLimit = " LIMIT " + getPara("iDisplayStart") + ", "
					+ getPara("iDisplayLength");
		}
		Map orderMap = new HashMap();
		// 获取总条数
		String totalWhere = "";
		String sql = "select count(1) total from arap_charge_order where id in("+chargeCheckOrderIds+")";
		Record rec = Db.findFirst(sql + totalWhere);
		logger.debug("total records:" + rec.getLong("total"));
		
		// 获取当前页的数据
		List<Record> orders = Db
				.find("select distinct aao.*, usl.user_name as creator_name,c.abbr cname"
						+ " from arap_charge_order aao "
						+ " left join party p on p.id = aao.payee_id "
						+ " left join contact c on c.id = p.contact_id"
						+ " left join user_login usl on usl.id=aao.create_by"
						+ " where aao.id in("+chargeCheckOrderIds+") order by aao.create_stamp desc " + sLimit);
		
		orderMap.put("sEcho", pageIndex);
		orderMap.put("iTotalRecords", rec.getLong("total"));
		orderMap.put("iTotalDisplayRecords", rec.getLong("total"));
		orderMap.put("aaData", orders);
		
		orderMap.put("sEcho", pageIndex);
		orderMap.put("iTotalRecords", rec.getLong("total"));
		orderMap.put("iTotalDisplayRecords", rec.getLong("total"));
		orderMap.put("aaData", orders);
		
		renderJson(orderMap);
	}
	
	@RequiresPermissions(value = {PermissionConstant.PERMSSION_CPIO_CREATE})
	public void chargeCheckOrderList2() {
		String sLimit = "";
		String pageIndex = getPara("sEcho");
		if (getPara("iDisplayStart") != null
				&& getPara("iDisplayLength") != null) {
			sLimit = " LIMIT " + getPara("iDisplayStart") + ", "
					+ getPara("iDisplayLength");
		}

		List<Record> BillingOrders = null;

		Map BillingOrderListMap = new HashMap();
		BillingOrderListMap.put("sEcho", pageIndex);

		BillingOrderListMap.put("aaData", BillingOrders);

		renderJson(BillingOrderListMap);
	}
	
	public void searchAllAccount(){
		List<Account> accounts = Account.dao.find("select * from fin_account where type != 'PAY'");
		renderJson(accounts);
	}
}
