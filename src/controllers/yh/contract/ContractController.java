package controllers.yh.contract;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import models.yh.contract.Contract;
import models.yh.contract.ContractItem;
import models.yh.profile.Contact;
import models.yh.profile.Route;

import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import controllers.yh.LoginUserController;

public class ContractController extends Controller {

    private Logger logger = Logger.getLogger(ContractController.class);

    // in config route已经将路径默认设置为/yh
    // me.add("/yh", controllers.yh.AppController.class, "/yh");
    public void index() {
        HttpServletRequest re = getRequest();
        String url = re.getRequestURI();
        logger.debug("URI:" + url);
        if (url.equals("/yh/customerContract")) {
            setAttr("contractType", "CUSTOMER");
            if (LoginUserController.isAuthenticated(this))
                render("contract/ContractList.html");
        } else {
            setAttr("contractType", "SP");
            if (LoginUserController.isAuthenticated(this))
                render("contract/ContractList.html");
        }

    }

    // 客户合同列表
    public void customerList() {

        String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null
                && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", "
                    + getPara("iDisplayLength");
        }

        // 获取总条数
        String totalWhere = "";
        String sql = "select count(1) total from contract c,party p,contact c1 where c.party_id= p.id and p.contact_id = c1.id and c.type='CUSTOMER'  ";
        System.out.println(sql);
        Record rec = Db.findFirst(sql + totalWhere);
        long total = rec.getLong("total");
        logger.debug("total records:" + total);

        // 获取当前页的数据
        List<Record> orders = Db
                .find("select *,c.id as cid from contract c,party p,contact c1 where c.party_id= p.id and p.contact_id = c1.id and c.type='CUSTOMER'");
        Map orderMap = new HashMap();
        orderMap.put("sEcho", pageIndex);
        orderMap.put("iTotalRecords", total);
        orderMap.put("iTotalDisplayRecords", total);
        orderMap.put("aaData", orders);

