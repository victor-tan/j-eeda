package controllers.yh.departOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import controllers.yh.LoginUserController;

public class DepartOrderController extends Controller {

	private Logger logger = Logger.getLogger(DepartOrderController.class);
	Subject currentUser = SecurityUtils.getSubject();

	public void index() {
		if (LoginUserController.isAuthenticated(this))
			render("departOrder/departOrderList.html");
	}

	public void list() {
		String sLimit = "";
		String pageIndex = getPara("sEcho");
		if (getPara("iDisplayStart") != null
		        && getPara("iDisplayLength") != null) {
			sLimit = " LIMIT " + getPara("iDisplayStart") + ", "
			        + getPara("iDisplayLength");
		}

		String sqlTotal = "select count(1) total from depart_order";
		Record rec = Db.findFirst(sqlTotal);
		logger.debug("total records:" + rec.getLong("total"));

		String sql = "select * from depart_order";

		List<Record> warehouses = Db.find(sql);

		Map map = new HashMap();
		map.put("sEcho", pageIndex);
		map.put("iTotalRecords", rec.getLong("total"));
		map.put("iTotalDisplayRecords", rec.getLong("total"));

		map.put("aaData", warehouses);

		renderJson(map);
	}

	public void add() {
		setAttr("saveOK", false);
		if (LoginUserController.isAuthenticated(this))
			render("/yh/departOrder/allTransferOrderList.html");
	}

	public void createTransferOrderList() {
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

		String sql = "select * from transfer_order order by create_stamp desc";

		List<Record> transferOrders = Db.find(sql);

		Map transferOrderListMap = new HashMap();
		transferOrderListMap.put("sEcho", pageIndex);
		transferOrderListMap.put("iTotalRecords", rec.getLong("total"));
		transferOrderListMap.put("iTotalDisplayRecords", rec.getLong("total"));

		transferOrderListMap.put("aaData", transferOrders);

		renderJson(transferOrderListMap);
	}

	public void addDepartOrder() {

		String[] list = this.getPara("localArr").split(",");
		List<String> alllist = new ArrayList<String>();
		List<String> onelist = new ArrayList<String>();
		int k = 0;
		int n = 0;
		for (int i = 1; i < list.length + 1; i++) {
			String j = "";
			j = list[i - 1].toString();
			onelist.add(n, j);
			n++;
			if (i % 2 == 0 && i != 0) {
				for (k = 0; k < list.length / 2;) {
					alllist.add(k, onelist.toString());
					k++;
					n = 0;
					onelist.clear();
					break;
				}
			}
		}
		int lang = alllist.size();

	}
}
