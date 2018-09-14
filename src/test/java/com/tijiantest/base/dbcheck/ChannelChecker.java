package com.tijiantest.base.dbcheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tijiantest.base.BaseTest;
import com.tijiantest.model.account.Account;
import com.tijiantest.model.company.ChannelManagerVO;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 渠道验证
 * @author huifang
 *
 */
public class ChannelChecker extends BaseTest{

	   /**
     * 获取渠道商下客户经理
     * @param channelId
     * @return
     */
    public static List<Account> getManagerByChannel(Integer channelId){
    	List<Account> channelManagerList = new ArrayList<Account>();
    	String sql = "SELECT a.* FROM tb_account a LEFT JOIN tb_manager_channel_relation mcr on mcr.manager_id = a.id WHERE mcr.channel_id = ?;";
    	List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, channelId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(list!=null&&!list.isEmpty())
    		for(Map<String,Object> m : list){
    			Account a = new Account();
    			a.setId(Integer.valueOf(m.get("id").toString()));
    			if(m.get("employee_id")!=null)
    				a.setEmployeeId(m.get("employee_id").toString());
    			if(m.get("idcard")!=null)
    				a.setIdCard(m.get("idcard").toString());
    			if(m.get("id_type")!=null)
    				a.setIdType(Integer.valueOf(m.get("id_type").toString()));
    			if(m.get("mobile")!=null)
    				a.setMobile(m.get("mobile").toString());
    			a.setName(m.get("name").toString());
    			a.setStatus(Integer.valueOf(m.get("status").toString()));
    			a.setSystem(Integer.valueOf(m.get("system").toString()));
    			if(m.get("type")!=null)
    				a.setType(Integer.valueOf(m.get("type").toString()));
    			channelManagerList.add(a);
    		}
    	return channelManagerList;
    }
    
    

    /**
     * 获取渠道商下客户经理
     * @param channelId
     * @return
     */
    public static List<ChannelManagerVO> getManagerAccountByChannel(Integer channelId){
    	List<ChannelManagerVO> channelManagerList = new ArrayList<ChannelManagerVO>();
    	String sql = "SELECT mcr.manager_id,a.name,mcr.channel_id,h.name AS organizationName "
    			+ "FROM tb_manager_channel_relation mcr "
    			+ "LEFT JOIN tb_account a on a.id=mcr.manager_id "
    			+ "LEFT JOIN tb_hospital h on h.id = mcr.channel_id "
    			+ "WHERE mcr.channel_id = ? AND a.id is not null;";
    	List<Map<String, Object>> list = null;
		try {
			list = DBMapper.query(sql, channelId);
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(list!=null&&!list.isEmpty())
    		for(Map<String,Object> m : list){
    			ChannelManagerVO a = new ChannelManagerVO();
    			a.setId(Integer.valueOf(m.get("manager_id").toString()));
    			a.setName(m.get("name").toString());
    			a.setOrganizationId(Integer.valueOf(m.get("channel_id").toString()));
    			a.setOrganizationName(m.get("organizationName").toString());
    			channelManagerList.add(a);
    		}
    	return channelManagerList;
    }


}
