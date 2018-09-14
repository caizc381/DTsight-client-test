package com.tijiantest.model.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.tijiantest.base.BaseTest;
import com.tijiantest.model.item.ItemSelectException.ConflictType;
import com.tijiantest.util.AssertUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class ItemRelationFunction extends BaseTest {

	/**
	 * 获取单项信息
	 * 
	 * @param itemIds
	 * @return List<ExamItem>
	 * @throws SqlException
	 */
	public static List<ExamItem> getExamItemsBySelected(List<Integer> itemIds) throws SqlException {
		if (itemIds != null && itemIds.size() > 0) {
			List<ExamItem> eis = new ArrayList<ExamItem>();
			String ids = '(' + ListUtil.IntegerlistToString(itemIds) + ')';
			List<Map<String, Object>> list = null;
			String str = "SELECT * FROM tb_examitem WHERE id IN " + ids + "";
			if (itemIds.size() > 0) {
				list = DBMapper.query(str);
				for (Map<String, Object> m : list) {
					ExamItem ei = new ExamItem();
					ei.setId((Integer.parseInt(m.get("id").toString())));
					ei.setName(m.get("name").toString());
					ei.setGender(Integer.parseInt(m.get("gender").toString()));
					ei.setHospitalId(Integer.parseInt(m.get("hospital_id").toString()));
					if (m.get("meal_id") != null)
						ei.setMealId(Integer.parseInt(m.get("meal_id").toString()));
					ei.setPrice(Integer.parseInt(m.get("price").toString()));
					if (m.get("group_id") != null)
						ei.setGroupId(Integer.parseInt(m.get("group_id").toString()));
					ei.setDiscount(((Integer) m.get("is_discount") == 1) ? true : false);
					ei.setShow(((Integer) m.get("is_show") == 1) ? true : false);
					ei.setEnableCustom(((Integer) m.get("enable_custom") == 1) ? true : false);
					if (m.get("his_item_id") != null)
						ei.setHisItemId(m.get("his_item_id").toString());
					ei.setSyncPrice(((Integer) m.get("sync_price") == 1) ? true : false);
					ei.setItemType(1);
					if(m.get("sequence") != null)
							ei.setSequence(Integer.parseInt(m.get("sequence").toString()));
					eis.add(ei);
				}
				return eis;
			}
			return null;
		}
		return null;
	}

	/**
	 * 获取与其有合并关系的单项
	 * 
	 * @param itemId
	 * @param hospitalId
	 * @return
	 * @throws SqlException
	 */
	public static List<Integer> getComposeItemId(Integer itemId, Integer hospitalId) throws SqlException {
		Integer logType = PriceEffectPropertyEnum.COMPOSE.getCode();
		List<Integer> ids = new ArrayList<Integer>();
		ExamItemChangeLog queryLog = new ExamItemChangeLog();
		queryLog.setItemId(itemId);
		queryLog.setType(logType);
		queryLog.setHospitalId(hospitalId);
		List<ExamItemChangeLog> LogList = selectExamItemChangeLog(queryLog);
		if (!LogList.isEmpty())
			ids = getIdsFromStr(LogList.get(0).getNewVal());
		else
			ids = selectConflictItems(itemId, ConflictType.COMPOSE.getCode());
		if (ids != null) {
			Set<Integer> set = new HashSet<Integer>(ids);
			log.debug("合并小项集合:"+set.toString());
			set.remove(itemId);
			return new ArrayList<Integer>(set);
		}
		return null;
	}

	/**
	 * 将String转化成List<Integer>
	 * 
	 * @param newVal
	 * @return
	 */
	public static List<Integer> getIdsFromStr(String newVal) {
		List<Integer> ids = new ArrayList<Integer>();
		String[] idsStr = newVal.split(",");
		for (String id : idsStr) {
			if ("".equals(id)) {
				continue;
			}
			ids.add(Integer.valueOf(id));
		}
		return ids;
	}

	/***
	 * 获取与其有互斥关系的项目
	 * 
	 * @param itemId
	 * @param hospitalId
	 * @return
	 * @throws SqlException
	 */
	public static List<Integer> getConflictItemId(Integer itemId, Integer hospitalId) throws SqlException {
		Integer logType = PriceEffectPropertyEnum.CONFLICT.getCode();
		List<Integer> ids = new ArrayList<Integer>();
		ExamItemChangeLog queryLog = new ExamItemChangeLog();
		queryLog.setItemId(itemId);
		queryLog.setType(logType);
		queryLog.setHospitalId(hospitalId);
		List<ExamItemChangeLog> LogList = selectExamItemChangeLog(queryLog);
		if (!LogList.isEmpty())
			for (ExamItemChangeLog log : LogList) {
				ids.addAll(getIdsFromStr(log.getNewVal()));
			}
		else
			ids = selectConflictItems(itemId, ConflictType.CONFLICT.getCode());

		if (logType == PriceEffectPropertyEnum.CONFLICT.getCode()) { // 如果是互斥项，查询在newVal中包含itemId的log
			ExamItemChangeLog queryLog2 = new ExamItemChangeLog();
			queryLog2.setNewVal(itemId + "");
			queryLog2.setType(logType);
			queryLog2.setHospitalId(hospitalId);
			List<ExamItemChangeLog> LogList2 = selectExamItemChangeLog(queryLog2);
			if (!LogList2.isEmpty()) {
				List<Integer> ids2 = LogList2.stream().map(item -> item.getItemId()).collect(Collectors.toList());
				ids.addAll(ids2);
			}
			List<Integer> id = selectConflictItems(itemId, ConflictType.CONFLICT.getCode());
			if (id != null)
				ids.addAll(id);
		}
		if (ids != null) {
			Set<Integer> set = new HashSet<Integer>(ids);
			set.remove(itemId);
			List<Integer> conflictlist = new ArrayList<Integer>(set);
			conflictlist = mergeItemIds(conflictlist, itemId, PriceEffectPropertyEnum.CONFLICT.getCode(), hospitalId);
			return conflictlist;
		}
		return null;
	}

	/**
	 * 获取其依赖的项目
	 * 
	 * @param itemId
	 * @param hospitalId
	 * @return
	 * @throws SqlException
	 */
	public static List<Integer> getDependItemIds(Integer itemId, Integer hospitalId) throws SqlException {
		List<Integer> ids = new ArrayList<>();
		ExamItemChangeLog queryLog = new ExamItemChangeLog();
		queryLog.setItemId(itemId);
		queryLog.setType(PriceEffectPropertyEnum.DEPEND.getCode());
		queryLog.setHospitalId(hospitalId);
		List<ExamItemChangeLog> LogList = selectExamItemChangeLog(queryLog);

		if (!LogList.isEmpty()) {
			for (ExamItemChangeLog examItemChangeLog : LogList) {
				String newVal = examItemChangeLog.getNewVal();
				if (!newVal.isEmpty()) {
					ids.add(Integer.valueOf(Integer.valueOf(newVal)));
				}
			}
		}
		List<Integer> relationIds = selectDependOnItems(itemId);
		ids.addAll(relationIds);
		for (Integer id : relationIds) {
			ExamItemChangeLog changeLog = new ExamItemChangeLog();
			changeLog.setItemId(itemId);
			changeLog.setOriginalVal(id + "");
			changeLog.setType(PriceEffectPropertyEnum.DEPEND.getCode());
			changeLog.setHospitalId(hospitalId);
			List<ExamItemChangeLog> changeLogList = selectExamItemOriginalValChangeLog(changeLog);
			List<Integer> list = changeLogList.stream().map(item -> Integer.valueOf(item.getOriginalVal()))
					.collect(Collectors.toList());
			ids.removeAll(list);
		}
		return ids;
	}

	/**
	 * 获取其被依赖的项目
	 * 
	 * @param itemId
	 * @param hospitalId
	 * @return
	 * @throws SqlException
	 */
	public static List<Integer> getDependedItemIds(Integer itemId, Integer hospitalId) throws SqlException {
		List<Integer> ids = new ArrayList<>();
		ExamItemChangeLog queryLog = new ExamItemChangeLog();
		queryLog.setNewVal(itemId + "");
		queryLog.setType(PriceEffectPropertyEnum.DEPEND.getCode());
		List<ExamItemChangeLog> LogList = selectExamItemNewValChangeLog(queryLog);
		if (!LogList.isEmpty()) {
			ids = LogList.stream().map(item -> item.getItemId()).collect(Collectors.toList());
		}
		ids.addAll(selectDependForItems(itemId));
		return mergeItemIds(ids, itemId, PriceEffectPropertyEnum.DEPEND.getCode(), hospitalId);
	}

	/**
	 * 获取子项
	 * 
	 * @param itemId
	 * @param hospitalId
	 * @return
	 * @throws SqlException
	 */
	public static List<Integer> getChildItems(Integer itemId, Integer hospitalId) throws SqlException {
		Integer logType = PriceEffectPropertyEnum.FAMILY.getCode();
		List<Integer> ids = new ArrayList<Integer>();
		ExamItemChangeLog queryLog = new ExamItemChangeLog();
		queryLog.setItemId(itemId);
		queryLog.setType(logType);
		queryLog.setHospitalId(hospitalId);
		List<ExamItemChangeLog> LogList = selectExamItemChangeLog(queryLog);
		if (!LogList.isEmpty()) {
			ids = getIdsFromStr(LogList.get(0).getNewVal());
		} else {
			ids = selectChildItems(itemId);
		}

		Set<Integer> set = new HashSet<Integer>(ids);
		set.remove(itemId);
		return new ArrayList<Integer>(set);
	}

	/**
	 * 获取父项
	 * 
	 * @param itemId
	 * @param hospitalId
	 * @return
	 * @throws SqlException
	 */
	public static List<Integer> getParentItemIds(Integer itemId, Integer hospitalId) throws SqlException {
		List<Integer> ids = new ArrayList<>();
		Integer logType = PriceEffectPropertyEnum.FAMILY.getCode();

		ExamItemChangeLog queryLog = new ExamItemChangeLog();
		queryLog.setNewVal(itemId + "");
		queryLog.setType(logType);
		queryLog.setHospitalId(hospitalId);
		ids = selectParentItems(itemId); // 正式表中的父项
		List<ExamItemChangeLog> LogList = selectExamItemNewValChangeLog(queryLog);// log表中的被合并的父项
		if (!LogList.isEmpty()) {
			for (ExamItemChangeLog log : LogList) {
				ids.add(log.getItemId());
			}
		}
		Set<Integer> idSet = new HashSet<Integer>(ids);

		return mergeItemIds(new ArrayList<Integer>(idSet), itemId, logType, hospitalId);
	}

	/**
	 * 获取同组项目
	 * 
	 * @param examItemId
	 * @param hospitalId
	 * @return
	 * @throws SqlException
	 */
	public static List<ExamItem> queryItemsInGroupId(int examItemId, int hospitalId) throws SqlException {
		List<ExamItem> eis = new ArrayList<ExamItem>();
		List<Map<String, Object>> list = null;
		String str = "SELECT id, NAME, group_id,price, sequence,gender,hospital_id ,is_discount,is_show,enable_custom,his_item_id,sync_price " + "FROM tb_examitem WHERE hospital_id = ? "
				+ "AND group_id IN ( SELECT group_id FROM tb_examitem WHERE id = ?)";
		list = DBMapper.query(str, hospitalId, examItemId);
		for (Map<String, Object> m : list) {
			ExamItem ei = new ExamItem();	
			ei.setId((Integer.parseInt(m.get("id").toString())));
			ei.setName(m.get("name").toString());
			ei.setGender(Integer.parseInt(m.get("gender").toString()));
			ei.setHospitalId(Integer.parseInt(m.get("hospital_id").toString()));
			ei.setPrice(Integer.parseInt(m.get("price").toString()));
			if (m.get("group_id") != null)
				ei.setGroupId(Integer.parseInt(m.get("group_id").toString()));
			ei.setDiscount(((Integer) m.get("is_discount") == 1) ? true : false);
			ei.setShow(((Integer) m.get("is_show") == 1) ? true : false);
			ei.setEnableCustom(((Integer) m.get("enable_custom") == 1) ? true : false);
			if (m.get("his_item_id") != null)
				ei.setHisItemId(m.get("his_item_id").toString());
			ei.setSyncPrice(((Integer) m.get("sync_price") == 1) ? true : false);
			ei.setItemType(1);
			if(m.get("sequence") != null)
					ei.setSequence(Integer.parseInt(m.get("sequence").toString()));
			eis.add(ei);
			}
		return eis;
	}
	
	
	/**
	 * 获取同组项目
	 * 
	 * @param examItemId
	 * @param hospitalId
	 * @return
	 * @throws SqlException
	 */
	public static List<Integer> queryGroupItems(int examItemId, int hospitalId) throws SqlException {
		List<Integer> eis = new ArrayList<Integer>();
		List<Map<String, Object>> list = null;
		String str = "SELECT id, NAME, group_id AS groupId, sequence " + "FROM tb_examitem WHERE hospital_id = ? "
				+ "AND group_id IN ( SELECT group_id FROM tb_examitem WHERE id = ?)";
		list = DBMapper.query(str, hospitalId, examItemId);
		for (Map<String, Object> m : list) {
			eis.add(Integer.parseInt(m.get("id").toString()));		}
		return eis;
	}

	/**
	 * 获取有冲突的项目
	 * 
	 * @param selectedItemId
	 * @param type
	 * @return
	 * @throws SqlException
	 */
	public static List<Integer> selectConflictItems(int selectedItemId, int type) throws SqlException {
		List<Integer> ids = new ArrayList<Integer>();
		List<Map<String, Object>> list = null;
		String str = "";
		if (type == 2)
			str = "SELECT ( CASE WHEN er.item_id = " + selectedItemId
					+ " THEN er.related_item_id ELSE er.item_id END ) AS item_id " + "FROM tb_examitem_relation er "
					+ "WHERE er.type = " + type + " " + "AND ( " + "er.item_id = " + selectedItemId + " "
					+ "OR er.related_item_id = " + selectedItemId + " " + ")";
		else
			str = "SELECT er.related_item_id AS item_id " + "FROM tb_examitem_relation er " + "WHERE er.type = " + type
					+ " " + "AND er.item_id = " + selectedItemId + "";

		list = DBMapper.query(str);
		if (list.size() > 0) {
			for (Map<String, Object> m : list) {
				ids.add(Integer.parseInt(m.get("item_id").toString()));
			}
			Set<Integer> set = new HashSet<Integer>(ids);
			set.remove(selectedItemId);
			return new ArrayList<Integer>(set);
		}
		return null;
	}

	/**
	 * 查询tb_examitem_change_log
	 * 
	 * @param queryLog
	 * @return
	 * @throws SqlException
	 */
	public static List<ExamItemChangeLog> selectExamItemChangeLog(ExamItemChangeLog queryLog) throws SqlException {
		List<ExamItemChangeLog> LogList = new ArrayList<ExamItemChangeLog>();
		List<Map<String, Object>> list = null;
		String string = "SELECT * FROM tb_examitem_change_log " + "WHERE is_complete = 0 " + "AND hospital_id = "
				+ queryLog.getHospitalId() + " " + "AND item_id = " + queryLog.getItemId() + " " + "AND type = "
				+ queryLog.getType() + " " + "AND new_val LIKE concat( '%', " + queryLog.getNewVal() + ", '%') "
				+ "ORDER BY item_id, type ASC";
		list = DBMapper.query(string);
		for (Map<String, Object> m : list) {
			ExamItemChangeLog eicl = new ExamItemChangeLog();
			eicl.setId(Integer.parseInt(m.get("id").toString()));
			eicl.setItemId(Integer.parseInt(m.get("item_id").toString()));
			eicl.setNewVal(m.get("new_val").toString());
			LogList.add(eicl);
		}
		return LogList;
	}

	/**
	 * 移除change_log中被解除的关系
	 * 
	 * @param itemList
	 * @param itemId
	 * @param logType
	 * @param hospitalId
	 * @return
	 * @throws SqlException
	 */
	public static List<Integer> mergeItemIds(List<Integer> itemList, Integer itemId, int logType, Integer hospitalId)
			throws SqlException {

		ExamItemChangeLog queryLog = new ExamItemChangeLog();
		queryLog.setOriginalVal(itemId + "");
		queryLog.setType(logType);
		queryLog.setHospitalId(hospitalId);
		queryLog.setNewVal(itemId + "");
		List<ExamItemChangeLog> LogList = selectExamItemOriginalValChangeLog(queryLog);
		List<Integer> list = LogList.stream().map(item -> item.getItemId()).collect(Collectors.toList());
		itemList.removeAll(list);

		if (logType == PriceEffectPropertyEnum.CONFLICT.getCode()) {
			ExamItemChangeLog queryLog2 = new ExamItemChangeLog();
			queryLog2.setItemId(itemId);
			queryLog2.setType(logType);
			queryLog2.setHospitalId(hospitalId);
			queryLog2.setNewVal("");
			List<ExamItemChangeLog> LogList2 = selectExamItemNewValChangeLog(queryLog2);
			List<Integer> list2 = LogList2.stream().map(item -> Integer.parseInt(item.getOriginalVal()))
					.collect(Collectors.toList());
			itemList.removeAll(list2);
		}

		return itemList;
	}

	/**
	 * 根据original_val查询tb_examitem_change_log
	 * 
	 * @param queryLog
	 * @return
	 * @throws SqlException
	 */
	public static List<ExamItemChangeLog> selectExamItemOriginalValChangeLog(ExamItemChangeLog queryLog)
			throws SqlException {
		List<ExamItemChangeLog> LogList = new ArrayList<ExamItemChangeLog>();
		List<Map<String, Object>> list = null;
		String string = "SELECT * FROM tb_examitem_change_log " + "WHERE is_complete = 0 " + "AND hospital_id = ? "
				+ "AND original_val LIKE concat('%', ?, '%') " + "AND new_val NOT LIKE concat('%', ?, '%') "
				+ "AND type = ? " + "AND item_id = ? " + "ORDER BY item_id, type ASC";
		list = DBMapper.query(string, queryLog.getHospitalId(), queryLog.getOriginalVal(), queryLog.getNewVal(),
				queryLog.getType(), queryLog.getItemId());
		for (Map<String, Object> m : list) {
			ExamItemChangeLog eicl = new ExamItemChangeLog();
			eicl.setId(Integer.parseInt(m.get("id").toString()));
			eicl.setItemId(Integer.parseInt(m.get("item_id").toString()));
			eicl.setNewVal(m.get("new_val").toString());
			LogList.add(eicl);
		}
		return LogList;
	}

	/**
	 * - 根据new_val查询tb_examitem_change_log
	 * 
	 * @param queryLog
	 * @return
	 * @throws SqlException
	 */
	public static List<ExamItemChangeLog> selectExamItemNewValChangeLog(ExamItemChangeLog queryLog)
			throws SqlException {
		List<ExamItemChangeLog> LogList = new ArrayList<ExamItemChangeLog>();
		List<Map<String, Object>> list = null;
		String str = null;
		if (queryLog.getNewVal() != null && queryLog.getNewVal() != "")
			str = "SELECT * FROM tb_examitem_change_log " + "WHERE is_complete = 0 " + "AND item_id = "
					+ queryLog.getItemId() + " " + "AND hospital_id = " + queryLog.getHospitalId() + " "
					+ "AND new_val LIKE concat('%', " + queryLog.getNewVal() + ", '%') " + "AND type = "
					+ queryLog.getType() + " " + "ORDER BY item_id, type ASC";
		else if (queryLog.getNewVal() != null && queryLog.getNewVal() == "")
			str = "SELECT * FROM tb_examitem_change_log " + "WHERE is_complete = 0 " + "AND item_id = "
					+ queryLog.getItemId() + " " + "AND hospital_id = " + queryLog.getHospitalId() + " "
					+ "AND new_val = '' " + "AND type = " + queryLog.getType() + " " + "ORDER BY item_id, type ASC";

		list = DBMapper.query(str);
		for (Map<String, Object> m : list) {
			ExamItemChangeLog eicl = new ExamItemChangeLog();
			eicl.setId(Integer.parseInt(m.get("id").toString()));
			eicl.setItemId(Integer.parseInt(m.get("item_id").toString()));
			eicl.setNewVal(m.get("new_val").toString());
			LogList.add(eicl);
		}
		return LogList;
	}

	/**
	 * 查询被合并的项目
	 * 
	 * @param itemId
	 * @param relationType
	 * @param hospitalId
	 * @return
	 * @throws SqlException
	 */
	public static List<Integer> getBeComposedItemIds(Integer itemId, int relationType, Integer hospitalId)
			throws SqlException {
		List<Integer> ids = new ArrayList<>();
		Integer logType = null;
		if (relationType == ConflictType.COMPOSE.getCode()) {
			logType = PriceEffectPropertyEnum.COMPOSE.getCode();
		}
		if (relationType == ConflictType.CONFLICT.getCode()) {
			logType = PriceEffectPropertyEnum.CONFLICT.getCode();
		}
		if (relationType == ConflictType.DEPEND.getCode()) {
			logType = PriceEffectPropertyEnum.DEPEND.getCode();
		}
		ExamItemChangeLog queryLog = new ExamItemChangeLog();
		queryLog.setNewVal(itemId + "");
		queryLog.setType(logType);
		queryLog.setHospitalId(hospitalId);
		ids = selectIdsByRelatedItem(itemId, relationType); // 正式表中的被合并的父项
		List<ExamItemChangeLog> LogList = selectExamItemNewValChangeLog(queryLog);// log表中的被合并的父项
		if (!LogList.isEmpty()) {
			for (ExamItemChangeLog log : LogList) {
				ids.add(log.getItemId());
			}
		}
		Set<Integer> idSet = new HashSet<Integer>(ids);

		return mergeItemIds(new ArrayList<Integer>(idSet), itemId, logType, hospitalId);
	}

	/**
	 * 根据type查询与之有关系的项目 tb_examitem_relation
	 * 
	 * @param itemId
	 * @param type
	 * @return
	 * @throws SqlException
	 */
	public static List<Integer> selectIdsByRelatedItem(int itemId, int type) throws SqlException {
		List<Integer> ids = new ArrayList<Integer>();
		List<Map<String, Object>> list = null;
		String str = "SELECT er.item_id " + "FROM tb_examitem_relation er " + "WHERE er.type = " + type + " "
				+ "AND er.related_item_id = " + itemId + "";
		list = DBMapper.query(str);
		for (Map<String, Object> m : list) {
			ids.add(Integer.parseInt(m.get("item_id").toString()));
		}
		return ids;
	}

	/**
	 * tb_examitem_relation
	 * 
	 * @param itemId
	 * @return
	 * @throws SqlException
	 */
	public static List<Integer> selectDependOnItems(int itemId) throws SqlException {
		List<Integer> ids = new ArrayList<Integer>();
		List<Map<String, Object>> list = null;
		String str = "SELECT er.related_item_id " + "FROM tb_examitem_relation er " + "WHERE er.type = 3 "
				+ "AND er.item_id = " + itemId + "";
		list = DBMapper.query(str);
		for (Map<String, Object> m : list) {
			ids.add(Integer.parseInt(m.get("related_item_id").toString()));
		}
		return ids;
	}

	public static List<Integer> selectDependForItems(int itemId) throws SqlException {
		List<Integer> ids = new ArrayList<Integer>();
		List<Map<String, Object>> list = null;
		String str = "SELECT er.item_id " + "FROM tb_examitem_relation er " + "WHERE er.type = 3 "
				+ "AND er.related_item_id = " + itemId + "";
		list = DBMapper.query(str);
		for (Map<String, Object> m : list) {
			ids.add(Integer.parseInt(m.get("item_id").toString()));
		}
		return ids;
	}

	public static List<Integer> selectChildItems(int itemId) throws SqlException {
		List<Integer> ids = new ArrayList<Integer>();
		List<Map<String, Object>> list = null;
		String str = "SELECT item_id " + "FROM tb_examitem_family ef " + "WHERE ef.parent_id = " + itemId + "";
		list = DBMapper.query(str);
		for (Map<String, Object> m : list) {
			ids.add(Integer.parseInt(m.get("item_id").toString()));
		}
		return ids;
	}

	/**
	 * 查询父项
	 * 
	 * @param itemId
	 * @return
	 * @throws SqlException
	 */
	public static List<Integer> selectParentItems(int itemId) throws SqlException {
		List<Integer> ids = new ArrayList<Integer>();
		List<Map<String, Object>> list = null;
		String str = "SELECT parent_id " + "FROM tb_examitem_family ef " + "WHERE ef.item_id = " + itemId + " ";
		list = DBMapper.query(str);
		for (Map<String, Object> m : list) {
			ids.add(Integer.parseInt(m.get("parent_id").toString()));
		}
		return ids;
	}

	/**
	 * 获取人数控制项目信息
	 * 
	 * @param ids
	 * @return
	 * @throws SqlException
	 */
	public static List<LimitItem> getLimitItemListByIds(List<Integer> ids) throws SqlException {
		String itemids = "(" + ListUtil.IntegerlistToString(ids) + ")";
		List<Map<String, Object>> list = null;
		List<LimitItem> lis = new ArrayList<LimitItem>();
		String str = "SELECT a.item_id AS itemId, b. NAME AS itemName, count(1) AS count "
				+ "FROM tb_examitem_relation a " + "LEFT JOIN tb_examitem b ON a.item_id = b.id "
				+ "WHERE a.related_item_id IN " + itemids + " " + "AND a.type = 4 " + "AND b.type = 5 "
				+ "GROUP BY a.item_id, b. NAME";
		list = DBMapper.query(str);
		for (Map<String, Object> m : list) {
			LimitItem li = new LimitItem();
			li.setCount(Integer.parseInt(m.get("count").toString()));
			li.setItemId(Integer.parseInt(m.get("itemId").toString()));
			li.setItemName(m.get("itemName").toString());
			lis.add(li);
		}
		return lis;
	}

	/**
	 * 获取未跑定时任务的change_log
	 * 
	 * @throws SqlException
	 * 
	 **/
	public static List<ExamItemChangeLog> getChangeLogList(int hospitalId) throws SqlException {
		String sql = "select id, item_id, hospital_id, type, original_val, new_val, operator_id, is_complete from tb_examitem_change_log where is_complete = 0 and hospital_id = ? order by item_id, type asc";
		List<Map<String, Object>> list = DBMapper.query(sql, hospitalId);
		List<ExamItemChangeLog> changeLogList = new ArrayList<>();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				ExamItemChangeLog log = new ExamItemChangeLog();
				log.setHospitalId(hospitalId);
				log.setId(Integer.valueOf(map.get("id").toString()));
				log.setItemId(Integer.valueOf(map.get("item_id").toString()));
				log.setNewVal(map.get("new_val").toString());
				log.setOperatorId(Integer.valueOf(map.get("operator_id").toString()));
				log.setOriginalVal(map.get("original_val").toString());
				log.setType(Integer.valueOf(map.get("type").toString()));
				log.setComplete(Boolean.getBoolean(map.get("is_complete").toString()));
				changeLogList.add(log);
			}
		}

		return changeLogList;
	}

	/**
	 * 把changeLogList 转换成changeLogMap
	 * 
	 * @throws SqlException
	 **/
	public static Map<Integer, List<ExamItemChangeLog>> getChangeLogMap(int hospitalId) throws SqlException {
		List<ExamItemChangeLog> changeLogList = getChangeLogList(hospitalId);
		Map<Integer, List<ExamItemChangeLog>> changeLogMap = changeLogList.stream()
				.collect(Collectors.groupingBy(ExamItemChangeLog::getType));
		return changeLogMap;
	}

	/**
	 * 获取删除关系的change_log
	 * 
	 * @throws SqlException
	 * 
	 **/
	public static List<ExamItemChangeLog> getDeleteChangeLogList(int hospitalId) throws SqlException {
		Map<Integer, List<ExamItemChangeLog>> changeLogMap = getChangeLogMap(hospitalId);
		List<ExamItemChangeLog> deleteChangeLogList = changeLogMap.get(PriceEffectPropertyEnum.DELETE.getCode());

		return deleteChangeLogList;
	}

	/**
	 * 把deleteChangeLogList 转换成deleteChangeLogMap
	 * 
	 * @throws SqlException
	 **/
	public static Map<Integer, ExamItemChangeLog> getDeleteChangeLogMap(int hospitalId) throws SqlException {
		List<ExamItemChangeLog> deleteChangeLogList = getDeleteChangeLogList(hospitalId);

		Map<Integer, ExamItemChangeLog> deleteChangeLogMap = new HashMap<>();
		if (AssertUtil.isNotEmpty(deleteChangeLogList)) {
			deleteChangeLogMap = deleteChangeLogList.stream()
					.collect(Collectors.toMap(ExamItemChangeLog::getItemId, val -> val));
		}
		return deleteChangeLogMap;
	}

	/**
	 * 获取所有的单项关系
	 * 
	 * @throws SqlException
	 * 
	 **/
	public static List<ExamItemRelationDto> getExamItemWithRelation(int hospitalId, String searchVal) throws SqlException {
		String sql = "select  rela.item_id as itemId, rela.related_item_id as relatedItemId, rela.type, ex.his_item_id as hisItemId from tb_examitem ei left join tb_examitem_relation rela on ei.id = rela.item_id left join tb_examitem ex on rela.related_item_id = ex.id where ei.hospital_id = ? and ei.type = 1 and rela.item_id is not null and rela.type != 4";
		String searchSql = " and (ei.name like concat('%',?,'%') or ei.pinyin like concat('%', ?, '%') or (ei.his_item_id like concat('%',?,'%') or ei.his_item_id is null))";
		if (searchVal != null && !searchVal.equals("")) {
			sql += searchSql;
		}
		sql += " union select  rela.related_item_id as itemId, rela.item_id as relatedItemId, rela.type, null as hisItemId from tb_examitem ei left join tb_examitem_relation rela on ei.id = rela.related_item_id where  ei.hospital_id = ? and ei.type = 1 and rela.related_item_id is not null and rela.type != 4";
		if (searchVal != null && !searchVal.equals("")) {
			sql += searchSql;
		}

		List<Map<String, Object>> list = new ArrayList<>();

		if (searchVal != null && !searchVal.equals("")) {
			list = DBMapper.query(sql, hospitalId, hospitalId,searchVal);
		} else {
			System.out.println(sql);
			list = DBMapper.query(sql, hospitalId,hospitalId);
		}

		List<ExamItemRelationDto> relationList = new ArrayList<>();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				ExamItemRelationDto dto = new ExamItemRelationDto();
				dto.setItemId(Integer.valueOf(map.get("itemId").toString()));
				dto.setRelatedItemId(Integer.valueOf(map.get("relatedItemId").toString()));
				dto.setType(Integer.valueOf(map.get("type").toString()));
				if (map.get("hisItemId")!=null) {
					dto.setHisItemId(map.get("hisItemId").toString());
				}				
				relationList.add(dto);
			}
		}
		return relationList;
	}

	/**
	 * 获取family关系
	 * 
	 * @throws SqlException
	 */
	public static List<ExamItemFamilyDto> getFamilyList(int hospitalId) throws SqlException {
		String sql = "select	fam.item_id as itemId, fam.parent_id as parentId from tb_examitem_family fam left join tb_examitem ei on ei.id = fam.parent_id where ei.hospital_id = ?  and ei.type = 1";
		List<Map<String, Object>> list = DBMapper.query(sql, hospitalId);
		List<ExamItemFamilyDto> familyList = new ArrayList<>();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				ExamItemFamilyDto dto = new ExamItemFamilyDto();
				dto.setItemId(Integer.valueOf(map.get("itemId").toString()));
				dto.setParentId(Integer.valueOf(map.get("parentId").toString()));
				familyList.add(dto);
			}
		}
		return familyList;
	}

	/**
	 * 综合tb_examitem_relation 和tb_examitem_family表，获取项目关系
	 **/
	public List<ExamItemRelationDto> addAllRelation(List<ExamItemRelationDto> relationList,
			List<ExamItemFamilyDto> familyList) {
		if (!familyList.isEmpty()) {
			for (ExamItemFamilyDto fd : familyList) {
				ExamItemRelationDto rd = new ExamItemRelationDto();
				rd.setItemId(fd.getParentId());
				rd.setRelatedItemId(fd.getItemId());
				rd.setType(ExamItemRelationEnum.FAMILY.getCode());
				relationList.add(rd);
			}
		}
		return relationList;
	}

	/**
	 * 综合tb_examitem_change_log和tb_examitem_family，获取单项数据 显示维护后台->单项管理->单项编辑
	 * 
	 * @throws SqlException
	 **/
	public static List<ExamItem> getExamItemList(int hospitalId, String searchVal, List<ExamItemRelationDto> relationDtoList,
			Map<Integer, ExamItemChangeLog> deleteMap, Map<Integer, List<ExamItemChangeLog>> changeLogMap)
			throws SqlException {
		String sql = "select id, hospital_id, name, description, detail, fit_people, unfit_people, gender, pinyin, price, group_id, is_discount, his_item_id, type, is_show, focus, sequence, sync_price, tag_name, warning, show_warning from tb_examitem where hospital_id =? and type = 1 order by sequence";
		List<Map<String, Object>> list = DBMapper.query(sql, hospitalId);
		List<ExamItem> itemList = getExamItems(list);
		List<ExamItem> newItemList = new ArrayList<>();
		List<ExamItemChangeLog> isShowChangeLogList = changeLogMap.get(PriceEffectPropertyEnum.IS_SHOW.getCode());
		List<ExamItemChangeLog> priceChangeLogList = changeLogMap.get(PriceEffectPropertyEnum.PRICE.getCode());
		List<ExamItemChangeLog> discountChangeLogList = changeLogMap.get(PriceEffectPropertyEnum.IS_DISCOUNT.getCode());
		Map<Integer, Boolean> showMap = new HashMap<>();
		if (AssertUtil.isNotEmpty(isShowChangeLogList)) {
			showMap = isShowChangeLogList.stream()
					.collect(Collectors.toMap(ExamItemChangeLog::getItemId, val -> Boolean.valueOf(val.getNewVal())));
		}

		Map<Integer, Integer> priceMap = new HashMap<>();
		if (AssertUtil.isNotEmpty(priceChangeLogList)) {
			priceMap = priceChangeLogList.stream()
					.collect(Collectors.toMap(ExamItemChangeLog::getItemId, val -> Integer.valueOf(val.getNewVal())));
		}

		Map<Integer, Boolean> discountMap = new HashMap<>();
		if (AssertUtil.isNotEmpty(discountChangeLogList)) {
			discountMap = discountChangeLogList.stream()
					.collect(Collectors.toMap(ExamItemChangeLog::getItemId, val -> Boolean.valueOf(val.getNewVal())));
		}
		// 获取组合项医院编号（合并关系）
		Map<Integer, String> composeHisitemidMap = getComposeHisitemidMap(relationDtoList,
				changeLogMap.get(PriceEffectPropertyEnum.COMPOSE.getCode()), deleteMap);
		for (ExamItem item : itemList) {
			if (!deleteMap.containsKey(item.getId())) {
				if (composeHisitemidMap.containsKey(item.getId())) {
					item.setHisItemId(composeHisitemidMap.get(item.getId()));
				}
				if (AssertUtil.isNotEmpty(searchVal) && AssertUtil.isEmpty(item.getHisItemId())
						&& (!item.getName().toLowerCase().contains(searchVal.toLowerCase())
								&& !item.getPinyin().toLowerCase().contains(searchVal.toLowerCase()))) {
					continue;
				}
				if (AssertUtil.isNotEmpty(searchVal) && composeHisitemidMap.containsKey(item.getId())
						&& (!item.getName().toLowerCase().contains(searchVal.toLowerCase())
								&& !item.getPinyin().toLowerCase().contains(searchVal.toLowerCase())
								&& !item.getHisItemId().toLowerCase().contains(searchVal.toLowerCase()))) {
					continue;
				}
				if (showMap.containsKey(item.getId())) {
					item.setShow(showMap.get(item.getId()));
				}
				if (priceMap != null && priceMap.containsKey(item.getId())) {
					item.setPrice(priceMap.get(item.getId()));
				}
				if (discountMap != null && discountMap.containsKey(item.getId())) {
					item.setDiscount(discountMap.get(item.getId()));
				}
				newItemList.add(item);
			}
		}
		return newItemList;
	}

	/**
	 * 获取tb_examitem数据
	 * 
	 * @throws SqlException
	 **/
	public static List<ExamItem> getExamItems(List<Map<String, Object>> list) throws SqlException {

		List<ExamItem> itemList = new ArrayList<>();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				ExamItem item = new ExamItem();
				item.setId(Integer.valueOf(map.get("id").toString()));
				item.setHospitalId(Integer.valueOf(map.get("hospital_id").toString()));
				item.setName(map.get("name").toString());
				if (map.get("description")!=null) {
					item.setDescription(map.get("description").toString());
				}				
				if (map.get("detail")!=null) {
					item.setDetail(map.get("detail").toString());
				}	
				if (map.get("fit_people")!=null) {
					item.setFitPeople(map.get("fit_people").toString());
				}
				if (map.get("unfit_people")!=null) {
					item.setUnfitPeople(map.get("unfit_people").toString());
				}
				
				item.setGender(Integer.valueOf(map.get("gender").toString()));
				item.setPinyin(map.get("pinyin").toString());
				item.setPrice(Integer.valueOf(map.get("price").toString()));
				if (map.get("group_id")!=null) {
					item.setGroupId(Integer.valueOf(map.get("group_id").toString()));
				}				
				item.setDiscount(Boolean.getBoolean(map.get("is_discount").toString()));
				if (map.get("his_item_id")!=null) {
					item.setHisItemId(map.get("his_item_id").toString());
				}				
				item.setItemType(Integer.valueOf(map.get("type").toString()));
				item.setShow(Boolean.getBoolean(map.get("is_show").toString()));
				item.setFocus(Boolean.getBoolean(map.get("focus").toString()));
				item.setSequence(Integer.valueOf(map.get("sequence").toString()));
				item.setSyncPrice(Boolean.getBoolean(map.get("sync_price").toString()));
				if (map.get("tag_name")!=null) {
					item.setTagName(map.get("tag_name").toString());
				}				
				if (map.get("warning")!=null) {
					item.setWarning(map.get("warning").toString());
				}				
				item.setShowWarning(Boolean.valueOf(map.get("show_warning").toString()));
				itemList.add(item);
			}
		}
		return itemList;
	}

	/**
	 * //获取组合项医院编号(合并关系)
	 * 
	 * @throws SqlException
	 **/
	public static Map<Integer, String> getComposeHisitemidMap(List<ExamItemRelationDto> relationDtoList,
			List<ExamItemChangeLog> changeLogList, Map<Integer, ExamItemChangeLog> deleteMap) throws SqlException {
		Map<Integer, String> composeHisitemidMap = relationDtoList.parallelStream()
				.filter(dto -> dto.getType() == ExamItemRelationEnum.COMPOSE.getCode()
						&& !deleteMap.containsKey(dto.getItemId()) && !deleteMap.containsKey(dto.getRelatedItemId())
						&& AssertUtil.isNotEmpty(dto.getHisItemId()))
				.collect(Collectors.toMap(ExamItemRelationDto::getItemId, val -> {
					String hisItemId = val.getHisItemId();
					if (!hisItemId.isEmpty()) {
						return hisItemId;
					} else {
						return "";
					}
				}, (oldVal, newVal) -> {
					if (oldVal.isEmpty()) {
						return newVal;
					}
					if (newVal.isEmpty()) {
						return oldVal;
					}
					return oldVal + "," + newVal;
				}));
		if (AssertUtil.isEmpty(changeLogList)) {
			return composeHisitemidMap;
		}
		for (ExamItemChangeLog log : changeLogList) {
			if (composeHisitemidMap.containsKey(log.getItemId()) && AssertUtil.isEmpty(log.getNewVal())) {
				composeHisitemidMap.remove(log.getItemId());
			}

			if (AssertUtil.isNotEmpty(log.getNewVal())) {
				List<ExamItem> examItemList = getChangeExamItem(log.getNewVal());
				String hisItemVal = "";
				for (ExamItem item : examItemList) {
					if (!deleteMap.containsKey(item.getId())) {
						hisItemVal += item.getHisItemId() + ",";
					}
				}

				if (AssertUtil.isNotEmpty(hisItemVal)) {
					composeHisitemidMap.put(log.getItemId(), hisItemVal.substring(0, hisItemVal.length() - 1));
				}
			}
		}
		return composeHisitemidMap;
	}

	private static List<ExamItem> getChangeExamItem(String changeVal) throws SqlException {
		String[] idArr = changeVal.split(",");
		List<Integer> itemIds = new ArrayList<>();

		for (String id : idArr) {
			itemIds.add(Integer.valueOf(id));
		}
		String itemIdsStr = "";
		for (Integer id : itemIds) {
			itemIdsStr += id + ",";
		}
		int index = itemIdsStr.lastIndexOf(",");
		itemIdsStr = itemIdsStr.substring(0, index);

		String sql = "select 	i.id, i.hospital_id, i.name, i.description, i.detail, i.fit_people, i.unfit_people, i.gender, i.pinyin, i.price, i.group_id, i.is_discount, i.his_item_id, i.type, i.enable_custom, i.focus, i.sync_price, i.tag_name, i.warning, i.show_warning, i.bottleneck from tb_examitem i where i.id =?";
		List<Map<String, Object>> list = DBMapper.query(sql, itemIdsStr);
		List<ExamItem> examItemList = getExamItems(list);
		return examItemList;
	}
	
	public static Map<Integer,String> getRelationMap(List<ExamItemRelationDto> relationDtoList,Integer hospitalId,String searchVal,Map<Integer, ExamItemChangeLog> deleteMap,Map<Integer, List<ExamItemChangeLog>> changeLogMap){
		Map<Integer, Integer> composeChangeMap = getChangeLogInfo(changeLogMap,deleteMap,PriceEffectPropertyEnum.COMPOSE);
		Map<Integer, Integer> conflictChangeMap = getChangeLogInfo(changeLogMap, deleteMap, PriceEffectPropertyEnum.CONFLICT);
		Map<Integer, Integer> dependChangeMap = getChangeLogInfo(changeLogMap, deleteMap, PriceEffectPropertyEnum.DEPEND);
		Map<Integer, Integer> familyChangeMap = getChangeLogInfo(changeLogMap, deleteMap, PriceEffectPropertyEnum.FAMILY);
		Map<Integer, String> relationMap = new HashMap<>();
		for (ExamItemRelationDto dto : relationDtoList) {
			if (!deleteMap.containsKey(dto.getItemId())) {
				insertOriRelation(dto, composeChangeMap, relationMap, ExamItemRelationEnum.COMPOSE, "合并");
				insertOriRelation(dto, conflictChangeMap, relationMap, ExamItemRelationEnum.CONFLICT, "互斥");
				insertOriRelation(dto, dependChangeMap, relationMap, ExamItemRelationEnum.DEPEND, "依赖");
				insertOriRelation(dto, familyChangeMap, relationMap, ExamItemRelationEnum.FAMILY, "父子");
			}
		}
		dealRelationByChange(relationMap, composeChangeMap, deleteMap, "合并");
		dealRelationByChange(relationMap, conflictChangeMap, deleteMap, "互斥");
		dealRelationByChange(relationMap, dependChangeMap, deleteMap, "依赖");
		dealRelationByChange(relationMap, familyChangeMap, deleteMap, "父子");

		return relationMap;
	}
	
	/**
	 * 获取增减关系次数
	 **/
	private static Map<Integer, Integer> getChangeLogInfo(Map<Integer, List<ExamItemChangeLog>> changeLogMap,Map<Integer, ExamItemChangeLog> deleteMap,PriceEffectPropertyEnum type){
		List<ExamItemChangeLog> itemChangeLogList = changeLogMap.get(type.getCode());
		//添加关系+1，删除关系-1
		Map<Integer, Integer> changeLogInfo = new HashMap<>();
		if (AssertUtil.isNotEmpty(itemChangeLogList)) {
			for (ExamItemChangeLog log : itemChangeLogList) {
				if (AssertUtil.isNotEmpty(log.getOriginalVal())) {
					String[] oldArr = log.getOriginalVal().split(",");
					for (String oldId : oldArr) {
						changeLogInfo.merge(Integer.valueOf(oldId), -1, (val,newVal)->val+newVal);
					}
				}else if (AssertUtil.isEmpty(log.getOriginalVal())&&!deleteMap.containsKey(log.getItemId())) {
					changeLogInfo.merge(log.getItemId(), 1, (val, newVal) -> val + newVal);
				}
				
				if (AssertUtil.isNotEmpty(log.getNewVal())) {
					String[] newArr = log.getNewVal().split(",");
					for(String newId:newArr){
						if (!deleteMap.containsKey(Integer.valueOf(newId))) {
							changeLogInfo.merge(Integer.valueOf(newId), 1, (val,newVal)->val+newVal);
						}
					}
				}else {
					changeLogInfo.merge(log.getItemId(), -1, (val,newVal)->val+newVal);
				}
			}
		}
		return changeLogInfo;
	}
	
	private static void insertOriRelation(ExamItemRelationDto dto,Map<Integer, Integer> changeMap,Map<Integer, String> relationMap,ExamItemRelationEnum relationType,String strRelationName){
		if((changeMap.get(dto.getItemId()) == null || changeMap.get(dto.getItemId()) >= 0)
				&& dto.getType() == relationType.getCode()){
			if(!relationMap.containsKey(dto.getItemId())){
				relationMap.put(dto.getItemId(), strRelationName);
			}
			if(relationMap.containsKey(dto.getItemId()) && !relationMap.get(dto.getItemId()).contains(strRelationName)){
				relationMap.put(dto.getItemId(), relationMap.get(dto.getItemId()) + "、" + strRelationName);
			}
		}
		if (dto.getType() == ExamItemRelationEnum.FAMILY.getCode() 
				&& (changeMap.get(dto.getItemId()) == null || changeMap.get(dto.getItemId()) >= 0)) {
			dealFamilyChildItem(dto, relationMap, relationType, "父子");
		}
		if(changeMap.get(dto.getItemId()) != null && changeMap.get(dto.getItemId()) < 0 
				&& dto.getType() == relationType.getCode()){
			changeMap.merge(dto.getItemId(), 1, (val, newVal) -> val + newVal);
		}
		
	}
	
	/**
	 * 父子项的子项加上关系标识
	 * @param dto
	 * @param changeMap
	 * @param relationMap
	 * @param relationType
	 * @param strRelationName
	 */
	private static void dealFamilyChildItem(ExamItemRelationDto dto, Map<Integer, String> relationMap, 
			ExamItemRelationEnum relationType, String strRelationName) {
		if(!relationMap.containsKey(dto.getRelatedItemId())){
			relationMap.put(dto.getRelatedItemId(), strRelationName);
		}
		if(relationMap.containsKey(dto.getRelatedItemId()) && !relationMap.get(dto.getRelatedItemId()).contains(strRelationName)){
			relationMap.put(dto.getRelatedItemId(), relationMap.get(dto.getRelatedItemId()) + "、" + strRelationName);
		}
		
	}
	
	private static void dealRelationByChange(Map<Integer, String> relationMap, Map<Integer, Integer> changeMap, 
			Map<Integer, ExamItemChangeLog> deleteMap, String strRelatioName){
		for(Integer id : changeMap.keySet()){
			if(changeMap.get(id) > 0 && !deleteMap.containsKey(id)){
				if(relationMap.get(id) == null){
					relationMap.put(id, strRelatioName);
				} else if(!relationMap.get(id).contains(strRelatioName)){
					relationMap.put(id, relationMap.get(id) + "、" + strRelatioName);
				}
			}
		}
	}

}
