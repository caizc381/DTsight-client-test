package com.tijiantest.util.fuc;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

public class CheckMysqlAndMongo1Test {

	
	/**
	 * 常用数据库对比
	 * 
	 * 
	 * 查询医院插入的订单数量
	 * Mongo:db.mongoOrder.find({"hospital._id":5,"insertTime":{$gt:new Date("2017-02-16"),$lt:new Date("2017-02-18")}}).count() 
	 * Mysql:select * from tb_order where hospital_id = 5 and insert_time > '2017-02-16' and insert_time < '2017-02-18' ;
	 * 
	 * 
	 * 
	 * 更新订单状态（已撤销）
	 * Mysql:update tb_order o set o.status = 5 where  o.hospital_id = 5  and o.status =  2  and o.insert_time like '2017-02-17%';
	 * Mongo:db.mongoOrder.update({'hospital._id':5,'status':2,"insertTime":{$gt:new Date("2017-02-16"),$lt:new Date("2017-02-18")}},{$set:{'status':5}},false,true)
	 */
	int STATUS = 2;
	protected final static Logger log = Logger.getLogger(CheckMysqlAndMongo1Test.class);
    //55844 -----8670   ----28
	//55842 -----8660   ----46
	//59368 -----15508  ----xixi
	//62108 ---- 16448  ----11900000004
	//250521 --- 4101557  ----managerxixi
	//250578  --- 4101669  ----xtest1
	private int mysqlCounts = 0;
	private int mongoCounts = 0 ;
	private int mysqlDiffCounts = 0;
	private int mysqlExtDiffCoutns = 0;
	private int mongoDiffCounts = 0;
	private List<Integer> mysqlDiffOrderIds = new ArrayList<Integer>();
	private List<Integer> mysqlExtDiffOrderIds = new ArrayList<Integer>();
	private List<Integer> mongoDiffOrderIds = new ArrayList<Integer>();
	
	//西溪医院
	@Test(enabled = false)
	public void test_01_batchOrder_mysqlCheck() throws SqlException{
		
		String sql = "select o.id,o.status,o.order_price,o.is_export,o.source,p.self_money,p.offline_pay_money, p.export_self_money,p.export_order_price,p.export_adjust_price "
				+ "from tb_order o left join tb_order_ext_properties p on o.id = p.order_id"
				+ " where o.hospital_id = 5 and o.entry_card_id = ? and o.status =  "+STATUS
				//+" and o.account_id in (250353,250354,250355,250356,250357,250358)"
				+ " and o.insert_time like '2017-02-21%' ";
		//check manager 28
		log.debug("sql...."+sql);
		List<Map<String,Object>> orderlists  = DBMapper.query(sql,8670);
		log.info("mysql用户1的订单数量:"+orderlists.size());
		mysqlCounts += orderlists.size();
		for(Map<String,Object> order : orderlists){
			int id = Integer.parseInt(order.get("id").toString());
			log.debug("orderId..."+ id );
			//if(id != 4130491) break;
			if(!order.get("status").toString().equals(STATUS+"")){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
				
			if(Integer.parseInt(order.get("order_price").toString()) != 335400){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("is_export").toString()) != 0){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);

				continue;
			}
			if(Integer.parseInt(order.get("is_export").toString())!= 0){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);

				continue;
			}
			if(Integer.parseInt(order.get("source").toString())!=3){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);

