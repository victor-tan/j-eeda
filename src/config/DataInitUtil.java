package config;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import models.Party;
import models.PartyAttribute;
import models.yh.profile.Contact;

import com.jfinal.plugin.c3p0.C3p0Plugin;

public class DataInitUtil {
    public static void initH2Tables(C3p0Plugin cp) {
        try {
            cp.start();
            Connection conn = cp.getDataSource().getConnection();
            Statement stmt = conn.createStatement();

            // 登陆及授权的3个表
            stmt.executeUpdate("create table if not exists user_login(id bigint auto_increment PRIMARY KEY, user_name VARCHAR(50) not null, password VARCHAR(50) not null, password_hint VARCHAR(255));");
            stmt.executeUpdate("create table if not exists user_roles(id bigint auto_increment PRIMARY KEY, user_name VARCHAR(50) not null, role_name VARCHAR(255) not null, remark VARCHAR(255));");
            stmt.executeUpdate("create table if not exists role_permissions(id bigint auto_increment PRIMARY KEY, role_name VARCHAR(50) not null, role_permission VARCHAR(50), remark VARCHAR(255));");
            stmt.executeUpdate("create table if not exists location(id bigint auto_increment PRIMARY KEY, code VARCHAR(50) not null, name VARCHAR(50), pcode VARCHAR(255));");
            stmt.executeUpdate("create table if not exists office(id bigint auto_increment PRIMARY KEY, office_code VARCHAR(50) not null, office_name VARCHAR(50), office_person VARCHAR(50),phone VARCHAR(255),address VARCHAR(255),email VARCHAR(50),type VARCHAR(50),company_intro VARCHAR(255),remark VARCHAR(255));");
            stmt.executeUpdate("create table if not exists fin_account(id bigint auto_increment PRIMARY KEY, name VARCHAR(20) not null, type VARCHAR(50), currency VARCHAR(50),org_name VARCHAR(50),account_pin VARCHAR(50), remark VARCHAR(255));");
            stmt.executeUpdate("create table if not exists contract(id bigint auto_increment PRIMARY KEY, name VARCHAR(50) not null, type VARCHAR(50), party_id bigint,period_from Timestamp,period_to Timestamp, remark VARCHAR(255));");
            stmt.executeUpdate("create table if not exists role_table(id bigint auto_increment PRIMARY KEY,role_name VARCHAR(50),role_time TIMESTAMP,role_people VARCHAR(50),role_lasttime TIMESTAMP,role_lastpeople VARCHAR(50));");
            stmt.executeUpdate("create table if not exists Fin_item(id bigint auto_increment PRIMARY KEY,code VARCHAR(20),name VARCHAR(20),type VARCHAR(20),Remark VARCHAR(255));");
            stmt.executeUpdate("create table if not exists privilege_table(id bigint auto_increment PRIMARY KEY,privilege VARCHAR(50));");

            stmt.executeUpdate("create table if not exists contract_item(id bigint auto_increment PRIMARY KEY,contract_id bigint,Fin_item_id bigint,priceType varchar(50),carType varchar(255),carLength varchar(255),ltlUnitType varchar(50), from_id VARCHAR(50),location_from VARCHAR(50),to_id VARCHAR(50),location_to VARCHAR(50) ,amount Double,remark VARCHAR(255));");

            // location init
            LocationDataInit.initLocation(stmt);

            // 配送单
            stmt.executeUpdate("create table if not exists delivery_order(id bigint auto_increment PRIMARY KEY,Order_no VARCHAR(50),Transfer_order_id VARCHAR(50), customer_id bigint,sp_id bigint,notify_party_id bigint,appointment_stamp timestamp,status VARCHAR(50),cargo_nature Varchar(20),from_warehouse_code Varchar(20),Remark Varchar(255),Create_by bigint,Create_stamp timestamp,Last_modified_by bigint,Last_modified_stamp timestamp);");

            // delivery_order_milestone 配送单里程碑
            stmt.executeUpdate("create table if not exists delivery_order_milestone(id bigint auto_increment PRIMARY KEY,status varchar(255),location varchar(255),create_by bigint,create_stamp TIMESTAMP,last_modified_by bigint,"
                    + "last_modified_stamp TIMESTAMP,delivery_id bigint,FOREIGN KEY(delivery_id) REFERENCES delivery_order(id));");

            // 干线表
            stmt.executeUpdate("create table if not exists route(id bigint auto_increment PRIMARY KEY,from_id VARCHAR(50), location_from VARCHAR(50) not null,to_id VARCHAR(50), location_to VARCHAR(50) not null, remark VARCHAR(255));");

            stmt.executeUpdate("create table if not exists leads(id bigint auto_increment PRIMARY KEY, "
                    + "title VARCHAR(255), priority varchar(50), create_date TIMESTAMP, creator varchar(50), status varchar(50),"
                    + "type varchar(50), region varchar(50), addr varchar(256), "
                    + "intro varchar(5120), remark VARCHAR(5120), lowest_price DECIMAL(20, 2), agent_fee DECIMAL(20, 2), "
                    + "introducer varchar(256), sales varchar(256), follower varchar(50), follower_phone varchar(50),"
                    + "owner varchar(50), owner_phone varchar(50), area decimal(10,2), total decimal(10,2), customer_source varchar(50), "
                    + "building_name varchar(255), building_unit varchar(50), building_no varchar(50), room_no varchar(50), is_have_car char(1) default 'N',"
                    + "is_public char(1) default 'N');");

            stmt.executeUpdate("create table if not exists support_case(id bigint auto_increment PRIMARY KEY, title VARCHAR(255), type varchar(50), create_date TIMESTAMP, creator varchar(50), status varchar(50), case_desc VARCHAR(5120), note VARCHAR(5120));");

            stmt.executeUpdate("create table if not exists order_header(id bigint auto_increment PRIMARY KEY, order_no VARCHAR(50) not null, type varchar(50), status varchar(50), creator VARCHAR(50), create_date TIMESTAMP, remark varchar(256));");
            stmt.executeUpdate("create table if not exists order_item(id bigint auto_increment PRIMARY KEY, order_id bigint, item_name VARCHAR(50), item_desc VARCHAR(50), quantity decimal(10,2), unit_price decimal(10,2), status varchar(50), FOREIGN KEY(order_id) REFERENCES order_header(id) );");

            // party 当事人，可以有各种type
            stmt.executeUpdate("create table if not exists party(id bigint auto_increment PRIMARY KEY, party_type VARCHAR(32), contact_id bigint, create_date TIMESTAMP, creator varchar(50), last_update_date TIMESTAMP, last_updator varchar(50), status varchar(50),remark VARCHAR(255),receipt varchar(50));");
            stmt.executeUpdate("create table if not exists party_attribute(id bigint auto_increment PRIMARY KEY, party_id bigint, attr_name varchar(60), attr_value VARCHAR(255), create_date TIMESTAMP, creator varchar(50), FOREIGN KEY(party_id) REFERENCES party(id));");
            stmt.executeUpdate("create table if not exists contact(id bigint auto_increment PRIMARY KEY, company_name varchar(100),abbr varchar(60), contact_person varchar(100),location varchar(255),introduction varchar(255),email varchar(100), mobile varchar(100), phone varchar(100), address VARCHAR(255), city varchar(100), postal_code varchar(60),"
                    + " create_date TIMESTAMP, Last_updated_stamp TIMESTAMP);");

            // product 产品
            stmt.executeUpdate("create table if not exists product(id bigint auto_increment PRIMARY KEY,item_name varchar(50),item_no varchar(255),size double,width double,unit varchar(255),volume double,weight double,item_desc varchar(5120),category varchar(255),customer_id bigint,FOREIGN KEY(customer_id) REFERENCES party(id));");

            // warehouse 仓库
            stmt.executeUpdate("create table if not exists warehouse(id bigint auto_increment PRIMARY KEY,warehouse_name varchar(50),warehouse_address varchar(255),warehouse_area double,path varchar(255),warehouse_desc VARCHAR(5120),contact_id bigint,FOREIGN KEY(contact_id) REFERENCES contact(id));");

            // order_status 里程碑
            stmt.executeUpdate("create table if not exists order_status(id bigint auto_increment PRIMARY KEY,status_code varchar(20),status_name varchar(20),order_type varchar(20),remark varchar(255));");
            // return_order 回单
            stmt.executeUpdate("create table if not exists return_order(id bigint auto_increment PRIMARY KEY, order_no varchar(50), status_code varchar(20),create_date TIMESTAMP,transaction_status varchar(20),order_type varchar(20),creator varchar(50),remark varchar(255), transfer_order_id bigint, delivery_order_id bigint, notity_party_id bigint,customer_id bigint,route_id bigint);");
            // transfer_order 运输单
            stmt.executeUpdate("create table if not exists transfer_order(id bigint auto_increment PRIMARY KEY,order_no varchar(255),status varchar(255),"
                    + "cargo_nature VARCHAR(255),pickup_mode VARCHAR(255),arrival_mode VARCHAR(255),remark varchar(255),create_by bigint,"
                    + "create_stamp TIMESTAMP,last_modified_by bigint,last_modified_stamp TIMESTAMP,eta TIMESTAMP,address varchar(255),route_from varchar(255),route_to varchar(255),"
                    + "route_id bigint,customer_id bigint,sp_id bigint,notify_party_id bigint,FOREIGN KEY(customer_id) REFERENCES party(id),FOREIGN KEY(sp_id) REFERENCES party(id),"
                    + "FOREIGN KEY(route_id) REFERENCES route(id),FOREIGN KEY(notify_party_id) REFERENCES party(id));");
            // transfer_order_item 货品明细
            stmt.executeUpdate("create table if not exists transfer_order_item(id bigint auto_increment PRIMARY KEY,item_no varchar(255),item_name varchar(255),item_desc varchar(255),"
                    + "amount double,unit varchar(255),volume double,weight double,remark varchar(5120),order_id bigint,FOREIGN KEY(order_id) REFERENCES transfer_order(id));");

            // Transfer_Order_item_detail 单件货品明细
            stmt.executeUpdate("create table if not exists transfer_order_item_detail(id bigint auto_increment PRIMARY KEY,order_id bigint,item_id bigint,item_no varchar(255),"
                    + "serial_no varchar(255),item_name varchar(255),item_desc varchar(255),unit varchar(255),volume double,weight double,notify_party_id bigint,"
                    + "remark varchar(5120),is_damage boolean,estimate_damage_amount double,damage_revenue double,damage_payment double,damage_remark varchar(255),FOREIGN KEY(order_id) REFERENCES transfer_order(id),"
                    + "FOREIGN KEY(item_id) REFERENCES transfer_order_item(id),FOREIGN KEY(notify_party_id) REFERENCES party(id));");
            // Transfer_Order_fin_item 运输单应收应付明细
            stmt.executeUpdate("create table if not exists transfer_order_fin_item (id bigint auto_increment PRIMARY KEY, order_id bigint, fin_item_id bigint,"
                    + "fin_item_code varchar(20), amount double, status varchar(50), "
                    + "creator varchar(50), create_date timestamp, last_updator varchar(50), last_update_date timestamp);");

            // transfer_order_milestone 运输单里程碑
            stmt.executeUpdate("create table if not exists transfer_order_milestone(id bigint auto_increment PRIMARY KEY,status varchar(255),location varchar(255),create_by bigint,create_stamp TIMESTAMP,last_modified_by bigint,"
                    + "last_modified_stamp TIMESTAMP,order_id bigint,FOREIGN KEY(order_id) REFERENCES transfer_order(id));");

            // billing_order 应收应付单主表
            stmt.executeUpdate("create table if not exists billing_order(id bigint auto_increment PRIMARY KEY, blling_order_no varchar(255), "
                    + "order_type varchar(50), customer_id bigint, customer_type varchar(50), charge_account_id bigint, payment_account_id bigint, status varchar(255),"
                    + "transfer_order_id bigint, delivery_order_id bigint, remark varchar(1024), creator bigint, create_stamp TIMESTAMP,last_modified_by bigint,"
                    + "last_modified_stamp TIMESTAMP, approver bigint, approve_date TIMESTAMP);");
            // billing_order_item 应收应付单从表
            stmt.executeUpdate("create table if not exists billing_order_item(id bigint auto_increment PRIMARY KEY,blling_order_id bigint, "
                    + "charge_account_id bigint, payment_account_id bigint, status varchar(255), amount double, remark varchar(1024),"
                    + "creator bigint, create_stamp TIMESTAMP,last_modified_by bigint,"
                    + "last_modified_stamp TIMESTAMP, approver bigint, approve_date TIMESTAMP);");

            stmt.executeUpdate("create table if not exists pickup_order(id bigint auto_increment PRIMARY KEY,order_no varchar(20),sp_id bigint,status varchar(20),cargo_nature varchar(20),to_type varchar(20),remark varchar(255),create_by bigint,Create_stamp timestamp,Last_modified_by bigint,Last_modified_stamp timestamp);"); // 拼车单表从表
                                                                                                                                                                                                                                                                                                                                      // stmt.executeUpdate(
            stmt.executeUpdate("create table if not exists Pickup_order_item(id bigint auto_increment PRIMARY KEY,Order_id bigint,Customer_id bigint);");
            // 发车单
            stmt.executeUpdate("create table if not exists departrue_order(id bigint auto_increment PRIMARY KEY,Order_id bigint,Transfer_id bigint,creat_by bigint,creat_stamp timestamp,last_modified_by bigint,remark varchar(255),last_modified_starp timestamp);");
            stmt.close();
            // conn.commit();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initData(C3p0Plugin cp) {
        try {
            cp.start();
            Connection conn = cp.getDataSource().getConnection();
            Statement stmt = conn.createStatement();

            stmt.executeUpdate("insert into user_login(user_name, password, password_hint) values('d_user1', '123456', '1-6');");
            stmt.executeUpdate("insert into user_login(user_name, password, password_hint) values('d_user2', '123456', '1-6');");
            stmt.executeUpdate("insert into user_login(user_name, password, password_hint) values('demo', '123456', '1-6');");
            stmt.executeUpdate("insert into user_login(user_name, password, password_hint) values('jason', '123456', '1-6');");

            stmt.executeUpdate("insert into office(office_code, office_name, office_person,phone,address,email,type,company_intro) values('1201', '广州分公司', '张三','020-12312322','香洲珠海市香洲区老香洲为农街为农市场','123@qq.com','自营','这是一家公司');");
            stmt.executeUpdate("insert into office(office_code, office_name, office_person,phone,address,email,type,company_intro) values('121', '珠公司', '张三','020-12312322','香洲珠海市香洲区老香洲为农街为农市场','123@qq.com','控股','这是一家公司');");
            stmt.executeUpdate("insert into office(office_code, office_name, office_person,phone,address,email,type,company_intro) values('101', '深圳分公司','张三','020-12312322','香洲珠海市香洲区老香洲为农街为农市场','123@qq.com','合作','这是一家公司');");

            stmt.executeUpdate("insert into fin_account(name,type,currency,org_name,account_pin,remark) values('李志坚','收费','人民币','建设银行','12123123123','');");
            stmt.executeUpdate("insert into fin_account(name,type,currency,org_name,account_pin,remark) values('李四','收费','人民币','建设银行','12123123123','');");
            stmt.executeUpdate("insert into fin_account(name,type,currency,org_name,account_pin,remark) values('张三','付费','人民币','建设银行','12123123123','');");

            stmt.executeUpdate("insert into contract(name,type,party_id,period_from,period_to,remark) values('客户合同','CUSTOMER', 4,'2014-11-12','2014-11-14','无');");
            stmt.executeUpdate("insert into contract(name,type,party_id,period_from,period_to,remark) values('客户合同','CUSTOMER', 5,'2014-10-12','2014-11-15','无');");
            stmt.executeUpdate("insert into contract(name,type,party_id,period_from,period_to,remark) values('供应商合同','SERVICE_PROVIDER', 6,'2011-1-12','2014-10-14','无');");
            stmt.executeUpdate("insert into contract(name,type,party_id,period_from,period_to,remark) values('供应商合同','SERVICE_PROVIDER', 7,'2013-11-12','2014-11-14','无');");

            stmt.executeUpdate("insert into route(from_id,location_from,to_id,location_to,remark) values('110000','北京','110103','宣武区','123123');");
            stmt.executeUpdate("insert into route(from_id,location_from,to_id,location_to,remark) values('110000','北京','120000','天津','123123');");
            stmt.executeUpdate("insert into route(from_id,location_from,to_id,location_to,remark) values('120000','天津','110000','北京','123123');");

            stmt.executeUpdate("insert into contract_item(contract_id,Fin_item_id,amount,from_id,location_from,to_id,location_to,remark) values('1','1','120000','110000','北京','110103','宣武区','路线');");
            stmt.executeUpdate("insert into contract_item(contract_id,Fin_item_id,amount,from_id,location_from,to_id,location_to,remark) values('2','2','130000','110000','北京','120000','天津','路线2');");
            stmt.executeUpdate("insert into contract_item(contract_id,Fin_item_id,amount,from_id,location_from,to_id,location_to,remark) values('1','3','120000','120000','天津','110000','北京','路线');");
            stmt.executeUpdate("insert into contract_item(contract_id,Fin_item_id,amount,remark) values('2','1','130000','路线2');");
            stmt.executeUpdate("insert into contract_item(contract_id,amount,remark) values('3','120000','路线');");
            stmt.executeUpdate("insert into contract_item(contract_id,amount,remark) values('4','130000','路线2');");

            // 系统权限
            stmt.executeUpdate("insert into role_permissions(role_name, role_permission, remark) values('root', '123456', '1-6');");
            // alter table leads add(priority varchar(50),customer_source
            // varchar(50), building_name varchar(255), building_no varchar(50),
            // room_no varchar(50)); 2-6
            // alter table leads add(building_unit varchar(50), is_have_car
            // char(1) default 'N');
            // alter table leads add(is_public char(1) default 'N');

            String propertySql = "insert into leads(title, create_date, creator, status, type, "
                    + "region, intro, remark, lowest_price, agent_fee, introducer, sales, follower, "
                    + "follower_phone, owner, owner_phone, customer_source, building_name, building_no, room_no, building_unit) values("
                    + "'%d 初始测试数据-老香洲两盘', CURRENT_TIMESTAMP(), 'jason', '出租', "
                    + "'1房', '老香洲', "
                    + "'本月均价8260元/㎡，环比上月 ↑0.22 ，同比去年 ↑14.67 ，查看房价详情>>二 手 房50 套 所在区域香洲 老香洲小区地址香洲珠海市香洲区老香洲为农街为农市场地图>>建筑年代1995-01-01',"
                    + "'remark.....', 7000, 7500, "
                    + "'介绍人金', 'kim', 'jason', '13509871234',"
                    + "'张生', '0756-12345678-123', '58自来客', '五洲花城2期', '2', '1320', '3');";

            // for (int i = 0; i < 50; i++) {
            // String newPropertySql = String.format(propertySql, i);
            // stmt.executeUpdate(newPropertySql);
            // }
            String sqlPrefix = "insert into leads(title, priority, create_date, creator, status, type, "
                    + "region, intro, remark, lowest_price, agent_fee, introducer, sales, follower, follower_phone, "
                    + "owner, owner_phone, area, total, customer_source, building_name, building_no, room_no, building_unit) values(";

            stmt.executeUpdate(sqlPrefix
                    + "'初始测试数据-老香洲楼盘', '1重要紧急', CURRENT_TIMESTAMP(), 'jason', '出租', "
                    + "'1房', '老香洲', "
                    + "'老香洲楼盘 2房2卫',"
                    + "'remark.....', 7000, 7500, "
                    + "'介绍人金', 'kim', 'jason', '13509871234',"
                    + "'张生', '0756-12345678-123', 36, 1200, '58自来客', '五洲花城2期', '2', '1320', '3');");

            stmt.executeUpdate(sqlPrefix
                    + "'初始测试数据-新香洲楼盘', '1重要紧急', CURRENT_TIMESTAMP(), 'jason', '出售', "
                    + "'2房', '新香洲', "
                    + "'新香洲楼盘 2房2卫',"
                    + "'remark.....', 7000, 7500, "
                    + "'介绍人金', 'kim', 'jason', '13509871234',"
                    + "'张生', '0756-12345678-123', 78, 56, '58自来客', '五洲花城2期', '3', '1321', '5');");

            stmt.executeUpdate(sqlPrefix
                    + "'初始测试数据-老香洲楼盘', '2重要不紧急', CURRENT_TIMESTAMP(), 'jason', '已租', "
                    + "'3房', '老香洲', "
                    + "'本月均价8260元/㎡，环比上月 ↑0.22 ，同比去年 ↑14.67 ，查看房价详情>>二 手 房50 套 所在区域香洲 老香洲小区地址香洲珠海市香洲区老香洲为农街为农市场地图>>建筑年代1995-01-01',"
                    + "'remark.....', 7000, 7500, "
                    + "'介绍人金', 'kim', 'jason', '13509871234',"
                    + "'张生', '0756-12345678-123', 92, 2300, '58自来客', '五洲花城2期', '4', '1320', '3');");

            stmt.executeUpdate(sqlPrefix
                    + "'初始测试数据-拱北楼盘', '2重要不紧急', CURRENT_TIMESTAMP(), 'jason', '已售', "
                    + "'4房', '拱北', "
                    + "'本月均价8260元/㎡，环比上月 ↑0.22 ，同比去年 ↑14.67 ，查看房价详情>>二 手 房50 套 所在区域香洲 老香洲小区地址香洲珠海市香洲区老香洲为农街为农市场地图>>建筑年代1995-01-01',"
                    + "'remark.....', 7000, 7500, "
                    + "'介绍人金', 'kim', 'jason', '13509871234',"
                    + "'张生', '0756-12345678-123', 150, 120, '58自来客', '五洲花城2期', '6', '1320', '3');");

            stmt.executeUpdate(sqlPrefix
                    + "'初始测试数据-柠溪楼盘', '3不重要紧急', CURRENT_TIMESTAMP(), 'jason', '出租', "
                    + "'5房', '柠溪', "
                    + "'本月均价8260元/㎡，环比上月 ↑0.22 ，同比去年 ↑14.67 ，查看房价详情>>二 手 房50 套 所在区域香洲 老香洲小区地址香洲珠海市香洲区老香洲为农街为农市场地图>>建筑年代1995-01-01',"
                    + "'remark.....', 7000, 7500, "
                    + "'介绍人金', 'kim', 'jason', '13509871234',"
                    + "'张生', '0756-12345678-123', 180, 5000, '58自来客', '五洲花城2期', '', '1325', '8');");

            stmt.executeUpdate(sqlPrefix
                    + "'初始测试数据-柠溪楼盘', '3不重要紧急', CURRENT_TIMESTAMP(), 'jason', '出租', "
                    + "'6房', '柠溪', "
                    + "'本月均价8260元/㎡，环比上月 ↑0.22 ，同比去年 ↑14.67 ，查看房价详情>>二 手 房50 套 所在区域香洲 老香洲小区地址香洲珠海市香洲区老香洲为农街为农市场地图>>建筑年代1995-01-01',"
                    + "'remark.....', 7000, 7500, "
                    + "'介绍人金', 'kim', 'jason', '13509871234',"
                    + "'张生', '0756-12345678-123', 180, 5000, '58自来客', '五洲花城2期', '2', '', '5');");

            stmt.executeUpdate(sqlPrefix
                    + "'初始测试数据-柠溪楼盘', '3不重要紧急', CURRENT_TIMESTAMP(), 'jason', '出租', "
                    + "'6房以上', '柠溪', "
                    + "'本月均价8260元/㎡，环比上月 ↑0.22 ，同比去年 ↑14.67 ，查看房价详情>>二 手 房50 套 所在区域香洲 老香洲小区地址香洲珠海市香洲区老香洲为农街为农市场地图>>建筑年代1995-01-01',"
                    + "'remark.....', 7000, 7500, "
                    + "'介绍人金', 'kim', 'jason', '13509871234',"
                    + "'张生', '0756-12345678-123', 180, 5000, '58自来客', '五洲花城2期', '2', '1322', '');");

            stmt.executeUpdate(sqlPrefix
                    + "'初始测试数据-前山地皮', '4不重要不紧急', CURRENT_TIMESTAMP(), 'd_user1', '已售', "
                    + "'地皮', '前山', "
                    + "'本月均价8260元/㎡，环比上月 ↑0.22 ，同比去年 ↑14.67 ，查看房价详情>>二 手 房50 套 所在区域香洲 老香洲小区地址香洲珠海市香洲区老香洲为农街为农市场地图>>建筑年代1995-01-01',"
                    + "'remark.....', 7000, 7500, "
                    + "'介绍人金', 'kim', 'd_user1', '13509871234',"
                    + "'张生', '0756-12345678-123', 40000, 3000, '58自来客', '五洲花城2期', '8', '1320', '3');");

            stmt.executeUpdate(sqlPrefix
                    + "'初始测试数据-柠溪楼盘', '3不重要紧急', CURRENT_TIMESTAMP(), 'jason', '出租', "
                    + "'6房以上', '柠溪', "
                    + "'本月均价8260元/㎡，环比上月 ↑0.22 ，同比去年 ↑14.67 ，查看房价详情>>二 手 房50 套 所在区域香洲 老香洲小区地址香洲珠海市香洲区老香洲为农街为农市场地图>>建筑年代1995-01-01',"
                    + "'remark.....', 7000, 7500, "
                    + "'介绍人金', 'kim', 'jason', '13509871234',"
                    + "'张生', '0756-12345678-123', 180, 5000, '58自来客', '五洲花城2期', '2', '1322', '');");

            stmt.executeUpdate(sqlPrefix
                    + "'初始测试数据-前山地皮', '4不重要不紧急', CURRENT_TIMESTAMP(), 'd_user1', '已售', "
                    + "'地皮', '前山', "
                    + "'本月均价8260元/㎡，环比上月 ↑0.22 ，同比去年 ↑14.67 ，查看房价详情>>二 手 房50 套 所在区域香洲 老香洲小区地址香洲珠海市香洲区老香洲为农街为农市场地图>>建筑年代1995-01-01',"
                    + "'remark.....', 7000, 7500, "
                    + "'介绍人金', 'kim', 'd_user1', '13509871234',"
                    + "'张生', '0756-12345678-123', 40000, 3000, '58自来客', '五洲花城2期', '8', '1320', '3');");

            stmt.executeUpdate(sqlPrefix
                    + "'初始测试数据-柠溪楼盘', '3不重要紧急', CURRENT_TIMESTAMP(), 'jason', '出租', "
                    + "'6房以上', '柠溪', "
                    + "'本月均价8260元/㎡，环比上月 ↑0.22 ，同比去年 ↑14.67 ，查看房价详情>>二 手 房50 套 所在区域香洲 老香洲小区地址香洲珠海市香洲区老香洲为农街为农市场地图>>建筑年代1995-01-01',"
                    + "'remark.....', 7000, 7500, "
                    + "'介绍人金', 'kim', 'jason', '13509871234',"
                    + "'张生', '0756-12345678-123', 180, 5000, '58自来客', '五洲花城2期', '2', '1322', '');");

            stmt.executeUpdate(sqlPrefix
                    + "'初始测试数据-前山地皮', '4不重要不紧急', CURRENT_TIMESTAMP(), 'd_user1', '已售', "
                    + "'地皮', '前山', "
                    + "'本月均价8260元/㎡，环比上月 ↑0.22 ，同比去年 ↑14.67 ，查看房价详情>>二 手 房50 套 所在区域香洲 老香洲小区地址香洲珠海市香洲区老香洲为农街为农市场地图>>建筑年代1995-01-01',"
                    + "'remark.....', 7000, 7500, "
                    + "'介绍人金', 'kim', 'd_user1', '13509871234',"
                    + "'张生', '0756-12345678-123', 40000, 3000, '58自来客', '五洲花城2期', '8', '1320', '3');");

            stmt.executeUpdate("insert into support_case(title, create_date, creator, status, type, case_desc, note) values("
                    + "'这是一个建议示例', CURRENT_TIMESTAMP(), 'jason', '新提交','出错', '这是一个建议示例，您可以在这里提交你所遇到的问题，我们会尽快跟进。', '这是回答的地方');");

            stmt.executeUpdate("insert into order_header(order_no, type, status, creator, create_date,  remark) values("
                    + "'SalesOrder001', 'SALES_ORDER', 'New', 'jason', CURRENT_TIMESTAMP(), '这是一个销售订单示例');");
            stmt.executeUpdate("insert into order_item(order_id, item_name, item_desc, quantity, unit_price) values("
                    + "1, 'P001', 'iPad Air', 1, 3200);");
            // 回单notity_party_id bigint,customer_id
            stmt.executeUpdate("insert into return_order(order_no,create_date,transaction_status,order_type,creator,remark,transfer_order_id, delivery_order_id, notity_party_id,customer_id,route_id) values('回单20132014001', CURRENT_TIMESTAMP(), 'new','应收','张三','这是一张回单','1', 1, '1','4','1');");
            stmt.executeUpdate("insert into return_order(order_no,create_date,transaction_status,order_type,creator,remark,transfer_order_id, notity_party_id,customer_id,route_id) values('回单20132014002', CURRENT_TIMESTAMP(), 'confirmed','应收','张三','这是一张回单','2', '2','4','2');");
            stmt.executeUpdate("insert into return_order(order_no,create_date,transaction_status,order_type,creator,remark,transfer_order_id, delivery_order_id, notity_party_id,customer_id,route_id) values('回单20132014003', CURRENT_TIMESTAMP(), 'cancel','应收','张三','这是一张回单','1', 1, '1','4','1');");
            stmt.executeUpdate("insert into return_order(order_no,create_date,transaction_status,order_type,creator,remark,transfer_order_id, delivery_order_id, notity_party_id,customer_id,route_id) values('回单20132014004', CURRENT_TIMESTAMP(), 'new','应收','张三','这是一张回单','2', 1, '2','5','2');");
            stmt.executeUpdate("insert into return_order(order_no,create_date,transaction_status,order_type,creator,remark,transfer_order_id, delivery_order_id, notity_party_id,customer_id,route_id) values('回单20132014005', CURRENT_TIMESTAMP(), 'new','应收','张三','这是一张回单','1', 1, '1','5','1');");
            stmt.executeUpdate("insert into return_order(order_no,create_date,transaction_status,order_type,creator,remark,transfer_order_id, delivery_order_id, notity_party_id,customer_id,route_id) values('回单20132014006', CURRENT_TIMESTAMP(), 'confirmed','应收','张三','这是一张回单','2', 1, '2','5','2');");
            stmt.executeUpdate("insert into return_order(order_no,create_date,transaction_status,order_type,creator,remark,transfer_order_id, delivery_order_id, notity_party_id,customer_id,route_id) values('回单20132014007', CURRENT_TIMESTAMP(), 'confirmed','应收','张三','这是一张回单','1', 1, '1','6','1');");
            stmt.executeUpdate("insert into return_order(order_no,create_date,transaction_status,order_type,creator,remark,transfer_order_id, delivery_order_id, notity_party_id,customer_id,route_id) values('回单20132014008', CURRENT_TIMESTAMP(), 'confirmed','应收','张三','这是一张回单','2', 1, '2','6','2');");
            stmt.executeUpdate("insert into return_order(order_no,create_date,transaction_status,order_type,creator,remark,transfer_order_id, delivery_order_id, notity_party_id,customer_id,route_id) values('回单20132014009', CURRENT_TIMESTAMP(), 'confirmed','应收','张三','这是一张回单','1', 1, '1','6','1');");
            stmt.executeUpdate("insert into return_order(order_no,create_date,transaction_status,order_type,creator,remark,transfer_order_id, delivery_order_id, notity_party_id,customer_id,route_id) values('回单20132014010', CURRENT_TIMESTAMP(), 'new','应收','张三','这是一张回单','2', 1, '2','7','2');");
            // 运输单应收应付明细id bigint auto_increment PRIMARY KEY, order_id bigint,
            // fin_item_id bigint,"
            // +
            // "fin_item_code varchar(20), amount double, status varchar(50), "
            stmt.executeUpdate("insert into transfer_order_fin_item(order_id,fin_item_id,fin_item_code,amount,status) values('1','1','20132014','5200','完成');");
            stmt.executeUpdate("insert into transfer_order_fin_item(order_id,fin_item_id,fin_item_code,amount,status) values('1','2','20132015','5200','完成');");
            stmt.executeUpdate("insert into transfer_order_fin_item(order_id,fin_item_id,fin_item_code,amount,status) values('1','3','20132016','5200','完成');");
            stmt.executeUpdate("insert into transfer_order_fin_item(order_id,fin_item_id,fin_item_code,amount,status) values('2','1','20132014','3200','未完成');");
            stmt.executeUpdate("insert into transfer_order_fin_item(order_id,fin_item_id,fin_item_code,amount,status) values('2','2','20132015','3200','未完成');");
            // 角色表

            // stmt.executeUpdate("insert into role_table(role_name,role_time,role_people,role_lasttime,role_lastpeople) values('浠撶',CURRENT_TIMESTAMP(),'寮犱笁',CURRENT_TIMESTAMP(),'鏉庡洓');");
            // stmt.executeUpdate("insert into role_table(role_name,role_time,role_people,role_lasttime,role_lastpeople) values('璋冨害',CURRENT_TIMESTAMP(),'鐜嬩簲',CURRENT_TIMESTAMP(),'璧靛叚');");
            // 鏀惰垂浠樿垂
            // stmt.executeUpdate("insert into Toll_table(code,name,type,Remark) values('20132014','杩愯緭鏀惰垂','鏀惰垂','杩欐槸涓€寮犺繍杈撴敹璐瑰崟');");
            // stmt.executeUpdate("insert into Toll_table(code,name,type,Remark) values('20142015','杩愯緭浠樿垂','浠樿垂','杩欐槸涓€寮犺繍杈撲粯璐瑰崟');");
            // 鏉冮檺琛ㄥ畾涔
            stmt.executeUpdate("insert into privilege_table(privilege) values('*');");
            stmt.executeUpdate("insert into privilege_table(privilege) values('view');");
            stmt.executeUpdate("insert into privilege_table(privilege) values('create');");
            stmt.executeUpdate("insert into privilege_table(privilege) values('update');");
            stmt.executeUpdate("insert into privilege_table(privilege) values('delete');");

            // 收费条目定义表code VARCHAR(50),name VARCHAR(50),type VARCHAR(50),Remark

            stmt.executeUpdate("insert into Fin_item(code,name,type,Remark) values("
                    + "'2013201448','干线运输费','应收','这是一张运输单收费');");
            stmt.executeUpdate("insert into Fin_item(code,name,type,Remark) values("
                    + "'2013201448','搬运费','应收','这是一张运输单收费');");
            stmt.executeUpdate("insert into Fin_item(code,name,type,Remark) values("
                    + "'2013201448','上楼费','应收','这是一张运输单收费');");

            // 贷款客户 attributes
            for (int i = 1; i <= 1; i++) {
                stmt.executeUpdate("insert into party(party_type, create_date, creator) values('贷款客户', CURRENT_TIMESTAMP(), 'demo');");
                stmt.executeUpdate("insert into party_attribute(party_id, attr_name, attr_value) values("
                        + i + ", 'priority', '1重要紧急');");
                stmt.executeUpdate("insert into party_attribute(party_id, attr_name, attr_value) values("
                        + i + ", 'name', '温生');");
                stmt.executeUpdate("insert into party_attribute(party_id, attr_name, attr_value) values("
                        + i + ", 'loan_max', '15万');");
                stmt.executeUpdate("insert into party_attribute(party_id, attr_name, attr_value) values("
                        + i + ", 'mobile', '1357038829');");
                stmt.executeUpdate("insert into party_attribute(party_id, attr_name, attr_value) values("
                        + i + ", 'email', 'test@test.com');");
            }

            // 地产客户
            Party p = new Party();
            Date createDate = Calendar.getInstance().getTime();
            p.set("party_type", "地产客户").set("create_date", createDate)
                    .set("creator", "jason").save();
            long partyId = p.getLong("id");
            PartyAttribute pa = new PartyAttribute();
            pa.set("party_id", partyId).set("attr_name", "title")
                    .set("attr_value", "求2房近3小").save();
            PartyAttribute pa1 = new PartyAttribute();
            pa1.set("party_id", partyId).set("attr_name", "client_name")
                    .set("attr_value", "温生").save();
            PartyAttribute paPriority = new PartyAttribute();
            paPriority.set("party_id", partyId).set("attr_name", "priority")
                    .set("attr_value", "1重要紧急").save();
            PartyAttribute pa2 = new PartyAttribute();
            pa2.set("party_id", partyId).set("attr_name", "status")
                    .set("attr_value", "求租").save();
            PartyAttribute pa3 = new PartyAttribute();
            pa3.set("party_id", partyId).set("attr_name", "region")
                    .set("attr_value", "老香洲").save();
            PartyAttribute pa4 = new PartyAttribute();
            pa4.set("party_id", partyId).set("attr_name", "type")
                    .set("attr_value", "1房").save();

            // 外部user 创建的客户
            Party p1 = new Party();
            createDate = Calendar.getInstance().getTime();
            p1.set("party_type", "地产客户").set("create_date", createDate)
                    .set("creator", "demo").save();
            partyId = p1.getLong("id");
            PartyAttribute p1_pa = new PartyAttribute();
            p1_pa.set("party_id", partyId).set("attr_name", "title")
                    .set("attr_value", "求前山小区").save();
            PartyAttribute p1_pa1 = new PartyAttribute();
            p1_pa1.set("party_id", partyId).set("attr_name", "client_name")
                    .set("attr_value", "温生").save();
            PartyAttribute p1_paPriority = new PartyAttribute();
            p1_paPriority.set("party_id", partyId).set("attr_name", "priority")
                    .set("attr_value", "1重要紧急").save();
            PartyAttribute p1_pa2 = new PartyAttribute();
            p1_pa2.set("party_id", partyId).set("attr_name", "status")
                    .set("attr_value", "求购").save();
            PartyAttribute p1_pa3 = new PartyAttribute();
            p1_pa3.set("party_id", partyId).set("attr_name", "region")
                    .set("attr_value", "拱北").save();
            PartyAttribute p1_pa4 = new PartyAttribute();
            p1_pa4.set("party_id", partyId).set("attr_name", "type")
                    .set("attr_value", "1房").save();
            PartyAttribute p1_pa5 = new PartyAttribute();
            p1_pa5.set("party_id", partyId).set("attr_name", "area")
                    .set("attr_value", "120").save();
            PartyAttribute p1_pa6 = new PartyAttribute();
            p1_pa6.set("party_id", partyId).set("attr_name", "total")
                    .set("attr_value", "200").save();

            newCustomer();
            // 其他客户 attributes
            stmt.executeUpdate("insert into party(party_type, create_date, creator) values('其他客户', CURRENT_TIMESTAMP(), 'demo');");
            stmt.executeUpdate("insert into party_attribute(party_id, attr_name, attr_value) values(1, 'note', '工商注册');");
            stmt.executeUpdate("insert into party_attribute(party_id, attr_name, attr_value) values(1, 'mobile', '1357038829');");
            stmt.executeUpdate("insert into party_attribute(party_id, attr_name, attr_value) values(1, 'email', 'test@test.com');");

            // 运输单
            stmt.executeUpdate("insert into transfer_order(CARGO_NATURE, SP_ID, NOTIFY_PARTY_ID, ORDER_NO, CREATE_BY, PICKUP_MODE, CUSTOMER_ID, STATUS, CREATE_STAMP, ARRIVAL_MODE,route_id,address,ROUTE_FROM,ROUTE_TO) values('ATM', '8', '9', '2014042600001', '3', 'routeSP', '5', '新建', '2014-04-20 16:33:35.1', 'delivery','1','珠海','110102','440402');");
            stmt.executeUpdate("insert into transfer_order(CARGO_NATURE, SP_ID, NOTIFY_PARTY_ID, ORDER_NO, CREATE_BY, PICKUP_MODE, CUSTOMER_ID, STATUS, CREATE_STAMP, ARRIVAL_MODE,route_id,address,ROUTE_FROM,ROUTE_TO) values('cargo ', '7', '10', '2014042600002', '4', 'pickupSP', '4', '已入库', '2014-04-16 16:40:35.1', 'gateIn','2','中山','110105','442000');");
            stmt.executeUpdate("insert into transfer_order(CARGO_NATURE, SP_ID, NOTIFY_PARTY_ID, ORDER_NO, CREATE_BY, PICKUP_MODE, CUSTOMER_ID, STATUS, CREATE_STAMP, ARRIVAL_MODE,address) values('cargo', '7', '9', '2014042600003', '4', 'pickupSP', '5', '已入库', '2014-04-28 16:46:35.1', 'gateIn','广州');");
            stmt.executeUpdate("insert into transfer_order(CARGO_NATURE, SP_ID, NOTIFY_PARTY_ID, ORDER_NO, CREATE_BY, PICKUP_MODE, CUSTOMER_ID, STATUS, CREATE_STAMP, ARRIVAL_MODE,address) values('cargo', '8', '10', '2014042600004', '3', 'own', '4', '已发车', '2014-04-25 16:35:35.1', 'gateIn','深圳');");
            stmt.executeUpdate("insert into transfer_order(CARGO_NATURE, SP_ID, NOTIFY_PARTY_ID, ORDER_NO, CREATE_BY, PICKUP_MODE, CUSTOMER_ID, STATUS, CREATE_STAMP, ARRIVAL_MODE,address) values('ATM', '7', '10', '2014042600005', '3', 'own', '5', '已入库', '2014-04-22 16:28:35.1', 'delivery','东莞');");
            stmt.executeUpdate("insert into transfer_order(CARGO_NATURE, SP_ID, NOTIFY_PARTY_ID, ORDER_NO, CREATE_BY, PICKUP_MODE, CUSTOMER_ID, STATUS, CREATE_STAMP, ARRIVAL_MODE,address) values('ATM', '7', '9', '2014042600006', '3', 'own', '5', '已发车', '2014-04-24 16:58:35.1', 'gateIn','东莞');");
            // 货品明细
            stmt.executeUpdate("insert into transfer_order_item(item_no, item_name, item_desc,amount,unit,volume,weight,remark,order_id) "
                    + "values('123456', 'ATM', '这是一台ATM','1','台','452','100','一台ATM','1');");
            stmt.executeUpdate("insert into transfer_order_item(item_no, item_name, item_desc,amount,unit,volume,weight,remark,order_id) "
                    + "values('54321', '音箱', '这是对音响','5','对','50','10','一对音响','2');");
            stmt.executeUpdate("insert into transfer_order_item(item_no, item_name, item_desc,amount,unit,volume,weight,remark,order_id) "
                    + "values('25895', '电视', '这是一台电视','10','台','452','100','TCL电视机','2');");
            stmt.executeUpdate("insert into transfer_order_item(item_no, item_name, item_desc,amount,unit,volume,weight,remark,order_id) "
                    + "values('51456', '电脑', '这是一台电脑','12','台','50','10','十二台电脑','2');");
            stmt.executeUpdate("insert into transfer_order_item(item_no, item_name, item_desc,amount,unit,volume,weight,remark,order_id) "
                    + "values('123456', '电脑', '这是一台电脑','3','台','452','100','一台ATM','3');");
            stmt.executeUpdate("insert into transfer_order_item(item_no, item_name, item_desc,amount,unit,volume,weight,remark,order_id) "
                    + "values('123456', '电视', '这是一台电视','2','台','452','100','一台ATM','4');");
            stmt.executeUpdate("insert into transfer_order_item(item_no, item_name, item_desc,amount,unit,volume,weight,remark,order_id) "
                    + "values('123456', 'ATM', '这是一台ATM','1','台','452','100','一台ATM','5');");
            stmt.executeUpdate("insert into transfer_order_item(item_no, item_name, item_desc,amount,unit,volume,weight,remark,order_id) "
                    + "values('123456', 'ATM', '这是一台ATM','1','台','452','100','一台ATM','6');");
            // transfer_order_item_detail(id bigint auto_increment PRIMARY
            // KEY,order_id bigint,item_id bigint,item_no varchar(255),"
            // +
            // "serial_no varchar(255),item_name varchar(255),item_desc varchar(255),unit varchar(255),volume double,weight double,notify_party_id bigint,contact_id bigint,"
            // +
            // "remark varchar(5120),is_damage boolean,estimate_damage_amount double,damage_revenue double,damage_payment double,damage_remark varchar(255),FOREIGN KEY(order_id) REFERENCES transfer_order(id),"
            // + "FOREIGN KEY(item_id) REFERENCES transfer_order_item(id
            stmt.executeUpdate("insert into transfer_order_item_detail(order_id,serial_no,estimate_damage_amount,item_name,is_damage,item_id,notify_party_id) "
                    + "values('2', 'dkjf5421', '10000', '音箱', 'true', '2', '9');");
            stmt.executeUpdate("insert into transfer_order_item_detail(order_id,serial_no,estimate_damage_amount,item_name,is_damage,item_id,notify_party_id) "
                    + "values('2', 'dkjf5421', '10000', '音箱', 'true', '2', '9');");
            stmt.executeUpdate("insert into transfer_order_item_detail(order_id,serial_no,estimate_damage_amount,item_name,is_damage,item_id,notify_party_id) "
                    + "values('2', 'dkjf5421', '10000', '音箱', 'true', '2', '10');");
            stmt.executeUpdate("insert into transfer_order_item_detail(order_id,serial_no,estimate_damage_amount,item_name,is_damage,item_id,notify_party_id) "
                    + "values('2', 'dkjf5421', '10000', '音箱', 'true', '3', '9');");
            stmt.executeUpdate("insert into transfer_order_item_detail(order_id,serial_no,estimate_damage_amount,item_name,is_damage,item_id,notify_party_id) "
                    + "values('2', 'dkjf5421', '10000', '音箱', 'true', '3', '10');");
            stmt.executeUpdate("insert into transfer_order_item_detail(order_id,serial_no,estimate_damage_amount,item_name,is_damage,item_id,notify_party_id) "
                    + "values('1','fdgh1265985','10000', 'ATM', 'true','1','9');");
            stmt.executeUpdate("insert into transfer_order_item_detail(order_id,serial_no,estimate_damage_amount,item_name,is_damage,item_id,notify_party_id) "
                    + "values('5','asdf1265985','10000', 'ATM', 'false','5','9');");
            stmt.executeUpdate("insert into transfer_order_item_detail(order_id,serial_no,estimate_damage_amount,item_name,is_damage,item_id,notify_party_id) "
                    + "values('6','iouu1265985','10000', 'ATM', 'false','6','10');");
            stmt.executeUpdate("insert into transfer_order_item_detail(order_id,serial_no,estimate_damage_amount,item_name,is_damage,item_id,notify_party_id) "
                    + "values('6','2221265985','10000', 'ATM', 'false','6','10');");

            // 配送单
            stmt.execute("insert into delivery_order(Order_no,Transfer_order_id,Customer_id,Sp_id,Notify_party_id,Status,) values('2014042600013','1','5','7','9','配送在途');");
            stmt.execute("insert into delivery_order(Order_no,Transfer_order_id,Customer_id,Sp_id,Notify_party_id,Status,) values('2014042600004','2','6','7','10','已签收');");
            stmt.execute("insert into delivery_order(Order_no,Transfer_order_id,Customer_id,Sp_id,Notify_party_id,Status,) values('2014042600014','3','5','8','9','取消');");
            stmt.execute("insert into delivery_order(Order_no,Transfer_order_id,Customer_id,Sp_id,Notify_party_id,Status,) values('2014042600003','4','6','8','10','配送在途');");

            // billing_order 应收应付单主表
            stmt.execute("insert into billing_order(blling_order_no, order_type, customer_id, customer_type, charge_account_id, payment_account_id, status,"
                    + "transfer_order_id, delivery_order_id, remark, creator, create_stamp, last_modified_by,"
                    + "last_modified_stamp, approver, approve_date) values"
                    + "('应收对账单001', 'charge_audit_order', 4, 'CUSTOMER', 1, 2, 'new', 1, 1, '演示数据', 1, CURRENT_TIMESTAMP(),1, CURRENT_TIMESTAMP(),"
                    + "1, CURRENT_TIMESTAMP());");
            stmt.execute("insert into billing_order(blling_order_no, order_type, customer_id, customer_type, charge_account_id, payment_account_id, status,"
                    + "transfer_order_id, delivery_order_id, remark, creator, create_stamp, last_modified_by,"
                    + "last_modified_stamp, approver, approve_date) values"
                    + "('应收对账单002', 'charge_audit_order', 4, 'CUSTOMER', 1, 2, 'checking', 1, 1, '演示数据', 1, CURRENT_TIMESTAMP(),1, CURRENT_TIMESTAMP(),"
                    + "1, CURRENT_TIMESTAMP());");
            stmt.execute("insert into billing_order(blling_order_no, order_type, customer_id, customer_type, charge_account_id, payment_account_id, status,"
                    + "transfer_order_id, delivery_order_id, remark, creator, create_stamp, last_modified_by,"
                    + "last_modified_stamp, approver, approve_date) values"
                    + "('应收对账单003', 'charge_audit_order', 4, 'CUSTOMER',1, 2, 'confirmed', 1, 1, '演示数据', 1, CURRENT_TIMESTAMP(),1, CURRENT_TIMESTAMP(),"
                    + "1, CURRENT_TIMESTAMP());");
            stmt.execute("insert into billing_order(blling_order_no, order_type, customer_id, customer_type, charge_account_id, payment_account_id, status,"
                    + "transfer_order_id, delivery_order_id, remark, creator, create_stamp, last_modified_by,"
                    + "last_modified_stamp, approver, approve_date) values"
                    + "('应收对账单004', 'charge_audit_order', 4, 'CUSTOMER',1, 2, 'completed', 1, 1, '演示数据', 1, CURRENT_TIMESTAMP(),1, CURRENT_TIMESTAMP(),"
                    + "1, CURRENT_TIMESTAMP());");
            stmt.execute("insert into billing_order(blling_order_no, order_type, customer_id, customer_type, charge_account_id, payment_account_id, status,"
                    + "transfer_order_id, delivery_order_id, remark, creator, create_stamp, last_modified_by,"
                    + "last_modified_stamp, approver, approve_date) values"
                    + "('应收对账单005', 'charge_audit_order',4, 'CUSTOMER', 1, 2, 'cancel', 1, 1, '演示数据', 1, CURRENT_TIMESTAMP(),1, CURRENT_TIMESTAMP(),"
                    + "1, CURRENT_TIMESTAMP());");
            stmt.execute("insert into billing_order(blling_order_no, order_type, customer_id, customer_type, charge_account_id, payment_account_id, status,"
                    + "transfer_order_id, delivery_order_id, remark, creator, create_stamp, last_modified_by,"
                    + "last_modified_stamp, approver, approve_date) values"
                    + "('应付对账单001', 'pay_audit_order', 7, 'SERVICE_PROVIDER', 1, 2, 'new', 1, 1, '演示数据', 1, CURRENT_TIMESTAMP(),1, CURRENT_TIMESTAMP(),"
                    + "1, CURRENT_TIMESTAMP());");
            stmt.execute("insert into billing_order(blling_order_no, order_type, customer_id, customer_type, charge_account_id, payment_account_id, status,"
                    + "transfer_order_id, delivery_order_id, remark, creator, create_stamp, last_modified_by,"
                    + "last_modified_stamp, approver, approve_date) values"
                    + "('应付对账单002', 'pay_audit_order', 7, 'SERVICE_PROVIDER', 1, 2, 'checking', 1, 1, '演示数据', 1, CURRENT_TIMESTAMP(),1, CURRENT_TIMESTAMP(),"
                    + "1, CURRENT_TIMESTAMP());");
            stmt.execute("insert into billing_order(blling_order_no, order_type, customer_id, customer_type, charge_account_id, payment_account_id, status,"
                    + "transfer_order_id, delivery_order_id, remark, creator, create_stamp, last_modified_by,"
                    + "last_modified_stamp, approver, approve_date) values"
                    + "('应付对账单003', 'pay_audit_order', 7, 'SERVICE_PROVIDER',1, 2, 'confirmed', 1, 1, '演示数据', 1, CURRENT_TIMESTAMP(),1, CURRENT_TIMESTAMP(),"
                    + "1, CURRENT_TIMESTAMP());");
            stmt.execute("insert into billing_order(blling_order_no, order_type, customer_id, customer_type, charge_account_id, payment_account_id, status,"
                    + "transfer_order_id, delivery_order_id, remark, creator, create_stamp, last_modified_by,"
                    + "last_modified_stamp, approver, approve_date) values"
                    + "('应付对账单004', 'pay_audit_order', 7, 'SERVICE_PROVIDER	',1, 2, 'completed', 1, 1, '演示数据', 1, CURRENT_TIMESTAMP(),1, CURRENT_TIMESTAMP(),"
                    + "1, CURRENT_TIMESTAMP());");
            stmt.execute("insert into billing_order(blling_order_no, order_type, customer_id, customer_type, charge_account_id, payment_account_id, status,"
                    + "transfer_order_id, delivery_order_id, remark, creator, create_stamp, last_modified_by,"
                    + "last_modified_stamp, approver, approve_date) values"
                    + "('应付对账单005', 'pay_audit_order',7, 'SERVICE_PROVIDER	', 1, 2, 'cancel', 1, 1, '演示数据', 1, CURRENT_TIMESTAMP(),1, CURRENT_TIMESTAMP(),"
                    + "1, CURRENT_TIMESTAMP());");
            // billing_order_item 应收应付单从表
            stmt.execute("create table if not exists billing_order_item(id bigint auto_increment PRIMARY KEY,blling_order_id bigint, "
                    + "charge_account_id bigint, payment_account_id bigint, status varchar(255), amount double, remark varchar(1024),"
                    + "creator bigint, create_stamp TIMESTAMP,last_modified_by bigint,"
                    + "last_modified_stamp TIMESTAMP, approver bigint, approve_date TIMESTAMP);");

            // 产品
            stmt.execute("insert into product(item_name,item_no,size,width,volume,weight,category,item_desc,customer_id) values('ATM', '2014042600001','1','5','7','9','ATM', '这是一台ATM', '4');");
            stmt.execute("insert into product(item_name,item_no,size,width,volume,weight,category,item_desc,customer_id) values('普通货品', '2014042600002','1','5','7','9','普通货品', '这是普通货品', '4');");

            // 仓库
            stmt.execute("insert into warehouse(WAREHOUSE_AREA,WAREHOUSE_NAME,WAREHOUSE_DESC,WAREHOUSE_ADDRESS,CONTACT_ID) values('582','源鸿总仓', '这是广州总仓','萝岗','2');");
            stmt.execute("insert into warehouse(WAREHOUSE_AREA,WAREHOUSE_NAME,WAREHOUSE_DESC,WAREHOUSE_ADDRESS,CONTACT_ID) values('582','源鸿总仓', '这是广州总仓','东莞','4');");

            stmt.close();
            // conn.commit();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void newCustomer() {
        Contact contact = new Contact();
        contact.set("company_name", "珠海创诚易达信息科技有限公司")
                .set("contact_person", "温生").set("email", "test@test.com");
        contact.set("mobile", "12345671").set("phone", "113527229313")
                .set("address", "香洲珠海市香洲区老香洲为农街为农市场1")
                .set("postal_code", "5190001").set("location", "110101").save();
        Contact contact7 = new Contact();
        contact7.set("company_name", "珠海博兆计算机科技有限公司")
                .set("contact_person", "温生").set("email", "test@test.com");
        contact7.set("mobile", "12345671").set("phone", "113527229313")
                .set("address", "香洲珠海市香洲区老香洲为农街为农市场1")
                .set("postal_code", "5190001").set("location", "441900").save();
        Contact contact2 = new Contact();
        contact2.set("company_name", "北京制药珠海分公司").set("contact_person", "黄生")
                .set("email", "test@test.com");
        contact2.set("mobile", "12345672").set("phone", "213527229313")
                .set("address", "香洲珠海市香洲区老香洲为农街为农市场2")
                .set("postal_code", "5190002").set("location", "110102").save();
        Contact contact3 = new Contact();
        contact3.set("company_name", "上海运输公司").set("contact_person", "李生")
                .set("email", "test@test.com");
        contact3.set("mobile", "12345673").set("phone", "313527229313")
                .set("address", "香洲珠海市香洲区老香洲为农街为农市场3")
                .set("postal_code", "5190003").set("location", "440116").save();
        Contact contact4 = new Contact();
        contact4.set("company_name", "天津运输有限公司").set("contact_person", "何生")
                .set("email", "test@test.com");
        contact4.set("mobile", "12345674").set("phone", "413527229313")
                .set("address", "香洲珠海市香洲区老香洲为农街为农市场4")
                .set("postal_code", "5190004").save();
        Contact contact5 = new Contact();
        contact5.set("company_name", "天津佛纳甘科技有限公司").set("contact_person", "何生")
                .set("email", "test@test.com");
        contact5.set("mobile", "12345674").set("phone", "413527229313")
                .set("address", "香洲珠海市香洲区老香洲为农街为农市场4")
                .set("postal_code", "5190004").set("location", "442000").save();
        Contact contact6 = new Contact();
        contact6.set("company_name", "天津佛纳甘科技有限公司").set("contact_person", "何生")
                .set("email", "test@test.com");
        contact6.set("mobile", "12345674").set("phone", "413527229313")
                .set("address", "香洲珠海市香洲区老香洲为农街为农市场4")
                .set("postal_code", "5190004").set("location", "440402").save();

        Party p1 = new Party();
        Party p2 = new Party();
        Party p3 = new Party();
        Party p4 = new Party();
        Party p5 = new Party();
        Party p6 = new Party();
        Party p7 = new Party();
        Date createDate = Calendar.getInstance().getTime();
        p1.set("contact_id", contact.getLong("id"))
                .set("party_type", "CUSTOMER").set("create_date", createDate)
                .set("creator", "demo").save();
        p7.set("contact_id", contact7.getLong("id"))
                .set("party_type", "CUSTOMER").set("create_date", createDate)
                .set("creator", "demo").save();
        p2.set("contact_id", contact2.getLong("id"))
                .set("party_type", "CUSTOMER").set("create_date", createDate)
                .set("creator", "demo").save();
        p3.set("contact_id", contact3.getLong("id"))
                .set("party_type", "SERVICE_PROVIDER")
                .set("create_date", createDate).set("creator", "demo").save();
        p4.set("contact_id", contact4.getLong("id"))
                .set("party_type", "SERVICE_PROVIDER")
                .set("create_date", createDate).set("creator", "demo").save();
        p5.set("contact_id", contact5.getLong("id"))
                .set("party_type", "NOTIFY_PARTY")
                .set("create_date", createDate).set("creator", "demo").save();
        p6.set("contact_id", contact6.getLong("id"))
                .set("party_type", "NOTIFY_PARTY")
                .set("create_date", createDate).set("creator", "demo").save();

    }

}
