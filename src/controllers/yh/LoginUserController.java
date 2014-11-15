package controllers.yh;import java.util.HashMap;import java.util.List;import java.util.Map;import models.UserLogin;import org.apache.shiro.SecurityUtils;import org.apache.shiro.authz.annotation.RequiresAuthentication;import org.apache.shiro.subject.Subject;import com.jfinal.core.Controller;import com.jfinal.log.Logger;import com.jfinal.plugin.activerecord.Db;import com.jfinal.plugin.activerecord.Record;@RequiresAuthenticationpublic class LoginUserController extends Controller {    private Logger logger = Logger.getLogger(LoginUserController.class);    // in config route已经将路径默认设置为/yh    // me.add("/yh", controllers.yh.AppController.class, "/yh");    static Subject currentUser = SecurityUtils.getSubject();        public static Long getLoginUserId(Controller controller) {        currentUser = SecurityUtils.getSubject();        if (!currentUser.isAuthenticated()) {            controller.redirect("/login");            UserLogin l = UserLogin.dao.findFirst("select * from user_login where user_name='"+currentUser.getPrincipal().toString()+"'");            return l.getLong("id");        }        return -1L;    }    public void index() {        render("/yh/profile/loginUser/loginUser.html");    }    // show增加用户页面    public void addUser() {        render("/yh/profile/loginUser/addUser.html");    }    // show编辑用户    public void edit() {        String username = currentUser.getPrincipal().toString();        String id = getPara();        if (id != null) {            index();            UserLogin l = UserLogin.dao.findById(id);            setAttr("lu", l);            render("/yh/profile/loginUser/addUser.html");        }        if (username != null && id == null) {            index();            UserLogin user = UserLogin.dao.findFirst(                    "select * from user_login where user_name=?", username);            setAttr("ul", user);            render("/yh/profile/loginUser/EditUser.html");        }    }    // 添加登陆用户    public void saveUser() {        /*         * if (!isAuthenticated()) return;         */        String id = getPara("userId");        if (id != "") {            UserLogin user = UserLogin.dao.findById(id);        }        Record user = new Record();        user.set("user_name", getPara("username"));        user.set("password", getPara("password"));        user.set("password_hint", getPara("pw_hint"));        String officeId = getPara("officeSelect");        if(officeId != null && !"".equals(officeId)){        	user.set("office_id", getPara("officeSelect"));        }        if (id != "") {            logger.debug("update....");            user.set("id", id);            Db.update("user_login", user);        } else {            logger.debug("insert....");            Db.save("user_login", user);        }        index();        // render("/yh/profile/loginUser/loginUser.html");    }    // 删除用户    public void del() {        /*         * UserLogin.dao.find("select * from user_login");         * UserLogin.dao.deleteById(getParaToInt());         */        String id = getPara();        if (id != null) {            UserLogin l = UserLogin.dao.findById(id);            l.delete();        }        render("/yh/profile/loginUser/loginUser.html");    }    // 列出用户信息    public void listUser() {        /*         * Paging         */        String sLimit = "";        String pageIndex = getPara("sEcho");        if (getPara("iDisplayStart") != null                && getPara("iDisplayLength") != null) {            sLimit = " LIMIT " + getPara("iDisplayStart") + ", "                    + getPara("iDisplayLength");        }        // 获取总条数        String totalWhere = "";        String sql = "select count(1) total from user_login ";        Record rec = Db.findFirst(sql + totalWhere);        logger.debug("total records:" + rec.getLong("total"));        // 获取当前页的数据        List<Record> orders = Db                .find("select id,user_name,password_hint from user_login");        Map orderMap = new HashMap();        orderMap.put("sEcho", pageIndex);        orderMap.put("iTotalRecords", rec.getLong("total"));        orderMap.put("iTotalDisplayRecords", rec.getLong("total"));        orderMap.put("aaData", orders);        renderJson(orderMap);    }}