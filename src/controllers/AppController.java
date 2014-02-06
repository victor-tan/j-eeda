package controllers;import java.util.Calendar;import java.util.Date;import java.util.HashMap;import java.util.List;import java.util.Map;import models.Leads;import org.apache.shiro.SecurityUtils;import org.apache.shiro.authc.AuthenticationException;import org.apache.shiro.authc.IncorrectCredentialsException;import org.apache.shiro.authc.LockedAccountException;import org.apache.shiro.authc.UnknownAccountException;import org.apache.shiro.authc.UsernamePasswordToken;import org.apache.shiro.authz.annotation.Logical;import org.apache.shiro.authz.annotation.RequiresRoles;import org.apache.shiro.subject.Subject;import com.jfinal.core.Controller;import com.jfinal.log.Logger;import com.jfinal.plugin.activerecord.Db;import com.jfinal.plugin.activerecord.Record;public class AppController extends Controller {	private Logger logger = Logger.getLogger(AppController.class);	Subject currentUser = SecurityUtils.getSubject();	private boolean isAuthenticated() {		if (!currentUser.isAuthenticated()) {			redirect("/login");			return false;		}		setAttr("userId", currentUser.getPrincipal());		return true;	}	public void index() {		if (isAuthenticated()) {			setAttr("userId", currentUser.getPrincipal());			render("/index.html");		}	}	public void login() {		if (getPara("username") == null) {			render("/login.html");			return;		}		UsernamePasswordToken token = new UsernamePasswordToken(getPara("username"), getPara("password"));		// ”Remember Me” built-in, just do this:		token.setRememberMe(true);		String errMsg = "";		try {			currentUser.login(token);		} catch (UnknownAccountException uae) {			errMsg = "用户名不存在";			errMsg = "用户名/密码不正确";			uae.printStackTrace();		} catch (IncorrectCredentialsException ice) {			errMsg = "密码不正确";			errMsg = "用户名/密码不正确";			ice.printStackTrace();		} catch (LockedAccountException lae) {			errMsg = "用户名已被冻结";			lae.printStackTrace();		} catch (AuthenticationException ae) {			errMsg = "用户名/密码不正确";			ae.printStackTrace();		}		if (isAuthenticated()) {			setAttr("userId", currentUser.getPrincipal());			redirect("/");		} else {			setAttr("errMsg", errMsg);			redirect("/login");		}	}	public void logout() {		currentUser.logout();		redirect("/login");	}	// 可改成异步，不用刷新页面	@RequiresRoles(value = { "admin", "property_mananger" }, logical = Logical.OR)	public void deleteLeads() {		if (!isAuthenticated())			return;		String id = getPara();		if (id != null) {			Leads l = Leads.dao.findById(id);			l.delete();		}		redirect("/list");	}	public void editLeads() {		if (!isAuthenticated())			return;		String id = getPara();		if (id != null) {			Leads l = Leads.dao.findById(id);			setAttr("leads", l);		}		render("/leadsForm.html");	}	public void viewLeads() {		if (!isAuthenticated())			return;		String id = getPara();		if (id != null) {			Leads l = Leads.dao.findById(id);			setAttr("leads", l);		}		render("/viewLeads.html");	}	public void saveLeads() {		if (!isAuthenticated())			return;		String id = getPara("leadsId");		if (id != "") {			Leads l = Leads.dao.findById(id);		}		// List<UserLogin> users =		// UserLogin.dao.find("select * from user_Login");		// System.out.println("users.size = " + users.size());		// renderText("此方法是一个action");		String title = getPara("title");		// String createDateStr = getPara("createDate");		//		//		// DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.CHINESE);		//		// try {		// createDate = df.parse(createDateStr);		// } catch (ParseException e) {		// // TODO Auto-generated catch block		// e.printStackTrace();		// }		String remark = getPara("remark");		Record leads = new Record();		leads.set("title", title);		leads.set("priority", getPara("priority"));		leads.set("type", getPara("type"));		leads.set("region", getPara("region"));		leads.set("addr", getPara("addr"));		leads.set("intro", getPara("intro"));		leads.set("remark", getPara("remark"));		leads.set("lowest_price", getPara("lowest_price"));		leads.set("agent_fee", getPara("agent_fee"));		leads.set("introducer", getPara("introducer"));		leads.set("customer_source", getPara("customer_source"));		leads.set("sales", getPara("sales"));		leads.set("follower", getPara("follower"));		leads.set("follower_phone", getPara("follower_phone"));		leads.set("owner", getPara("owner"));		leads.set("owner_phone", getPara("owner_phone"));		leads.set("area", getPara("area"));		leads.set("total", getPara("total"));		leads.set("status", getPara("status"));		leads.set("building_name", getPara("building_name"));		leads.set("building_no", getPara("building_no"));		leads.set("room_no", getPara("room_no"));		if (id != "") {			System.out.println("update....");			leads.set("id", id);			Db.update("leads", leads);		} else {			System.out.println("insert....");			Date createDate = Calendar.getInstance().getTime();			leads.set("create_Date", createDate);			leads.set("creator", currentUser.getPrincipal());			Db.save("leads", leads);		}		redirect("/list");	}	public void list() {		if (!isAuthenticated())			return;		List<Record> leadsList = Db.find("select * from leads order by create_date desc");		System.out.println("size:" + leadsList.size());		setAttr("userId", currentUser.getPrincipal());		setAttr("leadsList", leadsList).render("/list.html");	}	public void listLeads() {		/*		 * Paging		 */		String sLimit = " LIMIT 1, 10";		// if (getPara("iDisplayStart") != null		// && getPara("iDisplayLength") != null) {		// sLimit = "LIMIT " + getPara("iDisplayStart") + ", "		// + getPara("iDisplayLength");		// }		/*		 * Filtering NOTE this does not match the built-in DataTables filtering		 * which does it word by word on any field. It's possible to do here,		 * but concerned about efficiency on very large tables, and MySQL's		 * regex functionality is very limited		 */		String aColumns[] = { "title", "create_date", "remark" };		String sWhere = "";		if (getPara("sSearch") != null && !"".equals(getPara("sSearch"))) {			sWhere = "WHERE (";			for (int i = 0; i < aColumns.length; i++) {				sWhere += aColumns[i] + " LIKE '%" + getPara("sSearch") + "%' OR ";			}			sWhere = sWhere.substring(0, sWhere.length() - 3);			sWhere += ')';		}		String sql = "select * from leads " + sWhere + sLimit;		System.out.println(sql);		List<Record> orders = Db.find(sql);// Leads.dao.find(sql);		Map orderMap = new HashMap();		orderMap.put("aaData", orders);		orderMap.put("iTotalRecords", orders.size());		renderJson(orderMap);	}	public void property() {		if (!isAuthenticated())			return;		render("/property.html");	}	public void registrationFlow() {		if (!isAuthenticated())			return;		render("/registrationFlow.html");	}	public void pos() {		if (!isAuthenticated())			return;		render("/pos.html");	}	public void visa() {		if (!isAuthenticated())			return;		render("/visa.html");	}}