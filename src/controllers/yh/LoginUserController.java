package controllers.yh;import java.util.List;import java.util.HashMap;import java.util.Map;import models.UserLogin;import models.eeda.Leads;import com.jfinal.core.Controller;import com.jfinal.log.Logger;import com.jfinal.plugin.activerecord.Db;import com.jfinal.plugin.activerecord.Record;public class LoginUserController extends Controller {	private Logger logger = Logger.getLogger(LoginUserController.class);	// in config route已经将路径默认设置为/yh	// me.add("/yh", controllers.yh.AppController.class, "/yh");	public void index() {		render("/yh/profile/loginUser/loginUser.html");	}		//show增加用户页面	public void editUser(){		render("/yh/profile/loginUser/addUser.html");	}		//编辑用户	public void edit(){		String id = getPara();		if (id != null) {			UserLogin l = UserLogin.dao.findById(id);			setAttr("ul", l);		} else {			setAttr("ul", new UserLogin());		}		render("/yh/profile/loginUser/addUser.html");			}	//添加登陆用户	public void saveUser(){		/*if (!isAuthenticated())			return;*/		String id = getPara("userId");		if (id != "") {			UserLogin user = UserLogin.dao.findById(id);		}		Record user =new Record();		user.set("user_name", getPara("username"));		user.set("password", getPara("password"));		user.set("password_hint", getPara("pw_hint"));		if (id != "") {			logger.debug("update....");			user.set("id", id);			Db.update("user_login",user);		} else {			logger.debug("insert....");			Db.save("user_login", user);		}		render("/yh/profile/loginUser/loginUser.html");			}	//删除用户	public void del(){		/*UserLogin.dao.find("select * from user_login");		UserLogin.dao.deleteById(getParaToInt());*/		String id = getPara();		if (id != null) {		UserLogin l = UserLogin.dao.findById(id);		l.delete();		}		render("/yh/profile/loginUser/loginUser.html");	}	//列出用户信息	public void listUser(){		/*		 * Paging		 */		String sLimit = "";		String pageIndex = getPara("sEcho");		if (getPara("iDisplayStart") != null && getPara("iDisplayLength") != null) {			sLimit = " LIMIT " + getPara("iDisplayStart") + ", " + getPara("iDisplayLength");		}				//获取总条数		String totalWhere="";		String sql = "select count(1) total from user_login ";		Record rec = Db.findFirst(sql + totalWhere);		logger.debug("total records:" + rec.getLong("total"));				//获取当前页的数据		List<Record> orders = Db.find("select * from user_login");		Map orderMap = new HashMap();		orderMap.put("sEcho", pageIndex);		orderMap.put("iTotalRecords", rec.getLong("total"));		orderMap.put("iTotalDisplayRecords", rec.getLong("total"));		orderMap.put("aaData", orders);				renderJson(orderMap);	}}