package controllers.yh.order;

import java.util.List;

import models.Party;
import models.TransferOrderItem;
import models.TransferOrderItemDetail;
import models.yh.profile.Contact;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.jfinal.core.Controller;

public class TransferOrderItemDetailController extends Controller {

	private Logger logger = Logger.getLogger(TransferOrderItemDetailController.class);
	Subject currentUser = SecurityUtils.getSubject();

	public void transferOrderItemList() {
		List<TransferOrderItem> transferOrderItems = TransferOrderItem.dao.find("select * from transfer_order_item");
		renderJson(transferOrderItems);
	}

	public void edit1() {
		long id = getParaToLong();

		Party party = Party.dao.findById(id);
		setAttr("party", party);

		Contact contact = Contact.dao.findFirst("select * from contact where id=?", party.getLong("contact_id"));
		setAttr("contact", contact);

		render("transferOrder/transferOrderEdit.html");
	}

	public void edit() {
		render("transferOrder/editTransferOrder.html");
	}

	public void delete() {
		long id = getParaToLong();

		Party party = Party.dao.findById(id);
		party.delete();

		Contact contact = Contact.dao.findFirst("select * from contact where id=?", party.getLong("contact_id"));
		contact.delete();

		redirect("/yh/transferOrder");
	}

	// 保存单品
	public void saveTransferOrderItemDetail() {
		TransferOrderItemDetail item = null;
		String id = getPara("transfer_order_item_detail_id");
		if (id != null && !id.equals("")) {
			item = TransferOrderItemDetail.dao.findById(id);
			item.set("item_name", getPara("update_item_name"));
			item.set("amount", getPara("update_amount"));
			item.set("unit", getPara("update_unit"));
			item.set("volume", getPara("update_volume"));
			item.set("weight", getPara("update_weight"));
			item.set("remark", getPara("update_remark"));
			item.set("order_id", getPara("transfer_order_id"));
			item.update();
		} else {
			item = new TransferOrderItemDetail();
			item.set("serial_no", getPara("serial_no"));
			item.set("item_name", getPara("detail_item_name"));
			item.set("volume", getPara("detail_volume"));
			item.set("weight", getPara("detail_weight"));
			item.set("remark", getPara("detail_remark"));
			item.set("is_damage", getPara("detail_is_damage"));
			item.set("estimate_damage_amount", getPara("detail_estimate_damage_amount"));
			item.set("damage_revenue", getPara("detail_damage_revenue"));
			item.set("damage_payment", getPara("detail_damage_payment"));
			item.set("damage_remark", getPara("detail_damage_remark"));
			Contact contact = setContact();
			contact.save();
			item.set("contact_id", contact.get("id"));
			item.set("order_id", getPara("transfer_order_id"));
			item.set("item_id", getPara("transfer_order_item_id"));
			item.save();
		}
		renderJson(item.get("id"));
	}

	// 保存客户
	private Contact setContact() {
		Contact contact = new Contact();
		contact.set("contact_person", getPara("contact_person"));
		contact.set("phone", getPara("phone"));
		contact.set("address", getPara("address"));
		return contact;
	}

	// 获取TransferOrderItem对象
	public void getTransferOrderItem() {
		String id = getPara("transfer_order_item_id");
		TransferOrderItem transferOrderItem = TransferOrderItem.dao.findById(id);
		renderJson(transferOrderItem);
	}

	// 删除TransferOrderItem
	public void deleteTransferOrderItem() {
		String id = getPara("transfer_order_item_id");
		TransferOrderItem.dao.deleteById(id);
		renderJson("{\"success\":true}");
	}
}
