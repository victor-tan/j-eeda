package config;

import models.Party;
import models.PartyAttribute;
import models.UserLogin;
import models.eeda.Case;
import models.eeda.Leads;
import models.eeda.Order;
import models.eeda.OrderItem;

import org.bee.tl.ext.jfinal.BeetlRenderFactory;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.plugin.shiro.ShiroPlugin;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;

import controllers.eeda.AppController;
import controllers.eeda.CaseController;
import controllers.eeda.LoanController;
import controllers.eeda.PropertyClientController;
import controllers.eeda.SalesOrderController;
import controllers.eeda.UserProfileController;

public class EedaConfig extends JFinalConfig {
	private static String H2 = "H2";
	private static String Mysql = "Mysql";
	private static String ProdMysql = "ProdMysql";
	/**
	 * 
	 * 供Shiro插件使用。
	 */
	Routes routes;

	C3p0Plugin cp;
	ActiveRecordPlugin arp;

	public void configConstant(Constants me) {
		me.setDevMode(true);

		BeetlRenderFactory templateFactory = new BeetlRenderFactory();
		me.setMainRenderFactory(templateFactory);

		templateFactory.groupTemplate.setCharset("utf-8");// 没有这句，html上的汉字会乱码

		// 注册后，可以使beetl html中使用shiro tag
		templateFactory.groupTemplate.registerFunctionPackage("shiro", new ShiroExt());

		// me.setErrorView(401, "/login.html");
		// me.setErrorView(403, "/login.html");
		// me.setError404View("/login.html");
		// me.setError500View("/login.html");
		// me.setErrorView(503, "/login.html");

	}

	public void configRoute(Routes me) {
		this.routes = me;

		// eeda project controller
		me.add("/", AppController.class);
		me.add("/case", CaseController.class);
		me.add("/user", UserProfileController.class);
		me.add("/salesOrder", SalesOrderController.class);
		me.add("/loan", LoanController.class);
		me.add("/propertyClient", PropertyClientController.class);
		// me.add("/au", AdminUserController.class);

		// yh project controller
		me.add("/yh", controllers.yh.AppController.class, "/yh");
		me.add("/yh/loginUser", controllers.yh.LoginUserController.class, "/yh");
		me.add("/yh/role", controllers.yh.RoleController.class, "/yh");
		me.add("/yh/customer", controllers.yh.profile.CustomerController.class, "/yh");
		me.add("/yh/location", controllers.yh.LocationController.class, "/yh");
	}

	public void configPlugin(Plugins me) {
		// 加载Shiro插件, for backend notation, not for UI
		me.add(new ShiroPlugin(routes));

		loadPropertyFile("app_config.txt");

		// H2 or mysql
		initDBconnector();

		me.add(cp);

		arp = new ActiveRecordPlugin(cp);
		arp.setShowSql(true);// ShowSql
		me.add(arp);

		arp.setDialect(new MysqlDialect());
		// 配置属性名(字段名)大小写不敏感容器工厂
		arp.setContainerFactory(new CaseInsensitiveContainerFactory());

		arp.addMapping("leads", Leads.class);
		arp.addMapping("support_case", Case.class);
		arp.addMapping("user_login", UserLogin.class);
		arp.addMapping("order_header", Order.class);
		arp.addMapping("order_item", OrderItem.class);
		arp.addMapping("party", Party.class);
		arp.addMapping("party_attribute", PartyAttribute.class);

		// yh mapping

	}

	private void initDBconnector() {
		String dbType = getProperty("dbType");
		String url = getProperty("dbUrl");
		String username = getProperty("username");
		String pwd = getProperty("pwd");

		if (H2.equals(dbType)) {
			connectH2();
		} else {
			cp = new C3p0Plugin(url, username, pwd);
		}

	}

	private void connectH2() {
		cp = new C3p0Plugin("jdbc:h2:mem:eeda;", "sa", "");
		cp.setDriverClass("org.h2.Driver");
		DataInitUtil.initH2Tables(cp);
	}

	public void configInterceptor(Interceptors me) {
		// me.add(new ShiroInterceptor());

	}

	public void configHandler(Handlers me) {
		if (H2.equals(getProperty("dbType"))) {
			DataInitUtil.initData(cp);
		}
	}
}
