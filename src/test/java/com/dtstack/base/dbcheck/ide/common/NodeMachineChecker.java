package com.dtstack.base.dbcheck.ide.common;

import com.dtstack.base.BaseTest;
import com.dtstack.model.domain.ide.common.NodeMachine;
import com.dtstack.util.db.DBMapper;
import com.dtstack.util.db.SqlException;

import java.util.List;
import java.util.Map;

public class NodeMachineChecker extends BaseTest {

    public static String selectContentFragment = "id,ip,port,machine_type,is_deleted,gmt_create,gmt_modified,app_type,deploy_info";

    public static NodeMachine getOneNodeMachineByType(String machineAppType, int nodeMachineType) throws SqlException {
        String sql = " select " + selectContentFragment + " from rdos_node_machine where is_deleted=0 and app_type=? and machine_type=? limit 1";
        List<Map<String, Object>> list = DBMapper.query(sql, machineAppType, nodeMachineType);
        NodeMachine nodeMachine = map2NodeMachine(list.get(0));
        return nodeMachine;
    }

    public static NodeMachine map2NodeMachine(Map<String, Object> map) {
        NodeMachine nodeMachine = new NodeMachine();
        nodeMachine.setId(Long.valueOf(map.get("id").toString()));
        nodeMachine.setIp(map.get("ip").toString());
        nodeMachine.setPort(Integer.valueOf(map.get("port").toString()));
        nodeMachine.setMachineType(Integer.valueOf(map.get("machine_type").toString()));
        nodeMachine.setIsDeleted(Integer.valueOf(map.get("is_deleted").toString()));
        nodeMachine.setAppType(map.get("app_type").toString());
        nodeMachine.setDeployInfo(map.get("deploy_info").toString());
        return nodeMachine;
    }
}
