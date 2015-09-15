package controllers.yh.arap.ar;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Account;
import models.ArapAccountAuditLog;
import models.ArapCostInvoiceApplication;
import models.ArapCostOrder;
import models.ArapCostPayConfirmOrder;
import models.ArapCostPayConfirmOrderDtail;
import models.ArapCostPayConfirmOrderDtailLog;
import models.ArapCostPayConfirmOrderLog;
import models.CostApplicationOrderRel;
import models.DepartOrder;
import models.InsuranceOrder;
import models.yh.arap.ArapMiscCostOrder;
import models.yh.delivery.DeliveryOrder;
import models.yh.profile.Contact;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import controllers.yh.LoginUserController;
import controllers.yh.util.OrderNoGenerator;
import controllers.yh.util.PermissionConstant;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class ChargeConfirmController extends Controller {
    private Logger logger = Logger.getLogger(ChargeConfirmController.class);
    Subject currentUser = SecurityUtils.getSubject();
    @RequiresPermissions(value = {PermissionConstant.PERMSSION_CPO_CONFIRMATION})
    public void index() {
    	render("/yh/arap/CostConfirm/CostConfirmList.html");
    }
    
   	public void edit() {
   		String id = getPara("id");
   		String order_type = "";
   		setAttr("confirmId", id);
   		
   		List<ArapCostPayConfirmOrderDtail> list = ArapCostPayConfirmOrderDtail.dao.find("select * from arap_cost_pay_confirm_order_detail where order_id in(?)",id);
   		for(ArapCostPayConfirmOrderDtail a: list){
   			if(a.getLong("application_order_id")!=null){
   				order_type = "申请单";
   				setAttr("order_type", "申请单");
   			}else if(a.getLong("misc_cost_order_id")!=null){
   				order_type = "成本单";
   				setAttr("order_type", "成本单");
   			}
   		}
   		//setAttr("order_type", order_type);
   		ArapCostPayConfirmOrder arapCostPayConfirmOrder = ArapCostPayConfirmOrder.dao.findById(id);
   		setAttr("arapCostPayConfirmOrder", arapCostPayConfirmOrder);
   		
   		if(arapCostPayConfirmOrder.getLong("sp_id")!= null){
	   		String sql1 = "select * from  party p LEFT JOIN office o ON o.id = p.office_id where p.id = '"+arapCostPayConfirmOrder.getLong("sp_id")+"'";
	   		Record re1 = Db.findFirst(sql1);
	   		setAttr("abbr", re1.getStr("abbr"));
   		}
   		String sql = "";
   		if(order_type.equals("成本单")){
   			sql = "SELECT "
   	   				+ " (SELECT group_concat(cast(misc_cost_order_id as char) SEPARATOR ',' ) "
   	   				+ "FROM arap_cost_pay_confirm_order_detail where order_id = acp.id ) ids "
   	   				+ "FROM arap_cost_pay_confirm_order acp WHERE acp.id = '"+id+"'";
   		}else if(order_type.equals("申请单")){
   			sql = "SELECT "
   	   				+ " (SELECT group_concat(cast(application_order_id as char) SEPARATOR ',' ) "
   	   				+ "FROM arap_cost_pay_confirm_order_detail where order_id = acp.id ) ids "
   	   				+ "FROM arap_cost_pay_confirm_order acp WHERE acp.id = '"+id+"'";
   		}
   		
		Record re = Db.findFirst(sql);
		setAttr("orderIds", re.get("ids"));
		
		
		//获取已付款金额
   		String sql2 = "select sum(acp.amount) total from arap_cost_pay_confirm_order_log acp "
				+ "  where acp.order_id = '"+id+"'";
		Record re2 = Db.findFirst(sql2);
		setAttr("total_pay", re2.get("total"));

   		render("/yh/arap/CostConfirm/CostConfrimAdd.html");
   	}
   	
   	
	@SuppressWarnings("null")
	@Before(Tx.class)
	public void save() {
   		String pay_type = getPara("pay_type");
   		String pay_bank = getPara("pay_bank");
   		String pay_account_no = getPara("pay_account_no");
   		String pay_amount = getPara("pay_amount");
   		String nopay_amount = getPara("nopay_amount");
		
		ArapCostPayConfirmOrder arapCostPayConfirmOrder = null;
		Record re = null;
   		String confirmId = getPara("confirmId");
   		String orderIds = getPara("orderIds");
   		
   		String sp_filter = getPara("sp_filter");       //供应商
   		String payee_unit = getPara("payee_unit");    //收款单位
   		String account_name = getPara("account_name");//账户人名
   		String invoice_type = getPara("invoice_type");//开票类型
   		String payee_name = getPara("payee_name");    //收款人
   		String deposit_bank = getPara("deposit_bank");//开户行
   		String billing_unit = getPara("billing_unit"); //开票单位
   		String bank_no = getPara("bank_no");   
   		String order_type = getPara("order_type"); //单据类型
   		
   		if(confirmId != ""&& confirmId != null){
			//更新主表
			arapCostPayConfirmOrder = ArapCostPayConfirmOrder.dao.findById(confirmId);
			arapCostPayConfirmOrder.set("last_updator", LoginUserController.getLoginUserId(this));
			arapCostPayConfirmOrder.set("last_update_date", new Date()).update();				
		} else {
			Contact contact = Contact.dao.findFirst("select * from contact where abbr = '"+sp_filter+"'");
			String name = (String) currentUser.getPrincipal();
	        Long userId =  LoginUserController.getLoginUserId(this);
	      //创建主表
			arapCostPayConfirmOrder = new ArapCostPayConfirmOrder();
			arapCostPayConfirmOrder.set("order_no", OrderNoGenerator.getNextOrderNo("YFQR"));
			if(sp_filter!=null&&!sp_filter.equals("")){
				arapCostPayConfirmOrder.set("sp_id",contact.getLong("id"));
			}
			arapCostPayConfirmOrder.set("status", "新建");
			arapCostPayConfirmOrder.set("invoice_type",invoice_type);
			arapCostPayConfirmOrder.set("invoice_company", billing_unit);
			arapCostPayConfirmOrder.set("receive_company", payee_unit);
			arapCostPayConfirmOrder.set("receive_person", payee_name);
			arapCostPayConfirmOrder.set("receive_bank", deposit_bank);
			arapCostPayConfirmOrder.set("receive_bank_person_name", account_name);//账户人名
			arapCostPayConfirmOrder.set("receive_account_no",bank_no);
			arapCostPayConfirmOrder.set("creator",userId);
			arapCostPayConfirmOrder.set("create_date",new Date());
			arapCostPayConfirmOrder.save();		
			
			//新建字表
			String[] idArray = orderIds.split(",");
			for (int i = 0; i < idArray.length; i++) {
				ArapCostPayConfirmOrderDtail arapCostPayConfirmOrderDtail =new ArapCostPayConfirmOrderDtail();
				arapCostPayConfirmOrderDtail.set("order_id", arapCostPayConfirmOrder.getLong("id"));
				if(order_type.equals("成本单")){
					arapCostPayConfirmOrderDtail.set("misc_cost_order_id", idArray[i]);
				}else{
					arapCostPayConfirmOrderDtail.set("application_order_id", idArray[i]);
				}
				arapCostPayConfirmOrderDtail.save();
				
				//更新申请单状态
				if(order_type.equals("成本单")){
					ArapMiscCostOrder arapMiscCostOrder = ArapMiscCostOrder.dao.findById(idArray[i]);
					arapMiscCostOrder.set("status", "付款确认中").update();
				}else{
					ArapCostInvoiceApplication arapCostInvoiceApplication = ArapCostInvoiceApplication.dao.findById(idArray[i]);
					arapCostInvoiceApplication.set("status", "付款确认中").update();
					List<ArapCostOrder> arapcostorderList = ArapCostOrder.dao.find("select id, status from arap_cost_order where application_order_id = ?",idArray[i]);
					for(ArapCostOrder arapCostorder : arapcostorderList){
						arapCostorder.set("status", "付款确认中").update();
			    	}
				}
			}
		}
   		renderJson(arapCostPayConfirmOrder);
   	}
	
	@Before(Tx.class)
	public void saveConfirmLog(){
		String confirmId = getPara("confirmId");
		String pay_type = getPara("pay_type");
   		String pay_bank = getPara("pay_bank");
   		String pay_account_no = getPara("pay_account_no");
   		String pay_amount = getPara("pay_amount");
   		String nopay_amount = getPara("nopay_amount");
   		String total_amount = getPara("total_amount");
   		String orderIds = getPara("orderIds");
   		String order_type = getPara("order_type"); //单据类型
   		String detailJson = getPara("detailJson");
   		String applicationId = "";
   		String value = "";
   		String sql = "";
   		Gson gson = new Gson();
		List<LinkedTreeMap> list = gson.fromJson(detailJson, new TypeToken<List<LinkedTreeMap>>(){}.getType());
		for (Map obj: list) {
			logger.debug(obj.get("id").toString());
			logger.debug(obj.get("value").toString());
			applicationId = obj.get("id").toString();
			value = obj.get("value").toString();
			if(order_type.equals("成本单")){
				sql = "select * from arap_cost_pay_confirm_order_detail where misc_cost_order_id = '"+applicationId+"' and order_id = '"+confirmId+"'";
			}else{
				sql = "select * from arap_cost_pay_confirm_order_detail where application_order_id = '"+applicationId+"' and order_id = '"+confirmId+"'";
			}
			
			ArapCostPayConfirmOrderDtail detail = ArapCostPayConfirmOrderDtail.dao.findFirst(sql);
			ArapCostPayConfirmOrderDtailLog arapCostPayConfirmOrderDtailLog = new ArapCostPayConfirmOrderDtailLog();
			arapCostPayConfirmOrderDtailLog.set("order_id", confirmId);
			arapCostPayConfirmOrderDtailLog.set("detail_id", detail.getLong("id"));
			arapCostPayConfirmOrderDtailLog.set("pay_amount", value);
			arapCostPayConfirmOrderDtailLog.set("create_date", new Date());
			arapCostPayConfirmOrderDtailLog.save();
		}
		
		ArapCostPayConfirmOrderLog arapCostPayConfirmOrderLog = null;
   		Account account = Account.dao.findFirst("select * from fin_account where account_no = '"+pay_account_no+"'");
		//创建付款LOG记录表
		arapCostPayConfirmOrderLog = new ArapCostPayConfirmOrderLog();
		if(account!=null)
		arapCostPayConfirmOrderLog.set("pay_out_bank_id", account.getLong("id"));
		arapCostPayConfirmOrderLog.set("pay_type", pay_type);
		arapCostPayConfirmOrderLog.set("pay_out_bank_name", pay_bank);
		arapCostPayConfirmOrderLog.set("pay_out_account_no", pay_account_no);
		arapCostPayConfirmOrderLog.set("amount", pay_amount);
		arapCostPayConfirmOrderLog.set("create_date", new Date());
		arapCostPayConfirmOrderLog.set("creator",LoginUserController.getLoginUserId(this));
		arapCostPayConfirmOrderLog.set("order_id",confirmId);
		arapCostPayConfirmOrderLog.save();
		
		//获取已付款金额
   		sql = "select sum(acp.amount) total from arap_cost_pay_confirm_order_log acp "
				+ "  where acp.order_id = '"+confirmId+"'";
		Record re = Db.findFirst(sql);
		
		
		ArapCostPayConfirmOrder arapCostPayConfirmOrder = ArapCostPayConfirmOrder.dao.findById(confirmId);
		
		String[] idArray = orderIds.split(",");
		if(order_type.equals("成本单")){
			ArapMiscCostOrder arapMiscCostOrder = ArapMiscCostOrder.dao.findById(applicationId);
			if(re.getDouble("total") == Double.parseDouble(total_amount)){
				arapCostPayConfirmOrder.set("status", "已付款").update();
				arapMiscCostOrder.set("status", "已付款").update();
			}else{
				arapCostPayConfirmOrder.set("status", "部分已付款").update();
				arapMiscCostOrder.set("status", "部分已付款").update();
			}
		}else{
			if(re.getDouble("total") == Double.parseDouble(total_amount)){
				//更新确认表状态
				arapCostPayConfirmOrder.set("status", "已付款").update();
				
				//更新申请单状态
				for (int i = 0; i < idArray.length; i++) {
					ArapCostInvoiceApplication arapCostInvoiceApplication = ArapCostInvoiceApplication.dao.findById(idArray[i]);
					arapCostInvoiceApplication.set("status", "已付款确认").update();
					//List<Record> list= Db.find("SELECT STATUS from arap_cost_order where application_order_id =?",idArray[i]);
					List<ArapCostOrder> arapcostorderList = ArapCostOrder.dao.find("select id,status from arap_cost_order where application_order_id = ?",idArray[i]);
					for(ArapCostOrder arapCostorder : arapcostorderList){
						arapCostorder.set("status", "已付款确认").update();
			    	}
				}
			}else{
				arapCostPayConfirmOrder.set("status", "部分已付款").update();
			}
		}
		
		
		//新建日记账表数据
		 ArapAccountAuditLog auditLog = new ArapAccountAuditLog();
	        auditLog.set("payment_method", pay_type);
	        auditLog.set("payment_type", ArapAccountAuditLog.TYPE_COST);
	        auditLog.set("amount", pay_amount);
	        auditLog.set("creator", LoginUserController.getLoginUserId(this));
	        auditLog.set("create_date", new Date());
	        if(account!=null)
	        	auditLog.set("account_id", account.get("id"));
	        else
	        	auditLog.set("account_id", 4);
	        if(order_type.equals("成本单")){
	        	auditLog.set("misc_order_id", confirmId);
	        	auditLog.set("source_order", "成本单");
	        }
	        else{
	        	auditLog.set("invoice_order_id", confirmId);
	        	auditLog.set("source_order", "应付开票申请单");
	        }
	        auditLog.save();
	        
	        if("cash".equals(pay_type)){
	        	Account cash = Account.dao.findFirst("select * from fin_account where bank_name ='现金'");
	        	cash.set("amount", (cash.getDouble("amount")==null?0.0:cash.getDouble("amount")) - Double.parseDouble(pay_amount)).update();
	        }else{
	        	account.set("amount", (account.getDouble("amount")==null?0.0:account.getDouble("amount")) - Double.parseDouble(pay_amount)).update();
	        }
		
		Map BillingOrderListMap = new HashMap();
   		BillingOrderListMap.put("arapCostPayConfirmOrder", arapCostPayConfirmOrder);
   		BillingOrderListMap.put("re", re);
   		renderJson(BillingOrderListMap);
	}
	
   	
	public void logList() {
        String pageIndex = getPara("sEcho");
		String confirmId = getPara("confirmId");
		String sql = "select acp.*,ul.c_name from arap_cost_pay_confirm_order_log acp "
				+ " left join user_login ul on ul.id = acp.creator "
				+ "  where acp.order_id = '"+confirmId+"'";
		List<Record> re = Db.find(sql);
		
		Map BillingOrderListMap = new HashMap();
        BillingOrderListMap.put("sEcho", pageIndex);
        BillingOrderListMap.put("iTotalRecords", re.size());
        BillingOrderListMap.put("iTotalDisplayRecords", re.size());

        BillingOrderListMap.put("aaData", re);

		renderJson(BillingOrderListMap);
	}
	
	
    
    //@RequiresPermissions(value = {PermissionConstant.PERMSSION_CPIO_CREATE})
	
	public void create() {
		
		String all=getPara("chargeIds2");
        String[] alls=all.split(",");
        String sql = "";
        for(int i=0;i< alls.length;i++){
        	String[] one = alls[i].split(":");
			String id = one[0];
			String order_type = one[1];
			if(order_type.equals("成本单")){
				sql = " SELECT c.abbr FROM arap_misc_cost_order amco "
						+ " LEFT JOIN party p ON p.id = amco.sp_id "
						+ " LEFT JOIN office o ON o.id = p.office_id "
						+ " LEFT JOIN contact c ON c.id = p.contact_id "
						+ " WHERE amco.id = '"+id+"'";
			}else if(order_type.equals("报销单")){
				sql = "SELECT c.abbr,aci.bill_type,aci.billing_unit,aci.payee_unit,aci.payee_name,aci.bank_no,aci.bank_name FROM `arap_cost_invoice_application_order` aci"
						+ " LEFT JOIN cost_application_order_rel cao on cao.application_order_id = aci.id"
						+ " LEFT JOIN arap_cost_order aco on aco.id = cao.cost_order_id "
						+ " LEFT JOIN party p ON p.id = aci.payee_id "
						+ " LEFT JOIN office o ON o.id = p.office_id "
						+ " LEFT JOIN contact c ON c.id = p.contact_id"
						+ " where aci.id = '"+id+"'";
			}else{
				sql = "SELECT c.abbr,aci.bill_type,aci.billing_unit,aci.payee_unit,aci.payee_name,aci.bank_no,aci.bank_name FROM `arap_cost_invoice_application_order` aci"
						+ " LEFT JOIN cost_application_order_rel cao on cao.application_order_id = aci.id"
						+ " LEFT JOIN arap_cost_order aco on aco.id = cao.cost_order_id "
						+ " LEFT JOIN party p ON p.id = aci.payee_id "
						+ " LEFT JOIN office o ON o.id = p.office_id "
						+ " LEFT JOIN contact c ON c.id = p.contact_id"
						+ " where aci.id = '"+id+"'";
			}
        }
		Record record = Db.findFirst(sql);
		setAttr("invoiceApplicationOrder", record);
		setAttr("userName", LoginUserController.getLoginUserName(this));
		render("/yh/arap/ChargeConfirm/ChargeConfirmEdit.html");
		
//		setAttr("orderIds", ids);
//		setAttr("order_type", order_type);
//		String sql = "";
//		if(ids != null && !"".equals(ids)){
//			String[] idArray = ids.split(",");
//			logger.debug(String.valueOf(idArray.length));
//			if(order_type.equals("成本单")){
//				sql = " SELECT c.abbr FROM arap_misc_cost_order amco "
//						+ " LEFT JOIN party p ON p.id = amco.sp_id "
//						+ " LEFT JOIN office o ON o.id = p.office_id "
//						+ " LEFT JOIN contact c ON c.id = p.contact_id "
//						+ " WHERE amco.id = '"+idArray[0]+"'";
//			}else if(order_type.equals("报销单")){
//				sql = "SELECT c.abbr,aci.bill_type,aci.billing_unit,aci.payee_unit,aci.payee_name,aci.bank_no,aci.bank_name FROM `arap_cost_invoice_application_order` aci"
//						+ " LEFT JOIN cost_application_order_rel cao on cao.application_order_id = aci.id"
//						+ " LEFT JOIN arap_cost_order aco on aco.id = cao.cost_order_id "
//						+ " LEFT JOIN party p ON p.id = aci.payee_id "
//						+ " LEFT JOIN office o ON o.id = p.office_id "
//						+ " LEFT JOIN contact c ON c.id = p.contact_id"
//						+ " where aci.id = '"+idArray[0]+"'";
//			}else{
//				sql = "SELECT c.abbr,aci.bill_type,aci.billing_unit,aci.payee_unit,aci.payee_name,aci.bank_no,aci.bank_name FROM `arap_cost_invoice_application_order` aci"
//						+ " LEFT JOIN cost_application_order_rel cao on cao.application_order_id = aci.id"
//						+ " LEFT JOIN arap_cost_order aco on aco.id = cao.cost_order_id "
//						+ " LEFT JOIN party p ON p.id = aci.payee_id "
//						+ " LEFT JOIN office o ON o.id = p.office_id "
//						+ " LEFT JOIN contact c ON c.id = p.contact_id"
//						+ " where aci.id = '"+idArray[0]+"'";
//			}
//			
//			Record record = Db.findFirst(sql);
//			setAttr("invoiceApplicationOrder", record);
//			setAttr("userName", LoginUserController.getLoginUserName(this));
//		}
//		render("/yh/arap/CostConfirm/CostConfrimAdd.html");
	}
	
	
	public void applicationList() {
        
        String pageIndex = getPara("sEcho");
        String orderIds = getPara("orderIds");
        String order_type = getPara("order_type");
       
        String sql = "";
        List<Record> record = null;
        Record re = null;
        if (orderIds != null && !"".equals(orderIds)) {
			String[] idArray = orderIds.split(",");
			if(order_type.equals("成本单")){
				sql = " SELECT amco.id,amco.order_no,amco.create_stamp cost_stamp,amco.total_amount pay_amount,"
						+ " ifnull(amco.total_amount-( select sum(acpcodl.pay_amount) from arap_cost_pay_confirm_order_detail_log acpcodl  "
						+ " LEFT JOIN arap_cost_pay_confirm_order_detail acpcod on acpcod.id = acpcodl.detail_id "
						+ " where acpcod.misc_cost_order_id = amco.id ),amco.total_amount) nopay_amount "
						+ " FROM arap_misc_cost_order amco "
						+ " WHERE amco.id in(" + orderIds + ")";
				
			}else if(order_type.equals("报销单")){
				sql = "";
			}else{
				sql = "SELECT aci.*,"
						+ "( SELECT group_concat( DISTINCT aco.order_no SEPARATOR '<br/>' ) "
						+ " FROM "
						+ " arap_cost_order aco LEFT JOIN cost_application_order_rel caor "
						+ " ON caor.cost_order_id = aco.id"
						+ " WHERE "
						+ " caor.application_order_id = aci.id ) cost_order_no,"
						+ " ( SELECT sum(caor.pay_amount) "
						+ " FROM "
						+ " arap_cost_order aco LEFT JOIN cost_application_order_rel caor "
						+ " ON caor.cost_order_id = aco.id "
						+ " WHERE caor.application_order_id = aci.id ) pay_amount,"
						+ " IFNULL(( SELECT sum(caor.pay_amount) FROM arap_cost_order aco "
						+ " LEFT JOIN cost_application_order_rel caor ON caor.cost_order_id = aco.id "
						+ " WHERE caor.application_order_id = aci.id )- ( select sum(acpcodl.pay_amount) from arap_cost_pay_confirm_order_detail_log acpcodl "
						+ " where acpcodl.order_id = acpcod.order_id and acpcodl.detail_id = acpcod.id ),( SELECT sum(caor.pay_amount) "
						+ " FROM "
						+ " arap_cost_order aco LEFT JOIN cost_application_order_rel caor "
						+ " ON caor.cost_order_id = aco.id "
						+ " WHERE caor.application_order_id = aci.id )) nopay_amount,"
						+ " aco.create_stamp cost_stamp FROM arap_cost_invoice_application_order aci "
		        		+ " LEFT JOIN cost_application_order_rel cao on cao.application_order_id = aci.id "
		        		+ " LEFT JOIN arap_cost_order aco on aco.id = cao.cost_order_id "
		        		+ " LEFT JOIN arap_cost_pay_confirm_order_detail acpcod on acpcod.application_order_id=aci.id "
		        		+ " where aci.id in(" + orderIds + ") GROUP BY aci.id " ;
			}
			record = Db.find(sql);			
		}

        Map BillingOrderListMap = new HashMap();
        BillingOrderListMap.put("sEcho", pageIndex);
        BillingOrderListMap.put("iTotalRecords", record.size());
        BillingOrderListMap.put("iTotalDisplayRecords", record.size());

        BillingOrderListMap.put("aaData", record);

        renderJson(BillingOrderListMap);
    }
	
	
	
	

	// 更新支付金额信息
	public void updateMoney() {
		String costPreInvoiceOrderId = getPara("costPreInvoiceOrderId");
		String costOrderId = getPara("costOrderId");
		String sql = "select * from cost_application_order_rel where application_order_id = '"+costPreInvoiceOrderId+"' and cost_order_id = '"+costOrderId+"'";
		CostApplicationOrderRel costApplicationOrderRel = CostApplicationOrderRel.dao.findFirst(sql);
		String name = getPara("name");
		String value = getPara("value");
		
		if ("pay_amount".equals(name) && "".equals(value)) {
			value = "0";
		}
		costApplicationOrderRel.set(name, value);
		costApplicationOrderRel.update();
		
		renderJson(costApplicationOrderRel);
	}

	
	
	
	public void searchAllAccount(){
		List<Account> accounts = Account.dao.find("select * from fin_account where type != 'REC' and bank_name != '现金'");
		renderJson(accounts);
	}

    // 付款确认单列表
    //@RequiresPermissions(value = {PermissionConstant.PERMSSION_CTC_AFFIRM})
    public void list() {
    	String orderNo = getPara("orderNo")==null?null:getPara("orderNo").trim();
    	String appOrderNo = getPara("applicationOrderNo")==null?null:getPara("applicationOrderNo").trim();
		String status = getPara("status")==null?null:getPara("status").trim();
		String spName = getPara("sp_name")==null?null:getPara("sp_name").trim();
		String receiverName = getPara("receiverName")==null?null:getPara("receiverName").trim();
		String beginTime = getPara("beginTime")==null?null:getPara("beginTime").trim();
		String endTime = getPara("endTime")==null?null:getPara("endTime").trim();
		
    	String sLimit = "";
		String pageIndex = getPara("sEcho");
		if (getPara("iDisplayStart") != null
				&& getPara("iDisplayLength") != null) {
			sLimit = " LIMIT " + getPara("iDisplayStart") + ", "
					+ getPara("iDisplayLength");
		}
               
        String fromSql = " from arap_cost_pay_confirm_order cpco "
        			+ " left join party p1 on cpco.sp_id = p1.id "
					+ " left join contact c1 on p1.contact_id = c1.id"
					+ " left join user_login ul on ul.id=cpco.creator";
        
        String totalSql = "select count(1) total" + fromSql;
        
        String columsSql = "select cpco.*, "
        		+ " (select group_concat( DISTINCT cao.order_no SEPARATOR '<br/>' ) "
        		+ " FROM arap_cost_pay_confirm_order_detail co, arap_cost_invoice_application_order cao "
				+ " where co.application_order_id = cao.id and co.order_id = cpco.id) fksq_no,"
				+ " (SELECT group_concat( DISTINCT amco.order_no SEPARATOR '<br/>' )"
				+ " FROM arap_cost_pay_confirm_order_detail co, arap_misc_cost_order amco"
				+ " WHERE co.misc_cost_order_id = amco.id AND co.order_id = cpco.id ) misc_no,"
				+ " ( SELECT ifnull(sum(caor.pay_amount), "
				+ " (SELECT  amco.total_amount FROM arap_cost_pay_confirm_order_detail co,"
				+ "  arap_misc_cost_order amco WHERE co.misc_cost_order_id = amco.id AND co.order_id = cpco.id )) "
				+ " FROM cost_application_order_rel caor WHERE caor.application_order_id "
				+ " IN (SELECT acpcod.application_order_id FROM arap_cost_pay_confirm_order cpco1 LEFT JOIN arap_cost_pay_confirm_order_detail acpcod "
				+ " ON acpcod.order_id = cpco1.id WHERE cpco1.id = cpco.id)) pay_amount,"
//				+ " (select sum(cao.total_amount) "
//        		+ " FROM arap_cost_pay_confirm_order_detail co, arap_cost_invoice_application_order cao "
//				+ " where co.application_order_id = cao.id and co.order_id = cpco.id) pay_amount,"
				+ " (SELECT	ifnull(sum(log.amount), 0) FROM arap_cost_pay_confirm_order_log log "
				+ " where log.order_id = cpco.id) already_pay, "
        		+ " c1.abbr sp_name,"
        		+ "ifnull(nullif(ul.c_name,''), ul.user_name) user_name "
        		+ fromSql;
        String orderBy= " order by cpco.create_date desc ";
        
        String conditions=" where 1=1 ";
        if (StringUtils.isNotEmpty(orderNo)){
        	conditions+=" and UPPER(cpco.order_no) like '%"+orderNo.toUpperCase()+"%'";
        }
//        if (appOrderNo != null){
//        	conditions+=" fksq_no like'%"+appOrderNo+"%'";
//        }
        if (StringUtils.isNotEmpty(status)){
        	conditions+=" and cpco.status = '"+status+"'";
        }
        if (StringUtils.isNotEmpty(spName)){
        	conditions+=" and c1.abbr like '%"+spName+"%'";
        }
        if (StringUtils.isNotEmpty(receiverName)){
        	conditions+=" and cpco.receive_person like '%"+receiverName+"%'";
        }
        
        if (StringUtils.isNotEmpty(beginTime)){
        	beginTime = " and cpco.create_date between'"+beginTime+"'";
        }else{
        	beginTime =" and cpco.create_date between '1970-1-1'";
        }
        
        if (StringUtils.isNotEmpty(endTime)){
        	endTime =" and '"+endTime+"'";
        }else{
        	endTime =" and '3000-1-1'";
        }
        conditions+=beginTime+endTime;
        
        
        totalSql+=conditions;
        Record recTotal = Db.findFirst(totalSql);
        Long total = recTotal.getLong("total");
        logger.debug("total records:" + total);
        
        columsSql+=conditions + orderBy + sLimit;
        List<Record> costPayConfirmOrders = Db.find(columsSql);

        Map orderListMap = new HashMap();
        orderListMap.put("sEcho", pageIndex);
        orderListMap.put("iTotalRecords", total);
        orderListMap.put("iTotalDisplayRecords", costPayConfirmOrders.size());

        orderListMap.put("aaData", costPayConfirmOrders);

        renderJson(orderListMap);
    }
    @RequiresPermissions(value = {PermissionConstant.PERMSSION_CTC_AFFIRM})
    public void costConfiremReturnOrder(){
    	String ids = getPara("ids");
    	String orderNos = getPara("orderNos");
    	String[] idArr = ids.split(",");
    	String[] orderNoArr = orderNos.split(",");
    	for(int i=0 ; i<idArr.length ; i++){
    		if("提货".equals(orderNoArr[i])){
    			DepartOrder pickupOrder = DepartOrder.dao.findById(idArr[i]);
    			pickupOrder.set("audit_status", "已确认");
    			pickupOrder.update();
    		}else if("零担".equals(orderNoArr[i]) || "整车".equals(orderNoArr[i])){
    			DepartOrder departOrder = DepartOrder.dao.findById(idArr[i]);
    			departOrder.set("audit_status", "已确认");
    			departOrder.update();
    		}else if("配送".equals(orderNoArr[i])){
    			DeliveryOrder deliveryOrder = DeliveryOrder.dao.findById(idArr[i]);
    			deliveryOrder.set("audit_status", "已确认");
    			deliveryOrder.update();
    		}else if("成本单".equals(orderNoArr[i])){
    			ArapMiscCostOrder arapmisccostOrder = ArapMiscCostOrder.dao.findById(idArr[i]);
    			arapmisccostOrder.set("audit_status", "已确认");
    			arapmisccostOrder.update();
    		}else{
    			InsuranceOrder insuranceOrder = InsuranceOrder.dao.findById(idArr[i]);
    			insuranceOrder.set("audit_status", "已确认");
    			insuranceOrder.update();
    		}
    	}
        renderJson("{\"success\":true}");
    }
}