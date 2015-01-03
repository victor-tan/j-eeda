package controllers.yh;import models.UserLogin;import org.apache.commons.mail.DefaultAuthenticator;import org.apache.commons.mail.Email;import org.apache.commons.mail.SimpleEmail;import org.apache.shiro.SecurityUtils;import org.apache.shiro.authc.AuthenticationException;import org.apache.shiro.authc.IncorrectCredentialsException;import org.apache.shiro.authc.LockedAccountException;import org.apache.shiro.authc.UnknownAccountException;import org.apache.shiro.authc.UsernamePasswordToken;import org.apache.shiro.subject.Subject;import com.jfinal.core.Controller;public class AppController extends Controller {    // in config route已经将路径默认设置为/yh    // me.add("/yh", controllers.yh.AppController.class, "/yh");    Subject currentUser = SecurityUtils.getSubject();    private boolean isAuthenticated() {        // remember me 处理，自动帮user 登陆        if (!currentUser.isAuthenticated() && currentUser.isRemembered()) {            Object principal = currentUser.getPrincipal();            if (null != principal) {                UserLogin user = UserLogin.dao.findFirst("select * from user_login where user_name='" + String.valueOf(principal) + "' and (is_stop = 0 or is_stop is null)");                String password = user.getStr("password");                UsernamePasswordToken token = new UsernamePasswordToken(user.getStr("user_name"), password);                token.setRememberMe(true);                currentUser.login(token);// 登录            }        }        if (!currentUser.isAuthenticated()) {            redirect("/login");            return false;        }        setAttr("userId", currentUser.getPrincipal());        return true;    }    public void index() {        if (isAuthenticated()) {        	UserLogin user = UserLogin.dao.findFirst("select * from user_login where user_name=?",currentUser.getPrincipal());            if(user.get("c_name")!=null&&!"".equals(user.get("c_name"))){            	setAttr("userId", user.get("c_name"));            }else{            	 setAttr("userId", currentUser.getPrincipal());            }        	            render("/yh/index.html");        }    }    public void login() {        String username = getPara("username");        if (username == null) {            render("/yh/login.html");            return;        }        UsernamePasswordToken token = new UsernamePasswordToken(getPara("username"), getPara("password"));        if (getPara("remember") != null && "Y".equals(getPara("remember")))            token.setRememberMe(true);        String errMsg = "";        try {            currentUser.login(token);        } catch (UnknownAccountException uae) {            errMsg = "用户名不存在";            errMsg = "用户名/密码不正确";            uae.printStackTrace();        } catch (IncorrectCredentialsException ice) {            errMsg = "密码不正确";            errMsg = "用户名/密码不正确";            ice.printStackTrace();        } catch (LockedAccountException lae) {            errMsg = "用户名已被停用";            lae.printStackTrace();        } catch (AuthenticationException ae) {            errMsg = "用户名/密码不正确";            ae.printStackTrace();        }        if (errMsg.length()==0) {        	        	UserLogin user = UserLogin.dao.findFirst("select * from user_login where user_name=? and (is_stop = 0 or is_stop is null)",currentUser.getPrincipal());            if(user==null){            	errMsg = "用户名不存在或已被停用";            	setAttr("errMsg", errMsg);            	render("/yh/login.html");            }else if(user.get("c_name") != null && !"".equals(user.get("c_name"))){            	setAttr("userId", user.get("c_name"));            	render("/yh/index.html");            }else{            	setAttr("userId",currentUser.getPrincipal());            	render("/yh/index.html");            }                    } else {            setAttr("errMsg", errMsg);            render("/yh/login.html");        }    }    public void logout() {        currentUser.logout();        redirect("/login");    }    // 使用common-email, javamail    public void testMail() throws Exception {        Email email = new SimpleEmail();        email.setHostName("smtp.exmail.qq.com");        email.setSmtpPort(465);        email.setAuthenticator(new DefaultAuthenticator("red.luo@eeda123.com", "luo0330"));        email.setSSLOnConnect(true);        email.setFrom("red.luo@eeda123.com");        email.setSubject("忘记密码");        email.setMsg("你的密码已重置");        email.addTo("red.luo@eeda123.com");        email.send();            }}