				continue;
			}
			if(Integer.parseInt(order.get("self_money").toString())!=0){
				mysqlExtDiffCoutns++;
				mysqlExtDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("offline_pay_money").toString())!= 0){
				mysqlExtDiffCoutns++;
				mysqlExtDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("export_self_money").toString())!=0){
				mysqlExtDiffCoutns++;
				mysqlExtDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("export_order_price").toString())!=335400){
				mysqlExtDiffCoutns++;
				mysqlExtDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("export_adjust_price").toString())!= -1500){
				mysqlExtDiffCoutns++;
				mysqlExtDiffOrderIds.add(id);
				continue;
			}		
			
			if(order.get("self_money") != null){
				if(Integer.parseInt(order.get("self_money").toString())!=0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				
				if(Integer.parseInt(order.get("offline_pay_money").toString())!= 0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_self_money").toString())!=0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_order_price").toString())!=335400){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_adjust_price").toString())!=  -1500){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
			}else{
				mysqlExtDiffCoutns++;
				mysqlExtDiffOrderIds.add(id);
				continue;
			}
		}
		
		//check manager 46
		orderlists  = DBMapper.query(sql,8660);
		log.info("mysql用户2的订单数量:"+orderlists.size());
		mysqlCounts += orderlists.size();
		for(Map<String,Object> order : orderlists){
			int id = Integer.parseInt(order.get("id").toString());
//			if(id != 4130491) break;
			log.info("orderId..."+ id );
			if(!order.get("status").toString().equals(""+STATUS)){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("order_price").toString()) != 23500){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("is_export").toString()) != 0){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("is_export").toString())!= 0){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("source").toString())!=3){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(order.get("self_money") != null){
				if(Integer.parseInt(order.get("self_money").toString())!=0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				
				if(Integer.parseInt(order.get("offline_pay_money").toString())!= 0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_self_money").toString())!=0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_order_price").toString())!=23500){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_adjust_price").toString())!= 0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
			}else{
				mysqlExtDiffCoutns++;
				mysqlExtDiffOrderIds.add(id);
				continue;
			}
			
			

		}
		
		
//		//check manager xixi
		orderlists  = DBMapper.query(sql,15508);
		log.info("mysql用户3的订单数量:"+orderlists.size());
		mysqlCounts += orderlists.size();
		for(Map<String,Object> order : orderlists){
			int id = Integer.parseInt(order.get("id").toString());
//			if(id != 4130491) break;
			log.debug("orderId..."+ id );
			if(!order.get("status").toString().equals(""+STATUS)){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("order_price").toString()) != 23500){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("is_export").toString()) != 0){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("is_export").toString())!= 0){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("source").toString())!=3){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(order.get("self_money") != null){
				if(Integer.parseInt(order.get("self_money").toString())!=0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				
				if(Integer.parseInt(order.get("offline_pay_money").toString())!= 0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_self_money").toString())!=0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_order_price").toString())!=23500){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_adjust_price").toString())!= 0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
			}else{
				mysqlExtDiffCoutns++;
				mysqlExtDiffOrderIds.add(id);
				continue;
			}
			

		}
				
		//check manager 11900000004
		orderlists  = DBMapper.query(sql,16448);
		log.info("mysql用户4的订单数量:"+orderlists.size());
		mysqlCounts += orderlists.size();
		for(Map<String,Object> order : orderlists){
			int id = Integer.parseInt(order.get("id").toString());
//			if(id != 4130491) break;
			log.debug("orderId..."+ id );
			if(!order.get("status").toString().equals(""+STATUS)){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("order_price").toString()) != 23500){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("is_export").toString()) != 0){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("is_export").toString())!= 0){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("source").toString())!=3){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(order.get("self_money") != null){
				if(Integer.parseInt(order.get("self_money").toString())!=0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				
				if(Integer.parseInt(order.get("offline_pay_money").toString())!= 0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_self_money").toString())!=0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_order_price").toString())!=23500){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_adjust_price").toString())!= 0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
			}else{
				mysqlExtDiffCoutns++;
				mysqlExtDiffOrderIds.add(id);
				continue;
			}
			

		}		
	

//		//check manager managerxixi
		orderlists  = DBMapper.query(sql,4101557);
		log.debug("mysql用户5的订单数量:"+orderlists.size());
		mysqlCounts += orderlists.size();
		for(Map<String,Object> order : orderlists){
			int id = Integer.parseInt(order.get("id").toString());
//			if(id != 4130491) break;
			log.info("orderId..."+ id );
			if(!order.get("status").toString().equals(""+STATUS)){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("order_price").toString()) != 23500){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("is_export").toString()) != 0){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("is_export").toString())!= 0){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("source").toString())!=3){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(order.get("self_money") != null){
				if(Integer.parseInt(order.get("self_money").toString())!=0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				
				if(Integer.parseInt(order.get("offline_pay_money").toString())!= 0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_self_money").toString())!=0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_order_price").toString())!=23500){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_adjust_price").toString())!= 0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
			}else{
				mysqlExtDiffCoutns++;
				mysqlExtDiffOrderIds.add(id);
				continue;
			}
			

		}
		
		
