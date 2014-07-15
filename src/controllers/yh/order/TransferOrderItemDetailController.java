package controllers.yh.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Party;
import models.TransferOrderItem;
import models.TransferOrderItemDetail;
import models.yh.profile.Contact;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import controllers.yh.LoginUserController;

public class TransferOrderItemDetailController extends Controller {

    private Logger logger = Logger.getLogger(TransferOrderItemDetailController.class);
    Subject currentUser = SecurityUtils.getSubject();

    public void transferOrderDetailList() {
        String itemId = getPara("item_id");
        String orderId = getPara("orderId");
        if (itemId == null || "".equals(itemId)) {
            itemId = "-1";
        }
        logger.debug(itemId);

        String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");
        }
        String sql = "";
        String sqlTotal = "";
        if(itemId != "-1"){
	        sqlTotal = "select count(1) total from transfer_order_item_detail where item_id =" + itemId;
	
	        sql = "select d.id,d.notify_party_id,d.serial_no,d.item_name,d.volume,d.weight,c.contact_person,c.phone,c.address,d.remark from transfer_order_item_detail d,party p,contact c where d.item_id ="
	                + itemId + " and d.notify_party_id=p.id and p.contact_id=c.id"
	                + sLimit;	
        }else{
        	sqlTotal = "select count(1) total from transfer_order_item_detail where order_id="+orderId;
	
	        sql = "select d.id,d.notify_party_id,d.serial_no,d.item_name,d.volume,d.weight,c.contact_person,c.phone,c.address,d.remark from transfer_order_item_detail d,party p,contact c"
	                + " where order_id = "+orderId+" and d.notify_party_id=p.id and p.contact_id=c.id"
	                + sLimit;	
        }

        Record rec = Db.findFirst(sqlTotal);
        logger.debug("total records:" + rec.getLong("total"));
        List<Record> transferOrders = Db.find(sql);
        Map transferOrderListMap = new HashMap();
        transferOrderListMap.put("sEcho", pageIndex);
        transferOrderListMap.put("iTotalRecords", rec.getLong("total"));
        transferOrderListMap.put("iTotalDisplayRecords", rec.getLong("total"));

        transferOrderListMap.put("aaData", transferOrders);

        renderJson(transferOrderListMap);
    }

    public void edit1() {
        long id = getParaToLong();

        Party party = Party.dao.findById(id);
        setAttr("party", party);

        Contact contact = Contact.dao.findFirst("select * from contact where id=?", party.getLong("contact_id"));
        setAttr("contact", contact);
        if (LoginUserController.isAuthenticated(this))
            render("transferOrder/transferOrderEdit.html");
    }

    public void edit() {
        if (LoginUserController.isAuthenticated(this))
            render("transferOrder/editTransferOrder.html");
    }

    public void delete() {
        long id = getParaToLong();

        Party party = Party.dao.findById(id);
        party.delete();

        Contact contact = Contact.dao.findFirst("select * from contact where id=?", party.getLong("contact_id"));
        contact.delete();
        if (LoginUserController.isAuthenticated(this))
            redirect("/yh/transferOrder");
    }

    // 保存单品
    public void saveTransferOrderItemDetail() {
        TransferOrderItemDetail item = null;
        String id = getPara("transfer_order_item_detail_id");
        String itemId = getPara("transfer_order_item_id");
        TransferOrderItem transferOrderItem = TransferOrderItem.dao.findById(itemId);
        if (id != null && !id.equals("")) {
            item = TransferOrderItemDetail.dao.findById(id);
            item.set("item_no", transferOrderItem.get("item_no"));
            item.set("serial_no", getPara("serial_no"));
            item.set("item_name", getPara("detail_item_name"));
            item.set("volume", getPara("detail_volume").equals("") ? 0 : getPara("detail_volume"));
            item.set("weight", getPara("detail_weight").equals("") ? 0 : getPara("detail_weight"));
            item.set("remark", getPara("detail_remark"));
            
            Party party = Party.dao.findById(getPara("notify_party_id"));
            Contact contact = Contact.dao.findFirst("select * from contact where id=(select contact_id from party where id="
                    + party.get("id") + ")");
            contact.set("contact_person", getPara("detail_contact_person"));
            contact.set("phone", getPara("detail_phone"));
            contact.set("address", getPara("detail_address"));
            contact.update();
            party.set("contact_id", contact.get("id"));
            party.update();

            item.set("notify_party_id", party.get("id"));
            item.set("order_id", getPara("transfer_order_id"));
            item.set("item_id", getPara("transfer_order_item_id"));
            item.update();
        } else {
            item = new TransferOrderItemDetail();
            item.set("item_no", transferOrderItem.get("item_no"));
            item.set("serial_no", getPara("serial_no"));
            item.set("item_name", getPara("detail_item_name"));
            item.set("volume", getPara("detail_volume").equals("") ? 0 : getPara("detail_volume"));
            item.set("weight", getPara("detail_weight").equals("") ? 0 : getPara("detail_weight"));
            item.set("remark", getPara("detail_remark"));
            Party party = setParty();

            item.set("notify_party_id", party.get("id"));
            item.set("order_id", getPara("transfer_order_id"));
            item.set("item_id", getPara("transfer_order_item_id"));
            item.save();
        }
        renderJson(item);
    }

    // 保存收货人
    private Party setParty() {
        Party party = new Party();
        Contact contact = new Contact();
        contact.set("contact_person", getPara("detail_contact_person"));
        contact.set("phone", getPara("detail_phone"));
        contact.set("address", getPara("detail_address"));
        contact.save();
        party.set("contact_id", contact.get("id"));
        party.set("party_type", Party.PARTY_TYPE_NOTIFY_PARTY);
        party.save();
        return party;
    }

    // 获取getTransferOrderItemDetail对象
    public void getTransferOrderItemDetail() {
        Map<String, Object> map = new HashMap<String, Object>();
        String id = getPara("detail_id");
        String notify_party_id = getPara("notify_party_id");
        TransferOrderItemDetail transferOrderItemDetail = TransferOrderItemDetail.dao.findById(id);
        Contact contact = (Contact) Contact.dao.findFirst("select * from contact where id=(select contact_id from party where id="
                + notify_party_id + ")");
        map.put("transferOrderItemDetail", transferOrderItemDetail);
        map.put("contact", contact);
        renderJson(map);
    }

    // 删除
    public void deleteTransferOrderItemDetail() {
        String id = getPara("detail_id");
        TransferOrderItemDetail transferOrderItemDetail = TransferOrderItemDetail.dao.findById(id);
        transferOrderItemDetail.set("order_id", null);
        transferOrderItemDetail.set("item_id", null);
        transferOrderItemDetail.set("notify_party_id", null);
        TransferOrderItemDetail.dao.deleteById(id);
        renderJson("{\"success\":true}");
    }

    // 获取所有单品
    public void getAllTransferOrderItemDetail() {
        String item_id = getPara("transfer_order_item_id");
        Map<String, List> map = new HashMap<String, List>();
        List<TransferOrderItemDetail> transferOrderItemDetails = TransferOrderItemDetail.dao
                .find("select * from transfer_order_item_detail where item_id=" + item_id);
        List<Contact> contacts = Contact.dao
                .find("select * from contact  where id in(select contact_id from party where id in(select notify_party_id from transfer_order_item_detail where item_id="
                        + item_id + "))");
        map.put("transferOrderItemDetails", transferOrderItemDetails);
        map.put("contacts", contacts);
        renderJson(map);
    }
}
