package com.tijiantest.testcase.main.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.item.limitExamItemsVo;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
public class OrderBaseTest extends MainBase {
	

	public String getOrderNumByOrderId(Integer orderId){
		String orderNum = null;
		String sql = "select order_num from tb_order where id = "+orderId+"";
		Map<String, Object> map = null;
		try {
			map = DBMapper.query(sql).get(0);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		orderNum = map.get("order_num").toString();
		return orderNum;
	}
	
	public List<ExamItem> getLimitItems(Integer hospitalId,Integer itemId){
		List<ExamItem> items = new ArrayList<ExamItem>();
		String sql = "SELECT e.* FROM tb_examitem e "
				+ "LEFT JOIN tb_examitem_relation er on er.related_item_id = e.id "
				+ "WHERE er.type = 4 AND e.type =1 AND e.hospital_id =?";
		if(itemId!=0&&itemId!=null)
			sql = sql + " AND er.item_id = "+itemId+"";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, hospitalId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list.size()>0&&list!=null){
			for(Map<String,Object> map: list){
				ExamItem item = new ExamItem();
				item = ResourceChecker.setItem(map);
				items.add(item);
			}
			return items;
		}
		return null;
	}
	
	public List<limitExamItemsVo> getlimitItemsWithCount(Integer hospitalId,String curDate,Integer periodId){
		List<limitExamItemsVo> itemVos = new ArrayList<limitExamItemsVo>();
		String sql = "SELECT hcu.* "
				+ "FROM tb_hospital_capacity_used hcu "
				+ "LEFT JOIN tb_examitem_relation er on er.item_id = hcu.exam_item "
				+ "WHERE hcu.exam_item >0 "
				+ "AND hcu.hospital_id = "+hospitalId+" AND period_id = "+periodId+" "
				+ "AND hcu.available_num>0 "
				+ "AND hcu.cur_date = \'"+curDate+"\' "
						+ "AND er.related_item_id is not null GROUP BY hcu.exam_item;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list.size()>0&&list!=null){
			for(Map<String,Object> m : list){
				limitExamItemsVo vo = new limitExamItemsVo();
				vo.setItemId(Integer.valueOf(m.get("exam_item").toString()));
				List<ExamItem> items = new ArrayList<ExamItem>();
				items = getLimitItems(hospitalId,vo.getItemId());
				if(items.size()>0&&items!=null)
					vo.setLimitItems(items);
				itemVos.add(vo);
			}
		}
		
		return itemVos;
	}
	
	public List<Meal> getMealsWithType(Integer hospitalId,Integer type){
		List<Meal> meals = new ArrayList<Meal>();
		String sql = "SELECT * FROM tb_meal WHERE hospital_id = ? AND type = ? AND disable =0 order by sequence;";
		List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, hospitalId,type);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list.size()>0&&list!=null){
			list.forEach(m->{
				Meal meal = new Meal();
				meal.setId(Integer.parseInt(m.get("id").toString()));
				meal = ResourceChecker.getMealInfo(meal.getId());
				meals.add(meal);
			});
		}
		
		return meals;
	}
}