//
//		//check manager xtest1
		orderlists  = DBMapper.query(sql,4101669);
		log.debug("mysql用户6的订单数量:"+orderlists.size());
		mysqlCounts += orderlists.size();
		for(Map<String,Object> order : orderlists){
			int id = Integer.parseInt(order.get("id").toString());
//			if(id != 4130491) break;
			log.info("orderId..."+ id );
			if(!order.get("status").toString().equals(""+STATUS)){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("order_price").toString()) != 23500){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
	
			if(Integer.parseInt(order.get("is_export").toString())!= 0){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(order.get("source").toString())!=3){
				mysqlDiffCounts++;
				mysqlDiffOrderIds.add(id);
				continue;
			}
			if(order.get("self_money") != null){
				if(Integer.parseInt(order.get("self_money").toString())!=0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				
				if(Integer.parseInt(order.get("offline_pay_money").toString())!= 0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_self_money").toString())!=0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_order_price").toString())!=23500){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
				if(Integer.parseInt(order.get("export_adjust_price").toString())!= 0){
					mysqlExtDiffCoutns++;
					mysqlExtDiffOrderIds.add(id);
					continue;
				}
			}else{
				mysqlExtDiffCoutns++;
				mysqlExtDiffOrderIds.add(id);
				continue;
			}
			

		}
		
	  log.info("mysql总共用户数量:"+mysqlCounts);
	  log.info("mysqlDiffCounts...."+mysqlDiffCounts);
	  log.info("mysqlDiffOrderIds...."+mysqlDiffOrderIds.toString());
	  log.info("mysqlExtDiffCounts...."+mysqlExtDiffCoutns);
	  log.info("mysqlExtDiffOrderIds...."+mysqlExtDiffOrderIds.toString());


	}
	
	@Test(enabled = false)
	public void test_02_batch_mongoCheck() throws ParseException{
		@SuppressWarnings("unused")
		int accountId = 250353;
		int hospitalId = 5;
		String beginTime = "2017-02-21 00:00:00";
		String endTime = "2017-02-21 23:59:59";
		int card = 8670;
		//客户经理28 对应的用户
		
		List<Map<String,Object>> dblist1 = 
				MongoDBUtils.queryByInsertTime("{'hospital._id':"+hospitalId+",status:"+STATUS+",entryCardId:"+card+"}",
				beginTime,endTime, "mongoOrder"); 
		log.info("mongo用户1的订单数量:"+dblist1.size());
		mongoCounts += dblist1.size();
		for(Map<String,Object> map : dblist1){
			int id = Integer.parseInt(map.get("id").toString());
			log.info("orderId..."+map.get("id"));
			if(Integer.parseInt(map.get("status").toString()) != STATUS){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}	
			if(Integer.parseInt(map.get("orderPrice").toString())!= 335400){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportOrderPrice").toString().equals("3354.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(Boolean.parseBoolean(map.get("isExport").toString()) != false){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(map.get("source").toString()) != 3){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("selfMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportSelfMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("adjustPrice").toString().equals("-1500")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportAdjustPrice").toString().equals("-1500")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("offlinePayMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
		}
		
		//客户经理46对应的用户
		accountId = 250354;
		card = 8660;
		 dblist1 = 
			MongoDBUtils.queryByInsertTime("{'hospital._id':"+hospitalId+",status:"+STATUS+",entryCardId:"+card+"}",
			beginTime,endTime, "mongoOrder"); 
//		dblist1 = MongoDBUtils.query(
//				"{'account._id':"+accountId+","+
//				"{'hospital._id':"+hospitalId+",status:"+STATUS+",entryCardId:"+card+"}" ,"mongoOrder");
		log.info("mongo用户2的订单数量:"+dblist1.size());
		mongoCounts += dblist1.size();
		for(Map<String,Object> map : dblist1){
			
			int id = Integer.parseInt(map.get("id").toString());
			log.info("orderId..."+map.get("id"));
			if(Integer.parseInt(map.get("status").toString()) != STATUS){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}	
			if(Integer.parseInt(map.get("orderPrice").toString())!= 23500){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportOrderPrice").toString().equals("235.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(Boolean.parseBoolean(map.get("isExport").toString()) != false){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(map.get("source").toString()) != 3){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("selfMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportSelfMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("adjustPrice").toString().equals("0")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportAdjustPrice").toString().equals("0")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("offlinePayMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
		}
		
		//客户经理xixi对应的用户
		accountId = 250355;
		card = 15508;
		 dblist1 = 
					MongoDBUtils.queryByInsertTime("{'hospital._id':"+hospitalId+",status:"+STATUS+",entryCardId:"+card+"}",
					beginTime,endTime, "mongoOrder"); 
		log.info("mongo用户3的订单数量:"+dblist1.size());
		mongoCounts += dblist1.size();
		for(Map<String,Object> map : dblist1){
			int id = Integer.parseInt(map.get("id").toString());
			log.info("orderId..."+map.get("id"));
			if(Integer.parseInt(map.get("status").toString()) != STATUS){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}	
			if(Integer.parseInt(map.get("orderPrice").toString())!= 23500){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportOrderPrice").toString().equals("235.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(Boolean.parseBoolean(map.get("isExport").toString()) != false){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(map.get("source").toString()) != 3){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("selfMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportSelfMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("adjustPrice").toString().equals("0")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportAdjustPrice").toString().equals("0")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("offlinePayMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
		}
		
		//客户经理11900000004对应的用户
		accountId = 250356;
		card = 16448;
		 dblist1 = 
					MongoDBUtils.queryByInsertTime("{'hospital._id':"+hospitalId+",status:"+STATUS+",entryCardId:"+card+"}",
					beginTime,endTime, "mongoOrder"); 
		log.info("mongo用户4的订单数量:"+dblist1.size());
		mongoCounts += dblist1.size();
		for(Map<String,Object> map : dblist1){
			int id = Integer.parseInt(map.get("id").toString());
			log.info("orderId..."+map.get("id"));
			if(Integer.parseInt(map.get("status").toString()) != STATUS){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}	
			if(Integer.parseInt(map.get("orderPrice").toString())!= 23500){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportOrderPrice").toString().equals("235.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(Boolean.parseBoolean(map.get("isExport").toString()) != false){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(map.get("source").toString()) != 3){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("selfMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportSelfMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("adjustPrice").toString().equals("0")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportAdjustPrice").toString().equals("0")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("offlinePayMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
		}
		
		//客户经理managerxixi对应的用户
		accountId = 250357;
		card = 4101557;
		 dblist1 = 
					MongoDBUtils.queryByInsertTime("{'hospital._id':"+hospitalId+",status:"+STATUS+",entryCardId:"+card+"}",
					beginTime,endTime, "mongoOrder"); 
		log.info("mongo用户5的订单数量:"+dblist1.size());
		mongoCounts += dblist1.size();
		for(Map<String,Object> map : dblist1){
			int id = Integer.parseInt(map.get("id").toString());
			log.info("orderId..."+map.get("id"));
			if(Integer.parseInt(map.get("status").toString()) != STATUS){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}	
			if(Integer.parseInt(map.get("orderPrice").toString())!= 23500){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportOrderPrice").toString().equals("235.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(Boolean.parseBoolean(map.get("isExport").toString()) != false){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(map.get("source").toString()) != 3){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("selfMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportSelfMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("adjustPrice").toString().equals("0")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportAdjustPrice").toString().equals("0")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("offlinePayMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
		}
		
		//客户经理xtest1对应的用户
		accountId = 250358;
		card = 4101669;
		 dblist1 = 
					MongoDBUtils.queryByInsertTime("{'hospital._id':"+hospitalId+",status:"+STATUS+",entryCardId:"+card+"}",
					beginTime,endTime, "mongoOrder"); 
		log.info("mongo用户6的订单数量:"+dblist1.size());
		mongoCounts += dblist1.size();
		for(Map<String,Object> map : dblist1){
			int id = Integer.parseInt(map.get("id").toString());
			log.info("orderId..."+map.get("id"));
			if(Integer.parseInt(map.get("status").toString()) != STATUS){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}	
			if(Integer.parseInt(map.get("orderPrice").toString())!= 23500){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportOrderPrice").toString().equals("235.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(Boolean.parseBoolean(map.get("isExport").toString()) != false){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(Integer.parseInt(map.get("source").toString()) != 3){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("selfMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportSelfMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("adjustPrice").toString().equals("0")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("exportAdjustPrice").toString().equals("0")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
			if(!map.get("offlinePayMoney").toString().equals("0.00")){
				mongoDiffCounts++;
				mongoDiffOrderIds.add(id);
				continue;
			}
		}
		log.info("mongo统计的用户数量:"+mongoCounts);
		log.info("mongoDiffCounts...."+mongoDiffCounts);
		log.info("mongoDiffOrderIds...."+mongoDiffOrderIds.toString());

	}
	
}