        renderJson(orderMap);

    }

    // 供应商合同列表
    public void spList() {

        String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null
                && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", "
                    + getPara("iDisplayLength");
        }

        // 获取总条数
        String totalWhere = "";
        String sql = "select count(1) total from contract c,party p,contact c1 where c.party_id= p.id and p.contact_id = c1.id and c.type='SERVICE_PROVIDER'";
        System.out.println(sql);
        Record rec = Db.findFirst(sql + totalWhere);
        logger.debug("total records:" + rec.getLong("total"));

        // 获取当前页的数据
        List<Record> orders = Db
                .find("select *,c.id as cid from contract c,party p,contact c1 where c.party_id= p.id and p.contact_id = c1.id and c.type='SERVICE_PROVIDER'");
        Map orderMap = new HashMap();
        orderMap.put("sEcho", pageIndex);
        orderMap.put("iTotalRecords", rec.getLong("total"));
        orderMap.put("iTotalDisplayRecords", rec.getLong("total"));
        orderMap.put("aaData", orders);

        renderJson(orderMap);

    }

    public void add() {
        HttpServletRequest re = getRequest();
        String url = re.getRequestURI();
        logger.debug("URI:" + url);
        if (url.equals("/yh/customerContract/add")) {
            setAttr("contractType", "CUSTOMER");
            setAttr("saveOK", false);
            if (LoginUserController.isAuthenticated(this))
                render("/yh/contract/ContractEdit.html");
        } else {
            setAttr("contractType", "SERVICE_PROVIDER");
            setAttr("saveOK", false);
            if (LoginUserController.isAuthenticated(this))
                render("/yh/contract/ContractEdit.html");
        }
        setAttr("saveOK", false);

    }

    public void edit() {
        String id = getPara();
        if (id != null) {
            Contract contract = Contract.dao.findById(id);
            Contact contact = Contact.dao.findById(id);
            setAttr("c", contact);
            setAttr("ul", contract);
        }
        if (LoginUserController.isAuthenticated(this))
            render("/yh/contract/ContractEdit.html");
    }

    public void save() {
        String id = getPara("contractId");
        Date createDate = Calendar.getInstance().getTime();
        if (id != "") {
            Contract c = Contract.dao.findById(id);
        }
        Record c = new Record();
        c.set("name", getPara("contract_name"));
        c.set("party_id", getParaToInt("partyid"));
        c.set("period_from", createDate);
        c.set("period_to", createDate);
        c.set("remark", getPara("remark"));
        if (id != "") {
            logger.debug("update....");
            c.set("id", id);
            c.set("type", getPara("type3"));
            Db.update("contract", c);
        } else {
            logger.debug("insert....");
            c.set("type", getPara("type2"));
            Db.save("contract", c);
        }

        /*
         * Map orderMap = new HashMap(); orderMap.put("contractId",c.get("id"));
         * orderMap.put("success", true); render-Json(orderMap);
         */
        // 保存后，只需要把contractId 回传到页面上就可以了
        renderJson(c.get("id"));

    }

    public void delete() {
        String id = getPara();
        if (id != null) {
            Db.deleteById("contract", id);
        }
        if (LoginUserController.isAuthenticated(this))
            redirect("/yh/customerContract");
    }

    public void delete2() {
        String id = getPara();
        if (id != null) {
            Db.deleteById("contract", id);
        }
        if (LoginUserController.isAuthenticated(this))
            redirect("/yh/spContract");
    }

    // 列出客户公司名称
    public void search() {
        String locationName = getPara("locationName");
        // 不能查所有
        List<Record> locationList = Collections.EMPTY_LIST;
        if (locationName.trim().length() > 0) {
            locationList = Db
                    .find("select *,p.id as pid from party p,contact c where p.contact_id = c.id and p.party_type = 'CUSTOMER' and c.company_name like ?",
                            "%" + locationName + "%");
        }
        renderJson(locationList);
    }

    // 列出供应商公司名称
    public void search2() {
        String locationName = getPara("locationName");
        // 不能查所有
        if (locationName.trim().length() > 0) {
            List<Record> locationList = Db
                    .find("select *,p.id as pid from party p,contact c where p.contact_id = c.id and p.party_type = 'SERVICE_PROVIDER' and c.company_name like ?",
                            "%" + locationName + "%");
            renderJson(locationList);
        }
    }

    // contract模块的对应的干线
    public void routeEdit() {
        String contractId = getPara("routId");
        if (contractId.equals("")) {
            Map orderMap = new HashMap();
            orderMap.put("sEcho", 0);
            orderMap.put("iTotalRecords", 0);
            orderMap.put("iTotalDisplayRecords", 0);
            orderMap.put("aaData", null);
            renderJson(orderMap);
            return;
        }

        String sLimit = "";
        String sql = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null
                && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", "
                    + getPara("iDisplayLength");
        }

        // 获取总条数
        String totalWhere = "";
        if (contractId != null && contractId.length() > 0) {
            sql = "select count(1) total from route r, contract_item c where c.route_id=r.id and c.contract_id = "
                    + contractId + "";
        }

        System.out.println(sql);
        Record rec = Db.findFirst(sql + totalWhere);
        logger.debug("total records:" + rec.getLong("total"));

        // 获取当前页的数据
        List<Record> orders = null;
        if (contractId != null && contractId.length() > 0) {
            orders = Db
                    .find("select * from  route r, contract_item c where c.route_id=r.id and c.contract_id = ?",
                            contractId);
        }
        Map orderMap = new HashMap();
        orderMap.put("sEcho", pageIndex);
        orderMap.put("iTotalRecords", rec.getLong("total"));
        orderMap.put("iTotalDisplayRecords", rec.getLong("total"));
        orderMap.put("aaData", orders);
        renderJson(orderMap);
    }

    public void routeAdd() {
        ContractItem item = new ContractItem();
        String contractId = getPara("routeContractId");
        String routeId = getPara("routeId");
        if (routeId != null) {
            System.out.println(contractId);
            item.set("contract_id", contractId)
                    .set("route_id", getPara("routeId"))
                    .set("amount", getPara("price"));
            // .set("miles", getPara("miles"));\
            item.save();
            renderJson("{\"success\":true}");
        } else {
            Route route = new Route();
            route.set("contract_id", contractId)
                    .set("route_id", getPara("routeId"))
                    .set("amount", getPara("price"));
            renderJson("{\"success\":true}");
        }

    }

    // 通过输入起点和终点判断干线id
    public void searchRoute() {
        String fromName = getPara("fromName");
        String toName = getPara("toName");
        System.out.println(fromName);
        System.out.println(toName);
        List<Route> routeId = Route.dao
                .find("select id as rId from route where location_from like '%"
                        + fromName + "%' and location_to like '%" + toName
                        + "%'");
        System.out.println(routeId);
        renderJson(routeId);
    }

    public void contractRouteEdit() {
        String id = getPara();
        System.out.println(id);
        renderJson("{\"success\":true}");
    }

    public void routeDelete() {
        String id = getPara();
        if (id != null) {
            Db.deleteById("contract_item", id);
        }
        renderJson("{\"success\":true}");
    }
}
